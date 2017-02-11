package com.example.laibomusic.service;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.example.laibomusic.IMusicService;
import com.example.laibomusic.util.MusicUtils;

public class ServiceBinder implements ServiceConnection {

	private static ServiceConnection connection;
	   
	public ServiceBinder(ServiceConnection connection) {
		this.connection = connection;
	}
	   
	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.service =  IMusicService.Stub.asInterface(service);
		if (connection != null)
			connection.onServiceConnected(name, service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		if (connection != null)
			connection.onServiceDisconnected(name);
		MusicUtils.service = null;
	}

}
