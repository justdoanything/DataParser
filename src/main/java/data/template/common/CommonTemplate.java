package data.template.common;

import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.util.HashMap;
import java.util.Map;

public abstract class CommonTemplate {
    protected String readFilePath;
    protected String writeFilePath;
    protected boolean isWriteFile = true;
    protected boolean isOpenFile = false;
    protected boolean isGetString = false;
    protected String splitter = "\t";
    protected int startWithLine = 0;

    protected Map<String, Map<String, String>> codeMap = new HashMap<>();

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
