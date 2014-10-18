package com.example.util;

public class Weather {
	private String city;
	private String cityId;
	private String toptemp;
	
	private String lowtemp;
	private String weather;  //天气
	private String wind;     //风向
	private String windfl;   //风力
	
	private String gxtime;
	private String index;   //穿衣指数
	private String index_d; //穿衣建议

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getToptemp() {
		return toptemp;
	}

	public void setToptemp(String toptemp) {
		this.toptemp = toptemp;
	}

	public String getLowtemp() {
		return lowtemp;
	}

	public void setLowtemp(String lowtemp) {
		this.lowtemp = lowtemp;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getGxtime() {
		return gxtime;
	}

	public void setGxtime(String gxtime) {
		this.gxtime = gxtime;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getWindfl() {
		return windfl;
	}

	public void setWindfl(String windfl) {
		this.windfl = windfl;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getIndex_d() {
		return index_d;
	}

	public void setIndex_d(String index_d) {
		this.index_d = index_d;
	}
	

}
