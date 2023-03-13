package data;

import data.parser.ftiq.FileToInsertQueryBuilder;

public interface FileToInsertQuery {
    String parse();

    void addCodeMap(String name, String code, String value);

    static FileToInsertQueryBuilder builder(String readFilePath, String tableName) {
        return new FileToInsertQueryBuilder(readFilePath, tableName);
    }
}
