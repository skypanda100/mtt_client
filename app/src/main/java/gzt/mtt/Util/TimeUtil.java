package gzt.mtt.Util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
}
