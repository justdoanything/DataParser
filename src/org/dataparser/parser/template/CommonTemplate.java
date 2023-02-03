package org.dataparser.parser.template;

import java.util.HashMap;
import java.util.Map;

public abstract class CommonTemplate {
    protected String readFilePath;
    protected String writeFilePath;
    protected boolean isWriteFile = true;
    protected boolean isOpenFile = false;
    protected boolean isGetString = false;
    protected Map<String, Map<String, String>> codeMap = new HashMap<>();
}
