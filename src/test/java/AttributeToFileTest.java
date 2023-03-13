import data.parser.atf.AttributeToFile;

public class AttributeToFileTest {

    public static void main(String[] args) {
        try {
            AttributeToFile atf = AttributeToFile.builder("src/test/resources/question/ATF")
                    .isWriteFile(true)
                    .isGetString(true)
                    .splitter("|")
                    .build();
            System.out.println(atf.parse());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
