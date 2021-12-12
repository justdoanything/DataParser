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

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class FileToInsertQuery {
	/**
	 * Initial Values
	 */
	private int startWithLine = 0;
	private String readFilePath = MsgCode.MSG_CODE_FILE_PATH;
	private String writeFilePath = MsgCode.MSG_CODE_STRING_BLANK;
	private String spliter = MsgCode.MSG_CODE_FILE_DEFAULT_SPLITER;
	private boolean isWriteFile = true;
	private boolean isFileOpen = false;
	private boolean isGetString = false;
	private boolean isBulkInsert = true;
	private String tableName = MsgCode.MSG_CODE_STRING_BLANK;

	/**
	 * Class Constructor
	 */
	public FileToInsertQuery() {	}
	
	public FileToInsertQuery(int startWithLine) {
		this.startWithLine = startWithLine;
	}
	
	public FileToInsertQuery(int startWithLine, String readfilePath) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
	}
	
	public FileToInsertQuery(int startWithLine, String readfilePath, String writeFilePath) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
	}
	
	public FileToInsertQuery(int startWithLine, String readfilePath, String writeFilePath, String spliter) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
	}
	
	public FileToInsertQuery(int startWithLine, String readfilePath, String writeFilePath, String spliter, boolean isFileOpen) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isFileOpen = isFileOpen;
	}
	
	public FileToInsertQuery(int startWithLine, String readfilePath, String writeFilePath, String spliter, boolean isFileOpen, boolean isGetString) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isFileOpen = isFileOpen;
		this.isGetString = isGetString;
	}
	
	private String parseExcelType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {

		
		return "";
	}
	
	private String parseTextType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		
		BufferedReader br = null;
		BufferedWriter bw = null;
        try {      	
        	br = new BufferedReader(new FileReader(readFilePath));
            bw = new BufferedWriter(new FileWriter(writeFilePath));

            String line;
            String[] arr = new String[500000];
            int index = 0;
            while ((line = br.readLine()) != null) {
                arr[index++] = line;
            }
            
            String insertQuery = "";
            insertQuery += tableName;
            
            arr[0] = arr[0].replace("\t", ",");
            
            insertQuery += arr[0] + ") VALUES ";
                                    
    		for(int i=1; i< arr.length; i++) {
    			if(arr[i] == null)
    				break;
				arr[i] = "('" + arr[i] + "'";
				arr[i] = arr[i].replace("\t", "','");
				arr[i] = arr[i].replace("''", "null");
				if(arr[i+1] == null)
					arr[i] += ");";
				else
					arr[i] += "),";
    		}
    		
    		System.out.println(insertQuery);
			bw.write(insertQuery);
			bw.write("\n");
    		
    		for(int i = 1; i < arr.length; i++) {
    			if(arr[i] == null)
    				break;
    			System.out.println(arr[i]);
    			bw.write(arr[i]);
    			bw.write("\n");
    		}
    		bw.close();
    		if(isFileOpen) Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(br != null) try {br.close(); } catch (IOException e) {}
            if(bw != null) try {bw.close(); } catch (IOException e) {}
        }
		
		return "";
	}
	
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
	 * Valid private values
	 * @throws Exception
	 */
	private void validPrivateValues() throws ValidationException, NullPointerException {
		if(this.startWithLine < 0)
			throw new ValidationException("A required value has an exception : startWithLine should be over 0");
		
		if(this.readFilePath == null || this.writeFilePath == null || this.spliter == null || this.tableName == null)
			throw new NullPointerException("A required value has an exception : all of values cannot be null");
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
