package data.template;

import java.util.Map;

public class TaskTemplate {
    protected String changeCodeValue(Map<String, Map<String, String>> codeMap, String attributeName, String attributeValue) {
        return codeMap.get(attributeName).containsKey(attributeValue) ? codeMap.get(attributeName).get(attributeValue) : attributeValue;
    }
}
