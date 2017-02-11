package com.example.laibomusic;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fragments.AlbumsFragment;
import com.example.fragments.AllSongFragment;
import com.example.fragments.ArtistFragment;
import com.example.fragments.PlayListFrament;
import com.example.laibomusic.adapter.PagerAdapter;
import com.example.laibomusic.adapter.ScrollingTabsAdapter;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.service.ServiceToken;
import com.example.laibomusic.util.MusicUtils;
import com.example.widget.NewToast;
import com.example.widget.ScrollableTabView;

public class MusicLibrary extends Activity implements ServiceConnection {
	
	private MusicService musicservice;
	ServiceToken mToken;
	private long exittime = 0;
	private Toast toast = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.musicservice = new MusicService();
		Intent intent = new Intent(this,musicservice.getClass());
		startService(intent);
		//initActionBar ();
		setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
		initView();
	    initPager();
	   
	}
	
	
	private void initView() {
		TextView textview  = (TextView) findViewById(R.id.title_middle_text);
	    textview.setText("音乐播放器");
	    ImageView mSearch = (ImageView) findViewById(R.id.ib_search);
	    mSearch.setVisibility(View.VISIBLE);
	    mSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MusicLibrary.this.onSearchRequested();
			}
		});
	}


	@Override
	protected void onStart() {
	    mToken = MusicUtils.bindToService(this, this);
		super.onStart();
	}



	private void initActionBar() {
		ActionBar actionbar = getActionBar();
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);
	}
	

	@Override
	protected void onStop() {
	    if(MusicUtils.service != null){
	    	MusicUtils.unbindFromService(mToken);
	    }
 	    if(toast != null) toast .cancel();
	    Log.d("TAG	","EXIT");
		super.onStop();
	}



	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}   
	 
	private void initPager() {
		 
		PagerAdapter pageradapter = new PagerAdapter(getFragmentManager());
 		ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager_main);
 	    //viewpager.setOffscreenPageLimit(pageradapter.getCount());
 		pageradapter.addFragment (new AllSongFragment());
		pageradapter.addFragment (new ArtistFragment( ));
		pageradapter.addFragment (new AlbumsFragment( ));
		pageradapter.addFragment (new PlayListFrament( ));
    	viewpager.setAdapter(pageradapter);
    	initScrollableTabs(viewpager);
	}

	private void initScrollableTabs(ViewPager viewpager  ) {
	    ScrollableTabView scrollabletabview = (ScrollableTabView) findViewById(R.id.scrollingTabs);
		ScrollingTabsAdapter tabadapter = new ScrollingTabsAdapter(this);
		scrollabletabview.setAdapter(tabadapter);
		scrollabletabview.setViewPager(viewpager);
	}

	private void initScrollTabs(ViewPager mViewPager) {
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		MusicUtils.service = IMusicService.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		MusicUtils.service  = null;     
	}

	NewToast newview;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode== KeyEvent.KEYCODE_BACK && event.getAction()== KeyEvent.ACTION_DOWN){
			if(System.currentTimeMillis() - exittime > 2000 ){
//			    toast  = NewToast.makeText(MusicLibrary.this	,R.id.ib_play, "�ٴε����������", 1000);
//				toast.show();
				newview = new NewToast(MusicLibrary.this);
				newview.show();
				exittime = System.currentTimeMillis();
			    Handler mhandler = new Handler()
			    {
					@Override
					public void handleMessage(Message msg) {
						newview.slideup();
						super.handleMessage(msg);
					}
			    };
			    mhandler.sendEmptyMessageDelayed(1, 500);
			}else {
				
				 finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
