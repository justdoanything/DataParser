package data.parser.ftiq;

import data.parser.atf.AttributeToFileBuilder;
import data.template.QueryTemplate;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

public class FileToInsertQueryBuilder extends QueryTemplate {

    protected String getReadFilePath() {
        return readFilePath;
    }

    protected String getWriteFilePath() {
        return writeFilePath;
    }

    protected boolean isWriteFile() {
        return isWriteFile;
    }

    protected boolean isOpenFile() {
        return isOpenFile;
    }

    protected boolean isGetString() {
        return isGetString;
    }

    String getSplitter() {
        return splitter;
    }

    int getStartWithLine() {
        return startWithLine;
    }

    protected String getTableName() {
        return tableName;
    }

    protected int getBulkInsertCnt() {
        return bulkInsertCnt;
    }

    protected boolean isBulkInsert() { return isBulkInsert; }

    public FileToInsertQueryBuilder(String readFilePath, String tableName) {
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

