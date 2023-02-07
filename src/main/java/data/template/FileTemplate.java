package data.template;

public class FileTemplate extends CommonTemplate {
    protected String splitter;
    protected int startWithLine;

    @Override
    protected String parseTextFile() {
        return null;
    }

    @Override
    protected String parseExcelFile() {
        return null;
    }
}
