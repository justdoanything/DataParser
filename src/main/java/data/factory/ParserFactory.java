package data.factory;

import data.constant.TypeEnum;
import data.exception.ParseException;
import data.parser.atf.AttributeToExcelTaskTemplate;
import data.parser.atf.AttributeToTextTaskTemplate;
import data.template.TaskTemplate;

public class ParserFactory {
    public static TaskTemplate createTask(TypeEnum.ParseType type, String splitter) {
        switch (type) {
            case TEXT:
                return new AttributeToTextTaskTemplate(splitter);
            case EXCEL:
                return new AttributeToExcelTaskTemplate();
            default:
                throw new ParseException("Parser Type is not defined.");
        }
    }
}
