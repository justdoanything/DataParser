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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class AttributeToTextTask extends FileTaskTemplate {
    public AttributeToTextTask() {
        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = new ArrayList<>();
        valueList = new HashMap<>();
    }

    @Override
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

                lineArray = line.split(Pattern.quote(splitter));
                entityName = lineArray.length > 0 ? lineArray[0].trim() : "";
                attributeName = lineArray.length > 1 ? lineArray[1].trim() : "";
                attributeValue = lineArray.length > 2 ? lineArray[2].trim() : "";

                createResultMap(codeMap, resultMap, entityName, attributeName, attributeValue);
            }

            if (startWithLine != 0)
                throw new ParseException("startWithLine over than the row there is in file.");
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, String splitter) {
        String resultString = null;
        if (isWriteFile)
            writeResultFile(writeFilePath, isOpenFile, splitter);
        if (isGetString)
            resultString = writeResultString(splitter);
        return resultString;
    }

    @Override
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
                for (String value : valueList.get(entity)) {
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
}
