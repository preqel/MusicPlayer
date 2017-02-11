package com.example.fragments;

 
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.laibomusic.MusicPlaying;
import com.example.laibomusic.R;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.util.ArtistAlbumJson;
import com.example.laibomusic.util.HttpUtil;
import com.example.laibomusic.util.ImageUtil;
import com.example.laibomusic.util.MusicUtils;

public class PICFragment extends Fragment {
	
	private ImageView mDisplay;
	private TextView mName;
	private TextView mArtistOrAlbumName;
	List<String> urlList;
	private static boolean misAlive = false;
	public static boolean mNeedImgUpdate  = true;
	private static final int UPDATEINFO = 1;
	protected static final int UPDATEERROR = 2;
	
	private class ImgHandler extends Handler {
		private final WeakReference<MusicPlaying> mActivity;

		public ImgHandler(MusicPlaying act) {
			this.mActivity = new WeakReference<MusicPlaying>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			MusicPlaying m = mActivity.get();
			if (m != null) {
				if(!misAlive) return;
				switch (msg.what) {
				case UPDATEINFO:
					// to-do preqel
					// 目前从网络上加载都是直接从多个url当中获取过来，然后全部遍历，以后有时间可以优化下怎样当一个url失效时再去加载另外一个url
					if (msg.obj != null )
						urlList = (ArrayList<String>) msg.obj;
					updateUIandImage();
					break;
				case UPDATEERROR:
					Toast.makeText(getActivity(),
							R.string.picfragment_update_error,
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		}
	}
	
	ImgHandler imgHandler = null;
	private String artistname;
	private String albumname;
	private String trackname;
	
	public PICFragment(String artistname, String albumname, String trackname) {
		this.artistname = artistname;
		this.albumname = artistname;
		this.trackname = trackname;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater .inflate(R.layout.pic_fragment,
				null);
		mDisplay = (ImageView) view.findViewById(R.id.myimageview);
		mName = (TextView)view. findViewById(R.id.textview1);
	    mArtistOrAlbumName = (TextView) view.findViewById(R.id.textview2);
		return view;
	}
 
	
	  @Override
	public void onActivityCreated(Bundle savedInstanceState) {
		  loadPic();
		  mNeedImgUpdate = true ;
		  imgHandler = new ImgHandler((MusicPlaying)getActivity());
		  IntentFilter intentfilter = new IntentFilter();
		  intentfilter.addAction(MusicService.META_CHANGED);
		  getActivity().registerReceiver(mStatusListener, new IntentFilter(intentfilter));
		  super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		 updateUI();
		super.onStart();
	}


	@Override
	public void onResume() {
		super.onResume();
		misAlive = true;
	}

	@Override
	public void onStop() {
		misAlive = false;
		super.onStop();
	}

	/**
	 * �����߳�ȥ��������ͼƬ
	 */
	private void loadPic() {
		new Thread(new getPicUrls()).start();
	}

	private class getPicUrls implements Runnable {
		@Override
		public void run() {
			try {
				String responseText = HttpUtil.getArtistAlbumn(artistname);
				List<String> lists = ArtistAlbumJson.parseMusicText(artistname,
						responseText);
				//to-do finally 
			    if(lists!= null && lists.size() >0 ) Log.d("TAG","��õ�����ͼƬurl��ַ������"+ lists.size());
					imgHandler.obtainMessage(UPDATEINFO, lists).sendToTarget();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {

			}
		}
	}
	
	
	/**
     * Update what's playing and Picture in the middle
     * 
     * 
     *   * 
     * 
       //        String artistName = MusicUtils.service.();
//        String albumName = MusicUtils.getAlbumName();
//        String trackName = MusicUtils.getTrackName();
//        String albumId = String.valueOf(MusicUtils.getCurrentAlbumId());
//        mTrackName.setText(trackName);
//        mAlbumArtistName.setText(albumName + " - " + artistName);
//        mDuration = MusicUtils.getDuration();
//        mTotalTime.setText(MusicUtils.makeTimeString(getActivity(), mDuration / 1000));
//
//        ImageInfo mInfo = new ImageInfo();
//        mInfo.type = TYPE_ALBUM;
//        mInfo.size = SIZE_THUMB;
//        mInfo.source = SRC_FIRST_AVAILABLE;
//        mInfo.data = new String[]{ albumId , artistName, albumName };
//        
//        ImageProvider.getInstance( getActivity() ).loadImage( mAlbumArt, mInfo );
//        // Theme chooser
//        ThemeUtils.setTextColor(getActivity(), mTrackName, "audio_player_text_color");
//        ThemeUtils.setTextColor(getActivity(), mAlbumArtistName, "audio_player_text_color");
//        ThemeUtils.setTextColor(getActivity(), mTotalTime, "audio_player_text_color");
//
//
//		File sd = Environment.getExternalStorageDirectory(); 
//		String path = sd.getPath()+"/notes"; 
//		File file = new File(path); 
//		if(!file.exists()) 
//		file.mkdir(); 
//		final String fileParentPath = path;
      */
    private void updateUIandImage() {
        if (MusicUtils.service == null)  
            return;
        updateUI(); 

        try {
			artistname = MusicUtils.service.getArtistName();
			Log.d("TAG","updateMusicInfoAndPic���û�ȡ���ĸ�����" + artistname);
		} catch (RemoteException e) {
			artistname = "<unknown>";
			e.printStackTrace();
		}
		mDisplay.setTag(artistname);
		if(urlList == null ) {
			Log.d("TAG","updateMusicInfoAndPic  urlList ������Ϊ  0" + "mNeedImgUpdate:" + mNeedImgUpdate );
			ImageUtil.loadImage(getActivity(), mDisplay, "" , artistname);
		    return;
		}else {
			ImageUtil.loadImage(getActivity(), mDisplay , urlList.get(0),artistname);
		}
	}
    
	/**
	 * Update what's playing   
	 */
	private void updateUI(){
		if (MusicUtils.service == null) {
            return;
        }
		String artistname_ = "";
		String albumname_ =" ";
		String tracknme_ =" ";
		try {
			artistname_ = MusicUtils.service.getArtistName();
			albumname_ = MusicUtils.service.getAlbumName();
			tracknme_ = MusicUtils.service.getTrackName();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		mArtistOrAlbumName.setText("-- " + artistname_ + "" + albumname_ + " --");
		mName.setText(tracknme_);
		if(getActivity() != null){
		  TextView mTitle  = (TextView) getActivity().findViewById(R.id.title_middle_text);
		  if(mTitle != null)
			  mTitle.setText(tracknme_);
		}
		
    }

	/**
     * ��ʱ����ͼƬ
     */
    private Runnable myRunnable = new Runnable() {
		public void run() {
			int length = urlList.size();
			if (length == 0) {
				return;
			}
			int index = new Random().nextInt(length);
			imgHandler.postDelayed(this, 1000* 30);
		}
	};

	private BroadcastReceiver mStatusListener = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(MusicService.META_CHANGED) &&  misAlive)
				updateUIandImage();
		}
	};
	
	
	@Override
	public void onDestroy() {
		misAlive = false;
		imgHandler.removeMessages(UPDATEINFO);
		getActivity().unregisterReceiver(mStatusListener);
		super.onDestroy();
	}


	@Deprecated
	private boolean isURLok(String url) {
		boolean value = false;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url)
					.openConnection();
			int code = connection.getResponseCode();
			if (code != 200) {
				value = false;
			} else {
				value = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("TAG", "���url" + url + "value:" + value);
		return value;
	}
	
}
