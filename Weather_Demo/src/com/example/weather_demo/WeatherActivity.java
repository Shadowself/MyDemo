package com.example.weather_demo;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.util.CityId;
import com.example.util.Weather;
import com.example.util.WebUtil;
import com.example.util.isInterent;

public class WeatherActivity extends Activity implements
		OnGetGeoCoderResultListener {

	// 定位相关
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private TextView localweather, locationplace;
	private MyHandler handler;
	private JSONObject obj;

	// LocationManager locMan;

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
		locationplace = (TextView) findViewById(R.id.location);
		handler = new MyHandler();

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		// option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
		option.setProdName(getPackageName());
		mLocationClient.setLocOption(option);
		if (isInterent.hasInternet(this)) {
			mLocationClient.start();

		} else {
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络是否连接",
					Toast.LENGTH_LONG).show();
		}
	}

	public class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			String place = location.getAddrStr();
			locationplace.setText(location.getCity());

			Toast.makeText(getApplicationContext(), "" + place, 1).show();
			Log.i("", sb.toString() + place);
		}

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

		public GetWeather(URL url) {
			this.url = url;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null; // 连接对象
			String resultData = getResault(conn, url);

			Log.i("天气", resultData);
			String resultmsg = null;
			if (json(resultData) != null) {
				resultmsg = json(resultData);
				Message msg = new Message();
				msg.what = 1;
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
			e.printStackTrace();
		} // 使用URL打开一个链接
		catch (Exception e) {
			e.printStackTrace();
		}

		return resultData;
	}

	public void showWeather(View v) {

		URL url1;
		if (isInterent.hasInternet(this)) {

			String city = locationplace.getText().toString().trim();
			if(city.contains("市") || city.contains("省")){
				String c = city.substring(0, city.length()-1);
				Toast.makeText(getApplicationContext(), c, 1).show();
			}

			try {
				url1 = new URL(
						String.format(getString(R.string.weatherurl)
								+ "?cityCode=%1$s&weatherType=0",
								getCityIdByName("郑州")));

				GetWeather nt = new GetWeather(url1);
				nt.start();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络是否连接",
					Toast.LENGTH_LONG).show();

		}

	}

	public void getLocationplace(View v) {
		if (isInterent.hasInternet(this)) {
			mLocationClient.requestLocation();
		} else {
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络是否连接",
					Toast.LENGTH_LONG).show();

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

			jsonresult = weather.getCity() + ":  " + weather.getToptemp()
					+ "\n天气：" + weather.getWeather() + "   "
					+ weather.getWind() + ":" + weather.getWindfl();
		} catch (JSONException e) {
			Log.i("Tag", "解析json失败");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	protected void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();
		super.onDestroy();
	}

}
