package data.template.task;

import data.template.TaskTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileTaskTemplate extends TaskTemplate {
    protected Map<String, Map<String, String>> resultMap;
    protected List<String> entityList;
    protected List<String> attributeList;
    protected Map<String, List<String>> valueList;

    protected abstract void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String splitter);

    protected abstract void handleTask();

    protected abstract String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, String splitter);

    protected abstract void writeResultFile(String writeFilePath, boolean isOpenFile, String splitter);

    protected abstract String writeResultString(String splitter);

    protected void createResultMap(Map<String, Map<String, String>> codeMap, Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
        if (!resultMap.containsKey(entityName))
            resultMap.put(entityName, new HashMap<>());

        if (attributeValue != null && codeMap.containsKey(attributeName))
            attributeValue = changeCodeValue(codeMap, attributeName, attributeValue);

        resultMap.get(entityName).put(attributeName, attributeValue.equals("") ? "" : attributeValue);
    }
}
