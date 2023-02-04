package org.dataparser.parser.builder;

import org.dataparser.parser.impl.AttributeToFile;
import org.dataparser.parser.template.FileTemplate;
import org.dataparser.util.FileUtil;

import java.nio.file.FileSystemException;

public class AttributeToFileBuilder extends FileTemplate {
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

    public String getSplitter() {
        return splitter;
    }

    public int getStartWithLine() {
        return startWithLine;
    }

    public AttributeToFileBuilder(String readFilePath) {
        this.readFilePath = readFilePath;
    }

    public AttributeToFileBuilder writeFilePath(String writeFilePath) {
        this.writeFilePath = writeFilePath;
        return this;
    }

    public AttributeToFileBuilder isWriteFile(boolean isWriteFile) {
        this.isWriteFile = isWriteFile;
        return this;
    }

    public AttributeToFileBuilder isOpenFile(boolean isOpenFile) {
        this.isOpenFile = isOpenFile;
        return this;
    }

    public AttributeToFileBuilder isGetString(boolean isGetString){
        this.isGetString = isGetString;
        return this;
    }

    public AttributeToFileBuilder splitter(String  splitter) {
        this.splitter = splitter;
        return this;
    }

    public AttributeToFileBuilder startWithLine(int startWithLine) {
        this.startWithLine = startWithLine;
        return this;
    }

    public AttributeToFile build() throws FileSystemException {
        if(writeFilePath == null || writeFilePath.length() < 1){
            writeFilePath = FileUtil.setDefaultWriteFilePath(readFilePath);
        }
        return new AttributeToFile(this);
    }
}

