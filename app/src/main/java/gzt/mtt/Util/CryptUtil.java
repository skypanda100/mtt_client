package gzt.mtt.Util;

import android.util.Base64;
import java.security.MessageDigest;

public class CryptUtil {
    private static final String CHARSET = "UTF-8";

    public static byte[] sha256(String strSrc) {
        MessageDigest md;
        byte[] digests;
        try {
            byte[] bt = strSrc.getBytes(CHARSET);
            md = MessageDigest.getInstance("SHA-256");
            md.update(bt);
            digests = md.digest();
        } catch (Exception e) {
            return null;
        }
        return digests;
    }

    public static String bytes2Hex(byte[] src) {
        String des = "";
        String tmp;
        for (int i = 0; i < src.length; i++) {
            tmp = (Integer.toHexString(src[i] & 0xFF));
            if (tmp.length() == 1) {
                des += "0";
            }
            des += tmp;
        }
        return des;
    }

    public static String base64(byte[] src) {
        return Base64.encodeToString(src, Base64.NO_WRAP);
    }
}
