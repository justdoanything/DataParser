package data.parser.atf;

import data.AttributeToFile;
import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.factory.ParserFactory;
import data.template.FileTemplate;
import data.template.task.atf.AttributeToExcelTask;
import data.template.task.atf.AttributeToTextTask;

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
