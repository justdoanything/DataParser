package org.dataparser.test;

import org.dataparser.parser.AttributeToFile;
import org.dataparser.parser.AttributeToFileBuilder;

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
