package com.example.fragments;

 
 

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.laibomusic.R;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.util.MusicUtils;
import com.example.widget.BottomActionBar;

public class BottomActionBarFragment extends Fragment  {
	
	private ImageButton mPre,mNext,mPlay;
    private BottomActionBar mBottomActionbar ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rot = inflater.inflate (R.layout.bottom_action_bar, container);
		this.mPre=(ImageButton) rot.findViewById(R.id.bottom_action_bar_previous);
		this.mPlay=(ImageButton) rot.findViewById(R.id.bottom_action_bar_play);
		this.mNext=(ImageButton) rot.findViewById(R.id.bottom_action_bar_next);
		this.mBottomActionbar = (BottomActionBar) rot.findViewById(R.id.bottomactionbar);
		
		// pre
		mPre.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				if(MusicUtils.service == null) return ;
				try {
					MusicUtils.service.prev();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			 
			 
			}});
		// next
		mNext.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(MusicUtils.service == null)return ;
				try {
					MusicUtils.service.next();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			 
			}});
		mPlay.setOnClickListener(new OnClickListener(){ 
			@Override
			public void onClick(View v) {
//				Intent intent;
//				if (MusicPlaying.isplaying){
//					intent = new Intent(Consts.ACTION_PAUSE);
//				    MusicPlaying.isplaying = false;
//				}
//				else
//				{
//					intent = new Intent(Consts.ACTION_PLAY); 
//					MusicPlaying.isplaying = true;
//				}
//				getActivity().sendBroadcast(intent); 
				doPauseResume();
				setPauseButtonImage();
				
			}

			});
		return rot;
	}
	
	private void setPauseButtonImage() {
		try{
			if (MusicUtils.service != null
					&& MusicUtils.service.isplaying()) {
				 
				mPlay.setImageResource (R.drawable.kingsly_holo_light_pause);
			}
			else mPlay.setImageResource(R.drawable.kingsly_holo_light_play);
		}catch(RemoteException e ){
			e.printStackTrace();
		}
		
	}

	/**
	 * 播放/暂停
	 */
	protected void doPauseResume() {
		if (MusicUtils.service == null)
			return;
		try {
			if (MusicUtils.service.isplaying())
				MusicUtils.service.pause();
			else
				MusicUtils.service.play();

		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}



	@Override
	public void onStart() {
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(MusicService.META_CHANGED);
		//intentfilter.addAction(action);
		getActivity().registerReceiver(mMediaStateReceiver, intentfilter);
		super.onStart();
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mMediaStateReceiver);
		super.onStop();
	}


	private BroadcastReceiver mMediaStateReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			mBottomActionbar.updateBottomActionBar(getActivity());
			setPauseButtonImage();
		}
	};
	

}
