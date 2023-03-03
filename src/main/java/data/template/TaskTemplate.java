package data.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class TaskTemplate {
    protected Map<String, Map<String, String>> resultMap;
    protected List<String> entityList;
    protected List<String> attributeList;
    protected List<String> valueList;
    protected String splitter;

    protected abstract void preTextTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine);

    protected abstract void handleTextTask();

    protected abstract String doTextTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath);

    protected abstract void writeResultFile(String writeFilePath, boolean isOpenFile);

    protected abstract String writeResultString();

    protected void createResultMap(Map<String, Map<String, String>> codeMap, Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
        if(!resultMap.containsKey(entityName))
            resultMap.put(entityName, new HashMap<>());

        if(attributeValue != null && codeMap.containsKey(attributeName))
            attributeValue = changeCodeValue(codeMap, attributeName, attributeValue);

        resultMap.get(entityName).put(attributeName,  attributeValue.equals(" ") ? " " : attributeValue);
    }

    protected String changeCodeValue(Map<String, Map<String, String>> codeMap, String attributeName, String attributeValue) {
        return codeMap.get(attributeName).containsKey(attributeValue) ? codeMap.get(attributeName).get(attributeValue) : attributeValue;
    }
}
