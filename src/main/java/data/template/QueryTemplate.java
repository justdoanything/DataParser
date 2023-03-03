package data.template;

import data.exception.ParseException;
import data.util.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;

import static data.constant.FileConstant.FILE_EXTENSION_CSV;

public class QueryTemplate extends CommonTemplate {
    protected String tableName;
    protected int bulkInsertCnt = 10;
    protected boolean isBulkInsert = true;

    @Override
    protected String parseTextFile() {
        return null;
    }

    @Override
    protected String parseExcelFile() {
        return null;
    }

    @Override
    protected void validParameter() throws FileNotFoundException, FileSystemException {
        if (isWriteFile && (writeFilePath == null || writeFilePath.length() < 1))
            writeFilePath = FileUtil.setDefaultWriteFilePath(readFilePath);

        if (!isWriteFile && !isGetString)
            throw new ParseException("A required value has an exception : Either isWriteFile or isGetString must be true.");

        if (!isWriteFile && isOpenFile)
            throw new ParseException("A required value has an exception : isOpenFile must be false if isWriteFile is true.");

        if (readFilePath == null || (isWriteFile && writeFilePath == null) || splitter == null || tableName == null || codeMap == null)
            throw new ParseException("A required value has an exception : Required fields must be not null.");

        if (startWithLine < 0)
            throw new ParseException("A required value has an exception : startWithLine should be over 0.");

        if (bulkInsertCnt < 10)
            throw new ParseException("A required value has an exception : bulkInsertCnt should be over 10.");

        if (tableName.length() < 1)
            throw new ParseException("A required value has an exception : tableName must be set.");

        if (FileUtil.getFileExtension(readFilePath).equals(FILE_EXTENSION_CSV) && !splitter.equals(","))
            throw new ParseException("A required value has an exception : csv file must be ','.");

        if (!FileUtil.isFileExist(readFilePath))
            throw new FileNotFoundException("The file to read is not existed in [" + readFilePath + "]");
    }
}
