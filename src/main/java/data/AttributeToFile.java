package data;

import data.parser.atf.AttributeToFileBuilder;

public interface AttributeToFile {
    String parse();

    void addCodeMap(String name, String code, String value);

    static AttributeToFileBuilder builder(String readFilePath) {
        return new AttributeToFileBuilder(readFilePath);
    }
}
