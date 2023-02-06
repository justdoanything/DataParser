package org.dataparser.test;

import org.dataparser.parser.FileToInsertQuery;
import org.dataparser.parser.FileToInsertQueryBuilder;
import org.dataparser.util.FileUtil;

public class FileToInsertQueryTest {

  public static void main(String[] args) throws Exception {
    FileToInsertQuery ftiq = new FileToInsertQueryBuilder("", "")
                                          .writeFilePath("")
                                          .isWriteFile(true)
                                          .isOpenFile(true)
                                          .isGetString(true)
                                          .bulkInsertCnt(0)
                                          .isBulkInsert(true)
                                          .build();
  }
}
