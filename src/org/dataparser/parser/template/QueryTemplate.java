package org.dataparser.parser.template;

public class QueryTemplate extends CommonTemplate {
    private String tableName;
    private int bulkInsertCnt = 100;
    private boolean isBulkInsert = true;

    public QueryTemplate(QueryTemplateBuilder builder) {
        this.readFilePath = builder.readFilePath;
        this.writeFilePath = builder.writeFilePath;
        this.isWriteFile = builder.isWriteFile;
        this.isOpenFile = builder.isOpenFile;
        this.isGetString = builder.isGetString;
        this.tableName = builder.tableName;
        this.bulkInsertCnt = builder.bulkInsertCnt;
        this.isBulkInsert = builder.isBulkInsert;
    }

    public static class QueryTemplateBuilder extends CommonTemplate {
        private String tableName;
        private int bulkInsertCnt = 100;
        private boolean isBulkInsert = true;

        public QueryTemplateBuilder(String tableName) {
            this.tableName = tableName;
        }

        public QueryTemplateBuilder bulkInsertCnt(int bulkInsertCnt) {
            this.bulkInsertCnt = bulkInsertCnt;
            return this;
        }

        public QueryTemplateBuilder readFilePath(boolean isBulkInsert) {
            this.isBulkInsert = isBulkInsert;
            return this;
        }

        public QueryTemplateBuilder readFilePath(String readFilePath) {
            this.readFilePath = readFilePath;
            return this;
        }

        public QueryTemplateBuilder writeFilePath(String writeFilePath) {
            this.writeFilePath = writeFilePath;
            return this;
        }

        public QueryTemplateBuilder isWriteFile(boolean isWriteFile) {
            this.isWriteFile = isWriteFile;
            return this;
        }

        public QueryTemplateBuilder isOpenFile(boolean isOpenFile) {
            this.isOpenFile = isOpenFile;
            return this;
        }

        public QueryTemplate build() {
            return new QueryTemplate(this);
        }
    }
}
