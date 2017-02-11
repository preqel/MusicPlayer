package com.example.view;

import com.example.laibomusic.R;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderList {
	
	
	public final TextView mViewHolderLineOne;
	public final TextView mViewHolderLineTwo;
	public String m_id;
    public  final  View mPlaying;
    public ViewHolderList(View view ){ 
    	this.mViewHolderLineOne= (TextView) view.findViewById(R.id.textview_lineone); 
    	this.mViewHolderLineTwo= (TextView) view.findViewById(R.id.textview_linetwo);
    	this.mPlaying  = view.findViewById(R.id.v_playing);
    } 
}
