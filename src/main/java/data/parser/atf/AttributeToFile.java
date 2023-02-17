package data.parser.atf;

import data.exception.ParseException;
import data.template.inf.CommonInterface;
import data.template.FileTemplate;
import data.util.DateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import data.util.ExcelUtil;
import data.util.FileUtil;

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

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class AttributeToFile extends FileTemplate implements CommonInterface {

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
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		StringBuilder resultString = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(readFilePath));
			 BufferedWriter bw = isWriteFile ? new BufferedWriter(new FileWriter(writeFilePath)) : null;) {

			String line;
			String[] lineArray;
			String entityName, attributeName, attributeValue;
			while ((line = br.readLine()) != null){
				if(startWithLine != 0){
					startWithLine -= 1;
					continue;
				}

				lineArray = line.split("\\" + splitter);
				entityName = lineArray.length == 0 ? " " : lineArray[0].trim();
				attributeName = lineArray.length == 1 ? " " : lineArray[1].trim();
				attributeValue = lineArray.length == 2 ? " " : lineArray[2].trim();

				createResultMap(resultMap, entityName, attributeName, attributeValue);
			}

			if(startWithLine != 0)
				throw new ParseException("startWithLine over than the row there is in file.");

			List<String> entityList = new ArrayList<>();
			for(String entity : resultMap.keySet()) {
				if(!entityList.contains(entity))
					entityList.add(entity);
			}

			if(isWriteFile) bw.write("NAME" + splitter);
			if(isGetString) resultString.append("NAME").append(splitter);
			List<String> attributeList = new ArrayList<>();
			for(String entity : entityList) {
				for(String attribute : (resultMap.get(entity)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						if(isWriteFile) { bw.write(attribute); bw.write(splitter); bw.flush(); }
						if(isGetString) resultString.append(attribute).append(splitter);
					}
				}
			}

			if(isWriteFile) { bw.write("\r\n"); bw.flush(); }
			if(isGetString) resultString.append("\r\n");

			for(String entity : entityList) {
				if(isWriteFile) { bw.write(entity); bw.write(splitter); }
				if(isGetString) resultString.append(entity).append(splitter);
				for(String attribute : attributeList) {
					if((resultMap.get(entity)).containsKey(attribute)) {
						if(isWriteFile) { bw.write(resultMap.get(entity).get(attribute)); bw.write(splitter); bw.flush(); }
						if(isGetString) resultString.append(resultMap.get(entity).get(attribute)).append(splitter);
					} else {
						if(isWriteFile) { bw.write(""); bw.write(splitter); bw.flush(); }
						if(isGetString) resultString.append("").append(splitter);
					}
				}

				if(isWriteFile) { bw.write("\r\n"); bw.flush(); }
				if(isGetString) { resultString.append("\r\n"); }
			}

			if(isOpenFile) Desktop.getDesktop().edit(new File(writeFilePath));

		}catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
		return resultString.toString();
	}

	@Override
	protected String parseExcelFile() {
		Map<String, Map<String, String>> resultMap = new HashMap<>();
		StringBuilder resultString = new StringBuilder();
		Workbook workbook = null;

		try (FileInputStream fis = new FileInputStream(readFilePath);) {
			splitter = "\t";

			if(FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS))
				workbook = new HSSFWorkbook(fis);
			else
				workbook = new XSSFWorkbook(fis);
			fis.close();

			Sheet sheet = workbook.getSheetAt(0);
			if(sheet == null)
				throw new IOException("There is no sheet in file");

			if(startWithLine != 0 && sheet.getRow(startWithLine - 1) == null)
				throw new IOException("startWithLine over than the row there is in file");

			Row row = null;
			String entityName, attributeName, attributeValue;
			for(int index = startWithLine; index < sheet.getPhysicalNumberOfRows(); index++) {
				row = sheet.getRow(index);
				entityName = ExcelUtil.getCellValue(row.getCell(0)).trim();
				attributeName = ExcelUtil.getCellValue(row.getCell(1)).trim();
				attributeValue = ExcelUtil.getCellValue(row.getCell(2)).trim();

				createResultMap(resultMap, entityName, attributeName, attributeValue);
			}

			List<String> entityList = new ArrayList<>();
			for(String entity : resultMap.keySet()) {
				if(!entityList.contains(entity))
					entityList.add(entity);
			}

			List<String> attributeList = new ArrayList<>();
			int rowIndex = 0;
			int cellIndex = 0;
			Sheet resultSheet = workbook.createSheet("result" + "_" + DateUtil.getDate("yyyyMMddHHmmss", 0));
			row = resultSheet.createRow(rowIndex++);
			if(isGetString) resultString.append("NAME").append(splitter);
			if(isWriteFile) row.createCell(cellIndex++).setCellValue("NAME");
			for(String entity : entityList) {
				for(String attribute : (resultMap.get(entity)).keySet()) {
					if(!attributeList.contains(attribute)) {
						attributeList.add(attribute);
						if(isWriteFile) row.createCell(cellIndex++).setCellValue(attribute);
						if(isGetString) resultString.append(attribute).append(splitter);
					}
				}
			}
			if(isGetString) resultString.append("\r\n");

			for(String entity : entityList) {
				cellIndex = 0;
				row = resultSheet.createRow(rowIndex++);
				if(isWriteFile) row.createCell(cellIndex++).setCellValue(entity);
				if(isGetString) resultString.append(entity).append(splitter);

				for(String attribute : attributeList) {
					if((resultMap.get(entity)).containsKey(attribute)) {
						if(isWriteFile) row.createCell(cellIndex++).setCellValue(resultMap.get(entity).get(attribute));
						if(isGetString) resultString.append(resultMap.get(entity).get(attribute)).append(splitter);
					} else {
						if(isWriteFile) row.createCell(cellIndex++).setCellValue("");
						if(isGetString) resultString.append("").append(splitter);
					}
				}
			}

			// Write result into file if isWirteFile is true
			if(isWriteFile) {
				FileOutputStream fos = new FileOutputStream(writeFilePath, false);
				workbook.write(fos);
				fos.flush();
				fos.close();
			}

			// Set result if isGetString is true
			if(isGetString) {
				resultString.append("\r\n");
			}

			if(isOpenFile)
				Desktop.getDesktop().edit(new File(writeFilePath));
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		} finally {
			// I/O Close
			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new ParseException(e.getMessage());}
		}
		return resultString.toString();
	}

}
