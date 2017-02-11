package com.example.laibomusic;

import java.util.ArrayList;
import java.util.List;

import com.example.laibomusic.entity.Music;
import com.example.laibomusic.util.Musiclist;

import android.app.Application;

public class MyApplication extends Application {
	
   
	private static  MyApplication application ;
	
	private static   String TAG ;
	
	public static MyApplication getInstance() {
		return application;
	}
	
	@Override
	public void onCreate() {
		TAG = getClass().getSimpleName();
		this.application = this;
		super.onCreate();
			
	}
	
 
}
