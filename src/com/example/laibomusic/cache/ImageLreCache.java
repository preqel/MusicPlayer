package com.example.laibomusic.cache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import libcore.io.DiskLruCache;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.example.laibomusic.MyApplication;

public class ImageLreCache extends LruCache<String, Bitmap>  implements ImageLoader.ImageCache{


	private DiskLruCache disklrucache ;
	private String cacheFoler ;
	private int DISK_CACHE_SIZE ;
	public ImageLreCache(int memCacheSize, String string, int i) {
      	super(memCacheSize);
      	this.cacheFoler= string;
      	this.DISK_CACHE_SIZE = i;
      	try {
			disklrucache =    DiskLruCache.open (getDiskCacheDir(MyApplication.getInstance(),cacheFoler),getAppVersion(MyApplication.getInstance()), 1 , DISK_CACHE_SIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
      	
	}
	
	@Override
	protected int sizeOf(String key, Bitmap value) {
		if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.HONEYCOMB_MR1)
			return value.getByteCount();
		else return value.getRowBytes() * value.getHeight();
	}
	
	private int getAppVersion(MyApplication instance) {
		 
		try {
			 PackageInfo packageinfo = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);
			 return packageinfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	public static File getDiskCacheDir (Context context,String Foldername){
		String cachepath ;
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageDirectory()) && !Environment.isExternalStorageRemovable()){
			cachepath = context.getExternalCacheDir().getPath();
		}{
		cachepath = context.getCacheDir().getPath();
		}
		Log.d("TAG","cachepath:" + cachepath + "Foldername:" + Foldername);
		return new File(cachepath + File.separator + Foldername);
	}

	@Override
	public Bitmap getBitmap(String s) {
		String key = hashKeyForDisk(s);
		try {
			if(disklrucache.get(key) == null){
				return get(s); 
			}else {
				DiskLruCache.Snapshot snapshot = disklrucache.get(key);
			    Bitmap bitmap = null;
			    if(snapshot != null){
			    	 bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
			    }
			    return bitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void putBitmap(String arg0, Bitmap bitmap) {
		put(arg0, bitmap);
		String key = hashKeyForDisk(arg0);
		try {
			if(null == disklrucache.get(key) ){
				DiskLruCache.Editor editor =   disklrucache.edit(key);
				if(editor!= null){
					OutputStream outputstream = editor.newOutputStream(0);
					if(bitmap.compress(CompressFormat.JPEG, 100, outputstream)){
						editor.commit();
					}else {
						editor.abort();
					}
				}
			disklrucache.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String hashKeyForDisk(String key){
		String cachekey;
		try {
			MessageDigest messagedidgest = MessageDigest.getInstance("MD5");
			messagedidgest .update(key.getBytes());
			cachekey = bytesToHexString(messagedidgest.digest());
		} catch (NoSuchAlgorithmException e) {
			cachekey = String.valueOf(key.hashCode());
			e.printStackTrace();
		}
		return cachekey;
	}
	
	
	private String bytesToHexString(byte[] bytes) {  
        StringBuilder sb = new StringBuilder();  
        for (int i = 0; i < bytes.length; i++) {  
            String hex = Integer.toHexString(0xFF & bytes[i]);  
            if (hex.length() == 1) {  
                sb.append('0');  
            }  
            sb.append(hex);  
        }  
        return sb.toString();  
    }  
}
