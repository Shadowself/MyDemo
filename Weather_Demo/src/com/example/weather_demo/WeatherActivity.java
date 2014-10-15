package com.example.weather_demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.util.CityId;
import com.example.util.Weather;
import com.example.util.WebUtil;

public class WeatherActivity extends Activity {

	private TextView localweather, location;
	private MyHandler handler;
	private JSONObject obj;
	private Weather w;
	LocationManager locMan;

	@SuppressLint("HandlerLeak")
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			int what = msg.what;
			switch (what) {
			case 1:
				String result = (String) msg.obj;

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
		location = (TextView) findViewById(R.id.location);
		handler = new MyHandler();
		w = new Weather();

		locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// Location loc =
		// locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 定义当前的Location privider
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 10,
				new MyLocationListener());

	}

	/**
	 * 通过城市的名称获取对应的城市id，如果不存在则返回 null
	 * 
	 * @param name
	 *            城市名称
	 * @return cityid
	 * */
	public static String getCityIdByName(String name) {
		String cityId = null;

		int startIndex = CityId.cityIds.indexOf(name) + name.length() + 1;// 开始截取的位置
		if (startIndex == -1) {
			return null;
		}

		cityId = CityId.cityIds.trim().substring(startIndex, startIndex + 9);

		return cityId;
	}

	class GetWeather extends Thread {
		private URL url;
		private String isloacal;

		public GetWeather(URL url, String isloacal) {
			this.url = url;
			this.isloacal = isloacal;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null; // 连接对象
			String resultData = getResault(conn, url);

			// url = new URL("http://m.weather.com.cn/data/101180101.html");

			Log.i("天气", resultData);
			String resultmsg = null;
			if (json(resultData) != null) {
				resultmsg = json(resultData);
				Message msg = new Message();
				if (isloacal.endsWith("0")) {
					msg.what = 1;
				} else {
					msg.what = 2;
				}
				msg.obj = resultmsg;
				handler.sendMessage(msg);
			} else {
				Toast.makeText(getApplicationContext(), "json数据解析失败", 1).show();
			}

		}

	}

	public String getResault(HttpURLConnection conn, URL url) {
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // 使用URL打开一个链接
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultData;
	}

	public void showWeather(View v) {

		URL url1, url2;

		try {
			url1 = new URL(String.format(getString(R.string.weatherurl)
					+ "?cityCode=%1$s&weatherType=0", getCityIdByName("郑州")));
			
			GetWeather nt = new GetWeather(url1, "0");
			nt.start();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		// 获取所有的 providers
		List<String> providers = locMan.getAllProviders();
		for (Iterator iterator = providers.iterator(); iterator.hasNext();) {
			String pro = (String) iterator.next();
			Log.i("provider", "" + pro);
		}

	}

	public void bestprovider(View v) {
		// 生成一个 Criteria对象
		Criteria criteria = new Criteria();
		// 设置查询条件
		// criteria.setAccuracy(criteria.ACCURACY_FINE); //设置经度
		criteria.setPowerRequirement(criteria.POWER_LOW); // 电量

		criteria.setAltitudeRequired(false); // 不需要海拔
		criteria.setSpeedRequired(false); // 不需要速度
		criteria.setCostAllowed(false); // 不产生费用
		// false 表明不管privoder是否打开，如果是true 则在已经打开的provider中查找
		String provider = locMan.getBestProvider(criteria, false);

		Log.i("BestProvider", "" + provider);
	}

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location loc) {
			if (loc != null) {
				location.setText("" + loc.getLatitude() + "   "
						+ loc.getLongitude());
				Log.i("tag",
						"" + loc.getLatitude() + "   " + loc.getLongitude());
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	public String json(String json) {
		String jsonresult = null;
		Weather weather = new Weather();
		try {
			obj = new JSONObject(json);
			JSONObject contentObject = obj.getJSONObject("weatherinfo");
			weather.setCity(contentObject.getString("city"));
			weather.setToptemp(contentObject.getString("temp1"));
			weather.setWeather(contentObject.getString("weather1"));
			weather.setWind(contentObject.getString("wind1"));
			weather.setWindfl(contentObject.getString("fl1"));
			
			jsonresult = weather.getCity() + ":  "
			+ weather.getToptemp() + "\n天气：" + weather.getWeather()
			+ "   " + weather.getWind() +":" + weather.getWindfl();
		} catch (JSONException e) {
			Log.i("Tag", "解析json失败");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

}
