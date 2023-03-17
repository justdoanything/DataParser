package data.parser.otiq;

import data.template.QueryTemplate;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

public class ObjectToInsertQueryBuilder extends QueryTemplate {

    String getWriteFilePath() {
        return writeFilePath;
    }

    boolean isWriteFile() {
        return isWriteFile;
    }

    boolean isOpenFile() {
        return isOpenFile;
    }

    boolean isGetString() {
        return isGetString;
    }

    int getStartWithLine() {
        return startWithLine;
    }

    String getTableName() {
        return tableName;
    }

    int getBulkInsertCnt() {
        return bulkInsertCnt;
    }

    boolean isBulkInsert() { return isBulkInsert; }
    public ObjectToInsertQueryBuilder(String tableName) {
        this.tableName = tableName;
    }

    public ObjectToInsertQueryBuilder writeFilePath(String writeFilePath) {
        this.writeFilePath = writeFilePath;
        return this;
    }

    public ObjectToInsertQueryBuilder isWriteFile(boolean isWriteFile) {
        this.isWriteFile = isWriteFile;
        return this;
    }

    public ObjectToInsertQueryBuilder isOpenFile(boolean isOpenFile) {
        this.isOpenFile = isOpenFile;
        return this;
    }

    public ObjectToInsertQueryBuilder isGetString(boolean isGetString){
        this.isGetString = isGetString;
        return this;
    }

    public ObjectToInsertQueryBuilder startWithLine(int startWithLine) {
        this.startWithLine = startWithLine;
        return this;
    }

    public ObjectToInsertQueryBuilder bulkInsertCnt(int bulkInsertCnt) {
        this.bulkInsertCnt = bulkInsertCnt;
        return this;
    }

    public ObjectToInsertQueryBuilder isBulkInsert(boolean isBulkInsert) {
        this.isBulkInsert = isBulkInsert;
        return this;
    }

    public ObjectToInsertQueryImpl build() throws FileSystemException, FileNotFoundException {

        validParameter();

        return new ObjectToInsertQueryImpl(this);
    }
}

