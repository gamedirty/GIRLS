package com.example.girls;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.util.Log;

public class HTTPUtil {
	private static String baiduMeinvUrl = "http://image.baidu.com/channel?c=%E7%BE%8E%E5%A5%B3#%E7%BE%8E%E5%A5%B3";

	/**
	 * @Description 百度美妞图片首页盘查美妞品种
	 * @return
	 * @author zhaojunhui
	 * @date 2014年11月24日 下午10:20:01
	 */
	public static ArrayList<String> getTabs() {
		ArrayList<String> tabs = new ArrayList<String>();
		try {
			URL url = new URL(baiduMeinvUrl);
			URLConnection connection = url.openConnection();
			Log.i("info", "lenth:" + connection.getContentLength());
			InputStream in = connection.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String line = null;
			StringBuffer sb = new StringBuffer();
			while ((r.read() != -1)) {
				line = r.readLine();
				if (line.contains("title=\"美女>") && !line.contains("</a>")) {
					Log.i("info2", "" + line);
					String tab = line.substring(line.indexOf("title=\"美女>") + "title=\"美女>".length(), line.lastIndexOf("\">"));
					Log.i("info2", "" + tab);

					tabs.add(tab);
				}
				sb.append(line);
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tabs;
	}

}
