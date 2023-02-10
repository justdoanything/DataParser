package data.template;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

public abstract class CommonTemplate {
    protected String readFilePath;
    protected String writeFilePath;
    protected boolean isWriteFile = true;
    protected boolean isOpenFile = false;
    protected boolean isGetString = false;
    protected String splitter = "\t";
    protected int startWithLine = 0;

    protected Map<String, Map<String, String>> codeMap = new HashMap<>();

    public void addCodeMap(String name, String code, String value) {
        if(!codeMap.containsKey(name)) {
			codeMap.put(name, new HashMap<>());
		}
		codeMap.get(name).put(code, value);
    }

    protected void createResultMap(Map<String, Map<String, String>> resultMap, String entityName, String attributeName, String attributeValue) {
        if(!resultMap.containsKey(entityName))
            resultMap.put(entityName, new HashMap<>());

        if(attributeValue != null && codeMap.containsKey(attributeName))
            attributeValue = changeCodeValue(attributeName, attributeValue);

        resultMap.get(entityName).put(attributeName,  attributeValue.equals(" ") ? " " : attributeValue);
    }

    protected String changeCodeValue(String attributeName, String attributeValue) {
        return codeMap.get(attributeName).containsKey(attributeValue) ? codeMap.get(attributeName).get(attributeValue) : attributeValue;
    }

    protected abstract String parseTextFile();
    protected abstract String parseExcelFile();

    protected abstract void validParameter() throws FileNotFoundException, FileSystemException;
}
