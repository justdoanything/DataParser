package data.template.task;

import data.template.TaskTemplate;

import java.util.Map;

public abstract class QueryTaskTemplate extends TaskTemplate {
    protected StringBuilder queryHeader;
    protected StringBuilder queryBody;

    protected abstract void preTask(String tableName);

    protected abstract void handleTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, boolean isBulkInsert, String splitter, int bulkInsertCnt);

    protected abstract String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, boolean isBulkInsert);

    protected abstract void writeResultFile(String writeFilePath, boolean isOpenFile, boolean isBulkInsert);

    protected abstract String writeResultString(boolean isBulkInsert);
}