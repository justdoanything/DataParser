package data.template.task.ftiq;

import data.exception.ParseException;
import data.template.task.QueryTaskTemplate;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextToInsertQueryTask extends QueryTaskTemplate {

    @Override
    public void preTask(String tableName) {
        queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
        queryBody = new StringBuilder();
    }

    @Override
    public void handleTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, boolean isBulkInsert, String splitter, int bulkInsertCnt) {
        try (BufferedReader br = new BufferedReader(new FileReader(readFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (startWithLine != 0) {
                    startWithLine -= 1;
                    continue;
                }

                if (isBulkInsert)
                    handleBulkTask(codeMap, line, splitter, bulkInsertCnt);
                else
                    handleNonBulkTask(codeMap, line, splitter);
            }

            if (startWithLine != 0)
                throw new ParseException("startWithLine over than the row there is in file.");
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    private void handleBulkTask(Map<String, Map<String, String>> codeMap, String line, String splitter, int bulkInsertCnt) {
        boolean isFirst = true;
        int index = 0;

        if (isFirst) {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining(","));
            queryHeader.append(line).append(") VALUES \r\n");
            isFirst = false;
        }

        line = Arrays.stream(line.split(Pattern.quote(splitter)))
                .map(word -> word.trim())
                .collect(Collectors.joining("','", "('", "')"));

        // null 처리 및 chageCodeValue 처리
        line = line.replace("''", "null");

        if (index > 0 && index % bulkInsertCnt == 0) {
            queryBody.append(line.trim()).append(";\r\n\r\n");
            queryBody.append(queryHeader);
        } else {
            queryBody.append(line.trim()).append(";\r\n");
        }
        index++;
    }

    private void handleNonBulkTask(Map<String, Map<String, String>> codeMap, String line, String splitter) {
        boolean isFirst = true;
        if (isFirst) {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining(","));
            queryHeader.append(line).append(") VALUES ");
            isFirst = false;
        }

        line = Arrays.stream(line.split(Pattern.quote(splitter)))
                .map(word -> word.trim())
                .collect(Collectors.joining("','", "('", "')"));

        // null 처리 및 chageCodeValue 처리
        line = line.replace("''", "null");

        queryBody.append(queryHeader).append(line.trim()).append(";\r\n");
    }

    @Override
    public String doTask(boolean isWriteFile, boolean isGetString, boolean isOpenFile, String writeFilePath, boolean isBulkInsert) {
        String resultString = null;
        if (isWriteFile)
            writeResultFile(writeFilePath, isOpenFile, isBulkInsert);
        if (isGetString)
            resultString = writeResultString(isBulkInsert);
        return resultString;
    }

    @Override
    protected void writeResultFile(String writeFilePath, boolean isOpenFile, boolean isBulkInsert) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(writeFilePath))) {
            if (isBulkInsert) {
                bw.write(queryHeader.toString());
                bw.flush();
            }

            bw.write(queryBody.toString());
            bw.flush();

            if (isOpenFile)
                Desktop.getDesktop().edit(new File(writeFilePath));
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    protected String writeResultString(boolean isBulkInsert) {
        return isBulkInsert ? queryBody.append(queryBody).toString() : queryBody.toString();
    }
}
