package com.example.laibomusic.cache;

import java.util.HashMap;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.example.laibomusic.MyApplication;

public class ImageCacheManager {
	
	//设置为内存的8分之一
	private static final int MEM_CACHE_SIZE = 1024* 1024 * ( (ActivityManager)MyApplication.getInstance().getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() /8 ;
	
	private static final ImageLreCache imagelrucache  = new ImageLreCache(MEM_CACHE_SIZE,"images",10 * 1024 * 1024);
	public static ImageLoader imageloader  = new ImageLoader( RequestQueueManager.mRequestQueue, imagelrucache);
	HashMap a  = null;
	public static ImageLoader.ImageListener getImageListener(final ImageView view ,final Bitmap rightbitmap ,final Bitmap errorbitmap){
		return new ImageLoader.ImageListener() {
			
			@Override
			public void onErrorResponse(VolleyError arg0) {
				if(arg0!=null)
					view.setImageBitmap(errorbitmap);
			}
			
			@Override
			public void onResponse(ImageContainer arg0, boolean arg1) {
				if(arg0.getBitmap()!= null){
					view.setImageBitmap(arg0.getBitmap());
				}else if(rightbitmap!= null){
					view.setImageBitmap(rightbitmap);
				}
				
			}
		};
	}
	
	
	public static ImageLoader.ImageContainer  loadImage(String url,ImageView imageview, Bitmap rightbitmap,
			Bitmap errorbitmap,int width,int height){
		return loadImage(url ,getImageListener(imageview,rightbitmap,errorbitmap) , width,height);
	}

	private static ImageContainer loadImage(String url,
			ImageListener imageListener, int maxwidth, int maxheight) {
		return imageloader.get(url, imageListener,maxwidth,maxheight);
	}
	
	
	
	
}
