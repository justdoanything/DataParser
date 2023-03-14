import data.FileToInsertQuery;
import data.parser.ftiq.FileToInsertQueryBuilder;

public class FileToInsertQueryTest {

  public static void main(String[] args) {
    try {
      FileToInsertQuery ftiq = FileToInsertQuery.builder("src/test/resources/question/FTIQ", "TEMP_TABLE")
              .splitter("\t")
              .isWriteFile(true)
              .isGetString(true)
              .isBulkInsert(true)
              .build();
      System.out.println(ftiq.parse());

      System.out.println("================================================");

      FileToInsertQuery ftiqBuilder = new FileToInsertQueryBuilder("src/test/resources/question/FTIQ", "TEMP_TABLE")
              .splitter("\t")
              .isWriteFile(true)
              .isGetString(true)
              .isBulkInsert(false)
              .build();
      System.out.println(ftiqBuilder.parse());
    }catch (Exception e) {
      System.out.println("Error : " + e.getMessage());
    }
  }
}


