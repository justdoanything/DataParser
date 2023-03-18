package data.parser.ftiq;

import data.FileToInsertQuery;
import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.factory.ParserFactory;
import data.template.QueryTemplate;
import data.template.task.ftiq.ExcelToInsertQueryTask;
import data.template.task.ftiq.TextToInsertQueryTask;

public class FileToInsertQueryImpl extends QueryTemplate implements FileToInsertQuery {

	public FileToInsertQueryImpl(FileToInsertQueryBuilder builder) {
		this.readFilePath = builder.getReadFilePath();
		this.writeFilePath = builder.getWriteFilePath();
		this.isWriteFile = builder.isWriteFile();
		this.isOpenFile = builder.isOpenFile();
		this.isGetString = builder.isGetString();
		this.splitter = builder.getSplitter();
		this.startWithLine = builder.getStartWithLine();

		this.tableName = builder.getTableName();
		this.bulkInsertCnt = builder.getBulkInsertCnt();
		this.isBulkInsert = builder.isBulkInsert();
	}

	@Override
	protected String parseTextFile() {
		TextToInsertQueryTask textTask = (TextToInsertQueryTask) ParserFactory.createTask(ParseType.FTIQ, FileType.TEXT);
		textTask.preTask(tableName);
		textTask.handleTask(codeMap, readFilePath, startWithLine, isBulkInsert, splitter, bulkInsertCnt);
		return textTask.doTask(isWriteFile, isGetString, isOpenFile, writeFilePath, isBulkInsert);
	}

	@Override
	protected String parseExcelFile() {
		ExcelToInsertQueryTask excelTask = (ExcelToInsertQueryTask) ParserFactory.createTask(ParseType.FTIQ, FileType.EXCEL);
		excelTask.preTask(tableName);
		excelTask.handleTask(codeMap, readFilePath, startWithLine, isBulkInsert, splitter, bulkInsertCnt);
		return excelTask.doTask(isWriteFile, isGetString, isOpenFile, writeFilePath, isBulkInsert);
	}
}
