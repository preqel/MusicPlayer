package com.example.helpers;

import static com.example.laibomusic.util.Consts.SIZE_NORMAL;
import static com.example.laibomusic.util.Consts.SIZE_THUMB;
import static com.example.laibomusic.util.Consts.*;

import java.io.File;
import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.laibomusic.R;
import com.example.laibomusic.entity.ImageInfo;
import com.example.laibomusic.util.ImageUtils;
/*
异步加载图片，先从
 */
public class GetBitmapTask extends AsyncTask<String, Integer, Bitmap> {

	private WeakReference<OnBitmapReadyListener> mListenerReference;
	private WeakReference<Context> mContextReference;
	private   ImageInfo  imageinfo;
	int thumbSize;
	
	public GetBitmapTask(int thumbSize, ImageInfo image, OnBitmapReadyListener bitmapreadylistener ,Context context){
		  mListenerReference = new WeakReference(bitmapreadylistener);
		  mContextReference = new WeakReference(context);
		  this.thumbSize = thumbSize;
		  imageinfo =  image ;
	}
	
	@Override
	protected Bitmap doInBackground(String... params) {
		Context context = mContextReference.get();
		if( context == null ) return null;
		
		File nfile = null;
		
		if(imageinfo.source .equals(SRC_FILE) && !isCancelled()){
			nfile = ImageUtils.getImageFromMediaStore(context, imageinfo);
			
		}else if(imageinfo.source.equals(SRC_LASTFM) && !isCancelled() ){
		    nfile = null;	
		}else if(imageinfo.source.equals(SRC_GALLERY) && !isCancelled()){
			nfile = ImageUtils.getImageFromGallery(context,imageinfo);
			
		}else if("first_avail".equals(imageinfo.source) && !isCancelled() ){
			Bitmap bitmap = null;
			if(imageinfo.size.equals(SIZE_NORMAL)){
				bitmap = ImageUtils.getNormalImageFromDisk (context,imageinfo);
			}else if(imageinfo.size.equals(SIZE_THUMB)){
				bitmap = ImageUtils.getThumbImageFromDisk(context,imageinfo,thumbSize);
			}
			if(bitmap != null) return bitmap;
			if(imageinfo.type .equals(TYPE_ALBUM))
				nfile = ImageUtils.getImageFromMediaStore(context, imageinfo);
			if(nfile == null && (imageinfo.type.equals(TYPE_ALBUM) || imageinfo.type.equals(TYPE_ARTIST)))
			   nfile = ImageUtils.getImageFromWeb( context, imageinfo );
			
			}
			if(nfile != null){
				if(imageinfo.size.equals(SIZE_NORMAL))return BitmapFactory.decodeFile(nfile.getAbsolutePath());
				return ImageUtils.getThumbImageFromDisk(context, imageinfo, thumbSize);
			}
		return null;
	}
	
	
	@Override
	protected void onPostExecute(Bitmap result) {
	 
		super.onPostExecute(result);
		OnBitmapReadyListener mlistener  = mListenerReference.get();
		if(result == null && !isCancelled()){
			if(imageinfo.size.equals(SIZE_NORMAL))
				
			result = BitmapFactory.decodeResource(mContextReference.get().getResources(), R.drawable.no_art_normal );
			else if(imageinfo.size.equals(SIZE_THUMB))
				result = BitmapFactory.decodeResource(mContextReference.get().getResources(), R.drawable.no_art_small);
		}
		if(result != null && !isCancelled()){
			if( mlistener != null)
				mlistener.bitmapReady(result, ImageUtils.createShortTag(imageinfo) + imageinfo.size);
		}
		
	}


	public static interface OnBitmapReadyListener {
		public void bitmapReady(Bitmap bitmap, String tag);
	}
}
