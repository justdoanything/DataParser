package data.factory;

import data.constant.TypeEnum.FileType;
import data.constant.TypeEnum.ParseType;
import data.exception.ParseException;
import data.template.TaskTemplate;
import data.template.task.atf.AttributeToExcelTask;
import data.template.task.atf.AttributeToTextTask;
import data.template.task.ftiq.ExcelToInsertQueryTask;
import data.template.task.ftiq.TextToInsertQueryTask;

public class ParserFactory {
    public static TaskTemplate createTask(ParseType parseType, FileType fileType) {
        switch (parseType) {
            case ATF:
                switch (fileType) {
                    case TEXT:
                        return new AttributeToTextTask();
                    case EXCEL:
                        return new AttributeToExcelTask();
                    default:
                        throw new ParseException("Parser Type is not defined.");
                }
            case FTIQ:
                switch (fileType) {
                    case TEXT:
                        return new TextToInsertQueryTask();
                    case EXCEL:
                        return new ExcelToInsertQueryTask();
                    default:
                        throw new ParseException("Parser Type is not defined.");
                }
            default:
                throw new ParseException("Parser Type is not defined.");
        }
    }
}
