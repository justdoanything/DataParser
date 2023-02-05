package org.dataparser.parser.builder;

import org.dataparser.parser.impl.AttributeToFile;
import org.dataparser.parser.impl.FileToInsertQuery;
import org.dataparser.parser.template.FileTemplate;
import org.dataparser.parser.template.QueryTemplate;
import org.dataparser.util.FileUtil;

import java.nio.file.FileSystemException;

public class FileToInsertQueryBuilder extends QueryTemplate {
    public String getReadFilePath() {
        return readFilePath;
    }

    public String getWriteFilePath() {
        return writeFilePath;
    }

    public boolean isWriteFile() {
        return isWriteFile;
    }

    public boolean isOpenFile() {
        return isOpenFile;
    }

    public boolean isGetString() {
        return isGetString;
    }

    public String getTableName() {
        return tableName;
    }

    public int getBulkInsertCnt() {
        return bulkInsertCnt;
    }

    public boolean isBulkInsert() { return isBulkInsert; }

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

    public FileToInsertQueryBuilder tableName(String  tableName) {
        this.tableName = tableName;
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
}

