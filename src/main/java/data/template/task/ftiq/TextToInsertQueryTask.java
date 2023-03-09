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
import java.util.stream.Collectors;

public class TextToInsertQueryTask extends QueryTaskTemplate {

    @Override
    public void preTask(Map<String, Map<String, String>> codeMap, String readFilePath, int startWithLine, String tableName, boolean isBulkInsert, String splitter, int bulkInsertCnt) {
        queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
        queryBody = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(readFilePath))) {
            String line;
            boolean isFirst = true;
            int index = 0;
            while ((line = br.readLine()) != null) {
                if (startWithLine != 0) {
                    startWithLine -= 1;
                    continue;
                }

                if (isFirst) {
                    if (isBulkInsert) {
                        line = line.replace(splitter, ", ");
                        line = Arrays.stream(line.split("\\\\" + splitter))
                                .map(word -> word.trim())
                                .collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                        queryHeader.append(line).append(") VALUES \r\n");
                    } else {
                        line = Arrays.stream(line.split("\\\\" + splitter))
                                .map(word -> word.trim())
                                .collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                        line = line.replace(splitter, ", ");
                        queryHeader.append(line).append(") VALUES ");
                    }
                    isFirst = false;
                }

                if (isBulkInsert) {
                    line = line.replace(splitter, "', '").replace("''", "null");
                    line = Arrays.stream(line.split("\\\\" + splitter))
                            .map(word -> word.trim())
                            .collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                    if (index > 0 && index % bulkInsertCnt == 0) {
                        queryBody.append("('").append(line.trim()).append("');\r\n\r\n");
                        queryBody.append(queryHeader);
                    } else {
                        queryBody.append("('").append(line.trim()).append("'),\r\n");
                    }
                    index++;
                } else {
                    line = Arrays.stream(line.split("\\\\" + splitter))
                            .map(word -> word.trim())
                            .collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
                    line = line.replace(splitter, "', '").replace("''", "null");
                    queryBody.append(queryHeader).append("('").append(line.trim()).append("');\r\n");
                }
            }

            if (startWithLine != 0)
                throw new ParseException("startWithLine over than the row there is in file.");
        } catch (Exception e) {
            throw new ParseException(e.getMessage());
        }
    }

    @Override
    public void handleTask() {

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
        StringBuilder resultString = new StringBuilder();
        if (isBulkInsert) {
            resultString.append(queryHeader);
        }
        resultString.append(queryBody);
        return resultString.toString();
    }
}

//		StringBuilder resultString = new StringBuilder();
//
//		try (BufferedReader br = new BufferedReader(new FileReader(readFilePath));
//			 BufferedWriter bw = isWriteFile ? new BufferedWriter(new FileWriter(writeFilePath)) : null;) {
//
//			String line;
//			StringBuilder queryHeader = new StringBuilder("INSERT INTO " + tableName + " (");
//			StringBuilder queryBody = new StringBuilder();
//			boolean isFirst = true;
//			int index = 0;
//			while ((line = br.readLine()) != null) {
//				if(startWithLine != 0){
//					startWithLine -= 1;
//					continue;
//				}
//
//				if(isBulkInsert) {
//					if(isFirst) {
//						line = line.replace(splitter, ", ");
//						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
//						queryHeader.append(line).append(") VALUES \r\n");;
//						isFirst = false;
//					} else {
//						line = line.replace(splitter, "', '").replace("''", "null");
//						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
//						if(index > 0 && index % bulkInsertCnt == 0) {
//							queryBody.append("('").append(line.trim()).append("');\r\n\r\n");
//							queryBody.append(queryHeader);
//						} else {
//							queryBody.append("('").append(line.trim()).append("'),\r\n");
//						}
//					}
//					index++;
//				} else {
//					if(isFirst) {
//						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
//						line = line.replace(splitter, ", ");
//						queryHeader.append(line).append(") VALUES ");
//						isFirst = false;
//					} else {
//						line = Arrays.stream(line.split("\\\\" + splitter)).map(word -> word.trim()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
//						line = line.replace(splitter, "', '").replace("''", "null");
//						queryBody.append(queryHeader).append("('").append(line.trim()).append("');\r\n");
//					}
//				}
//			}
//
//			if(startWithLine != 0)
//				throw new ParseException("startWithLine over than the row there is in file.");
//
//			if(isBulkInsert)
//				queryBody.replace(queryBody.lastIndexOf(","), queryBody.lastIndexOf(",") + 1, ";");
//
//			if(isWriteFile) {
//				if(isBulkInsert) {
//					bw.write(queryHeader.toString());
//					bw.flush();
//				}
//
//				bw.write(queryBody.toString());
//				bw.flush();
//
//				if(isOpenFile)
//					Desktop.getDesktop().edit(new File(writeFilePath));
//			}
//
//			if(isGetString) {
//				if(isBulkInsert) {
//					resultString.append(queryHeader);
//				}
//				resultString.append(queryBody);
//			}
//		} catch (Exception e) {
//			throw new ParseException(e.getMessage());
//		}
//		return resultString.toString();
