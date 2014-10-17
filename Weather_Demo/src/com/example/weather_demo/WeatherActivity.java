package com.example.weather_demo;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.example.util.CityId;
import com.example.util.HttpUtil;
import com.example.util.Weather;
import com.example.util.isInterent;

public class WeatherActivity extends Activity {

	// 定位相关
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();

	private TextView localweather, locationplace;
	private TextView nowweather;
	private MyHandler handler;
	private JSONObject obj;
	private String City;

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
			case 2:
				String result1 = (String) msg.obj;

				nowweather.setText(result1);
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
		nowweather = (TextView) findViewById(R.id.nowweather);

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

	// 获取当前位置天气
	public void showWeather(View v) {

		URL url1, url2;
		if (isInterent.hasInternet(this)) {

			String city = locationplace.getText().toString().trim();
			if (city.contains("市") || city.contains("省")) {
				City = city.substring(0, city.length() - 1);
			}

			try {
				url1 = new URL(
						String.format(getString(R.string.weatherurl)
								+ "?cityCode=%1$s&weatherType=0",
								CityId.getCityIdByName(City)));

				GetWeather nt = new GetWeather(url1, "0");
				nt.start();

				url2 = new URL(
						String.format(getString(R.string.weatherurl)
								+ "?cityCode=%1$s&weatherType=1",
								CityId.getCityIdByName(City)));

				GetWeather nt1 = new GetWeather(url2, "1");
				nt1.start();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络是否连接",
					Toast.LENGTH_LONG).show();
		}
	}

	// 重新定位
	public void getLocationplace(View v) {
		if (isInterent.hasInternet(this)) {
			mLocationClient.requestLocation();
		} else {
			Toast.makeText(getApplicationContext(), "网络异常，请检查网络是否连接",
					Toast.LENGTH_LONG).show();
		}
	}

	// 定位
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
		}

	}

	

	class GetWeather extends Thread {
		private URL url;
		private String IsNow;

		public GetWeather(URL url, String IsNow) {
			this.url = url;
			this.IsNow = IsNow;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null; // 连接对象
			String resultData = HttpUtil.getResault(conn, url);

			Log.i("天气", resultData);
			String resultmsg = null;

			Looper.prepare();
			if (IsNow.equals("0")) {
				Message msg = new Message();
				resultmsg = json(resultData);
				msg.what = 1;
				msg.obj = resultmsg;
				handler.sendMessage(msg);
			} else {
				Message msg1 = new Message();
				resultmsg = json1(resultData);
				msg1.what = 2;
				msg1.obj = resultmsg;
				handler.sendMessage(msg1);
			}
			Looper.loop();

		}

	}

	// 解析天气的json数据
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
					+ "\n天气：" + weather.getWeather() + "   ";
		} catch (JSONException e) {
			Log.i("Tag", "解析json失败");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

	// 解析实时天气的json数据
	public String json1(String json) {
		String jsonresult = null;
		Weather weather = new Weather();
		try {
			obj = new JSONObject(json);
			JSONObject contentObject = obj.getJSONObject("weatherinfo");
			weather.setToptemp(contentObject.getString("temp"));
			weather.setWind(contentObject.getString("WD"));
			weather.setWindfl(contentObject.getString("WS"));
			weather.setGxtime(contentObject.getString("time"));

			jsonresult = "当前温度：" + weather.getToptemp() + "  风向风力：  "
					+ weather.getWind() + weather.getWindfl() + "  "
					+ weather.getGxtime() + "发布";
		} catch (JSONException e) {
			Log.i("Tag", "解析json失败");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

	protected void onDestroy() {
		// 退出时销毁定位
		mLocationClient.stop();
		super.onDestroy();
	}

}
