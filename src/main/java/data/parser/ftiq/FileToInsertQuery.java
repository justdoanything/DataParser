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
	public String parse() throws FileNotFoundException {
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
		return null;
	}

	@Override
	protected String parseExcelFile() {
		return null;
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
     			if(readFileExtension.equals(CommonConstant.MSG_CODE_FILE_EXTENSION_CSV))
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
										line = Arrays.stream(line.split("\\\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		queryHeader.append(line).append(") VALUES \r\n");;
                		isFirst = false;
                	} else {
                		// Merge Query Body
                		line = line.replace(this.spliter, "', '").replace("''", "null");
										line = Arrays.stream(line.split("\\\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
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
										line = Arrays.stream(line.split("\\\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                		line = line.replace(this.spliter, ", ");
                		queryHeader.append(line).append(") VALUES ");
                		isFirst = false;
                	} else {
                		// Merge Query Body
										line = Arrays.stream(line.split("\\\\" + this.spliter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
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
						throw new FileNotFoundException();
        } catch (IOException e) {
						throw new IOException(e);
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
			this.spliter = CommonConstant.MSG_CODE_STRING_TAB;

			if(readFileExtension.equals(CommonConstant.MSG_CODE_FILE_EXTENSION_XLS))
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
				try(BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath));){
					if(this.isBulkInsert) {
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

			// Set result if isGetString is true
			if(this.isGetString) {
				if(this.isBulkInsert) {
					resultString.append(queryHeader);
				}
				resultString.append(queryBody);
			}

		}catch (Exception e) {
			throw new IOException(e);
		} finally {
			// I/O Close
			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new IOException(e);}
		}

		return resultString.toString();
	}
}
