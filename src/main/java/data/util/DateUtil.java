package data.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class DateUtil {
	public static String getDate(String dateformat, int addDate) throws DateTimeParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, addDate);
		return sdf.format(calender.getTime());
	}
}
