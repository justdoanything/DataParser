package data.parser.ftiq;

import data.template.QueryTemplate;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

public class FileToInsertQueryBuilder extends QueryTemplate {

    String getReadFilePath() {
        return readFilePath;
    }

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

    String getSplitter() {
        return splitter;
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

    FileToInsertQueryBuilder(String readFilePath, String tableName) {
        this.readFilePath = readFilePath;
        this.tableName = tableName;
    }

    public FileToInsertQueryBuilder writeFilePath(String writeFilePath) {
        this.writeFilePath = writeFilePath;
        return this;
    }

    public FileToInsertQueryBuilder isWriteFile(boolean isWriteFile) {
        this.isWriteFile = isWriteFile;
        return this;
    }

    public FileToInsertQueryBuilder isOpenFile(boolean isOpenFile) {
        this.isOpenFile = isOpenFile;
        return this;
    }

    public FileToInsertQueryBuilder isGetString(boolean isGetString){
        this.isGetString = isGetString;
        return this;
    }

    public FileToInsertQueryBuilder splitter(String splitter) {
        this.splitter = splitter;
        return this;
    }

    public FileToInsertQueryBuilder startWithLine(int startWithLine) {
        this.startWithLine = startWithLine;
        return this;
    }

    public FileToInsertQueryBuilder bulkInsertCnt(int bulkInsertCnt) {
        this.bulkInsertCnt = bulkInsertCnt;
        return this;
    }

    public FileToInsertQueryBuilder isBulkInsert(boolean isBulkInsert) {
        this.isBulkInsert = isBulkInsert;
        return this;
    }

    public FileToInsertQuery build() throws FileSystemException, FileNotFoundException {

        validParameter();

        return new FileToInsertQuery(this);
    }
}

