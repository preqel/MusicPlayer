package com.example.laibomusic.cache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.laibomusic.MyApplication;

public class RequestQueueManager {
	public static RequestQueue mRequestQueue  = Volley.newRequestQueue(MyApplication.getInstance());

	public static void addRequest(Request<?> request, Object object) {
		if (object != null) {
			request.setTag(object);
		}
		mRequestQueue.add(request);
	}

	public static void cancelAll(Object tag) {
		mRequestQueue.cancelAll(tag);
	}
}
