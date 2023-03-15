import data.FileToInsertQuery;
import data.parser.ftiq.FileToInsertQueryBuilder;

public class FileToInsertQueryTest {

  public static void main(String[] args) {
    try {
      FileToInsertQuery ftiq = FileToInsertQuery.builder("src/test/resources/question/FTIQ", "TEMP_TABLE")
              .splitter("|")
              .startWithLine(0)
              .isWriteFile(true)
              .isOpenFile(true)
              .writeFilePath("src/test/resources/question/FTIQ_2023")
              .isGetString(true)
              .isBulkInsert(true)
              .bulkInsertCnt(100)
              .build();
      System.out.println(ftiq.parse());

      System.out.println("================================================");

      FileToInsertQuery ftiqBuilder = new FileToInsertQueryBuilder("src/test/resources/question/FTIQ.xlsx", "TEMP_TABLE")
              .splitter("\t")
              .startWithLine(0)
              .isWriteFile(true)
              .isOpenFile(false)
              .writeFilePath("src/test/resources/question/FTIQ_2023.xlsx")
              .isGetString(true)
              .isBulkInsert(false)
              .build();
      System.out.println(ftiqBuilder.parse());
    }catch (Exception e) {
      System.out.println("Error : " + e.getMessage());
    }
  }
}