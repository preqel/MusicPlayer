package com.example.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.example.helpers.GetBitmapTask;
import com.example.helpers.GetBitmapTask.OnBitmapReadyListener;
import com.example.laibomusic.R;
import static  com.example.laibomusic.util.Consts.*;
import com.example.laibomusic.entity.ImageInfo;
import com.example.laibomusic.util.ImageUtils;

public class ImageProvider implements  OnBitmapReadyListener{

	
	private ImageCache memCache;
	private HashMap<String,Set<ImageView> > pendingImagesMap = new HashMap<String,Set<ImageView>>();
	private Set<String> unavailable =new HashSet<String>();
	private Context mcontext;
	private int thumbSize;
	public static ImageProvider mInstance;
	protected  ImageProvider(Activity act) {
		mcontext  = act;
		memCache = ImageCache.getInstance(mcontext);
		Resources res = mcontext.getResources();
		DisplayMetrics displaymetric  = res.getDisplayMetrics();
		thumbSize =  (int) (153*(displaymetric.densityDpi/160f) +0.5f);
		
	}
	
	public static ImageProvider getInstance(Activity act){
		if (mInstance == null) {

			mInstance = new ImageProvider(act);
			mInstance.setImageCache(ImageCache.findOrCreateCache(act));
		}
		 return mInstance;
	}
	
    public void setImageCache(final ImageCache cacheCallback) { //设置缓存
    	memCache = cacheCallback;
    }
	
    public void loadImage( ImageView imageView, ImageInfo imageInfo ){  //加载图片到imageview
    	String tag = ImageUtils.createShortTag(imageInfo) + imageInfo.size;
    	if( imageInfo.source.equals(SRC_FILE) || imageInfo.source.equals(SRC_LASTFM) || imageInfo.source.equals(SRC_GALLERY)){
    		clearFromMemoryCache( ImageUtils.createShortTag(imageInfo) );
    		asyncLoad( tag, imageView, new GetBitmapTask( thumbSize, imageInfo, this, imageView.getContext() ) );
		}
    	if(!setCachedBitmap(imageView, tag)){
            asyncLoad( tag, imageView, new GetBitmapTask( thumbSize, imageInfo, this, imageView.getContext() ) );
        }
    }

    private boolean setCachedBitmap(ImageView imageView, String tag) { 
        if (unavailable.contains(tag)) {
            handleBitmapUnavailable(imageView, tag);
            return true;
        }
        Bitmap bitmap = memCache.get(tag);
        if (bitmap == null)
            return false;
        imageView.setTag(tag);
        imageView.setImageBitmap(bitmap);
        return true;
    }

    private void handleBitmapUnavailable(ImageView imageView, String tag) {//���� Ϊ��ȡ��bitmap
        imageView.setTag(tag);
        imageView.setImageDrawable(null);
    }

    private void setLoadedBitmap(ImageView imageView, Bitmap bitmap, String tag) {//���������bitmap
        if (!tag.equals(imageView.getTag()))
            return;

        final TransitionDrawable transition = new TransitionDrawable(new Drawable[]{
                new ColorDrawable(android.R.color.transparent),
                new BitmapDrawable(imageView.getResources(), bitmap)
        });

        imageView.setImageDrawable(transition);
        final int duration = imageView.getResources().getInteger(R.integer.image_fade_in_duration);
        transition.startTransition(duration);
    }

    private void asyncLoad(String tag, ImageView imageView, AsyncTask<String, Integer, Bitmap> task) { //�첽����
        Set<ImageView> pendingImages = pendingImagesMap.get(tag);
        if (pendingImages == null) {
            pendingImages = Collections.newSetFromMap(new WeakHashMap<ImageView, Boolean>()); //����  weak set
            pendingImagesMap.put(tag, pendingImages);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        pendingImages.add(imageView);
        imageView.setTag(tag);
        imageView.setImageDrawable(null);
    }

    @Override
    public void bitmapReady(Bitmap bitmap, String tag) {
        if (bitmap == null) {
            unavailable.add(tag);
        }
        else
        {
            memCache.add(tag, bitmap);
        }
        Set<ImageView> pendingImages = pendingImagesMap.get(tag);
        if (pendingImages != null) {
            pendingImagesMap.remove(tag);
            for (ImageView imageView : pendingImages) {
                setLoadedBitmap(imageView, bitmap, tag);
            }
        }
    }
    
    public void clearFromMemoryCache(String tag){     //�����ڴ�
        if (unavailable.contains(tag + SIZE_THUMB)) {
        	unavailable.remove(tag + SIZE_THUMB);
        }
        if (pendingImagesMap.get(tag + SIZE_THUMB)!=null){
        	pendingImagesMap.remove(tag + SIZE_THUMB);
        }
        if (memCache.get(tag + SIZE_THUMB)!=null){
        	memCache.remove(tag + SIZE_THUMB);
        }
        if (unavailable.contains(tag + SIZE_NORMAL)) {
        	unavailable.remove(tag + SIZE_NORMAL);
        }
        if (pendingImagesMap.get(tag + SIZE_NORMAL)!=null){
        	pendingImagesMap.remove(tag + SIZE_NORMAL);
        }
        if (memCache.get(tag + SIZE_NORMAL)!=null){
        	memCache.remove(tag + SIZE_NORMAL);
        }
    }
    
    public void clearAllCaches(){   //�������е�cache
    	try{
    		ImageUtils.deleteDiskCache(mcontext);
    		memCache.clearMemCache();
    	}
    	catch(Exception e){}
    }

}
