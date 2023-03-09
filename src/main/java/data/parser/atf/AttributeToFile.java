package data.parser.atf;

import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.exception.ParseException;
import data.factory.ParserFactory;
import data.template.FileTemplate;
import data.template.common.CommonInterface;
import data.template.task.atf.AttributeToExcelTask;
import data.template.task.atf.AttributeToTextTask;
import data.util.FileUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class AttributeToFile extends FileTemplate implements CommonInterface {
    private final Map<String, Map<String, String>> resultMap;
    private java.util.List<String> entityList;
    private final java.util.List<String> attributeList;
    private final List<String> valueList;

    public static AttributeToFileBuilder builder(String readFilePath) {
        return new AttributeToFileBuilder(readFilePath);
    }

    public AttributeToFile(AttributeToFileBuilder builder) {
        this.readFilePath = builder.getReadFilePath();
        this.writeFilePath = builder.getWriteFilePath();
        this.isWriteFile = builder.isWriteFile();
        this.isOpenFile = builder.isOpenFile();
        this.isGetString = builder.isGetString();

        this.splitter = builder.getSplitter();
        this.startWithLine = builder.getStartWithLine();

        resultMap = new HashMap<>();
        entityList = new ArrayList<>();
        attributeList = Arrays.asList("Entity");
        valueList = new ArrayList<>();
    }

    @Override
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

    @Override
    protected String parseTextFile() {
        AttributeToTextTask textTask = (AttributeToTextTask) ParserFactory.createTask(ParseType.ATF, FileType.TEXT);
        textTask.preTask(codeMap, readFilePath, startWithLine, splitter);
        textTask.handleTask();
        return textTask.doTask(isWriteFile, isGetString, isOpenFile, writeFilePath, splitter);
    }

    @Override
    protected String parseExcelFile() {
        splitter = "\t";
        AttributeToExcelTask excelTask = (AttributeToExcelTask) ParserFactory.createTask(ParseType.ATF, FileType.EXCEL);
        excelTask.preTask(codeMap, readFilePath, startWithLine, splitter);
        excelTask.handleTask();
        return excelTask.doTask(isWriteFile, isGetString, isOpenFile, writeFilePath, splitter);
    }

}
