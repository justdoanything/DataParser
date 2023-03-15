package data.template.common;

import data.exception.ParseException;
import data.util.FileUtil;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public abstract class CommonTemplate {
    protected String readFilePath;
    protected String writeFilePath;
    protected boolean isWriteFile = true;
    protected boolean isOpenFile = false;
    protected boolean isGetString = false;
    protected String splitter = "\t";
    protected int startWithLine = 0;

    protected Map<String, Map<String, String>> codeMap = new HashMap<>();

    public String parse() {
        String resultString;
        String readFileExtension = FileUtil.getFileExtension(readFilePath).toLowerCase();

        switch (readFileExtension) {
            case FILE_EXTENSION_TXT:
            case FILE_EXTENSION_BLANK:
            case FILE_EXTENSION_CSV:
                resultString = parseTextFile();
                break;
            case FILE_EXTENSION_XLS:
            case FILE_EXTENSION_XLSX:
                resultString = parseExcelFile();
                break;
            default:
                throw new ParseException("A extension of file must be '.csv', '.xls', '.xlsx', '.txt' or empty");
        }
        return resultString;
    }

    public void addCodeMap(String name, String code, String value) {
        if (!codeMap.containsKey(name)) {
            codeMap.put(name, new HashMap<>());
        }
        codeMap.get(name).put(code, value);
    }

    protected abstract String parseTextFile();

    protected abstract String parseExcelFile();

    protected abstract void validParameter() throws FileNotFoundException, FileSystemException;
}
