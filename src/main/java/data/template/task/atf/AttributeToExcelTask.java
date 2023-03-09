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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_XLS;

public class AttributeToExcelTask extends FileTaskTemplate {

    private Workbook workbook = null;

    public AttributeToExcelTask() {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = Arrays.asList("Entity");
        valueList = new ArrayList<>();
    }

    @Override
    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String splitter) {
        try (FileInputStream fis = new FileInputStream(readFilePath);) {
            workbook = FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS) ? new HSSFWorkbook(fis) : new XSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
                throw new ParseException("There is no sheet in file");

            if (sheet.getRow(startWithLine - 1) == null)
                throw new ParseException("startWithLine over than the row range.");

            Row row = null;
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
    public void handleTask() {
        for (String entity : resultMap.keySet()) {
            if (!entityList.contains(entity))
                entityList.add(entity);

            for (String attribute : (resultMap.get(entity)).keySet()) {
                if (!attributeList.contains(attribute)) {
                    attributeList.add(attribute);
                }
                if ((resultMap.get(entity)).containsKey(attribute)) {
                    valueList.add(resultMap.get(entity).get(attribute));
                } else {
                    valueList.add("");
                }
            }
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
        try {
            Sheet resultSheet = workbook.createSheet("result" + "_" + DateUtil.getDate("yyyyMMddHHmmss", 0));
            int rowIndex = 0;
            int cellIndex = 0;
            Row row = resultSheet.createRow(rowIndex++);
            row.createCell(cellIndex++).setCellValue("NAME");

            for (String attribute : attributeList) {
                row.createCell(cellIndex++).setCellValue(attribute);
            }

            for (String entity : entityList) {
                cellIndex = 0;
                row = resultSheet.createRow(rowIndex++);
                row.createCell(cellIndex++).setCellValue(entity);
                for (String value : valueList) {
                    row.createCell(cellIndex++).setCellValue(value);
                }
            }

            FileOutputStream fos = new FileOutputStream(writeFilePath, false);
            workbook.write(fos);
            fos.flush();
            fos.close();

            if (isOpenFile)
                Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    protected String writeResultString(String splitter) {
        StringBuilder resultString = new StringBuilder();
        for (String attribute : attributeList) {
            resultString.append(attribute).append(splitter);
        }
        resultString.append("\r\n");

        for (String entity : entityList) {
            resultString.append(entity).append(splitter);
            for (String value : valueList) {
                resultString.append(value).append(splitter);
            }
            resultString.append("\r\n");
        }
        return resultString.toString();
    }
}
