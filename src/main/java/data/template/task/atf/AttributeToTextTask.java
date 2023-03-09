package data.template.task.atf;

import data.exception.ParseException;
import data.template.task.FileTaskTemplate;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AttributeToTextTask extends FileTaskTemplate {
    public AttributeToTextTask() {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = Arrays.asList("Entity");
        valueList = new ArrayList<>();
    }

    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String splitter) {
        try (BufferedReader br = new BufferedReader(new FileReader(readFilePath))) {
            String line;
            String[] lineArray;
            String entityName, attributeName, attributeValue;
            while ((line = br.readLine()) != null) {
                if (startWithLine != 0) {
                    startWithLine -= 1;
                    continue;
                }

                lineArray = line.split("\\\\" + splitter);
                entityName = lineArray.length == 0 ? " " : lineArray[0].trim();
                attributeName = lineArray.length == 1 ? " " : lineArray[1].trim();
                attributeValue = lineArray.length == 2 ? " " : lineArray[2].trim();

                createResultMap(codeMap, resultMap, entityName, attributeName, attributeValue);
            }

            if (startWithLine != 0)
                throw new ParseException("startWithLine over than the row there is in file.");
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    public void handleTask() {
        for (String entity : resultMap.keySet()) {
            if (!entityList.contains(entity))
                entityList.add(entity);

            for (String attribute : (resultMap.get(entity)).keySet()) {
                if (!attributeList.contains(attribute)) {
                    attributeList.add(attribute);
                }
                if ((resultMap.get(entity)).containsKey(attribute)) {
                    valueList.add(resultMap.get(entity).get(attribute));
                } else {
                    valueList.add("");
                }
            }
        }
    }

    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, String splitter) {
        String resultString = null;
        if (isWriteFile)
            writeResultFile(writeFilePath, isOpenFile, splitter);
        if (isGetString)
            resultString = writeResultString(splitter);
        return resultString;
    }

    protected void writeResultFile(String writeFilePath, boolean isOpenFile, String splitter) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath))) {
            for (String attribute : attributeList) {
                bw.write(attribute);
                bw.write(splitter);
                bw.flush();
            }

            bw.flush();
            bw.write("\r\n");

            for (String entity : entityList) {
                bw.write(entity);
                bw.write(splitter);
                for (String value : valueList) {
                    bw.write(value);
                    bw.write(splitter);
                }
                bw.flush();
                bw.write("\r\n");
            }

            if (isOpenFile)
                Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    protected String writeResultString(String splitter) {
        StringBuilder resultString = new StringBuilder();
        for (String attribute : attributeList) {
            resultString.append(attribute).append(splitter);
        }
        resultString.append("\r\n");

        for (String entity : entityList) {
            resultString.append(entity).append(splitter);
            for (String value : valueList) {
                resultString.append(value).append(splitter);
            }
            resultString.append("\r\n");
        }
        return resultString.toString();
    }
}
