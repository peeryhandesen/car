package com.d5.john.car.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.System.currentTimeMillis;

/**
 * 时间工具类
 * Created by idisfkj on 16/4/26.
 * Email : idisfkj@qq.com.
 */
public class CalendarUtils {

    public CalendarUtils() {
    }

    public static String getCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm");
        Date date = new Date(currentTimeMillis());
        String time = format.format(date);
        return time;
    }

    public static Long getLongTime(String str) {
        Long time = 0l;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            time = format.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String getCurrentDate(String creatTime){
        String MM = creatTime.substring(5,7);
        String dd = creatTime.substring(8,10);
        String HH = creatTime.substring(11,13);
        String mm = creatTime.substring(14,16);
        String time = MM +"月"+dd+"日"+ " "+HH+":"+mm;
        return time;
    }

    public static int formatDate2Now(String dateStr){
        String res = getCurrent24Date();
        String res2 = getCurrent12Date();
        String[] dd = new String[2];
        String[] HH = new String[2];
        dd[0] = dateStr.substring(8,10);
        dd[1] = res.substring(8,10);
        HH[0] = dateStr.substring(11,13);
        HH[1] = res.substring(11,13);
        int day = Integer.parseInt(dd[0]) - Integer.parseInt(dd[1]);
        int hour = Integer.parseInt(HH[0]) - Integer.parseInt(HH[1]);
        int last = day * 24 + hour;
        if(res.equals(res2)){
            last = last -12;
        }
        return last;
    }



    public static String getCurrent24Date(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(currentTimeMillis());
        String time = format.format(date);
        return time;
    }

    public static String getCurrent12Date(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = new Date(currentTimeMillis());
        String time = format.format(date);
        return time;
    }
}
