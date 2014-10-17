package com.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtil {
	
	public static String getResault(HttpURLConnection conn, URL url) {
		InputStream is = null;
		String resultData = "";
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				resultData = WebUtil.getStr(is);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} // 使用URL打开一个链接
		catch (Exception e) {
			e.printStackTrace();
		}

		return resultData;
	}

}
