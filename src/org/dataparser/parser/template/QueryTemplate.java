package org.dataparser.parser.template;

public abstract class QueryTemplate extends CommonTemplate {
    protected String tableName;
    protected int bulkInsertCnt;
    protected boolean isBulkInsert;
}
