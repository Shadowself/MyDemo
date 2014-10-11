package com.example.weather_demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.util.Weather;
import com.example.util.WebUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WeatherActivity extends Activity {

	private TextView localweather;
	private MyHandler handler;
	private JSONObject obj;
	private Weather w;

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				String result = "地点：" + w.getCity() + "\n温度：" + w.getLowtemp()
						+ "-" + w.getToptemp() + "\n天气：" + w.getWeather() + "\n更新时间 :" + w.getGxtime();

				localweather.setText(result);
				break;

			default:
				break;
			}

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);

		localweather = (TextView) findViewById(R.id.localweather);
		handler = new MyHandler();
		w = new Weather();

	}

	class GetWeather extends Thread {

		@Override
		public void run() {
			HttpURLConnection conn = null; // 连接对象
			InputStream is = null;
			String resultData = "";
			URL url;
			try {
				url = new URL(
						"http://www.weather.com.cn/data/cityinfo/101180101.html");

				// url = new URL("http://m.weather.com.cn/data/101180101.html");
				conn = (HttpURLConnection) url.openConnection(); // 使用URL打开一个链接
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setRequestProperty("Connection", "keep-alive");
				conn.setRequestProperty("Content-Type",
						"text/html; charset=utf-8");
				conn.setRequestProperty("User-Agent",
						"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0)");
				if (conn.getResponseCode() == 200) {
					is = conn.getInputStream();
					resultData = WebUtil.getStr(is);
				}

				Log.i("天气", resultData);

				json(resultData);

				Message msg = new Message();
				msg.what = 1;
				msg.obj = resultData;
				handler.sendMessage(msg);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void showWeather(View v) {
		GetWeather nt = new GetWeather();
		nt.start();
	}

	public void json(String json) {
		try {
			obj = new JSONObject(json);
			JSONObject contentObject = obj.getJSONObject("weatherinfo");
			String difang = contentObject.getString("city");
			Log.i("tag", difang);
			w.setCity(contentObject.getString("city"));
			w.setLowtemp(contentObject.getString("temp2"));
			w.setToptemp(contentObject.getString("temp1"));
			w.setWeather(contentObject.getString("weather"));
			w.setGxtime(contentObject.getString("ptime"));
		} catch (JSONException e) {
			Log.i("Tag", "解析json失败");
			e.printStackTrace();
		}
	}

}
