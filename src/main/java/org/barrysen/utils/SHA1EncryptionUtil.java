package org.barrysen.utils;

import java.security.MessageDigest;

/**
 * 功能：SHA-1 加密算法
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:45
 */
public class SHA1EncryptionUtil {

    /**
     *  加密
     * @param inStr
     * @return
     * @throws Exception
     */
    public static String shaEncode(String inStr) throws Exception {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }


}
