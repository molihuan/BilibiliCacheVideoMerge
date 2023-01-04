package com.molihua.hlbmerge.utils;

public class StringTools {

    /**
     * 去除所有空格（包括圆角和半角空格） 通过ACSII匹配空格
     * @param str
     * @return
     */

    public static String deleteAllSpaceByJudgeACSII(String str) {
            StringBuilder sb = new StringBuilder();
            char[] chars = str.toCharArray();
            for (char c : chars) {
                if (32 == c || 12288 == c ) {
                    continue;
                }
                sb.append(c);
            }
            return sb.toString();
    }

}
