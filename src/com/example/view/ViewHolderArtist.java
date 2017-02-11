package com.example.view;

import com.example.laibomusic.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderArtist {

	public ImageView imageview ;
	public TextView artistname;
	public TextView numberalbums;
	public   ViewHolderArtist(View view){
		this.imageview = (ImageView) view.findViewById(R.id.gridview_iamgeview);
		this.artistname = (TextView) view.findViewById(R.id.gridview_line_one);
		this.numberalbums = (TextView) view.findViewById(R.id.gridview_line_two);
	}
	
}
