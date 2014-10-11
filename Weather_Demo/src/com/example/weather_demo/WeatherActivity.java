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

import com.example.util.Weather;
import com.example.util.WebUtil;

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

public class WeatherActivity extends Activity {

	private TextView localweather,location;
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
				String result = "�ص㣺" + w.getCity() + "\n�¶ȣ�" + w.getLowtemp()
						+ "-" + w.getToptemp() + "\n������" + w.getWeather() + "\n����ʱ�� :" + w.getGxtime();

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
//		Location loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//���嵱ǰ��Location privider
		locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600, 10,
				new MyLocationListener());

	}

	class GetWeather extends Thread {

		@Override
		public void run() {
			HttpURLConnection conn = null; // ���Ӷ���
			InputStream is = null;
			String resultData = "";
			URL url;
			try {
				url = new URL(
						"http://www.weather.com.cn/data/cityinfo/101180101.html");

				// url = new URL("http://m.weather.com.cn/data/101180101.html");
				conn = (HttpURLConnection) url.openConnection(); // ʹ��URL��һ������
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

				Log.i("����", resultData);

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

		//��ȡ���е�  providers
		List<String> providers = locMan.getAllProviders();
		for(Iterator iterator = providers.iterator();iterator.hasNext() ;){
			String pro = (String)iterator.next();
			Log.i("provider",""+pro);
		}
		
	}
	
	public void bestprovider(View v){
		//����һ�� Criteria����
		Criteria criteria = new Criteria();
		//���ò�ѯ����
//		criteria.setAccuracy(criteria.ACCURACY_FINE);  //���þ���
		criteria.setPowerRequirement(criteria.POWER_LOW);  //����
		
		criteria.setAltitudeRequired(false);  //����Ҫ����
		criteria.setSpeedRequired(false);     //����Ҫ�ٶ�
		criteria.setCostAllowed(false);       //����������
		//false ��������privoder�Ƿ�򿪣������true �����Ѿ��򿪵�provider�в���
		String provider = locMan.getBestProvider(criteria, false);
		
		Log.i("BestProvider","" + provider);
	}
	
	public class MyLocationListener implements LocationListener {
		    @Override
		    public void onLocationChanged(Location loc) {
		        if (loc != null) {
		        	location.setText(""+ loc.getLatitude() + "   " +  loc.getLongitude());
		        	Log.i("tag",""+ loc.getLatitude() + "   " +  loc.getLongitude());
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
			Log.i("Tag", "����jsonʧ��");
			e.printStackTrace();
		}
	}

}
