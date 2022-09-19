package org.dataparser.test;

import java.io.FileInputStream;
import java.io.IOException;

import org.dataparser.parser.impl.FileToInsertQuery;

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
                                            .isBulkInsert(false)
                                            .build();
    System.out.println(ftiq.parse()); 
    System.out.println(compareFile(answerFilePath, ftiq.getWriteFilePath()));
  }

  public static boolean compareFile(String answerFilePath, String resultFilePath) throws IOException{
    FileInputStream fis1 = null;  
    FileInputStream fis2 = null;

    try {
      fis1 = new FileInputStream(answerFilePath);
      fis2 = new FileInputStream(resultFilePath);

      int readData1 = 0; int readData2 = 0; 
      while(true){
        readData1 = fis1.read();
        readData2 = fis2.read();
        if(readData1!=readData2||(readData1!=-1&&readData2==-1)) {
          return false;
        }
        if(readData1==-1) {
          return true;
        }
      } 
    }catch (Exception e) { 
      e.printStackTrace();
    }finally{
      try { fis1.close(); fis2.close(); } catch (IOException e) { e.printStackTrace(); }
    }
    return false;
  }
}
