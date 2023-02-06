package data.parser.atf;

import data.constant.CommonConstant;
import data.exception.ParseException;
import data.template.FileTemplate;
import data.util.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

public class AttributeToFileBuilder extends FileTemplate {
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

    public AttributeToFile build() throws FileSystemException, FileNotFoundException {
        if(writeFilePath == null || writeFilePath.length() < 1){
            writeFilePath = FileUtil.setDefaultWriteFilePath(readFilePath);
        }

        filtering();

        return new AttributeToFile(this);
    }

    private void filtering() throws FileNotFoundException, FileSystemException {
        if(!FileUtil.isFileExist(this.readFilePath))
            throw new FileNotFoundException("There is no file in " + this.readFilePath);

        if(this.isWriteFile && (this.writeFilePath == null || this.writeFilePath.length() == 0))
            this.writeFilePath = FileUtil.setDefaultWriteFilePath(this.readFilePath);

        if(this.startWithLine < 0)
            throw new ParseException("A required value has an exception : startWithLine should be over 0.");

        if(this.readFilePath == null || (this.isWriteFile && this.writeFilePath == null) || this.splitter == null || this.codeMap == null)
            throw new NullPointerException("A required value has an exception : All of values cannot be null.");

        if(!this.isWriteFile && !this.isGetString)
            throw new ParseException("A required value has an exception : Either isWriteFile or isGetString must be true.");

        if(!this.isWriteFile && this.isOpenFile)
            throw new ParseException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");

        if(FileUtil.getFileExtension(this.readFilePath).equals(CommonConstant.MSG_CODE_FILE_EXTENSION_CSV) && !this.splitter.equals(","))
            throw new ParseException("A required value has an exception : csv file must be ','.");
    }
}

