package data.parser.ftiq;

import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.exception.ParseException;
import data.factory.ParserFactory;
import data.template.QueryTemplate;
import data.template.common.CommonInterface;
import data.template.task.ftiq.ExcelToInsertQueryTask;
import data.template.task.ftiq.TextToInsertQueryTask;
import data.util.FileUtil;

import static data.constant.FileConstant.FILE_EXTENSION_BLANK;
import static data.constant.FileConstant.FILE_EXTENSION_CSV;
import static data.constant.FileConstant.FILE_EXTENSION_TXT;
import static data.constant.FileConstant.FILE_EXTENSION_XLS;
import static data.constant.FileConstant.FILE_EXTENSION_XLSX;

public class FileToInsertQuery extends QueryTemplate implements CommonInterface {

	public static FileToInsertQueryBuilder builder(String readFilePath, String tableName) {
		return new FileToInsertQueryBuilder(readFilePath, tableName);
	}

	public FileToInsertQuery(FileToInsertQueryBuilder builder) {
		this.readFilePath = builder.getReadFilePath();
		this.writeFilePath = builder.getWriteFilePath();
		this.isWriteFile = builder.isWriteFile();
		this.isOpenFile = builder.isOpenFile();
		this.isGetString = builder.isGetString();

		this.tableName = builder.getTableName();
		this.bulkInsertCnt = builder.getBulkInsertCnt();
		this.isBulkInsert = builder.isBulkInsert();
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
