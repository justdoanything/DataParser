package prj.yong.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	
	public static String getDate(String dateformat, int addDate) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
		Calendar calender = Calendar.getInstance();
		calender.add(Calendar.DATE, addDate);
		return sdf.format(calender.getTime());
	}
}
