package prj.yong.parser;

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

import lombok.Getter;
import lombok.Setter;
import msg.MsgCode;
import prj.yong.util.CommonUtil;
import prj.yong.util.DateUtil;
import prj.yong.util.ExcelUtil;
import prj.yong.util.FileUtil;

@Getter
@Setter
@SuppressWarnings("resource")
public class AttributeToExcel {

	/******************************************************
	 * 
	 * This class makes Excel Chart format like below.
	 * 
	 * [ Input ]
	 * Name  | Attribute Name | Attribute Value
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | Size			  | 65 inch
	 * TV	 | Company		  | LG
	 * TV	 | Quality		  | HIGH
	 * Audio | Size			  | 32
	 * Audio | Company		  | Apple
	 * Audio | Channel		  | Dual
	 * 
	 * ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
	 * 
	 * [ Output]
	 * Name	 | Size		| Company	| Quality	| Channel
	 * ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
	 * TV	 | 65 inch	| LG		| HIGH		|
	 * Autio | 32		| Apple		|			| Dual
	 *
	 ******************************************************/	
	
	/**
	 * Initial Values
	 */
	private int startWithLine = 0;
	private String readFilePath = MsgCode.MSG_CODE_FILE_PATH;
	private String writeFilePath = MsgCode.MSG_CODE_STRING_BLANK;
	private String spliter = MsgCode.MSG_CODE_FILE_DEFAULT_SPLITER;
	private boolean isFileOpen = false;
	private boolean isGetString = false;
	private Map<String, Map<String, String>> codeMap = new HashMap<>();
	
	/**
	 * Class Constructor
	 */
	public AttributeToExcel() {	}
	
	public AttributeToExcel(int startWithLine) {
		this.startWithLine = startWithLine;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath, String spliter) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath, String spliter, boolean isFileOpen) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isFileOpen = isFileOpen;
	}
	
	public AttributeToExcel(int startWithLine, String readfilePath, String writeFilePath, String spliter, boolean isFileOpen, boolean isGetString) {
		this.startWithLine = startWithLine;
		this.readFilePath = readfilePath;
		this.writeFilePath = writeFilePath;
		this.spliter = spliter;
		this.isFileOpen = isFileOpen;
		this.isGetString = isGetString;
	}
	
	/**
	 * Add code value to codeMap
	 * @param name
	 * @param code
	 * @param value
	 */
	public void addCodeValue(String name, String code, String value) {
		if(!codeMap.containsKey(name)) {
			codeMap.put(name, new HashMap<String, String>());
		}
		codeMap.get(name).put(code, value);
	}
	
	/**
	 * Parse the data as a extension of your file
	 * @throws IOException 
	 * @throws DateTimeParseException 
	 * @throws StringIndexOutOfBoundsException 
	 * @throws Exception
	 */
	public String parse() throws ValidationException, NullPointerException, StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		String resultString = "";
		String readFileExtension = this.readFilePath.substring(this.readFilePath.lastIndexOf("."), readFilePath.length());
		
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
		
		if(this.readFilePath == null || this.writeFilePath == null || this.spliter == null || this.codeMap == null)
			throw new NullPointerException("A required value has an exception : all of values cannot be null");
	}
	
	/**
	 * Parse excel file (.xlsx, .xls)
	 * @param readFileExtension
	 * @throws DateTimeParseException 
	 * @throws StringIndexOutOfBoundsException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	private String parseExcelType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		Workbook workbook = null;
		StringBuilder resultString = new StringBuilder();

		// Checking file is existed and Set writeFilePath
		if(FileUtil.isFileExist(this.readFilePath)) {
			// Set writeFilePath if do not set manually
			this.setDefaultWriteFilePath(readFileExtension);
		}

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
	 	if(this.isGetString) resultString.append(MsgCode.MSG_CODE_FIELD_NAME + this.spliter);
	 	row.createCell(cellIndex++).setCellValue(MsgCode.MSG_CODE_FIELD_NAME);;
	 	for(String entity : entityList) {
	 		for(String attribute : (resultMap.get(entity)).keySet()) {
	 			if(!attributeList.contains(attribute)) {
	 				attributeList.add(attribute);
	 				row.createCell(cellIndex++).setCellValue(attribute);
	 				if(this.isGetString) resultString.append(attribute + MsgCode.MSG_CODE_STRING_TAB);
	 			}
	 		}
	 	}
	 	if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
	 	
	 	// Write Attribute value as attribute and name
	 	for(String entity : entityList) {
	 		cellIndex = 0;
	 		row = resultSheet.createRow(rowIndex++);
	 		row.createCell(cellIndex++).setCellValue(entity);
	 		if(this.isGetString) resultString.append(entity + MsgCode.MSG_CODE_STRING_TAB);
	 		
	 		for(String attribute : attributeList) {
	 			if((resultMap.get(entity)).containsKey(attribute)) {
	 				row.createCell(cellIndex++).setCellValue(resultMap.get(entity).get(attribute));
	 				if(this.isGetString) resultString.append(resultMap.get(entity).get(attribute) + MsgCode.MSG_CODE_STRING_TAB);
	 			} else {
	 				row.createCell(cellIndex++).setCellValue(MsgCode.MSG_CODE_STRING_BLANK);
	 				if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_BLANK + MsgCode.MSG_CODE_STRING_TAB);
	 			}
	 		}
	 	}
	 	if(this.isGetString) {
	 		resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
//	 		CommonUtil.copyClipboard(resultString.toString());
	 	}
	 	
	 	// Write result file
	 	FileOutputStream fos = new FileOutputStream(this.writeFilePath, false);
	    workbook.write(fos);
	    fos.flush();
	    
	    // I/O Close
	 	workbook.close();
	 	fos.close();
	 	
	 	if(this.isFileOpen)
			Desktop.getDesktop().edit(new File(writeFilePath));
	 	
	 	return resultString.toString();
	}
	
	/**
	 * Parse text file (.txt, .csv)
	 * @param readFileExtension
	 * @throws DateTimeParseException 
	 * @throws StringIndexOutOfBoundsException 
	 * @throws IOException 
	 * @throws Exception 
	 */
	private String parseTextType(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException, IOException {
		Map<String, Map<String, String>> resultMap = new HashMap<>();
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
			bw = new BufferedWriter(new FileWriter(writeFilePath));
			
			// Put line to resultMap from file
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
				
				lineArray = line.split(this.spliter);
				entityName = lineArray[0] == null ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[0];
				attributeName = lineArray[1] == null ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[1];
				attributeValue = lineArray[2] == null ? MsgCode.MSG_CODE_STRING_SPACE : lineArray[2];
				this.createResultMap(resultMap, entityName, attributeName, attributeValue);
				index = 1;
			}
			if(index == 0)
				throw new IOException("startWithLine over than the row there is in file");
			
		    // Add entity to entityList
		 	List<String> entityList = new ArrayList<>();
		 	for(String entity : resultMap.keySet()) {
		 		if(!entityList.contains(entity))
		 			entityList.add(entity);
		 	}
			
			// Write attribute in first line
			bw.write(MsgCode.MSG_CODE_FIELD_NAME + this.spliter);
			List<String> attributeList = new ArrayList<>();
			for(String entity : entityList) {
				for(String attribute : (resultMap.get(entity)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						bw.write(attribute);
						bw.write(this.spliter);
						if(this.isGetString) resultString.append(attribute + this.spliter);
						bw.flush();
						
					}
				}
			}
			bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
			if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
			bw.flush();

			
			// Write Attribute value as attribute and name
			for(String entity : entityList) {
				bw.write(entity);
				bw.write(this.spliter);
				if(this.isGetString) resultString.append(entity + this.spliter);
				for(String attribute : attributeList) {
					if((resultMap.get(entity)).containsKey(attribute)) {
						bw.write(resultMap.get(entity).get(attribute));
						bw.write(this.spliter);
						if(this.isGetString) resultString.append(resultMap.get(entity).get(attribute) + this.spliter);
					} else {
						bw.write(MsgCode.MSG_CODE_STRING_BLANK);
						bw.write(this.spliter);
						if(this.isGetString) resultString.append(MsgCode.MSG_CODE_STRING_BLANK + this.spliter);
					}
					bw.flush();
				}
				bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
				if(this.isGetString) {
					resultString.append(MsgCode.MSG_CODE_STRING_NEW_LINE);
//					CommonUtil.copyClipboard(resultString.toString());
				}
				bw.flush();
			}
			
			if(this.isFileOpen)
				Desktop.getDesktop().edit(new File(writeFilePath));
		}catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}finally {
			if(br != null) try { br.close(); } catch(IOException e) {}
			if(bw != null) try { br.close(); } catch(IOException e) {}
		}
		return resultString.toString();
	}
	
	/**
	 * Set default writeFilePath if do not set manually
	 * @param readFileExtension
	 * @throws Exception
	 */
	private void setDefaultWriteFilePath(String readFileExtension) throws StringIndexOutOfBoundsException, DateTimeParseException {
		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		if(this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			this.writeFilePath = readFilePath.replace(readFileExtension, "") + "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) + readFileExtension;
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
