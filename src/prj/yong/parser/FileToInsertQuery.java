package prj.yong.parser;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import msg.MsgCode;
import prj.yong.util.DateUtil;
import prj.yong.util.FileUtil;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class FileToInsertQuery {
	
	/******************************************************
	 * 
	 * This class makes Excel Chart format like below.
	 * 
	 * [ Input ]
	 * Name	 | Size		| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | 65 inch	| LG		| HIGH		|
	 * Audio | 32		| Apple		|			| Dual
	 *
	 * 
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * [ Output ]
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel )
	 * VALUES
	 * ('TV', '65 inch', 'LG', 'HIGH' null)
	 * ,('Audio', '32', 'Apple', null, 'Dual');
	 *
	 ******************************************************/
	
	/**
	 * Initial Values
	 */
	private String readFilePath = MsgCode.MSG_CODE_FILE_PATH;
	private String writeFilePath = MsgCode.MSG_CODE_STRING_BLANK;
	private String spliter = MsgCode.MSG_CODE_FILE_DEFAULT_SPLITER;
	private boolean isWriteFile = true;
	private boolean isOpenFile = false;
	private boolean isGetString = false;
	private boolean isBulkInsert = true;
	private String tableName = MsgCode.MSG_CODE_STRING_BLANK;

	/**
	 * Class Constructor
	 */
	public FileToInsertQuery() {	}
	
	public FileToInsertQuery(String readfilePath) {
		this.readFilePath = readfilePath;
	}
	
	public FileToInsertQuery(String readfilePath, String writeFilePath) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
	}
	
	public FileToInsertQuery(String readfilePath, String writeFilePath, String spliter) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
	}
	
	public FileToInsertQuery(String readfilePath, String writeFilePath, String spliter, boolean isWriteFile, boolean isOpenFile) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isWriteFile = isWriteFile;
		this.isOpenFile = isOpenFile;
	}
	
	public FileToInsertQuery(String readfilePath, String writeFilePath, String spliter, boolean isWriteFile, boolean isOpenFile, boolean isGetString) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isWriteFile = isWriteFile;
		this.isOpenFile = isOpenFile;
		this.isGetString = isGetString;
	}
	
	public FileToInsertQuery(String readfilePath, String writeFilePath, String spliter, boolean isWriteFile, boolean isOpenFile, boolean isGetString, boolean isBulkInsert) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isWriteFile = isWriteFile;
		this.isOpenFile = isOpenFile;
		this.isGetString = isGetString;
		this.isBulkInsert = isBulkInsert;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public String parse() throws Exception {
		String resultString = "";
		String readFileExtension = this.readFilePath.contains(".") ? this.readFilePath.substring(this.readFilePath.lastIndexOf("."), readFilePath.length()) : MsgCode.MSG_CODE_STRING_BLANK;
		
		this.validPrivateValues();
		
		if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_TXT)
				|| readFileExtension.equals(MsgCode.MSG_CODE_STRING_BLANK)){
			resultString = this.parseTextType(readFileExtension);
		} else if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLS)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLSX)){
			resultString = this.parseExcelType(readFileExtension);
		} else {
			throw new FileNotFoundException("A extension of file you read must be '.csv', '.xls', '.xlsx' and '.txt'");
		}
		return resultString;
	}
	
	/**
	 * 
	 * @param readFileExtension
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws IOException
	 */
	private String parseTextType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		
		BufferedReader br = null;
		BufferedWriter bw = null;
		StringBuilder resultString = new StringBuilder();
		
		// Checking file is existed and Set writeFilePath
		if(FileUtil.isFileExist(this.readFilePath)) {
			// Set writeFilePath if do not set manually
			this.setDefaultWriteFilePath(readFileExtension);
		}
		
        try {      	
        	// spliter of csv should be ,
     		if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV))
     			this.spliter = ",";
        	
        	br = new BufferedReader(new FileReader(readFilePath));
        	if(this.isWriteFile) bw = new BufferedWriter(new FileWriter(writeFilePath));

        	// Read File
            String line;
            StringBuilder queryHeader = new StringBuilder("INSERT INTO " + this.tableName + " (");
            StringBuilder queryBody = new StringBuilder();
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
            	if(isFirst) {
            		line = line.replace(this.spliter, ", ");
            		queryHeader.append(line).append(") VALUES ");;
            		isFirst = false;
            	} else {
            		line = line.replace(this.spliter, "', '").replace("''", "null");
            		
            		if(isBulkInsert){
            			queryBody.append("('" + line + "'),\r\n");
            		} else {
            			queryBody.append(queryHeader).append("('" + line + "');\r\n");
            		}
            	}
            }
            if(isBulkInsert)
            	queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(","), ";");
            
            if(this.isWriteFile) {
            	bw.write(queryHeader.toString());
            	bw.write(queryBody.toString());
            	bw.flush();
            }
            if(this.isGetString)
            	resultString.append(queryHeader).append(queryBody);            	
            	
    		bw.close();
    		if(isOpenFile) 
    			Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) try {br.close(); } catch (IOException e) {throw new IOException(e);}
            if(bw != null) try {bw.close(); } catch (IOException e) {throw new IOException(e);}
        }
		
		return resultString.toString();
	}
	
	/**
	 * 
	 * @param readFileExtension
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws IOException
	 */
	private String parseExcelType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {

		
		return "";
	}
	
	
	
	/**
	 * Valid private values
	 * @throws Exception
	 */
	private void validPrivateValues() throws ValidationException, NullPointerException {
		if(this.readFilePath == null || this.writeFilePath == null || this.spliter == null || this.tableName == null)
			throw new NullPointerException("A required value has an exception : all of values cannot be null");
		
		if(!this.isWriteFile && !this.isGetString)
			throw new ValidationException("A required value has an exception : Either isWriteFile or isGetString must be true.");
		
		if(!isWriteFile)
			this.isOpenFile = false;
	}
	
	/**
	 * Set default writeFilePath if do not set manually
	 * @param readFileExtension
	 * @throws Exception
	 */
	private void setDefaultWriteFilePath(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException {
		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		if(this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			this.writeFilePath = readFilePath.replace(readFileExtension, "") 
									+ "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) 
									+ readFileExtension;
		}
	}

	
	public List<String> printBulkInsertQuery(Object vo, Map additional, JSONArray contentList, int bulkInsertCnt) throws Exception {
		String bulkInsertQuery = "";
		List<String> bulkInsertQueryList = new ArrayList<>();

		for (int index = 0; index < contentList.length(); index++) {
			JSONObject json = contentList.getJSONObject(index);

			// Put additional key/value to JSONObject
			if(additional != null) {
				for(Object key : additional.keySet()) {
					json.put(key.toString(), additional.get(key).toString());
				}
			}
			
			// JSONObject to VO
			Gson gson = new Gson();
			vo = gson.fromJson(json.toString(), vo.getClass());
//			bulkInsertQuery += vo.getAllValues();
			
			if (index > 0 && (index+1) % bulkInsertCnt == 0) {
				bulkInsertQueryList.add(bulkInsertQuery);
				bulkInsertQuery = "";
			} else {
				if (index != contentList.length() - 1)
					bulkInsertQuery += ", ";
				else{
					bulkInsertQueryList.add(bulkInsertQuery);
				}
			}
		}

		return bulkInsertQueryList;
	}
}
