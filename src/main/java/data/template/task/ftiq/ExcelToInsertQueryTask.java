package data.template.task.ftiq;

import data.template.task.QueryTaskTemplate;

import java.util.Map;

public class ExcelToInsertQueryTask extends QueryTaskTemplate {
    @Override
    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String tableName, boolean isBulkInsert, String splitter, int bulkInsertCnt) {

    }

    @Override
    public void handleTask() {

    }

    @Override
    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, boolean isBulkInsert) {
        return null;
    }

    @Override
    protected void writeResultFile(String writeFilePath, boolean isOpenFile, boolean isBulkInsert) {

    }

    @Override
    protected String writeResultString(boolean isBulkInsert) {
        return null;
    }
}

//		Workbook workbook = null;
//		StringBuilder resultString = new StringBuilder();
//
//		try (FileInputStream fis = new FileInputStream(readFilePath);){
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
//			StringBuilder queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
//			StringBuilder queryBody = new StringBuilder();
//			boolean isFirst = true;
//			Row row = null;
//			int cellCount = -1;
//			int index = 0;
//			for(int rowNum = 0; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
//				StringBuilder queryBodyLine = new StringBuilder();
//				row = sheet.getRow(rowNum);
//				int cellIndex = 0;
//
//				if(isFirst) {
//					while(row.getCell(cellIndex) != null) {
//						queryHeader.append(ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", ""));
//						if(row.getCell(cellIndex + 1) != null) queryHeader.append(", ");
//						cellIndex++;
//					}
//					cellCount = cellIndex;
//				} else {
//					while(cellIndex < cellCount) {
//						queryBodyLine.append(("'" + ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", "") + "'").replace("''", "null"));
//						if(cellIndex + 1 != cellCount) queryBodyLine.append(", ");
//						cellIndex++;
//					}
//				}
//
//				if(isFirst && isBulkInsert) {
//					queryHeader.append(") VALUES \r\n");
//					isFirst = false;
//				} else if(isFirst && !isBulkInsert) {
//					queryHeader.append(") VALUES ");
//					isFirst = false;
//				} else if(!isFirst && isBulkInsert) {
//					if(index > 0 && (index  + 1) % bulkInsertCnt == 0) {
//						queryBody.append("(").append(queryBodyLine).append(");\r\n\r\n");
//						queryBody.append(queryHeader);
//					} else {
//						queryBody.append("(").append(queryBodyLine).append("),\r\n");
//					}
//					index++;
//				} else if (!isFirst && !isBulkInsert) {
//					queryBody.append(queryHeader).append("('").append(queryBodyLine).append("');\r\n");
//				} else {
//
//				}
//			}
//
//			if(isBulkInsert)
//				queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");
//
//			if(isWriteFile) {
//				writeFilePath = writeFilePath.replace("xlsx", "txt").replace("xls", "txt");
//				try(BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath));){
//					if(isBulkInsert) {
//						bw.write(queryHeader.toString());
//						bw.flush();
//					}
//
//					bw.write(queryBody.toString());
//					bw.flush();
//				}catch(IOException e){
//					throw new IOException(e);
//				}
//
//				if(isOpenFile)
//					Desktop.getDesktop().edit(new File(writeFilePath));
//			}
//
//			if(isGetString) {
//				if(isBulkInsert) {
//					resultString.append(queryHeader);
//				}
//				resultString.append(queryBody);
//			}
//
//		}catch (Exception e) {
//			throw new ParseException(e.getMessage());
//		} finally {
//			if(workbook != null) try { workbook.close(); } catch(IOException e) {throw new ParseException(e.getMessage());}
//		}
//
//		return resultString.toString();
