package data.template.task;

import data.exception.ParseException;
import data.template.TaskTemplate;
import data.util.FileUtil;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public abstract class QueryTaskTemplate extends TaskTemplate {
    protected StringBuilder queryHeader;
    protected StringBuilder queryBody;
    protected boolean isFirst;

    protected abstract void preTask(String tableName);

    protected abstract void handleTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, boolean isBulkInsert, String splitter, int bulkInsertCnt);

    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, boolean isBulkInsert) {
        String resultString = null;
        if (isWriteFile)
            writeResultFile(writeFilePath, isOpenFile, isBulkInsert);
        if (isGetString)
            resultString = writeResultString(isBulkInsert);
        return resultString;
    }

    protected void writeResultFile(String writeFilePath, boolean isOpenFile, boolean isBulkInsert) {
        if(FileUtil.getFileExtension(writeFilePath).equals(FILE_EXTENSION_XLS) || FileUtil.getFileExtension(writeFilePath).equals(FILE_EXTENSION_XLSX)) {
            writeFilePath = writeFilePath.replace(FILE_EXTENSION_XLSX, FILE_EXTENSION_TXT).replace(FILE_EXTENSION_XLS, FILE_EXTENSION_TXT);
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath))) {
            if (isBulkInsert) {
                bw.write(queryHeader.toString());
                bw.flush();
            }

            bw.write(queryBody.toString());
            bw.flush();

            if (isOpenFile)
                Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    protected String writeResultString(boolean isBulkInsert) {
        return isBulkInsert ? queryHeader.append(queryBody).toString() : queryBody.toString();
    }
}