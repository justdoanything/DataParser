package dataparser.parser;

import dataparser.msg.MsgCode;
import dataparser.template.QueryTemplate;
import dataparser.util.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

public class FileToInsertQueryBuilder extends QueryTemplate {
    protected String tableName;
    protected int bulkInsertCnt;
    protected boolean isBulkInsert;

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

    public FileToInsertQueryBuilder bulkInsertCnt(int bulkInsertCnt) {
        this.bulkInsertCnt = bulkInsertCnt;
        return this;
    }

    public FileToInsertQueryBuilder isBulkInsert(boolean isBulkInsert) {
        this.isBulkInsert = isBulkInsert;
        return this;
    }

    public FileToInsertQuery build() throws FileSystemException {
        if(writeFilePath == null || writeFilePath.length() < 1){
            writeFilePath = FileUtil.setDefaultWriteFilePath(readFilePath);
        }
        return new FileToInsertQuery(this);
    }

    private void filtering() throws FileNotFoundException, FileSystemException {
        if(!FileUtil.isFileExist(this.readFilePath))
            throw new FileNotFoundException("There is no file in " + this.readFilePath);

        if(this.isWriteFile && (this.writeFilePath == null || this.writeFilePath.length() == 0))
            this.writeFilePath = FileUtil.setDefaultWriteFilePath(this.readFilePath);

        if(this.readFilePath == null || (this.isWriteFile && this.writeFilePath == null) || this.tableName == null)
            throw new NullPointerException("A required value has an exception : all of values cannot be null");

        if(this.tableName.length() < 1)
            throw new NullPointerException("A required value has an exception : tableName must be set.");

        if(!this.isWriteFile && !this.isGetString)
            throw new ParseException("A required value has an exception : Either isWriteFile or isGetString must be true.");

        if(!this.isWriteFile && this.isOpenFile)
            throw new ParseException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");

        if(FileUtil.getFileExtension(this.readFilePath).equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV))
            throw new ParseException("A required value has an exception : csv file must be ','.");
    }
}

