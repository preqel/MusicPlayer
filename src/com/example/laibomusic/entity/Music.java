package com.example.laibomusic.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.net.ParseException;

public class Music {

	public  String id ; 
	private String singer;
	private String name;
	private String url;
	private String title;
	private long time;
  
	public  String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	 	
	 	
}
