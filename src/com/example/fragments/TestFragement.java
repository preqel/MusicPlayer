package com.example.fragments;

import java.util.ArrayList;

import com.example.laibomusic.MyApplication;
import com.example.laibomusic.R;
 
import com.example.laibomusic.entity.Music;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.Musiclist;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class TestFragement extends Fragment {

	private ImageButton btn1, btn2, btn3 ;
	private ArrayList<Music> lists;
	private ImageButton previous;
	private ImageButton paly;
	private ImageButton next;
	MyApplication myapplication;
	private TextView tvsinger;
	private TextView tvname;
	private TextView starttime;
	private TextView endtime; 
	public boolean isplaying;
	public static int id;
	private InnerReceiver innerreceiver;
	private SharedPreferences ps;
	private SeekBar seekbar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
	 
	}

	
	OnClickListener   MyOnClickListener = new  OnClickListener(){

		@Override
		public void onClick(View v) {
			doImageButtonClick(v);
			
			
		}};
	
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.music, container,false); 
		this.tvname = (TextView) view.findViewById(R.id.textview1);
		this.tvsinger = (TextView) view.findViewById(R.id.textview2);
		this.previous = (ImageButton) view.findViewById(R.id.ib_prev);
		this.paly = (ImageButton) view.findViewById(R.id.ib_play);
		this.next = (ImageButton) view.findViewById(R.id.ib_next);
		this.previous.setOnClickListener(MyOnClickListener);
		this.paly.setOnClickListener(MyOnClickListener);
		this.next.setOnClickListener(MyOnClickListener);
		this.seekbar = (SeekBar) view.findViewById(R.id.music_progress);
		this.myapplication = (MyApplication) this.getActivity()
				.getApplication();
		this.lists = new Musiclist(this.getActivity()).getMusicList();
		this.innerreceiver = new InnerReceiver();
		IntentFilter intentfliter = new IntentFilter();
		intentfliter.addAction(Consts.ACTION_CURRENT_MUSIC_CHANGED);
		intentfliter.addAction(Consts.ACTION_UPDATE_PROGRESS);
		intentfliter.addAction(Consts.ACTION_RESPONSE_PLAY_STATE);
		intentfliter.addAction(Consts.ACTION_CURRENT_MUSIC_CHANGED_ITEM);
		this.getActivity().registerReceiver(innerreceiver, intentfliter);
		setupView();
		addOnListener();
		
		return  view;
	}
	private void addOnListener() {
		
	}

	private void setupView() {
		
	}
	
	public void doImageButtonClick(View v)
	{
		Intent intent =null;
		switch(v.getId())
		{
		
		case R.id.ib_prev :
			intent= new Intent(Consts.ACTION_PREVIOUS);
			
			break;
		case R.id.ib_play:
			
			if(this.isplaying){
			intent = new Intent(Consts.ACTION_PAUSE);
			this.isplaying=false;
			intent.putExtra(Consts.ITEM_ID,this.id );
			this.paly.setImageResource(R.drawable.pause1); 
			}
			else
			{ 
				intent = new Intent(Consts.ACTION_PLAY);
				this.isplaying=true;
				intent.putExtra(Consts.ACTION_PAUSE,this.id);
		     	this.paly.setImageResource(R.drawable.play1);
				
			}
			
			break;
		case R.id.ib_next:
			intent = new Intent(Consts.ACTION_NEXT);
			break;
		}
		
	}
	private class InnerReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Consts.ACTION_CURRENT_MUSIC_CHANGED.equals(action)) {

//				 Music m = myapplication.getCurrentMusic();
//				tvname.setText(m.getName());
//				tvsinger.setText(m.getSinger());
			}
			
		}
		
		
		
	}
	

}
