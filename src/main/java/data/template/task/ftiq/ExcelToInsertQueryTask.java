package data.template.task.ftiq;

import data.exception.ParseException;
import data.template.task.QueryTaskTemplate;
import data.util.ExcelUtil;
import data.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static data.constant.FileConstant.FILE_EXTENSION_XLS;

public class ExcelToInsertQueryTask extends QueryTaskTemplate {

    private Workbook workbook = null;

    @Override
    public void preTask(String tableName) {
        queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
        queryBody = new StringBuilder();
        isFirst = true;
    }

    @Override
    public void handleTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, boolean isBulkInsert, String splitter, int bulkInsertCnt) {
        try (FileInputStream fis = new FileInputStream(readFilePath)) {
            workbook = FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS) ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
                throw new ParseException("There is no sheet in file");

            if (sheet.getRow(startWithLine) == null)
                throw new ParseException("startWithLine over than the row range.");

            if (isBulkInsert)
                handleBulkTask(codeMap, sheet, startWithLine, bulkInsertCnt);
            else
                handleNonBulkTask(codeMap, sheet, startWithLine);

            workbook.close();
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }

    }

    private void handleBulkTask(Map<String, Map<String, String>> codeMap, Sheet sheet, int startWithLine, int bulkInsertCnt) {
        Row row;
        StringBuilder queryBodyLine = null;
        int maxCellCount = 0, lineCnt = 0;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            if (startWithLine != 0) {
                rowIterator.next();
                startWithLine -= 1;
                continue;
            }

            row = rowIterator.next();
            if (isFirst) {
                writeQueryHeader(row);
                queryHeader.append(") VALUES\r\n");
                maxCellCount = row.getPhysicalNumberOfCells();
            } else {
                queryBodyLine = this.writeQueryBody(row, maxCellCount);
                if ((++lineCnt) % bulkInsertCnt == 0) {
                    queryBody.append("(").append(queryBodyLine).append(");\r\n\r\n");
                    queryBody.append(queryHeader);
                } else {
                    queryBody.append("(").append(queryBodyLine).append("),\r\n");
                }
            }
        }

        if (startWithLine != 0)
            throw new ParseException("startWithLine over than the row there is in file.");

        queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");
    }

    private void handleNonBulkTask(Map<String, Map<String, String>> codeMap, Sheet sheet, int startWithLine) {
        Row row;
        StringBuilder queryBodyLine = null;
        int maxCellCount = 0;
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            if (startWithLine != 0) {
                rowIterator.next();
                startWithLine -= 1;
                continue;
            }

            row = rowIterator.next();
            if (isFirst) {
                this.writeQueryHeader(row);
                queryHeader.append(") VALUES ");
                maxCellCount = row.getPhysicalNumberOfCells();
            } else {
                queryBodyLine = this.writeQueryBody(row, maxCellCount);
                queryBody.append(queryHeader).append("(").append(queryBodyLine).append(");\r\n");
            }
        }
    }

    private void writeQueryHeader(Row row) {
        queryHeader.append(StreamSupport.stream(row.spliterator(), false)
                .map(cell -> ExcelUtil.getCellValue(cell))
                .collect(Collectors.joining(",")));
        isFirst = false;
    }

    private StringBuilder writeQueryBody(Row row, int maxCellCount) {
        int cellIndex = 0;
        StringBuilder queryBodyLine = new StringBuilder();
        while (cellIndex < maxCellCount) {
            queryBodyLine.append(("'" + (ExcelUtil.getCellValue(row.getCell(cellIndex)).replace("'", "")) + "'").replace("''", "null"));
            if (++cellIndex < maxCellCount)
                queryBodyLine.append(",");
        }
        return queryBodyLine;
    }
}
