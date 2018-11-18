package gzt.mtt.Util;

import android.content.Context;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PhotoUtil {
    public static String getTime(String path) {
        String photoTimeStr = null;
        Date date = null;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String timeStr = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
            date = TimeUtil.str2date(timeStr, "yyyy:MM:dd HH:mm:ss");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (date != null) {
            photoTimeStr = TimeUtil.date2str(date, "yyyy-MM-dd HH:mm");
        } else {
            File photo = new File(path);
            if(photo.exists() && photo.isFile()){
                photoTimeStr = TimeUtil.date2str(new Date(photo.lastModified()), "yyyy-MM-dd HH:mm");
            } else {
                photoTimeStr = TimeUtil.date2str(new Date(), "yyyy-MM-dd HH:mm");
            }
        }
        return photoTimeStr;
    }

    public static String getAddress(Context context, String path) {
        String address = null;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            String latitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            String longitude = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);

            //转换经纬度格式
            double lat = score2dimensionality(latitude);
            double lon = score2dimensionality(longitude);

            address = GpsUtil.location2address(context, lat, lon);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private static double score2dimensionality(String string) {
        double dimensionality = 0.0;
        if (null == string) {
            return dimensionality;
        }

        //用 ，将数值分成3份
        String[] split = string.split(",");
        for (int i = 0;i < split.length;i++) {
            String[] s = split[i].split("/");
            //用112/1得到度分秒数值
            double v = Double.parseDouble(s[0]) / Double.parseDouble(s[1]);
            //将分秒分别除以60和3600得到度，并将度分秒相加
            dimensionality = dimensionality + v / Math.pow(60, i);
        }
        return dimensionality;
    }
}
