package com.example.laibomusic.util;



import static com.example.laibomusic.util.Consts.ALBUM_SUFFIX;
import static com.example.laibomusic.util.Consts.ARTIST_SUFFIX;
import static com.example.laibomusic.util.Consts.GENRE_SUFFIX;
import static com.example.laibomusic.util.Consts.PLAYLIST_SUFFIX;
import static com.example.laibomusic.util.Consts.TYPE_ALBUM;
import static com.example.laibomusic.util.Consts.TYPE_ARTIST;
import static com.example.laibomusic.util.Consts.TYPE_GENRE;
import static com.example.laibomusic.util.Consts.TYPE_PLAYLIST;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.widget.ImageView;

import com.example.laibomusic.R;
import com.example.laibomusic.entity.ImageInfo;

public class ImageUtils {


	private static  String   IMAGE_EXTENSION = ".mp3";
	public static File getImageFromMediaStore(Context context,ImageInfo imageinfo){
		String mAlumn  = imageinfo.data[0];
		String projection[] = {
				BaseColumns._ID, Audio.Albums._ID,Audio.Albums.ALBUM_ART,Audio.Albums.ALBUM
		};
		Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
		Cursor cursor = null;
		cursor = context.getContentResolver().query(uri, projection, BaseColumns._ID +"=" + DatabaseUtils.sqlEscapeString(mAlumn), null	, null);

		int column_index = cursor.getColumnIndex(Audio.Albums.ALBUM_ART);

		if(cursor.getCount() > 0){
			cursor.moveToFirst();
			String columnart = cursor.getString(column_index);
			if(columnart != null){
				try{
					File orgfile = new File(columnart);
					File newfile = new File(context.getExternalCacheDir(),createShortTag(imageinfo) + ".img");
					InputStream in = new FileInputStream(orgfile);
					OutputStream out = new FileOutputStream(newfile);
					byte[] bytes = new byte[1024];
					int len ;
					while((len = in.read(bytes)) > 0){
						out.write(bytes);
					}
					in.close();
					out.close();
					return newfile;
				}catch(Exception e ){
				}
			}
		}
		return null;

	}


	public static File getImageFromGallery(Context context,ImageInfo imageInfo){
		String albumArt = ( imageInfo.type == TYPE_ALBUM ) ? imageInfo.data[3] : imageInfo.data[1];
		if(albumArt != null){
			try{
				File orgFile = new File(albumArt);
				File newFile = new File(context.getExternalCacheDir(), createShortTag(imageInfo)+IMAGE_EXTENSION);
				InputStream in = new FileInputStream(orgFile);
				OutputStream out = new FileOutputStream(newFile);
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0){
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				return newFile;
			}
			catch( Exception e){
			}
		}
		return null;
	}


	public static Bitmap getNormalImageFromDisk(Context context,ImageInfo imageinfo){

		File nfile = getFile(context,imageinfo);
		if(nfile.exists()){
			Log.d("TAG",nfile.getAbsolutePath());
			return BitmapFactory.decodeFile(nfile.getAbsolutePath());}
		else return null;
	}


	private static File getFile(Context context, ImageInfo imageInfo) {
		return new File(context.getExternalCacheDir(), createShortTag(imageInfo) + IMAGE_EXTENSION);
	}


	public static String createShortTag(ImageInfo imageInf){ //创建短名称
		String tag = null;
		if( imageInf.type.equals(  TYPE_ALBUM ) ){ //专辑id+专辑后缀
			tag = imageInf.data[0] + ALBUM_SUFFIX;
		}
		else if (imageInf.type.equals( TYPE_ARTIST )){ //艺术家名+艺术家后缀
			tag = imageInf.data[0] + ARTIST_SUFFIX;
		}
		else if (imageInf.type.equals( TYPE_GENRE )){ //流派名+流派后缀
			tag = imageInf.data[0] + GENRE_SUFFIX;
		}
		else if (imageInf.type.equals( TYPE_PLAYLIST )){ //播放列表名+播放列表后缀
			tag = imageInf.data[0] + PLAYLIST_SUFFIX;
		}
		CharactersUtils.escapeForFileSystem(tag);
		return tag;
	}


	public static Bitmap getThumbImageFromDisk(Context context,
											   ImageInfo imageinfo,int thumbsize) {
		File nfile = getFile(context, imageinfo);
		return getThumbImageFromDisk(context,imageinfo,nfile ,thumbsize);
	}


	private static Bitmap getThumbImageFromDisk(Context context,
												ImageInfo imageinfo, File nFile,int thumbSize) {
		if(!nFile.exists())return null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try{
			BitmapFactory.decodeFile( nFile.getAbsolutePath() , options);
		}
		catch(Exception e){
		}
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > thumbSize || width > thumbSize) {
			if (width > height) {
				inSampleSize = Math.round((float)height / (float)thumbSize);
			} else {
				inSampleSize = Math.round((float)width / (float)thumbSize);
			}
			final float totalPixels = width * height;
			final float totalReqPixelsCap = thumbSize * thumbSize * 2;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		options.inSampleSize = inSampleSize;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = null;
		try{
			bitmap = BitmapFactory.decodeFile( nFile.getAbsolutePath() , options );
			Log.d("TAG","bitmap factory decode local file path:" + nFile.getAbsolutePath());
		}
		catch (Exception e){
		}
		return  bitmap;

	}


	public static void deleteDiskCache(Context context) throws IOException { //删除磁盘cache
		final File dir = context.getExternalCacheDir();
		final File[] files = dir.listFiles();
		for (final File file : files) {
			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}


	public static File getImageFromWeb(Context context, ImageInfo imageinfo) {
//		String imgURL = "";
//		if(imageinfo.type.equals(TYPE_ALBUM)){
//		     Album album = Album.getInfo(imageinfo.data[0],imageinfo.data[1],LASTFM_API_KEY);
//			 
//		}else if(imageinfo.type.equals(TYPE_ARTIST)){
//		}
		return null;
	}

	public  void setLoadedBitmap(ImageView imageView, Bitmap bitmap, String tag) {//设置载入的bitmap
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


}
