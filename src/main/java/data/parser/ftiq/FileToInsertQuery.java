package data.parser.ftiq;

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
import java.util.Arrays;
import java.util.stream.Collectors;

import data.exception.ParseException;
import data.template.inf.CommonInterface;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import data.template.QueryTemplate;
import data.util.ExcelUtil;
import data.util.FileUtil;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class FileToInsertQuery extends QueryTemplate implements CommonInterface {

	public FileToInsertQuery(FileToInsertQueryBuilder builder) {
		this.readFilePath = builder.getReadFilePath();
		this.writeFilePath = builder.getWriteFilePath();
		this.isWriteFile = builder.isWriteFile();
		this.isOpenFile = builder.isOpenFile();
		this.isGetString = builder.isGetString();

		this.tableName = builder.getTableName();
		this.bulkInsertCnt = builder.getBulkInsertCnt();
		this.isBulkInsert = builder.isBulkInsert();
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
		StringBuilder resultString = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(readFilePath));
			 BufferedWriter bw = isWriteFile ? new BufferedWriter(new FileWriter(writeFilePath)) : null;) {

			String line;
			StringBuilder queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
			StringBuilder queryBody = new StringBuilder();
			boolean isFirst = true;
			int index = 0;
			while ((line = br.readLine()) != null) {
				if(isBulkInsert) {
					if(isFirst) {
						line = line.replace(splitter, ", ");
						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
						queryHeader.append(line).append(") VALUES \r\n");;
						isFirst = false;
					} else {
						line = line.replace(splitter, "', '").replace("''", "null");
						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
						if(index > 0 && index % bulkInsertCnt == 0) {
							queryBody.append("('").append(line.trim()).append("');\r\n\r\n");
							queryBody.append(queryHeader);
						} else {
							queryBody.append("('").append(line.trim()).append("'),\r\n");
						}
					}
					index++;
				} else {
					if(isFirst) {
						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
						line = line.replace(splitter, ", ");
						queryHeader.append(line).append(") VALUES ");
						isFirst = false;
					} else {
						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
						line = line.replace(splitter, "', '").replace("''", "null");
						queryBody.append(queryHeader).append("('").append(line.trim()).append("');\r\n");
					}
				}
			}
			if(isBulkInsert)
				queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");

			if(isWriteFile) {
				if(isBulkInsert) {
					bw.write(queryHeader.toString());
					bw.flush();
				}

				bw.write(queryBody.toString());
				bw.flush();

				if(isOpenFile)
					Desktop.getDesktop().edit(new File(writeFilePath));
			}

			if(isGetString) {
				if(isBulkInsert) {
					resultString.append(queryHeader);
				}
				resultString.append(queryBody);
			}
		} catch (Exception e) {
			throw new ParseException(e.getMessage());
		}
		return resultString.toString();
	}

	@Override
	protected String parseExcelFile() {
		Workbook workbook = null;
		StringBuilder resultString = new StringBuilder();

		try (FileInputStream fis = new FileInputStream(readFilePath);){
			splitter = "\t";

			if(FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS))
				workbook = new HSSFWorkbook(fis);
			else
				workbook = new XSSFWorkbook(fis);
			fis.close();

			Sheet sheet = workbook.getSheetAt(0);
			if(sheet == null)
				throw new IOException("There is no sheet in file");

			StringBuilder queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
			StringBuilder queryBody = new StringBuilder();
			boolean isFirst = true;
			Row row = null;
			int cellCount = -1;
			int index = 0;
			for(int rowNum = 0; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
				StringBuilder queryBodyLine = new StringBuilder();
				row = sheet.getRow(rowNum);
				int cellIndex = 0;

				if(isFirst) {
					while(row.getCell(cellIndex) != null) {
						queryHeader.append(ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", ""));
						if(row.getCell(cellIndex + 1) != null) queryHeader.append(", ");
						cellIndex++;
					}
					cellCount = cellIndex;
				} else {
					while(cellIndex < cellCount) {
						queryBodyLine.append(("'" + ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", "") + "'").replace("''", "null"));
						if(cellIndex + 1 != cellCount) queryBodyLine.append(", ");
						cellIndex++;
					}
				}

				if(isFirst && isBulkInsert) {
					queryHeader.append(") VALUES \r\n");
					isFirst = false;
				} else if(isFirst && !isBulkInsert) {
					queryHeader.append(") VALUES ");
					isFirst = false;
				} else if(!isFirst && isBulkInsert) {
					if(index > 0 && (index  + 1) % bulkInsertCnt == 0) {
						queryBody.append("(").append(queryBodyLine).append(");\r\n\r\n");
						queryBody.append(queryHeader);
					} else {
						queryBody.append("(").append(queryBodyLine).append("),\r\n");
					}
					index++;
				} else if (!isFirst && !isBulkInsert) {
					queryBody.append(queryHeader).append("('").append(queryBodyLine).append("');\r\n");
				} else {

				}
			}

			if(isBulkInsert)
				queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");

			if(isWriteFile) {
				writeFilePath = writeFilePath.replace("xlsx", "txt").replace("xls", "txt");
				try(BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath));){
					if(isBulkInsert) {
						bw.write(queryHeader.toString());
						bw.flush();
					}

					bw.write(queryBody.toString());
					bw.flush();
				}catch(IOException e){
					throw new IOException(e);
				}

				if(isOpenFile)
					Desktop.getDesktop().edit(new File(writeFilePath));
			}

			if(isGetString) {
				if(isBulkInsert) {
					resultString.append(queryHeader);
				}
				resultString.append(queryBody);
			}

		}catch (Exception e) {
			throw new ParseException(e.getMessage());
		} finally {
			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new ParseException(e.getMessage());}
		}

		return resultString.toString();
	}
}
