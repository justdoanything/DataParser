package data.parser.atf;

import data.factory.AbstractFactoryTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class AttributeToExcelTask extends AbstractFactoryTask {
    public AttributeToExcelTask(String splitter) {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = Arrays.asList("Entity");
        valueList = new ArrayList<>();
        this.splitter = splitter;
    }

    @Override
    protected void preTextTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine) {

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
