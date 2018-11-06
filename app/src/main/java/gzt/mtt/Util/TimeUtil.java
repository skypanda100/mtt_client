package gzt.mtt.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeUtil {
    public static String date2str(Date date, String fmt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        return simpleDateFormat.format(date);
    }

    public static Date str2date(String dateStr, String fmt) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(fmt);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

    public static List<String> date2chstr(Date date) {
        List<String> dateChStrs = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        String yearStr = calendar.get(Calendar.YEAR) + "年";
        dateChStrs.add(yearStr);
        String monthStr = calendar.get(Calendar.MONTH) + 1 + "月";
        dateChStrs.add(monthStr);
        String dayStr = calendar.get(Calendar.DATE) + "日";
        dateChStrs.add(dayStr);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (week) {
            case 1:
                weekStr = "周日";
                break;
            case 2:
                weekStr = "周一";
                break;
            case 3:
                weekStr = "周二";
                break;
            case 4:
                weekStr = "周三";
                break;
            case 5:
                weekStr = "周四";
                break;
            case 6:
                weekStr = "周五";
                break;
            case 7:
                weekStr = "周六";
                break;
            default:
                break;
        }
        dateChStrs.add(weekStr);
        String hourStr = calendar.get(Calendar.HOUR_OF_DAY) + "时";
        dateChStrs.add(hourStr);
        String minuteStr = calendar.get(Calendar.MINUTE) + "分";
        dateChStrs.add(minuteStr);
        String secondStr = calendar.get(Calendar.SECOND) + "秒";
        dateChStrs.add(secondStr);

        return dateChStrs;
    }
}
