package com.example.fragments;

import java.io.File;
import java.util.List;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.laibomusic.R;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.util.ArtistAlbumJson;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.HttpUtil;
import com.example.laibomusic.util.LRCUtils;
import com.example.laibomusic.util.MusicUtils;
import com.example.widget.LrcView;

public class LRCFragment extends Fragment {
	private LrcView lrcview;
	//private ScrollView srollview;
	private static final  int LRC_FAIL = 1;
	private static final  int LRC_SUCCESS = 2;
	private static final  int LRC_UPDATE = 3;
	private String trackname;
	private String artistname;
	private int progress = 0;
	private boolean isGECIdone = false;
	private Handler lrcHandler = new Handler(){
	
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case LRC_FAIL:
				Toast.makeText(getActivity(), "lrc fialed", Toast.LENGTH_SHORT).show();
				break;
			case LRC_SUCCESS:
				Log.d("TAG","获取lrc成功 :" + msg.obj.toString());
			    LRCUtils.AnalyzeLRC(msg.obj.toString());
				break;
			case LRC_UPDATE:
				if(msg.obj  == null) return;
	 		        int progress = (Integer) msg.obj ;
	 		    if(LRCUtils.lrclist == null	 ) return ;
	 		    if(lrcview != null)lrcview.updateIndex(progress);
	 		    
	 		    String append =  LRCUtils.getLRCStr(progress)  ;
	 		    if(append != null && append.length() > 0){
		 		   //	lrcview.append(append + "\n");
					lrcview.postInvalidate();
					Message message = lrcHandler.obtainMessage();
					message.what = LRC_UPDATE;
					message.obj = msg.obj;
				    //srollview.scrollTo(0,lrcview.getBottom());  ;
					lrcHandler.sendMessageDelayed(message, 1000 * 3);
	 		    }
				break;
			 default:break;
			
		}}
	};
	 
	
	public LRCFragment(String artistname, String trackname) {
		this.artistname = artistname;
		this.trackname = trackname;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.lrc_fragment, null);
		this.lrcview = (LrcView)view.findViewById(R.id.lrcview);
		//this.srollview = (ScrollView) view.findViewById(R.id.scrollView);
		// lrcview.setMovementMethod(ScrollingMovementMethod.getInstance());
 
		if (savedInstanceState != null) {
			String text = savedInstanceState.get("text").toString();
			lrcview.setText(text);
		}
		return view;
	}
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if(this.trackname != null){
			if (!getMusicInfoLocal(trackname))
				getMusicInfo(true);
		}
		IntentFilter intentfliter = new IntentFilter(); 
		intentfliter.addAction(Consts.ACTION_UPDATE_PROGRESS);
		intentfliter.addAction(MusicService.META_CHANGED);
		getActivity().registerReceiver(lrcReiver, intentfliter);
		super.onActivityCreated(savedInstanceState);
	}


	private class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Consts.ACTION_UPDATE_PROGRESS)) {
				  progress = intent.getIntExtra (Consts.EXTRA_PROGRESS, 0);
				  Message message = lrcHandler.obtainMessage();
				  message.what = LRC_UPDATE;
				  message.obj =  progress;
				  message.sendToTarget();
			}else if(intent.getAction().equals(MusicService.META_CHANGED)){
				    trackname = MusicUtils.getTrackName();
				    LRCUtils.clearLrcList( );
					if (!getMusicInfoLocal(trackname))
						getMusicInfo(true);
				    lrcview.invalidate();
				
			}
		}
	}
	InnerReceiver lrcReiver = new InnerReceiver();
	
	private boolean getMusicInfoLocal(String trackname2) {
		File sd = Environment.getExternalStorageDirectory(); 
		String fileParentPath = sd.getPath()+"/notes"; 
		String filePath = fileParentPath + File.separator
				+ trackname2  + ".lrc";
		return LRCUtils.getLRC(filePath) ;
	}

	private void getMusicInfo( final boolean isSave) {
		new Thread(){
			@Override
			public void run() {
				String jsonGECI = HttpUtil.getGECIjson(trackname, null);
			    List<String> lists = ArtistAlbumJson.parseLRCText(jsonGECI);
			    if(lists == null || lists.size() <= 0)
			    	return;
			    String url  = lists.get(0);
			    String result = "";
			    try {
					  result = HttpUtil.readParse(url);
				} catch (Exception e) {
					e.printStackTrace();
				}
			    if(result.isEmpty() || result.length() <= 0)
			    lrcHandler.obtainMessage(LRC_FAIL  , result ).sendToTarget();
			    lrcHandler.obtainMessage(LRC_SUCCESS , result).sendToTarget();
			    isGECIdone = true;
			    if(isSave){
			        String filepat = Environment.getExternalStorageDirectory() + File.separator + 
			        "notes" + File.separator + trackname+".lrc";
			    	LRCUtils.putLRC(result,filepat);
			    }
				super.run();
			}
			
		}.start();
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("text", lrcview.getText().toString());
		super.onSaveInstanceState(outState);
	}
	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(lrcReiver);
		super.onDestroy();
	}
}
 
