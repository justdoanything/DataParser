package org.dataparser.test;

import org.dataparser.parser.template.ExcelFileTemplate;

public class AttributeToFileTest {

  public static void main(String[] args) throws Exception {

//    String sysdir = System.getProperty("user.dir") + "/test/atf/";
//
//    String readFilePath = sysdir + "test_no_extension";
//    String writeFilePath = sysdir + "test_no_extension_result";
//    String answerFilePath = sysdir + "test_no_extension_answer";
//
//    AttributeToFile atf = AttributeToFile.builder()
//                                            .readFilePath(readFilePath)
//                                            .isWriteFile(true)
//                                            .isGetString(true)
//            .writeFilePath(writeFilePath)
//                                            .spliter("|")
//                                            .startWithLine(1)
//                                            .build();
//    atf.setCodeMap("Company","Apple","애플");
//    atf.setCodeMap("Company","LG","엘쥐");
//
//    System.out.println(atf.parse());
//    System.out.println(FileUtil.compareFile(answerFilePath, atf.getWriteFilePath()));

    ExcelFileTemplate fileTemplate = (ExcelFileTemplate) new ExcelFileTemplate.FileTemplateBuilder("test")
            .splitter("test")
            .build();
  }
}
