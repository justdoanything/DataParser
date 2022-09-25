package org.dataparser.parser.impl;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.xml.bind.ValidationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dataparser.msg.MsgCode;
import org.dataparser.parser.FileToInsertQueryInterface;
import org.dataparser.util.ExcelUtil;
import org.dataparser.util.FileUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileToInsertQuery implements FileToInsertQueryInterface {
	
	/**
	 * Initial Values
	 */
	private String readFilePath;
	private String writeFilePath;
	private String tableName = MsgCode.MSG_CODE_STRING_BLANK;
	@Builder.Default private int bulkInsertCnt = 100;
	@Builder.Default private String spliter = MsgCode.MSG_CODE_FILE_DEFAULT_SPLITER;
	@Builder.Default private boolean isWriteFile = true;
	@Builder.Default private boolean isOpenFile = false;
	@Builder.Default private boolean isGetString = false;
	@Builder.Default private boolean isBulkInsert = true;
	
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
		String readFileExtension = FileUtil.getFileExtension(readFilePath);
		
		this.validRequiredValues();
		
		if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_TXT)
				|| readFileExtension.equals(MsgCode.MSG_CODE_STRING_BLANK)){
			resultString = this.parseTextType(readFileExtension);
		} else if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLS)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLSX)){
			resultString = this.parseExcelType(readFileExtension);
		} else {
			throw new FileNotFoundException("A extension of file must be '.csv', '.xls', '.xlsx', '.txt' or empty");
		}
		return resultString;
	}
	
	/**
	 * Valid private values
	 * @throws ValidationException
	 * @throws NullPointerException
	 * @throws FileNotFoundException
	 * @throws StringIndexOutOfBoundsException
	 * @throws DateTimeParseException
	 * @throws FileSystemException
	 */
	private void validRequiredValues() throws ValidationException, NullPointerException, FileNotFoundException, StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException {
		if(!FileUtil.isFileExist(this.readFilePath))
			throw new FileNotFoundException("There is no file in " + this.readFilePath); 

		if(this.isWriteFile && (this.writeFilePath == null || this.writeFilePath.length() == 0))
			this.writeFilePath = FileUtil.setDefaultWriteFilePath(this.readFilePath);
			
		if(this.readFilePath == null || (this.isWriteFile && this.writeFilePath == null) || this.spliter == null || this.tableName == null)
			throw new NullPointerException("A required value has an exception : all of values cannot be null");
		
		if(this.tableName.length() < 1)
			throw new NullPointerException("A required value has an exception : tableName must be set.");
		
		if(!this.isWriteFile && !this.isGetString)
			throw new ValidationException("A required value has an exception : Either isWriteFile or isGetString must be true.");

		if(!this.isWriteFile && this.isOpenFile)
			throw new ValidationException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");		

		if(FileUtil.getFileExtension(this.readFilePath).equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV) && !this.spliter.equals(","))
			throw new ValidationException("A required value has an exception : csv file must be ','.");	
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
		StringBuilder resultString = new StringBuilder();
		
        try (
					BufferedReader br = new BufferedReader(new FileReader(readFilePath));
					BufferedWriter bw = this.isWriteFile ? new BufferedWriter(new FileWriter(writeFilePath)) : null;
				) {      	
        	// spliter of csv should be ,
     			if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV))	
     				this.spliter = ",";
        	
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
										line = Arrays.stream(line.split("\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		queryHeader.append(line).append(") VALUES \r\n");;
                		isFirst = false;
                	} else {
                		// Merge Query Body
                		line = line.replace(this.spliter, "', '").replace("''", "null");
										line = Arrays.stream(line.split("\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		if(index > 0 && index % this.bulkInsertCnt == 0) {
                			// End of bulkInsertCnt
            				queryBody.append("('").append(line.trim()).append("');\r\n\r\n");
            				queryBody.append(queryHeader);
                		} else {
                			queryBody.append("('").append(line.trim()).append("'),\r\n");
                		}
                	}
            		index++;
            	} else {
            		if(isFirst) {
                		// Write Query Header
										line = Arrays.stream(line.split("\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		line = line.replace(this.spliter, ", ");
                		queryHeader.append(line).append(") VALUES ");
                		isFirst = false;
                	} else {
                		// Merge Query Body
										line = Arrays.stream(line.split("\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		line = line.replace(this.spliter, "', '").replace("''", "null");
                		queryBody.append(queryHeader).append("('").append(line.trim()).append("');\r\n");
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

		try (
			FileInputStream fis = new FileInputStream(this.readFilePath);	
		){
			// spliter of xls, xlsx should be \t
			this.spliter = MsgCode.MSG_CODE_STRING_TAB;
			
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
}
