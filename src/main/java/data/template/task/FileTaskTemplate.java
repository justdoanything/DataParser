package data.template.task;

import data.template.TaskTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FileTaskTemplate extends TaskTemplate {
    protected Map<String, Map<String, String>> resultMap;
    protected List<String> entityList;
    protected List<String> attributeList;
    protected Map<String, List<String>> valueList;

    protected abstract void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String splitter);

    public void handleTask() {
        for (String entity : resultMap.keySet()) {
            for (String attribute : (resultMap.get(entity)).keySet()) {
                if (!attributeList.contains(attribute)) {
                    attributeList.add(attribute);
                }
            }
        }

        for (String entity : resultMap.keySet()) {
            if (!entityList.contains(entity)) {
                entityList.add(entity);
                valueList.put(entity, new ArrayList<>());
            }

            for (String attribute : attributeList) {
                if ((resultMap.get(entity)).containsKey(attribute)) {
                    valueList.get(entity).add(resultMap.get(entity).get(attribute));
                } else {
                    valueList.get(entity).add("");
                }
            }
        }

        attributeList.add(0, "Entity");
    }

    protected abstract String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, String splitter);

    protected abstract void writeResultFile(String writeFilePath, boolean isOpenFile, String splitter);

    protected String writeResultString(String splitter) {
        StringBuilder resultString = new StringBuilder();
        for (String attribute : attributeList) {
            resultString.append(attribute).append(splitter);
        }
        resultString.append("\r\n");

        for (String entity : entityList) {
            resultString.append(entity).append(splitter);
            for (String value : valueList.get(entity)) {
                resultString.append(value).append(splitter);
            }
            resultString.append("\r\n");
        }
        return resultString.toString();
    };

    protected void createResultMap(Map<String, Map<String, String>> codeMap, Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
        if (!resultMap.containsKey(entityName))
            resultMap.put(entityName, new HashMap<>());

        if (attributeValue != null && codeMap.containsKey(attributeName))
            attributeValue = changeCodeValue(codeMap, attributeName, attributeValue);

        resultMap.get(entityName).put(attributeName, attributeValue.equals("") ? "" : attributeValue);
    }
}
