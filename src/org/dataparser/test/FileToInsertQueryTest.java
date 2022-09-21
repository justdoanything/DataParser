package org.dataparser.test;

import org.dataparser.parser.impl.FileToInsertQuery;
import org.dataparser.util.FileUtil;

public class FileToInsertQueryTest {
  
  public static void main(String[] args) throws Exception { 

    String sysdir = System.getProperty("user.dir") + "/test/ftiq/";

    String readFilePath = sysdir + "test_ftiq_no_extension";
    String writeFilePath = sysdir + "test_ftiq_no_extension_result";
    String answerFilePath = sysdir + "test_ftiq_no_extension_answer";
    
    FileToInsertQuery ftiq = FileToInsertQuery.builder()
                                            .readFilePath(readFilePath)
                                            .isWriteFile(true)
                                            .isGetString(true)
                                            .spliter("|")
                                            .tableName("testTable")
                                            .isBulkInsert(true)
                                            .build();
    System.out.println(ftiq.parse()); 
    System.out.println(FileUtil.compareFile(answerFilePath, ftiq.getWriteFilePath()));
  }
}
