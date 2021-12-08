package prj.yong.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import lombok.Getter;
import lombok.Setter;
import msg.MsgCode;
import prj.yong.util.DateUtil;

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
	private String spliter = MsgCode.MSG_CODE_FILE_SPLITER;
	private boolean isFileOpen = false;
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
	 * 
	 * @param readFileExtension
	 * @throws IOException
	 */
	private void checkingFileExist(String readFileExtension) throws IOException {
		// Checking a file is existed
		File file = new File(this.readFilePath);
		if(!file.exists()) {
			// throw FileNotFoundException if there is no file
			throw new FileNotFoundException("There is no file in " + this.readFilePath); 
		}

		//if do not set writeFilePath, this should be readFilePath_{dateformat}
		SimpleDateFormat sdf = new SimpleDateFormat(MsgCode.MSG_VALUE_DATE_FORMAT);
		sdf.format(new Date());
		if(this.writeFilePath.equals(MsgCode.MSG_CODE_STRING_BLANK)) {
			this.writeFilePath = readFilePath.replace(readFileExtension, "") + "_" + DateUtil.getDate(MsgCode.MSG_VALUE_DATE_FORMAT, 0) + readFileExtension;
		}
	}
	
	/**
	 * 
	 * @param resultMap
	 * @param entityName
	 * @param attributeName
	 * @param attributeValue
	 */
	private void createResultMap(Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
		if(!resultMap.containsKey(entityName)) 
			resultMap.put(entityName, new HashMap<String, String>());

		// Change value if there is code in codeMap
		if(attributeValue != null && codeMap.containsKey(attributeValue)) 
			this.changeCodeValue(attributeName, attributeValue);
		// Put blank " " if the attribute value is empty.
		resultMap.get(entityName).put(attributeName,  attributeValue.equals(MsgCode.MSG_CODE_STRING_SPACE) ? MsgCode.MSG_CODE_STRING_SPACE : attributeValue);
	}
	

	/**
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	private String changeCodeValue(String attributeName, String attributeValue) {
		return codeMap.get(attributeName).get(attributeValue) != null ? codeMap.get(attributeName).get(attributeValue) : MsgCode.MSG_CODE_STRING_NULL;
	}
	
	/**
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellValue(Cell cell) {
		String msg = "";
		if (cell != null) {
			switch (cell.getCellType()) {
			case FORMULA:
				msg = cell.getCellFormula();
				break;
			case NUMERIC:
				msg = cell.getNumericCellValue() == (int) cell.getNumericCellValue() ? 
						String.valueOf((int) cell.getNumericCellValue()) : String.valueOf(cell.getNumericCellValue());   
			case STRING:
				msg = cell.getStringCellValue().trim();
				break;
			case BLANK:
				msg = cell.toString();
				break;
			case BOOLEAN:
				msg = String.valueOf(cell.getBooleanCellValue());
				break;
			case ERROR:
				msg = String.valueOf(cell.getErrorCellValue());
				break;
			default:
				msg = "";
				break;
			}
		}
		return msg;
	}
	
	/**
	 * 
	 * @param readFileExtension
	 * @throws IOException
	 */
	private void parseExcelType(String readFileExtension) throws IOException {
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		XSSFWorkbook workbook = null;

		// Checking file is existed and Set writeFilePath
		this.checkingFileExist(readFileExtension);

		// Set FileInputStream and Open file
	   	FileInputStream fis = new FileInputStream(this.readFilePath);
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
    		row = sheet.getRow(index++);
	    	entityName = this.getCellValue(row.getCell(0));
	    	attributeName = this.getCellValue(row.getCell(1));
	    	attributeValue = this.getCellValue(row.getCell(2));
	    	
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
	 	row.createCell(cellIndex++).setCellValue(MsgCode.MSG_CODE_FIELD_NAME);;
	 	for(String entity : entityList) {
	 		for(String attribute : (resultMap.get(entity)).keySet()) {
	 			if(!attributeList.contains(attribute)) {
	 				attributeList.add(attribute);
	 				row.createCell(cellIndex++).setCellValue(attribute);
	 			}
	 		}
	 	}
	 	
	 	// Write Attribute value as attribute and name
	 	for(String entity : entityList) {
	 		cellIndex = 0;
	 		row = resultSheet.createRow(rowIndex++);
	 		row.createCell(cellIndex++).setCellValue(entity);
	 		
	 		for(String attribute : attributeList) {
	 			if((resultMap.get(entity)).containsKey(attribute)) {
	 				row.createCell(cellIndex).setCellValue(resultMap.get(entity).get(attribute));
	 			} else {
	 				row.createCell(cellIndex).setCellValue(MsgCode.MSG_CODE_STRING_BLANK);
	 			}
	 			cellIndex++;
	 		}
	 	}
	 	
	 	// Write result file
	 	FileOutputStream fos = new FileOutputStream(this.writeFilePath, false);
	    workbook.write(fos);
	    fos.flush();
	    
	    // I/O Close
	 	workbook.close();
	 	fos.close();
	}
	
	/**
	 * 
	 * @param readFileExtension
	 * @throws IOException
	 */
	private void parseTextType(String readFileExtension) throws IOException {
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		// Checking file is existed and set writeFilePath
		this.checkingFileExist(readFileExtension);
		
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
			
			// Add name to namelist
			List<String> nameList = new ArrayList<>();
			for(String key : resultMap.keySet()) {
				if(!nameList.contains(key))
					nameList.add(key);
			}
			
			// Write attribute in first line
			bw.write(MsgCode.MSG_CODE_FIELD_NAME + this.spliter);
			List<String> attributeList = new ArrayList<>();
			for(String name : nameList) {
				for(String attribute : (resultMap.get(name)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						bw.write(attribute);
						bw.write(this.spliter);
						bw.flush();
					}
				}
			}
			bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
			bw.flush();
			
			// Write Attribute value as attribute and name
			for(String name : nameList) {
				bw.write(name);
				bw.write(this.spliter);
				
				for(String attribute : attributeList) {
					if((resultMap.get(name)).containsKey(attribute)) {
						bw.write(resultMap.get(name).get(attribute));
						bw.write(this.spliter);
					} else {
						bw.write(MsgCode.MSG_CODE_STRING_BLANK);
						bw.write(this.spliter);
					}
					bw.flush();
				}
				bw.write(MsgCode.MSG_CODE_STRING_NEW_LINE);
				bw.flush();
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			throw new IOException(e);
		}finally {
			if(br != null) try { br.close(); } catch(IOException e) {}
			if(bw != null) try { br.close(); } catch(IOException e) {}
		}
	}
	
	/**
	 * 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	public void parse() throws IOException {
		String readFileExtension = this.readFilePath.substring(this.readFilePath.lastIndexOf("."), readFilePath.length());
		
		if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_TXT)){
			this.parseTextType(readFileExtension);
		} else if(readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLS)
				|| readFileExtension.equals(MsgCode.MSG_CODE_FILE_EXTENSION_XLSX)){
			this.parseExcelType(readFileExtension);
		} else {
			throw new FileNotFoundException("A extension of file you read must be .csv, .xls, .xlsx and .txt");
		}
	}
	
	public static void main(String[] args) throws IOException {
		AttributeToExcel ate = new AttributeToExcel();
		ate.setReadFilePath("C:\\Users\\82736\\Desktop\\attr.xlsx");
		ate.setStartWithLine(1);
		ate.addCodeValue("Model Type", "57", "A");
		ate.addCodeValue("Model Type", "54", "B");
		ate.parse();
	}
}
