package com.wagchoir.settings;

import com.wagchoir.crm.utils.DateTimeUtil;
import com.wagchoir.crm.utils.MD5Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Test1 {
    public static void main(String[] args) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
//        System.out.println(dateStr);

//        System.out.println(DateTimeUtil.getSysTime());

        String nowStr = DateTimeUtil.getSysTime();
        String expireTime = "2022-10-09 13:30:12";
//        System.out.println(expireTime.compareTo(nowStr));

        System.out.println(MD5Util.getMD5("13571759504"));
    }
}
