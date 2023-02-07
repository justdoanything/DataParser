package data.test;

import data.parser.atf.AttributeToFile;
import data.parser.atf.AttributeToFileBuilder;

public class AttributeToFileTest {

  public static void main(String[] args) throws Exception {
    AttributeToFile atf = new AttributeToFileBuilder("")
                                        .writeFilePath("")
                                        .isWriteFile(true)
                                        .isOpenFile(true)
                                        .isGetString(true)
                                        .splitter("")
                                        .startWithLine(0)
                                        .build();
    atf.parse();
  }
}
