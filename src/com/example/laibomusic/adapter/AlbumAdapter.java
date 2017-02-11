package com.example.laibomusic.adapter;

import com.example.cache.ImageProvider;
import com.example.laibomusic.R;
import com.example.laibomusic.adapter.MusicAdapter.ViewHolder;
import com.example.laibomusic.entity.ImageInfo;
import com.example.view.ViewHolderGrid;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import static com.example.laibomusic.util.Consts.*;

public class  AlbumAdapter extends SimpleCursorAdapter {
	
	private Context context;
	private ImageProvider imageprovider ;
	
	public AlbumAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
		this.context = context;
		imageprovider = ImageProvider.getInstance( (Activity) context );
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parnent) {
		final View view =  super.getView(position, convertView, parnent);
		Cursor cursor = (Cursor) getItem(position);
		ViewHolderGrid viewholder ;
		if(view != null){
			viewholder = new ViewHolderGrid(view);
		    view.setTag(viewholder);
		}else 
			viewholder = (ViewHolderGrid) view.getTag();
		String albumid = cursor.getString(0);
		String  album= cursor.getString(1);
		String artistname= cursor.getString(2);
		TextView textview1  = (TextView) view.findViewById(R.id.gridview_line_one);
		TextView textview2 = (TextView) view.findViewById(R.id.gridview_line_two);
		textview1.setText(artistname);
		textview2.setText(album);
		ImageInfo imageinfo = new ImageInfo();
		imageinfo.type = TYPE_ALBUM;
		imageinfo.size = SIZE_THUMB;
		imageinfo.source = SRC_FIRST_AVAILABLE;
		imageinfo.data  = new String[]{albumid ,artistname, album  };
		Log.d("TAG","albumid:" + albumid + "artistname:" + artistname
				+ "album:" + album);
		imageprovider.loadImage( viewholder.mViewHolder, imageinfo );
		return view;
	}

}
