package data.parser.atf;

import data.constant.TypeEnum;
import data.exception.ParseException;
import data.factory.ParserFactory;
import data.template.CommonInterface;
import data.template.FileTemplate;
import data.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class AttributeToFile extends FileTemplate implements CommonInterface {
	private final Map<String, Map<String, String>> resultMap;
	private java.util.List<String> entityList;
	private final java.util.List<String> attributeList;
	private final List<String> valueList;

	public static AttributeToFileBuilder builder(String readFilePath) {
		return new AttributeToFileBuilder(readFilePath);
	}

	public AttributeToFile(AttributeToFileBuilder builder) {
		this.readFilePath = builder.getReadFilePath();
		this.writeFilePath = builder.getWriteFilePath();
		this.isWriteFile = builder.isWriteFile();
		this.isOpenFile = builder.isOpenFile();
		this.isGetString = builder.isGetString();

		this.splitter = builder.getSplitter();
		this.startWithLine = builder.getStartWithLine();

		resultMap = new HashMap<>();
		entityList = new ArrayList<>();
		attributeList = Arrays.asList("Entity");
		valueList = new ArrayList<>();
	}

	@Override
	public String parse() {
		String resultString;
		String readFileExtension = FileUtil.getFileExtension(readFilePath).toLowerCase();

		switch (readFileExtension) {
			case FILE_EXTENSION_TXT:
			case FILE_EXTENSION_BLANK:
			case FILE_EXTENSION_CSV:
				resultString = parseTextFile();
				break;
			case FILE_EXTENSION_XLS:
			case FILE_EXTENSION_XLSX:
				resultString = parseExcelFile();
				break;
			default:
				throw new ParseException("A extension of file must be '.csv', '.xls', '.xlsx', '.txt' or empty");
		}
		return resultString;
	}

	@Override
	protected String parseTextFile() {
		AttributeToTextTaskTemplate ft = (AttributeToTextTaskTemplate) ParserFactory.createTask(TypeEnum.ParseType.TEXT, splitter);
		ft.preTextTask(codeMap, readFilePath, startWithLine);
		ft.handleTextTask();
		return ft.doTextTask(isWriteFile, isGetString, isOpenFile, writeFilePath);
	}

	@Override
	protected String parseExcelFile() {
		AttributeToExcelTaskTemplate et = (AttributeToExcelTaskTemplate) ParserFactory.createTask(TypeEnum.ParseType.EXCEL, splitter);
		et.preTextTask(codeMap, readFilePath, startWithLine);
		et.handleTextTask();
		return et.doTextTask(isWriteFile, isGetString, isOpenFile, writeFilePath);

//		Map<String, Map<String, String>> resultMap = new HashMap<>();
//		StringBuilder resultString = new StringBuilder();
//		Workbook workbook = null;
//
//		try (FileInputStream fis = new FileInputStream(readFilePath);) {
//			splitter = "\t";
//
//			if(FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS))
//				workbook = new HSSFWorkbook(fis);
//			else
//				workbook = new XSSFWorkbook(fis);
//			fis.close();
//
//			Sheet sheet = workbook.getSheetAt(0);
//			if(sheet == null)
//				throw new IOException("There is no sheet in file");
//
//			if(startWithLine != 0 && sheet.getRow(startWithLine - 1) == null)
//				throw new IOException("startWithLine over than the row there is in file");
//
//			Row row = null;
//			String entityName, attributeName, attributeValue;
//			for(int index = startWithLine; index < sheet.getPhysicalNumberOfRows(); index++) {
//				row = sheet.getRow(index);
//				entityName = ExcelUtil.getCellValue(row.getCell(0)).trim();
//				attributeName = ExcelUtil.getCellValue(row.getCell(1)).trim();
//				attributeValue = ExcelUtil.getCellValue(row.getCell(2)).trim();
//
//				createResultMap(codeMap, resultMap, entityName, attributeName, attributeValue);
//			}
//
//			List<String> entityList = new ArrayList<>();
//			for(String entity : resultMap.keySet()) {
//				if(!entityList.contains(entity))
//					entityList.add(entity);
//			}
//
//			List<String> attributeList = new ArrayList<>();
//			int rowIndex = 0;
//			int cellIndex = 0;
//			Sheet resultSheet = workbook.createSheet("result" + "_" + DateUtil.getDate("yyyyMMddHHmmss", 0));
//			row = resultSheet.createRow(rowIndex++);
//			if(isGetString) resultString.append("NAME").append(splitter);
//			if(isWriteFile) row.createCell(cellIndex++).setCellValue("NAME");
//			for(String entity : entityList) {
//				for(String attribute : (resultMap.get(entity)).keySet()) {
//					if(!attributeList.contains(attribute)) {
//						attributeList.add(attribute);
//						if(isWriteFile) row.createCell(cellIndex++).setCellValue(attribute);
//						if(isGetString) resultString.append(attribute).append(splitter);
//					}
//				}
//			}
//			if(isGetString) resultString.append("\r\n");
//
//			for(String entity : entityList) {
//				cellIndex = 0;
//				row = resultSheet.createRow(rowIndex++);
//				if(isWriteFile) row.createCell(cellIndex++).setCellValue(entity);
//				if(isGetString) resultString.append(entity).append(splitter);
//
//				for(String attribute : attributeList) {
//					if((resultMap.get(entity)).containsKey(attribute)) {
//						if(isWriteFile) row.createCell(cellIndex++).setCellValue(resultMap.get(entity).get(attribute));
//						if(isGetString) resultString.append(resultMap.get(entity).get(attribute)).append(splitter);
//					} else {
//						if(isWriteFile) row.createCell(cellIndex++).setCellValue("");
//						if(isGetString) resultString.append("").append(splitter);
//					}
//				}
//			}
//
//			// Write result into file if isWirteFile is true
//			if(isWriteFile) {
//				FileOutputStream fos = new FileOutputStream(writeFilePath, false);
//				workbook.write(fos);
//				fos.flush();
//				fos.close();
//			}
//
//			// Set result if isGetString is true
//			if(isGetString) {
//				resultString.append("\r\n");
//			}
//
//			if(isOpenFile)
//				Desktop.getDesktop().edit(new File(writeFilePath));
//		} catch (Exception e) {
//			throw new ParseException(e.getMessage());
//		} finally {
//			// I/O Close
//			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new ParseException(e.getMessage());}
//		}
//		return resultString.toString();
	}

}
