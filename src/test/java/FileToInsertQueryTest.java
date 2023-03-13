import data.FileToInsertQuery;

public class FileToInsertQueryTest {

  public static void main(String[] args) {
    try {
      FileToInsertQuery ftiq = FileToInsertQuery.builder("readFilePath", "tableName")
              .isWriteFile(true)
              .isGetString(true)
              .build();
      System.out.println(ftiq.parse());
    }catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}


