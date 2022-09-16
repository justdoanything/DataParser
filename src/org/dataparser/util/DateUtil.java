package org.dataparser.util;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class DateUtil {
	
	/**
	 * Return date like dateformat you want
	 * @param dateformat
	 * @param addDate
	 * @return
	 */
	public static String getDate(String dateformat, int addDate) throws DateTimeParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, addDate);
		return sdf.format(calender.getTime());
	}
}
