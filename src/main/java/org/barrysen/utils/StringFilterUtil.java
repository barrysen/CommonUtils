package org.barrysen.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * 功能：字符串过滤工具类
 *
 * @author: Barrysen
 * @date: 2025/1/3 13:46
 */
public class StringFilterUtil {

    /**
     * 特殊字符过滤
     *
     * @param str
     * @return
     * @throws PatternSyntaxException
     */
    public static String filter(String str) throws PatternSyntaxException {
        // 清除掉所有特殊字符
        String regEx = "[`_《》~!@#$%^&*()+=|{}':;',\\[\\].<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 删除末尾指定字符串
     *
     * @param inStr
     * @param suffix
     * @return: java.lang.String
     * @author: Barry
     * @date: 2021/6/9 15:04
     */
    public static String trimEnd(String inStr, String suffix) {
        while (inStr.endsWith(suffix)) {
            inStr = inStr.substring(0, inStr.length() - suffix.length());
        }
        return inStr;
    }

    /**
     * 提取位于两个指定字符串之间的子字符串。
     *
     * @param str 原始字符串
     * @param open 起始字符串
     * @param close 结束字符串
     * @return 返回两个字符串之间的子字符串，如果没有找到匹配的，返回 null
     */
    public static String substringBetween(String str, String open, String close) {
        // 如果输入的字符串或起始、结束子串为空，返回 null
        if (str == null || open == null || close == null) {
            return null;
        }

        // 查找起始子字符串的位置
        int start = str.indexOf(open);
        if (start != -1) {
            // 从起始子字符串之后开始查找结束子字符串的位置
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                // 提取并返回位于起始和结束子字符串之间的内容
                return str.substring(start + open.length(), end);
            }
        }

        // 如果没有找到起始或结束子串，返回 null
        return null;
    }
}
