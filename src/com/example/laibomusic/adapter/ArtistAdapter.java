package com.example.laibomusic.adapter;

 

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.WeakHashMap;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

import com.example.fragments.ArtistFragment;
import com.example.laibomusic.R;
import com.example.laibomusic.util.ImageUtil;
import com.example.view.ViewHolderArtist;

 

public class ArtistAdapter extends SimpleCursorAdapter {
	LayoutInflater mInflater ;
	private Context context;
     WeakReference<ViewHolderArtist> holderReference ;
	public ArtistAdapter(Context context, int layout, Cursor c, String[] from,
			int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
 		Cursor mcursor = (Cursor) getItem(position);
		ViewHolderArtist  viewholder ;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.gridview_items, null);
			viewholder  = new ViewHolderArtist(convertView);
			convertView.setTag(viewholder);
		} else{
			viewholder = (ViewHolderArtist) convertView.getTag();
		}
		String artistName = mcursor.getString(ArtistFragment.mArtistNameIndex); //��������
		String artist = mcursor.getString(1);
		String numberalbums = mcursor.getString(2);
		viewholder.artistname.setText(artist); 
		viewholder.numberalbums.setText("专辑数" + numberalbums);
		if(viewholder.imageview.getTag()!= null)
		viewholder.imageview.setTag(artist);
 		File sd = Environment.getExternalStorageDirectory(); 
		String path = sd.getPath()+"/notes"; 
		File file = new File(path); 
		if(!file.exists() ) 
		file.mkdir(); 
		final String fileParentPath = path;	
		
		Object [] obja =   ImageUtil.pendingImagesMap.keySet().toArray();
		for(Object s :obja)Log.i("TAG","ImageUtil.pendingImagesMap.keySet().toArray()" + s.toString());
		Set<ImageView> pendingImages = ImageUtil.pendingImagesMap.get(artist);
		if(pendingImages != null){
		}else{
			pendingImages = Collections.newSetFromMap(new WeakHashMap<ImageView, Boolean>());
			ImageUtil.pendingImagesMap.put(artist,pendingImages);
		}
		pendingImages.add(viewholder.imageview);
		viewholder.imageview.setImageDrawable(context.getResources().getDrawable(R.drawable.no_art_normal));
 		ImageUtil.loadImage(context, viewholder.imageview, null, artistName);
		return convertView;
	}
	
	
	

	 

}
