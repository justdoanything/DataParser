package prj.yong.parser;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeParseException;

import javax.xml.bind.ValidationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Getter;
import lombok.Setter;
import prj.yong.msg.MsgCode;
import prj.yong.util.DateUtil;
import prj.yong.util.ExcelUtil;
import prj.yong.util.FileUtil;

@Getter
@Setter
@SuppressWarnings("resource")
public class FileToInsertQuery {
	
	/******************************************************
	 * 
	 * This class read a file and makes insert query format like below.
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
	 * 1. Bulk Insert Type
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel )
	 * VALUES
	 * ('TV', '65 inch', 'LG', 'HIGH' null)
	 * ,('Audio', '32', 'Apple', null, 'Dual');
	 * 
	 * 2. Non-Bulk Insert Type
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel ) VALUES ('TV', '65 inch', 'LG', 'HIGH' null);
	 * INSERT INTO {tableName} ( Name, Size, Company, Quality, Channel ) VALUES ('Audio', '32', 'Apple', null, 'Dual');; 
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
	private int bulkInsertCnt = 100;
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
	
	public FileToInsertQuery(String readfilePath, String writeFilePath, String spliter, boolean isWriteFile, boolean isOpenFile, boolean isGetString, boolean isBulkInsert, int bulkInsertCnt) {
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isWriteFile = isWriteFile;
		this.isOpenFile = isOpenFile;
		this.isGetString = isGetString;
		this.isBulkInsert = isBulkInsert;
		this.bulkInsertCnt = bulkInsertCnt;
	}
	
	/**
	 * Parse the data as a extension of your file
	 * @return
	 * @throws ValidationException
	 * @throws NullPointerException
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws IOException
	 */
	public String parse() throws ValidationException, NullPointerException, StringIndexOutOfBoundsException, DateTimeParseException, IOException {
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
	 * @throws ValidationException
	 * @throws NullPointerException
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
	 * Parse text file (.txt, .csv)
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
        	
     		// Set Reader and Writer and Open file
        	br = new BufferedReader(new FileReader(readFilePath));
        	if(this.isWriteFile) bw = new BufferedWriter(new FileWriter(writeFilePath));

        	// Read Excel File and write queryHeader and queryBody
            String line;
            StringBuilder queryHeader = new StringBuilder("INSERT INTO " + this.tableName + " (");
            StringBuilder queryBody = new StringBuilder();
            boolean isFirst = true;
            int index = 0;
            while ((line = br.readLine()) != null) {
            	if(isBulkInsert) {
            		if(isFirst) {
                		// Write Query Header
                		line = line.replace(this.spliter, ", ");
                		queryHeader.append(line).append(") VALUES \r\n");;
                		isFirst = false;
                	} else {
                		// Merge Query Body
                		line = line.replace(this.spliter, "', '").replace("''", "null");
                		if(index > 0 && index % this.bulkInsertCnt == 0) {
                			// End of bulkInsertCnt
            				queryBody.append("('").append(line).append("');\r\n\r\n");
            				queryBody.append(queryHeader);
                		} else {
                			queryBody.append("('").append(line).append("'),\r\n");
                		}
                	}
            		index++;
            	} else {
            		if(isFirst) {
                		// Write Query Header
                		line = line.replace(this.spliter, ", ");
                		queryHeader.append(line).append(") VALUES ");;
                		isFirst = false;
                	} else {
                		// Merge Query Body
                		line = line.replace(this.spliter, "', '").replace("''", "null");
                		queryBody.append(queryHeader).append("('").append(line).append("');\r\n");
                	}
            	}
            }
            // Replace last word ',' to ';'
            if(isBulkInsert)
            	queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");
            
            // Write result into file if isWirteFile is true
            if(this.isWriteFile) {
            	if(this.isBulkInsert) {
            		bw.write(queryHeader.toString());
            		bw.flush();
            	}
            	
            	bw.write(queryBody.toString());
            	bw.flush();
            	
            	if(isOpenFile) 
        			Desktop.getDesktop().edit(new File(writeFilePath));
            }
            
            // Set result if isGetString is true
            if(this.isGetString) {
            	if(this.isBulkInsert) {
					resultString.append(queryHeader);
            	}
            	resultString.append(queryBody);            	
            }
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
	 * Parse excel file (.xlsx, .xls)
	 * @param readFileExtension
	 * @return
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws IOException
	 */
	private String parseExcelType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		Workbook workbook = null;
		StringBuilder resultString = new StringBuilder();

		// Checking file is existed and Set writeFilePath
		if(FileUtil.isFileExist(this.readFilePath)) {
			// Set writeFilePath if do not set manually
			this.setDefaultWriteFilePath(readFileExtension);
		}

		try {
			// spliter of xls, xlsx should be \t
			this.spliter = MsgCode.MSG_CODE_STRING_TAB;
			
			// Set FileInputStream and Open file
			FileInputStream fis = new FileInputStream(this.readFilePath);
			if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLS)) 
				workbook = new HSSFWorkbook(fis);
			else
				workbook = new XSSFWorkbook(fis);
			fis.close();
			
			// Select first sheet
			Sheet sheet = workbook.getSheetAt(0);
			if(sheet == null)
				throw new IOException("There is no sheet in file");
			
			// Read Excel File and write queryHeader and queryBody
			StringBuilder queryHeader = new StringBuilder("INSERT INTO " + this.tableName + " (");
			StringBuilder queryBody = new StringBuilder();
			boolean isFirst = true;
			Row row = null;
			int cellCount = -1;
			int index = 0;
			for(int rowNum = 0; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
				StringBuilder queryBodyLine = new StringBuilder();
				row = sheet.getRow(rowNum);
				int cellIndex = 0;
				
				// Write queryHeader
				if(isFirst) {
					// Write queryHeader and count header fields 
					while(row.getCell(cellIndex) != null) {
						// Write Query Header
						queryHeader.append(ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", ""));
						if(row.getCell(cellIndex + 1) != null) queryHeader.append(", ");
						cellIndex++;
					}
					cellCount = cellIndex;
				} else {
					// Write queryBody as much as cellCount 
					while(cellIndex < cellCount) {
						// Merge Query Body
						queryBodyLine.append(("'" + ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", "") + "'").replace("''", "null"));
						if(cellIndex + 1 != cellCount) queryBodyLine.append(", ");
						cellIndex++;
					}
				} 
				
				// Write queryBody
				if(isFirst && this.isBulkInsert) {
					queryHeader.append(") VALUES \r\n");
					isFirst = false;
				} else if(isFirst && !this.isBulkInsert) {
					queryHeader.append(") VALUES ");
					isFirst = false;
				} else if(!isFirst && this.isBulkInsert) {
					if(index > 0 && (index  + 1) % this.bulkInsertCnt == 0) {
						queryBody.append("(").append(queryBodyLine).append(");\r\n\r\n");
						queryBody.append(queryHeader);
					} else {
						queryBody.append("(").append(queryBodyLine).append("),\r\n");
					}
					index++;
				} else if (!isFirst && !this.isBulkInsert) {
					queryBody.append(queryHeader).append("('").append(queryBodyLine).append("');\r\n");
				} else {
					
				}
			}
				
			// Replace last word ',' to ';'
			if(isBulkInsert)
				queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");
			
			// Write result into file if isWirteFile is true
			if(this.isWriteFile) {
				//Write txt file (not excel file_
				writeFilePath = writeFilePath.replace("xlsx", "txt").replace("xls", "txt");
				BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath));
            	
				if(this.isBulkInsert) {
            		bw.write(queryHeader.toString());
            		bw.flush();
            	}
				
				bw.write(queryBody.toString());
            	bw.flush();
            	
            	if(isOpenFile) 
    				Desktop.getDesktop().edit(new File(writeFilePath));
			}
			
			// Set result if isGetString is true
			if(this.isGetString) {
				if(this.isBulkInsert) {
					resultString.append(queryHeader);
            	}
				resultString.append(queryBody);            	
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		} finally {
			// I/O Close
			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new IOException(e);}
		}
		
		return resultString.toString();
	}

	
	/**
	 * Set default writeFilePath if do not set manually
	 * @param readFileExtension
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 */
	private void setDefaultWriteFilePath(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException {
		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		if(this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			this.writeFilePath = readFilePath.replace(readFileExtension, "") 
									+ "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) 
									+ readFileExtension;
		}
	}
}
