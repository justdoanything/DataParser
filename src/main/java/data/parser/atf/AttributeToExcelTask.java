package data.parser.atf;

import data.exception.ParseException;
import data.factory.AbstractFactoryTask;
import data.util.ExcelUtil;
import data.util.FileUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_XLS;

class AttributeToExcelTask extends AbstractFactoryTask {

    private Workbook workbook = null;

    public AttributeToExcelTask(String splitter) {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = Arrays.asList("Entity");
        valueList = new ArrayList<>();
        this.splitter = splitter;
    }

    @Override
    protected void preTextTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine) {
		try (FileInputStream fis = new FileInputStream(readFilePath)) {
            splitter = "\t";

            if (FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_XLS))
                workbook = new HSSFWorkbook(fis);
            else
                workbook = new XSSFWorkbook(fis);
            fis.close();

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
                throw new ParseException("There is no sheet in file");

            if (startWithLine != 0 && sheet.getRow(startWithLine - 1) == null)
                throw new ParseException("startWithLine over than the row there is in file");

            Row row = null;
            String entityName, attributeName, attributeValue;
            for (int index = startWithLine; index < sheet.getPhysicalNumberOfRows(); index++) {
                row = sheet.getRow(index);
                entityName = ExcelUtil.getCellValue(row.getCell(0)).trim();
                attributeName = ExcelUtil.getCellValue(row.getCell(1)).trim();
                attributeValue = ExcelUtil.getCellValue(row.getCell(2)).trim();

                createResultMap(codeMap, resultMap, entityName, attributeName, attributeValue);
            }
        }catch (Exception e){
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    protected void handleTextTask() {

    }

    @Override
    protected String doTextTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath) {
        return null;
    }

    @Override
    protected void writeResultFile(String writeFilePath, boolean isOpenFile) {

    }

    @Override
    protected String writeResultString() {
        return null;
    }
}
