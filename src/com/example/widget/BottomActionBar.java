package com.example.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.laibomusic.MusicPlaying;
import com.example.laibomusic.R;
import com.example.laibomusic.util.MusicUtils;



public class BottomActionBar extends LinearLayout   implements OnClickListener ,OnLongClickListener   {

	public BottomActionBar(Context context){
		super(context);
	}
	
	
	public BottomActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	    setOnLongClickListener(this);
	} 
	
	
	 /**
     * Updates the bottom ActionBar's info
     * 
     * @param activity
     * @throws RemoteException
     */
    public void updateBottomActionBar(Activity activity) { // Updates the bottom ActionBar's info
    	
    	View bottomactionbar = activity.findViewById(R.id.bottomactionbar);
    	if(bottomactionbar == null	)return ;
    	
    	if(MusicUtils.service != null && MusicUtils.getCurrentAudioId() != -1){
			TextView mArtistName = (TextView) findViewById(R.id.bottom_action_bar_artist_name);
			mArtistName.setText(MusicUtils.getArtistName()); 
			TextView mTrackName = (TextView) findViewById(R.id.bottom_action_bar_track_name);
			//MusicUtils.service.get
			mTrackName.setText(MusicUtils.getTrackName());
    	}
    	
    }
    
    
	@Override
	public boolean onLongClick(View v) {
		return false;
	}
	
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		intent.setClass(getContext(), MusicPlaying.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		v.getContext().startActivity(intent);
	}

}
