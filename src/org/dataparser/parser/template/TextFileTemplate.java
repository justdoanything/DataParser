package org.dataparser.parser.template;

import lombok.Builder;
import lombok.NonNull;
import lombok.Setter;
import org.dataparser.msg.MsgCode;
import org.dataparser.util.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.time.format.DateTimeParseException;

@Setter
@Builder
public abstract class TextFileTemplate {
    // template patter으로 공통 사항 정리 및 각 클래스에서 필요한 함수는 abstract로만 정의한다.
    // 공통 함수는 readTextFile, writeTextfile 이정도?

    @NonNull protected String readFilePath;
    @NonNull protected String writeFilePath;
    @Builder.Default protected String splitter = "\t";
    @Builder.Default protected boolean isWriteFile = true;
    @Builder.Default protected boolean isOpenFile = false;
    @Builder.Default protected boolean isGetString = false;

    private void filter() throws NullPointerException, StringIndexOutOfBoundsException, DateTimeParseException, FileSystemException, FileNotFoundException {
        if(!FileUtil.isFileExist(this.readFilePath))
            throw new FileNotFoundException("There is no file in " + this.readFilePath);

        if(this.isWriteFile && (this.writeFilePath == null || this.writeFilePath.length() == 0))
            this.writeFilePath = FileUtil.setDefaultWriteFilePath(this.readFilePath);

        if(this.readFilePath == null || (this.isWriteFile && this.writeFilePath == null) || this.spliter == null || this.codeMap == null)
            throw new NullPointerException("A required value has an exception : All of values cannot be null.");

        if(!this.isWriteFile && !this.isGetString)
            throw new RuntimeException("A required value has an exception : Either isWriteFile or isGetString must be true.");

        if(!this.isWriteFile && this.isOpenFile)
            throw new RuntimeException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");

        if(FileUtil.getFileExtension(this.readFilePath).equals(MsgCode.MSG_CODE_FILE_EXTENSION_CSV) && !this.spliter.equals(","))
            RuntimeException    }

    public abstract String parse();
}
