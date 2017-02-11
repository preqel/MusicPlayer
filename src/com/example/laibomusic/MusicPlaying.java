package com.example.laibomusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragments.LRCFragment;
import com.example.fragments.PICFragment;
import com.example.laibomusic.adapter.PagerAdapter;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.service.ServiceToken;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.MusicUtils;
import com.example.laibomusic.util.TimeUtil;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
 

public class MusicPlaying extends Activity implements ServiceConnection {

	private ImageButton mPrev , mPlay , mNext ;
	private ViewPager mViewPager;
	private ImageView mRepeat,mFavourite;
	private TextView mName,starttime,endtime,mArtistOrAlbumName;
	private long duration;
	private InnerReceiver innerreceiver;
	private SeekBar seekbar;
	private ServiceToken mToken;
	private long mPosOverride;
	protected boolean mFromTouch ;
	protected long mLastSeekEventTime;
	 
 
    private static final int CHANGEMUSIC = 1;
    private static final int UPDATEINFO = 2;
    private static final int TIMEUPDATE = 3;

   
	private Handler mhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case CHANGEMUSIC:
					break;
//				case TIMEUPDATE:
//					 if(urlList.size() != 0){
//						 updateMusicInfoAndPic();
//					 }
//					break;
			}
			super.handleMessage(msg);
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.music);
 	    Bundle bundle = getIntent().getExtras();
		initView();
		initTitleBar();
		 
		Intent intent1 = new Intent(this, MusicService.class);
		startService(intent1);
		innerreceiver = new InnerReceiver();
		setRepeatButtonImage();
		setFavourteButtonImage(true);
        //broadcast'receiver
		IntentFilter intentfliter = new IntentFilter(); 
 		intentfliter.addAction(Consts.ACTION_UPDATE_PROGRESS);
		intentfliter.addAction(MusicService.META_CHANGED);
		registerReceiver(innerreceiver, intentfliter);  
		String url = Consts.imageThumbUrls[0];
	//	SkinMessage skinMessage = (SkinMessage) msg.obj;
	}   





	private void initTitleBar() {
		ImageView mUpImg = (ImageView) findViewById(R.id.title_up_img);
		mUpImg.setVisibility(View.VISIBLE);
		mUpImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
        mRepeat = (ImageView) findViewById(R.id.ib_repeat);
        mRepeat.setVisibility(View.VISIBLE);
       
	}
	
	
	private void initView() {
		
		this.mPrev = (ImageButton) findViewById(R.id.ib_prev);
		this.mPlay = (ImageButton) findViewById(R.id.ib_play);
		this.mRepeat =  (ImageView) findViewById(R.id.ib_repeat);
		this.mNext = (ImageButton) findViewById(R.id.ib_next);
		this.endtime = (TextView) findViewById(R.id.endtime);
		this.starttime = (TextView) findViewById(R.id.starttime);
		this.mFavourite = (ImageButton) findViewById(R.id.img_favourite);
		this.seekbar = (SeekBar) findViewById(R.id.music_progress);
		this.seekbar.setOnSeekBarChangeListener(mSeekListener);
		this.mViewPager  = (ViewPager) findViewById(R.id.viewpager);
		
	    PagerAdapter pageradapter = new PagerAdapter(getFragmentManager());
	    String artistname = MusicUtils.getArtistName();
    	String albumname  = MusicUtils.getAlbumName();
    	String trackname  = MusicUtils.getTrackName();
    	
	    pageradapter.addFragment(new PICFragment(artistname,albumname,trackname));
	    pageradapter.addFragment(new LRCFragment(artistname,trackname));
		this.mViewPager.setAdapter(pageradapter);
		
		RelativeLayout rl_title = (RelativeLayout) findViewById(R.id.title_bar);
		ViewHelper.setTranslationY(rl_title, -rl_title.getHeight() + 25);

		ViewHelper.setY(rl_title, -100);
		ViewPropertyAnimator.animate(rl_title)
				.translationY(rl_title.getHeight() + 50).yBy(100)
				.setInterpolator(new DecelerateInterpolator()).setDuration(500)
				.setStartDelay(0).start();

		LinearLayout rl_bottom = (LinearLayout) findViewById(R.id.ll_one);
		ViewHelper.setTranslationY(rl_bottom, rl_bottom.getHeight() + 200);

		ViewPropertyAnimator.animate(rl_bottom)
				.translationY(rl_bottom.getHeight())
				.setInterpolator(new DecelerateInterpolator()).setDuration(500)
				.setStartDelay(0).start();
		  
	}
 
	
	@Override
	protected void onStart() {
		super.onStart();
		Bundle bundle = getIntent().getExtras();
		duration = MusicUtils.getDuration();
		Log.i("TAG","onStart�����duration" + duration	);
		String transDuration = MusicUtils.TransformSecs(this, duration/1000);
		Log.i("TAG","onStart�����duration" + transDuration) ;
		endtime.setText(transDuration);
		mToken  = MusicUtils.bindToService(this,this);
	}
	

	///�㲥������
	private class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
         
         if(MusicService.META_CHANGED.equals(intent.getAction())){
        	 	mhandler.obtainMessage(UPDATEINFO).sendToTarget();
         }else if(Consts.ACTION_UPDATE_PROGRESS.equals(intent.getAction())){
        	 
				int progress = intent.getIntExtra (Consts.EXTRA_PROGRESS, 0);
				try {
					long duration =  MusicUtils.getDuration();
					starttime.setText(TimeUtil.getTime(progress));
					if(duration ==0) 
						progress = 0;
					else 
						progress = (int)(progress * 100 / duration  );
//					Log.d("TAG","TEST progresess"+progress);
					seekbar.setProgress((int)progress);
			       
				} catch (Exception e) {
					e.printStackTrace();
				}
        	 
         } 
 
		}
	} 
	  //�˵�����
	 @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 Intent intent = null;
		switch(item.getItemId())
		{
		case R.id.exit :
			intent = new Intent(Consts.ACTION_EXIT);
			sendBroadcast(intent);
			this.finish();
		    break;
		case R.id.flush:
              break;
		}
		return super.onOptionsItemSelected(item);
	}
	 
	 @Override
		protected void onStop() {
		    if(MusicUtils.service != null){
		    	MusicUtils.unbindFromService(mToken);
		        mToken = null;
		    }
		 
			Intent intent = new Intent(Consts.ACTION_EXIT);
			sendBroadcast(intent);
			super.onStop();
		}
		 
		
		@Override
		protected void onDestroy() {
			unregisterReceiver(innerreceiver);
			super.onDestroy();
		}
	 

	public void doClick(View v ) {
		Intent intent = null;
		switch(v.getId()){
			case R.id.ib_prev:
				if(MusicUtils.service == null) return ;
			try {
				MusicUtils.service.prev();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
				 break; 
			case R.id.ib_play: 
				try{
					if(MusicUtils.service  == null)return ;
					if(MusicUtils.service.isplaying())
						MusicUtils.service.pause();
					else MusicUtils.service.play();
					setPauseButtonImage();
				}catch(RemoteException e ){
					e.printStackTrace();
				}
				
			 break;
 		case R.id.ib_next:
			if (MusicUtils.service == null)
				return;
			try {
				MusicUtils.service.next();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
				break; 
		case R.id.play_mode_loop: 
			
				break;
		case R.id.play_mode_random: 
			
				break; 
		case R.id.ib_repeat:
			cycleRepeat();
			break;
		case R.id.img_favourite:
			cycleFavourite();
			break;
			
		}
		 
	 }

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.service = IMusicService.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		MusicUtils.service = null;
	}
	
	/**
     * Set the play and pause image
     */
    private void setPauseButtonImage() {
    	mPlay.setBackgroundColor(0);
        try {
            if (MusicUtils.service != null && MusicUtils.service.isplaying()) {
            	mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause)); //R.drawable.ic_pause 
                // Theme chooser
                //mPlay.setImageDrawable( getResources().getDrawable(R.drawable.kingsly_holo_light_pause));
            	
            } else {
				 
                mPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_play));
                // Theme chooser
               // mPlay.setImageDrawable( getResources().getDrawable(R.drawable.kingsly_holo_light_play));

            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
	
    
	private void setFavourteButtonImage(boolean init) {
		 if(MusicUtils.service == null || MusicUtils.getCurrentAudioId() == -1){
			 return;
		 }
		
		 if( MusicUtils.isFavorite(this, MusicUtils.getCurrentAudioId())){
			if(init == false)
			Toast.makeText(this, R.string.add_to_favorite, Toast.LENGTH_SHORT).show();
		    mFavourite.setImageResource(R.drawable.love);
		 }
		 else mFavourite.setImageResource(R.drawable.unlove);
	}

    /**
     * Cycle repeat states
     */
    private void cycleRepeat() { //ѭ���ظ�
    	
        if (MusicUtils.service == null)  
            return;
        try {
            int mode = MusicUtils.service.getRepeatMode();
            if (mode == MusicService.REPEAT_NONE) {
                    MusicUtils.service.setRepeatMode(MusicService.REPEAT_CURRENT);
                    Toast.makeText(this, R.string.repeat_current,Toast.LENGTH_SHORT).show();
            } else {
                	MusicUtils.service.setRepeatMode(MusicService.REPEAT_NONE);
                	Toast.makeText(this, R.string.repeat_now, Toast.LENGTH_SHORT).show();
            }
             setRepeatButtonImage();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }
    
    
	private void cycleFavourite() {
         if(MusicUtils.service == null)return ;
         MusicUtils.toggleFavouite();
         setFavourteButtonImage(false);
	}


	private void setRepeatButtonImage() {
        if( mRepeat == null || MusicUtils.service == null)return;
		try {
			int mode = MusicUtils.service.getRepeatMode();
			if(mode == MusicService.REPEAT_CURRENT)
				this.mRepeat.setImageResource (R.drawable.repeat);
			else this.mRepeat.setImageResource(R.drawable.unrepeat);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}

	private class MyOrientationEventListener extends OrientationEventListener{

		public MyOrientationEventListener(Context context) {
			super(context);
		}

		@Override
		public void onOrientationChanged(int orientation) {
		}
    }
    
    
	   /**
     * Drag to a specfic duration
     */
    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
       

		@Override
        public void onStartTrackingTouch(SeekBar bar) {
            mLastSeekEventTime = 0;
            mFromTouch  = true;
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) { //��ȵĸı�
            if (!fromuser || (MusicUtils.service == null))
                return;
            long now = SystemClock.elapsedRealtime();
            if ((now - mLastSeekEventTime) > 250) {
                mLastSeekEventTime = now;
                //mPosOverride = duration * progress / 1000;
                mPosOverride = duration * progress /100 ;
                try {
                    MusicUtils.service.seekTo(mPosOverride);
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }

                if (!mFromTouch) {
                   // refreshNow();
                    mPosOverride = -1;
                }
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mPosOverride = -1;
            mFromTouch = false;
        }
    };
    
	
}
