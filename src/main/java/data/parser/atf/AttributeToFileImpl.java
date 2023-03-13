package data.parser.atf;

import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.exception.ParseException;
import data.factory.ParserFactory;
import data.AttributeToFile;
import data.template.FileTemplate;
import data.template.task.atf.AttributeToExcelTask;
import data.template.task.atf.AttributeToTextTask;
import data.util.FileUtil;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class AttributeToFileImpl extends FileTemplate implements AttributeToFile {

    public AttributeToFileImpl(AttributeToFileBuilder builder) {
        this.readFilePath = builder.getReadFilePath();
        this.writeFilePath = builder.getWriteFilePath();
        this.isWriteFile = builder.isWriteFile();
        this.isOpenFile = builder.isOpenFile();
        this.isGetString = builder.isGetString();

        this.splitter = builder.getSplitter();
        this.startWithLine = builder.getStartWithLine();
    }

    public String getWriteFilePath() {
        return writeFilePath;
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
