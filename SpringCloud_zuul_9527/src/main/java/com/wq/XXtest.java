package com.wq;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * 插入一个金融项目的测试。。。。
 */
public class XXtest {
    //解码
    public static String unicodetoString(String unicode) {
        if (unicode == null || "".equals(unicode)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = -1;
        int pos = 0;
        while ((i = unicode.indexOf("\\u", pos)) != -1) {
            sb.append(unicode.substring(pos, i));
            if (i + 5 < unicode.length()) {
                pos = i + 6;
                sb.append((char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16));
            }
        }
        return sb.toString();
    }
    //编码
    public static String stringtoUnicode(String string) {
        if (string == null || "".equals(string)) {
            return null;
        }
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }
    //Unicode转中文方法
    private static String unicodeToCn(String unicode) {

        StringBuffer string = new StringBuffer();

//        unicode = unicode.replaceAll(" ","");
//        String[] hex = unicode.replace("&#x", "").split(";");
//        for (int i = 0; i < hex.length; i++) {
//            int data = Integer.parseInt(hex[i], 16);
//            string.append((char) data);
//        }
//        int start = unicode.indexOf("&#x");
//        int end = unicode.indexOf(";");
//        System.out.println(start-end);
        for (int i = 0; i < unicode.length(); i++) {
            if(i+1<unicode.length()&&i+2<unicode.length()&&i+7<unicode.length()){
                if(unicode.charAt(i)=='&'&&unicode.charAt(i+1)=='#'&&unicode.charAt(i+2)=='x'&&unicode.charAt(i+7)==';'){
                    String s = unicode.substring(i,i+8);
                    //System.out.println(s);
                    String[] hex = s.replace("&#x", "").split(";");
                    for (int j = 0; j < hex.length; j++) {
                        int data = Integer.parseInt(hex[j], 16);
                        string.append((char) data);
                    }
                    i+=7;
                }else {
                    string.append(unicode.charAt(i));
                }
            }else {
                string.append(unicode.charAt(i));
            }

        }
        return string.toString();
    }

    public static void main(String[] args) {
        String s = XXtest.unicodeToCn("&#x94F6;&#x4FDD;21321&#x76D1;&#x4F1A;&#x9881;&#x65B0;&#x653F; 213&#x4E0A;&#x5E02;213&#x4FE1;&#x6258;&#x516C;&#x53F8;&#x80A1;&#x6743;&#x9700;&#x96C6;&#x4E2D;&#x5B58;&#x7BA1;");
        System.out.println(s);
    }
}
