package com.hive.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Hive
 * Description:解决中文文件名下载乱码问题的工具类
 * Date: 2022/2/6 14:47
 */

public class DownloadUtils {

    public static String getFileName(String agent, String filename) throws UnsupportedEncodingException {
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        }
        else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }
}
