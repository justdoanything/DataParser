package data.factory;

import data.exception.ParseException;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileTask {
    private void handleTextTask() {
        for(String entity : resultMap.keySet()) {
            if(!entityList.contains(entity))
                entityList.add(entity);

            for(String attribute : (resultMap.get(entity)).keySet()) {
                if(!attributeList.contains(attribute)) {
                    attributeList.add(attribute);
                }
                if((resultMap.get(entity)).containsKey(attribute)) {
                    valueList.add(resultMap.get(entity).get(attribute));
                } else {
                    valueList.add("");
                }
            }
        }
    }

    private String doTextTask() {
        String resultString = null;
        if(isWriteFile)
            writeResultFile();
        if(isGetString)
            resultString = writeResultString();
        return resultString;
    }

    private void writeResultFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath))) {
            for(String attribute : attributeList){
                bw.write(attribute); bw.write(splitter); bw.flush();
            }

            bw.flush();
            bw.write("\r\n");

            for (String entity : entityList) {
                bw.write(entity);
                bw.write(splitter);
                for(String value : valueList){
                    bw.write(value);
                    bw.write(splitter);
                }
                bw.flush();
                bw.write("\r\n");
            }

            if(isOpenFile) Desktop.getDesktop().edit(new File(writeFilePath));
        }catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    private String writeResultString() {
        StringBuilder resultString = new StringBuilder();
        for(String attribute : attributeList){
            resultString.append(attribute).append(splitter);
        }
        resultString.append("\r\n");

        for (String entity : entityList) {
            resultString.append(entity).append(splitter);
            for(String value : valueList){
                resultString.append(value).append(splitter);
            }
            resultString.append("\r\n");
        }
        return resultString.toString();
    }
}
