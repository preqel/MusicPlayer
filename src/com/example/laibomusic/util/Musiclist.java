package com.example.laibomusic.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.example.laibomusic.entity.Music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.Media;

public class Musiclist {
	private ContentResolver sr;
	private final static long[] sEmptyList = new long[0];
	private static SimpleDateFormat simpledataformat = new SimpleDateFormat("mm:ss");

	public Musiclist(Context context) {
		this.sr = context.getContentResolver();
	}
	public ArrayList<Music> getMusicList() {
		ArrayList<Music> arraylist = new ArrayList<Music>();

		Cursor c = sr.query(Media.EXTERNAL_CONTENT_URI, null, null, null,
				MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if (c == null)
			return null;
		c.moveToFirst();

		if(c.moveToFirst()){
			do{
				Music music = new Music();
				String title = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String id =  c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID));
				String uril = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DATA));

				String singer = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				Long time  = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
				String name = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				String sbr = name.substring(name.length() - 3, name.length());
				if (sbr.equals("mp3")) {
					music.setName(name);
					music.setSinger(singer);
					music.setTitle(title);
					music.setUrl(uril);
					music.setId(id);
					music.setTime(time);
					arraylist.add(music);
				}
			}while(c.moveToNext());
		}
		return arraylist;
	}
	//媒体扫描ContentResolver方法
	public static Cursor query(Context context, Uri uri, String[] projection, String selection,
							   String[] selectionArgs, String sortOrder, int limit) {
		try {
			ContentResolver resolver = context.getContentResolver();
			if (resolver == null) {
				return null;
			}
			if (limit > 0) {
				uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
			}
			return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
		} catch (UnsupportedOperationException ex) {
			return null;
		}
	}


	public static Cursor query(Context context, Uri uri, String[] projection, String selection,
							   String[] selectionArgs, String sortOrder) {
		return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
	}


	public static String formatTime(long time){
		return simpledataformat.format(new Date(time));
	}


	public static long[] getSonglistForCursor(Cursor cursor ){
		if (cursor == null) {
			return sEmptyList;
		}
		int len = cursor.getCount();
		long[] list = new long[len];
		cursor.moveToFirst();
		int colidx = -1;
		try {
			colidx= cursor.getColumnIndexOrThrow(Audio.Playlists.Members._ID);
		} catch (IllegalArgumentException e) {
			colidx=cursor.getColumnIndexOrThrow(BaseColumns._ID);
		}
		for(int i= 0;i < len;i ++){
			list[i]= cursor.getLong(colidx);
			cursor.moveToNext();
		}
		return list;

	}


}