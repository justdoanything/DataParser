package org.dataparser.test;

import java.io.FileInputStream;
import java.io.IOException;

import org.dataparser.parser.AttributeToFile;

public class AttributeToFileTest {
  
  public static void main(String[] args) throws Exception { 

    String sysdir = System.getProperty("user.dir") + "/testingFile/atf/";

    String readFileName = "test_atf";
    String wrtieFileName = "test_atf_result";
    String answerFileName = "test_atf_answer";
    AttributeToFile atf = AttributeToFile.builder()
                                            .readFilePath(sysdir + readFileName)
                                            .writeFilePath(sysdir + wrtieFileName)
                                            .isWriteFile(true)
                                            .isOpenFile(true)
                                            .isGetString(true)
                                            .spliter("|")
                                            .startWithLine(1)
                                            .build();
    atf.parse();

    
    System.out.println(fileCat(sysdir + answerFileName, sysdir + wrtieFileName));

  }

  public static boolean fileCat(String first, String second) throws IOException{
    FileInputStream fis1 = null;  
    FileInputStream fis2 = null;

    try {
      fis1 = new FileInputStream(first);
      fis2 = new FileInputStream(second);

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
