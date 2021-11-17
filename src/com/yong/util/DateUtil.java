package com.yong.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	
	public static String getDate(String dateFormat, int addDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, addDate);
		return sdf.format(calender.getTime());
	}
}
