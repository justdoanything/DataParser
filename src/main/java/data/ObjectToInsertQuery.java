package data;

import data.parser.otiq.ObjectToInsertQueryBuilder;

public interface ObjectToInsertQuery {
    String parse();

    void addCodeMap(String name, String code, String value);

    static ObjectToInsertQueryBuilder builder(String tableName) {
        return new ObjectToInsertQueryBuilder(tableName);
    }
}
