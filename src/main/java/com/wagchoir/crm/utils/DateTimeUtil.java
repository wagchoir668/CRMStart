package com.wagchoir.crm.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {
	public static String getSysTime(){
		//日期时间的工具类
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date date = new Date();
		String dateStr = sdf.format(date);
		
		return dateStr;
	}
}
