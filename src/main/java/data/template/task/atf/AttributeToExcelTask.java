package data.template.task.atf;

import data.exception.ParseException;
import data.template.task.FileTaskTemplate;
import data.util.DateUtil;
import data.util.ExcelUtil;
import data.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_XLS;

public class AttributeToExcelTask extends FileTaskTemplate {

    private Workbook workbook = null;

    public AttributeToExcelTask() {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = new ArrayList<>();
        valueList = new HashMap<>();
    }

    @Override
    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String splitter) {
        try (FileInputStream fis = new FileInputStream(readFilePath)) {
            workbook = FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS) ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
                throw new ParseException("There is no sheet in file");

            if (sheet.getRow(startWithLine) == null)
                throw new ParseException("startWithLine over than the row range.");

            Row row;
            String entityName, attributeName, attributeValue;
            for (int index = startWithLine; index < sheet.getPhysicalNumberOfRows(); index++) {
                row = sheet.getRow(index);
                entityName = ExcelUtil.getCellValue(row.getCell(0)).trim();
                attributeName = ExcelUtil.getCellValue(row.getCell(1)).trim();
                attributeValue = ExcelUtil.getCellValue(row.getCell(2)).trim();

                createResultMap(codeMap, resultMap, entityName, attributeName, attributeValue);
            }
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, String splitter) {
        String resultString = null;
        if (isWriteFile)
            writeResultFile(writeFilePath, isOpenFile, splitter);
        if (isGetString)
            resultString = writeResultString(splitter);

        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new ParseException(e.getMessage());
            }
        }
        return resultString;
    }

    @Override
    protected void writeResultFile(String writeFilePath, boolean isOpenFile, String splitter) {
        try (FileOutputStream fos = new FileOutputStream(writeFilePath, false)) {
            Sheet resultSheet = workbook.createSheet("result" + "_" + DateUtil.getDate("yyyyMMddHHmmss", 0));
            int rowIndex = 0;
            int cellIndex = 0;
            Row row = resultSheet.createRow(rowIndex++);

            for (String attribute : attributeList) {
                row.createCell(cellIndex++).setCellValue(attribute);
            }

            for (String entity : entityList) {
                cellIndex = 0;
                row = resultSheet.createRow(rowIndex++);
                row.createCell(cellIndex++).setCellValue(entity);
                for (String value : valueList.get(entity)) {
                    row.createCell(cellIndex++).setCellValue(value);
                }
            }

            workbook.write(fos);
            fos.flush();

            if (isOpenFile)
                Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }
}
