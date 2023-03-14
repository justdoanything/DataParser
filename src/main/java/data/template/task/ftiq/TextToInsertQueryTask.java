package data.template.task.ftiq;

import data.exception.ParseException;
import data.template.task.QueryTaskTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextToInsertQueryTask extends QueryTaskTemplate {

    @Override
    public void preTask(String tableName) {
        queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
        queryBody = new StringBuilder();
        isFirst = true;
    }

    @Override
    public void handleTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, boolean isBulkInsert, String splitter, int bulkInsertCnt) {
        try (BufferedReader br = new BufferedReader(new FileReader(readFilePath))) {
            int lineCnt = 0;
            String line;
            while ((line = br.readLine()) != null) {
                if (startWithLine != 0) {
                    startWithLine -= 1;
                    continue;
                }

                if (isBulkInsert) {
                    handleBulkTask(codeMap, line, splitter, bulkInsertCnt, lineCnt);
                    lineCnt++;
                } else
                    handleNonBulkTask(codeMap, line, splitter);
            }

            if(isBulkInsert)
                queryBody.replace(queryBody.lastIndexOf(","), queryBody.length(),";");

            if (startWithLine != 0)
                throw new ParseException("startWithLine over than the row there is in file.");
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    private void handleBulkTask(Map<String, Map<String, String>> codeMap, String line, String splitter, int bulkInsertCnt, int lineCnt) {
        if (isFirst) {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining(","));
            queryHeader.append(line).append(") VALUES \r\n");
            isFirst = false;
        } else {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining("','", "('", "')"));

            // null 처리 및 chageCodeValue 처리
            line = line.replace("''", "null");

            if (lineCnt > 0 && lineCnt % bulkInsertCnt == 0) {
                queryBody.append(line.trim()).append(";\r\n\r\n");
                queryBody.append(queryHeader);
            } else {
                queryBody.append(line.trim()).append(",\r\n");
            }
        }
    }

    private void handleNonBulkTask(Map<String, Map<String, String>> codeMap, String line, String splitter) {
        if (isFirst) {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining(","));
            queryHeader.append(line).append(") VALUES ");
            isFirst = false;
        }else {
            line = Arrays.stream(line.split(Pattern.quote(splitter)))
                    .map(word -> word.trim())
                    .collect(Collectors.joining("','", "('", "')"));

            // null 처리 및 chageCodeValue 처리
            line = line.replace("''", "null");

            queryBody.append(queryHeader).append(line.trim()).append(";\r\n");
        }
    }
}
