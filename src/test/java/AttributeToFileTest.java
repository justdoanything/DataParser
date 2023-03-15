import data.AttributeToFile;
import data.parser.atf.AttributeToFileBuilder;

public class AttributeToFileTest {

    public static void main(String[] args) {
        try {
            AttributeToFile atf = AttributeToFile.builder("src/test/resources/question/ATF")
                    .splitter("|")
                    .startWithLine(0)
                    .isWriteFile(true)
                    .isOpenFile(true)
                    .writeFilePath("src/test/resources/question/ATF_2023.txt")
                    .isGetString(true)
                    .build();
            atf.addCodeMap("name", "code", "value");
            System.out.println(atf.parse());

            System.out.println("=============================================================");

            AttributeToFile atfBuilder = new AttributeToFileBuilder("src/test/resources/question/ATF.xlsx")
                    .splitter("\t")
                    .startWithLine(0)
                    .isWriteFile(true)
                    .isOpenFile(false)
                    .writeFilePath("src/test/resources/question/ATF_2023.xlsx")
                    .isGetString(true)
                    .build();
            atfBuilder.addCodeMap("name", "code", "value");
            System.out.println(atfBuilder.parse());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }
}
