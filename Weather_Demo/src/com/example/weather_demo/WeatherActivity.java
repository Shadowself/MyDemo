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

	// ��λ���
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

		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������

		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ��,Ĭ��ֵgcj02
		// option.setScanSpan(5000);//���÷���λ����ļ��ʱ��Ϊ5000ms
		option.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		option.setNeedDeviceDirect(true);// ���صĶ�λ��������ֻ���ͷ�ķ���
		option.setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy);// ���ö�λģʽ
		option.setProdName(getPackageName());
		mLocationClient.setLocOption(option);
		if (isInterent.hasInternet(this)) {
			mLocationClient.start();

		} else {
			Toast.makeText(getApplicationContext(), "�����쳣�����������Ƿ�����",
					Toast.LENGTH_LONG).show();
		}
	}

	// ��ȡ��ǰλ������
	public void showWeather(View v) {

		URL url1, url2;
		if (isInterent.hasInternet(this)) {

			String city = locationplace.getText().toString().trim();
			if (city.contains("��") || city.contains("ʡ")) {
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
			Toast.makeText(getApplicationContext(), "�����쳣�����������Ƿ�����",
					Toast.LENGTH_LONG).show();
		}
	}

	// ���¶�λ
	public void getLocationplace(View v) {
		if (isInterent.hasInternet(this)) {
			mLocationClient.requestLocation();
		} else {
			Toast.makeText(getApplicationContext(), "�����쳣�����������Ƿ�����",
					Toast.LENGTH_LONG).show();
		}
	}

	// ��λ
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
			HttpURLConnection conn = null; // ���Ӷ���
			String resultData = HttpUtil.getResault(conn, url);

			Log.i("����", resultData);
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

	// ����������json����
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
					+ "\n������" + weather.getWeather() + "   ";
		} catch (JSONException e) {
			Log.i("Tag", "����jsonʧ��");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

	// ����ʵʱ������json����
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

			jsonresult = "��ǰ�¶ȣ�" + weather.getToptemp() + "  ���������  "
					+ weather.getWind() + weather.getWindfl() + "  "
					+ weather.getGxtime() + "����";
		} catch (JSONException e) {
			Log.i("Tag", "����jsonʧ��");
			e.printStackTrace();
		}
		weather = null;
		return jsonresult;
	}

	protected void onDestroy() {
		// �˳�ʱ���ٶ�λ
		mLocationClient.stop();
		super.onDestroy();
	}

}
