package org.dataparser.parser.impl;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.ValidationException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dataparser.msg.MsgCode;
import org.dataparser.parser.AttributeToFileInterface;
import org.dataparser.util.DateUtil;
import org.dataparser.util.ExcelUtil;
import org.dataparser.util.FileUtil;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AttributeToFile implements AttributeToFileInterface {
	/**
	 * Initial Values
	 */
	@Builder.Default private int startWithLine = 0;
	private String readFilePath;
	private String writeFilePath;
	@Builder.Default private String spliter = MsgCode.MSG_CODE_FILE_DEFAULT_SPLITER;
	@Builder.Default private boolean isWriteFile = true;
	@Builder.Default private boolean isOpenFile = false;
	@Builder.Default private boolean isGetString = false;
	@Builder.Default private Map<String, Map<String, String>> codeMap = new HashMap<>();
	
	/**
	 * Add code value to codeMap
	 * @param name
	 * @param code
	 * @param value
	 */
	public void setCodeMap(String name, String code, String value) {
		if(!codeMap.containsKey(name)) {
			codeMap.put(name, new HashMap<String, String>());
		}
		codeMap.get(name).put(code, value);
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
	 * @throws FileSystemException
	 * @throws DateTimeParseException
	 * @throws StringIndexOutOfBoundsException
	 * @throws FileNotFoundException
	 */
	private void validRequiredValues() throws ValidationException, NullPointerException, StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException, FileNotFoundException {
		if(!FileUtil.isFileExist(this.readFilePath))
			throw new FileNotFoundException("There is no file in " + this.readFilePath); 

		if(this.startWithLine < 0)
			throw new ValidationException("A required value has an exception : startWithLine should be over 0.");
		
		if(this.readFilePath == null || this.spliter == null || this.codeMap == null)
			throw new NullPointerException("A required value has an exception : All of values cannot be null.");
		
		if(!this.isWriteFile && !this.isGetString)
			throw new ValidationException("A required value has an exception : Either isWriteFile or isGetString must be true.");

		if(FileUtil.getFileExtension(this.readFilePath).equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV) && !this.spliter.equals(","))
			throw new ValidationException("A required value has an exception : csv file must be ','.");	
		
		if(!this.isWriteFile && this.isOpenFile)
			throw new ValidationException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");		
		this.isOpenFile = false;

		if(this.readFilePath == null || this.readFilePath.length() == 0)
			this.setDefaultWriteFilePath(this.readFilePath);
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
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		BufferedReader br = null;
		BufferedWriter bw = null;
		StringBuilder resultString = new StringBuilder();
		
		try {
			// Set Reader and Writer and Open file
			br = new BufferedReader(new FileReader(readFilePath));
			if(this.isWriteFile) bw = new BufferedWriter(new FileWriter(writeFilePath));
			
			// Read Excel File and Put line to resultMap
			String line;
			String[] lineArray;
			int index = 0;
			String entityName, attributeName, attributeValue;
			while((line = br.readLine()) != null) {
				// Checking startWithLine
				if(this.startWithLine != 0) {
					this.startWithLine -= 1;
					continue;
				}
				
				lineArray = line.split("\\" + this.spliter);
				entityName = lineArray.length == 0 ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[0];
				attributeName = lineArray.length == 1 ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[1];
				attributeValue = lineArray.length == 2 ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[2];
				this.createResultMap(resultMap, entityName, attributeName, attributeValue);
				index = 1;
			}
			
			// Throw IOException if startWithLine over than the row there is in file
			if(index == 0)
				throw new IOException("startWithLine over than the row there is in file");
			
		  // Add entity to entityList
		 	List<String> entityList = new ArrayList<>();
		 	for(String entity : resultMap.keySet()) {
		 		if(!entityList.contains(entity))
		 			entityList.add(entity);
		 	}
			
			// Write attribute in first line
			if(this.isWriteFile) bw.write(MsgCode.MSG_CODE_FIELD_NAME + this.spliter);
			if(this.isGetString) resultString.append(MsgCode.MSG_CODE_FIELD_NAME).append(this.spliter);
			List<String> attributeList = new ArrayList<>();
			for(String entity : entityList) {
				for(String attribute : (resultMap.get(entity)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						if(this.isWriteFile) { bw.write(attribute); bw.write(this.spliter); bw.flush(); }
						if(this.isGetString) resultString.append(attribute).append(this.spliter);
						
					}
				}
			}
			if(this.isWriteFile) { bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE); bw.flush(); }
			if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
			
			// Write Attribute value as attribute and name
			for(String entity : entityList) {
				if(this.isWriteFile) { bw.write(entity); bw.write(this.spliter); }
				if(this.isGetString) resultString.append(entity).append(this.spliter);
				for(String attribute : attributeList) {
					if((resultMap.get(entity)).containsKey(attribute)) {
						if(this.isWriteFile) { bw.write(resultMap.get(entity).get(attribute)); bw.write(this.spliter); bw.flush(); }
						if(this.isGetString) resultString.append(resultMap.get(entity).get(attribute)).append(this.spliter);
					} else {
						if(this.isWriteFile) { bw.write(MsgCode.MSG_CODE_STRING_BLANK); bw.write(this.spliter); bw.flush(); }
						if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_BLANK).append(this.spliter);
					}
				}
				
				// Write result into file if isWirteFile is true
				if(this.isWriteFile) { bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE); bw.flush(); }
				
		    // Set result if isGetString is true				
				if(this.isGetString) {
					resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
				}
			}
			
			if(this.isOpenFile)
				Desktop.getDesktop().edit(new File(writeFilePath));
		}catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}finally {
			if(br != null) try { br.close(); } catch(IOException e) {throw new IOException(e);}
			if(bw != null) try { br.close(); } catch(IOException e) {throw new IOException(e);}
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
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		Workbook workbook = null;
		StringBuilder resultString = new StringBuilder();

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
			
			// Throw IOException if startWithLine over than the row there is in file
			if(this.startWithLine != 0 && sheet.getRow(this.startWithLine - 1) == null)
				throw new IOException("startWithLine over than the row there is in file");
			
			// Read Excel File and Put line to resultMap
			Row row = null;
			String entityName, attributeName, attributeValue; 
			for(int index = this.startWithLine; index < sheet.getPhysicalNumberOfRows(); index++) {
				row = sheet.getRow(index);
				entityName = ExcelUtil.getCellValue(row.getCell(0));
				attributeName = ExcelUtil.getCellValue(row.getCell(1));
				attributeValue = ExcelUtil.getCellValue(row.getCell(2));
				
				this.createResultMap(resultMap, entityName, attributeName, attributeValue);
			}
			
			// Add name to namelist
			List<String> entityList = new ArrayList<>();
			for(String entity : resultMap.keySet()) {
				if(!entityList.contains(entity))
					entityList.add(entity);
			}
			
			// Write attribute in first line
			List<String> attributeList = new ArrayList<>();
			int rowIndex = 0;
			int cellIndex = 0;
			Sheet resultSheet = workbook.createSheet(MsgCode.MSG_CODE_RESULT_SHEET_NAME);
			row = resultSheet.createRow(rowIndex++);
			if(this.isGetString) resultString.append(MsgCode.MSG_CODE_FIELD_NAME).append(this.spliter);
			if(this.isWriteFile) row.createCell(cellIndex++).setCellValue(MsgCode.MSG_CODE_FIELD_NAME);
			for(String entity : entityList) {
				for(String attribute : (resultMap.get(entity)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						if(this.isWriteFile) row.createCell(cellIndex++).setCellValue(attribute);
						if(this.isGetString) resultString.append(attribute).append(MsgCode.MSG_CODE_STRING_TAB);
					}
				}
			}
			if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
			
			// Write Attribute value as attribute and name
			for(String entity : entityList) {
				cellIndex = 0;
				row = resultSheet.createRow(rowIndex++);
				if(this.isWriteFile) row.createCell(cellIndex++).setCellValue(entity);
				if(this.isGetString) resultString.append(entity).append(MsgCode.MSG_CODE_STRING_TAB);
				
				for(String attribute : attributeList) {
					if((resultMap.get(entity)).containsKey(attribute)) {
						if(this.isWriteFile) row.createCell(cellIndex++).setCellValue(resultMap.get(entity).get(attribute));
						if(this.isGetString) resultString.append(resultMap.get(entity).get(attribute)).append(MsgCode.MSG_CODE_STRING_TAB);
					} else {
						if(this.isWriteFile) row.createCell(cellIndex++).setCellValue(MsgCode.MSG_CODE_STRING_BLANK);
						if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_BLANK).append(MsgCode.MSG_CODE_STRING_TAB);
					}
				}
			}
			
			// Write result into file if isWirteFile is true
			if(this.isWriteFile) {
				FileOutputStream fos = new FileOutputStream(this.writeFilePath, false);
				workbook.write(fos);
				fos.flush();
				fos.close();
			}
						
			// Set result if isGetString is true
			if(this.isGetString) {
				resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
			}
			
			if(this.isOpenFile)
				Desktop.getDesktop().edit(new File(writeFilePath));
		} catch (Exception e) {
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
	 * @throws FileSystemException
	 */
	private void setDefaultWriteFilePath(String readFilePath) throws StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException {
		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		if(this.writeFilePath == null || this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			this.writeFilePath = FileUtil.getFileName(readFilePath)
			+ "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0);
			
			if(FileUtil.getFileExtension(readFilePath) != null)
			  this.writeFilePath += "." + FileUtil.getFileExtension(readFilePath);
		}
	}
	
	/**
	 * Put lines in file to resultMap
	 * @param resultMap
	 * @param entityName
	 * @param attributeName
	 * @param attributeValue
	 */
	private void createResultMap(Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
		if(!resultMap.containsKey(entityName)) 
			resultMap.put(entityName, new HashMap<String, String>());

		// Change value if there is code in codeMap
		if(attributeValue != null && codeMap.containsKey(attributeName)) 
			attributeValue = this.changeCodeValue(attributeName, attributeValue);
		// Put blank " " if the attribute value is empty.
		resultMap.get(entityName).put(attributeName,  attributeValue.equals(MsgCode.MSG_CODE_STRING_SPACE) ? MsgCode.MSG_CODE_STRING_SPACE : attributeValue);
	}
	
	/**
	 * Change an attribute value if there is mapped code
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	private String changeCodeValue(String attributeName, String attributeValue) {
		return codeMap.get(attributeName).get(attributeValue) != null ? codeMap.get(attributeName).get(attributeValue) : attributeValue;
	}
}
