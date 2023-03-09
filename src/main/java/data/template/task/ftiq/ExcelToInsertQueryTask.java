package data.template.task.ftiq;

import data.template.TaskTemplate;

import java.util.Map;

public class ExcelToInsertQueryTask extends TaskTemplate {
    @Override
    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine) {

    }

    @Override
    public void handleTask() {

    }

    @Override
    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath) {
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
