package data.template;

public class QueryTemplate extends CommonTemplate {
    protected String tableName;
    protected int bulkInsertCnt;
    protected boolean isBulkInsert;

    @Override
    protected String parseTextFile() {
        return null;
    }

    @Override
    protected String parseExcelFile() {
        return null;
    }
}
