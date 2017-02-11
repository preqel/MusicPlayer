package com.example.view;

import com.example.laibomusic.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHolderGrid {

	public ImageView mViewHolder;
	public TextView HolderLineOne;
	public TextView HolderLineTwo;
	public LinearLayout HolderLinear;
	
	public ViewHolderGrid (View view){
		this.HolderLineOne  = (TextView) view.findViewById(R.id.gridview_line_one);
		this.HolderLineTwo = (TextView) view.findViewById(R.id.gridview_line_two);
		this.mViewHolder = (ImageView) view.findViewById(R.id.gridview_iamgeview);
	}
	
}
