package data.test;

import data.factory.FileTask;
import data.parser.atf.AttributeToFile;
import data.parser.atf.AttributeToFileBuilder;
import data.parser.ftiq.FileToInsertQuery;
import data.parser.ftiq.FileToInsertQueryBuilder;

public class AttributeToFileTest {

  public static void main(String[] args) throws Exception {
    AttributeToFile atf = AttributeToFile.builder("")
                                        .writeFilePath("")
                                        .isWriteFile(true)
                                        .isOpenFile(true)
                                        .isGetString(true)
                                        .splitter("")
                                        .startWithLine(0)
                                        .build();
    atf.addCodeMap("name", "code", "value");
    atf.parse();

    FileToInsertQuery ftiq = FileToInsertQuery.builder("", "")
                                      .writeFilePath("")
                                      .isWriteFile(true)
                                      .isOpenFile(true)
                                      .isGetString(true)
                                      .splitter("")
                                      .startWithLine(0)
                                      .bulkInsertCnt(0)
                                      .isBulkInsert(true)
                                      .build();
    ftiq.addCodeMap("name", "code", "value");
    ftiq.parse();
  }
}
