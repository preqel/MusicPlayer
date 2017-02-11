package com.example.laibomusic;

import java.util.ArrayList;
import java.util.List;

import com.example.fragments.AlbumsFragment;
import com.example.fragments.AllSongFragment;
import com.example.fragments.ArtistFragment;
import com.example.fragments.PlayListFrament;
import com.example.laibomusic.adapter.*;
import com.example.laibomusic.entity.Music;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.Musiclist;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
 
import android.support.v4.view.ViewPager;
import android.test.mock.MockApplication;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity { 
	private ListView lv;
	private List<Music> lists;
    private MusicService musicservice; 
	private MusicAdapter musicadapter;
	private ViewPager viewpager;
	private MyPagerAdapter myadapter;
	private Musiclist utillist;  
	private PagerAdapter pageradapter; 
	MockApplication myapplication; 
	LinearLayout linearBottom;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.myapplication =(MockApplication) this.getApplication();
		this.musicservice = new MusicService();
		this.utillist= new Musiclist(this); 
		Intent intent = new Intent(this, musicservice.getClass());
		startService(intent); 
		initView(); 
	} 

	private void initView(){ 
		this.myadapter = new MyPagerAdapter(getViews());
		this.viewpager.setAdapter(myadapter); 
		linearBottom = (LinearLayout) findViewById(R.id.linearlayout_bottom);
	}

	public void initActionBar() {
		ActionBar actionar = getActionBar();
		actionar.setDisplayUseLogoEnabled(true);
		actionar.setDisplayShowTitleEnabled(false);
	}
	public void initPagerAdapter() {
	
		pageradapter = new PagerAdapter(getFragmentManager());
	
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
	private ArrayList<View> getViews() {
		ArrayList<View> lists = new ArrayList<View>();
		lists.add (CreateView(Consts.TYPE_LSIT)); 
		return lists;
	}

	private  View CreateView(int type ) {
		lv = new ListView(this);
		switch(type){
		case Consts.TYPE_LSIT: 
			lv.setBackgroundResource (R.drawable.listbg1);
			this.lists = this.utillist.getMusicList();
			//�������Utilist == null
			 lv.setAdapter(new MusicAdapter(this,lists)); 
			 setClickListener();
			break; 
		case  Consts.TYPE_ALUM:
			break; 
		}
		return lv;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}  
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch(item.getItemId())
		{
		case R.id.exit:
			intent = new Intent(Consts.ACTION_EXIT);
			sendBroadcast(intent);
			Log.e("debug","exit");
			finish(); 
			break;
		case R.id.flush:
			LayoutInflater layoutinflator = LayoutInflater.from(this);
			RelativeLayout relativelayout  = (RelativeLayout) layoutinflator.inflate(R.layout.dialog, null);
			final AlertDialog alterdialog = new AlertDialog.Builder(MainActivity.this).create();
              alterdialog.show();
              alterdialog.setContentView(relativelayout);
              Button btncancel = (Button) relativelayout.findViewById(R.id.buttn_2);
              btncancel.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
			 
					 Toast.makeText(getApplicationContext(), "cancel", Toast.LENGTH_LONG).show();
					  
				}});
               ImageButton imagebutton = (ImageButton)relativelayout.findViewById(R.id.dialog_close);
               imagebutton.setOnClickListener(new OnClickListener(){
 
 			@Override
 			public void onClick(View v) {
                    alterdialog.dismiss();
 			}});
				break;
		case R.id.elseitem:
			final CharSequence[] items ={"else1","optionitems","ui"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("pick a item");
			builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
				 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(getApplicationContext(), items[which], Toast.LENGTH_LONG).show();
				}
			}).show() ;
			AlertDialog alert= builder.create();
				break;
		case R.id.introduce: 
			AlertDialog.Builder alertdialogbuilder;
			AlertDialog alertdialog;
			Context context = getApplicationContext();
			LayoutInflater flator = (LayoutInflater) context
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View layout = flator.inflate(R.layout.custom_dialog,
					(ViewGroup) findViewById(R.id.layout_root));

			TextView textview = (TextView) layout.findViewById(R.id._textview);
			ImageView iamgeview = (ImageView) layout
					.findViewById(R.id._imageview);
			iamgeview.setImageResource(R.drawable.ic_launcher);

			new AlertDialog.Builder(MainActivity.this).setTitle("��Ȩ����")
					.setView(layout).show();  
			break; 
		}
		return super.onOptionsItemSelected(item);
	}

	
	private void setClickListener() {
		this.lv.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent2 = new Intent(Consts.ACTION_ITEM_PLAY);
				intent2.putExtra(Consts.ITEM_ID, position); 
				sendBroadcast(intent2);   
				Intent intent = new Intent(MainActivity.this,
						MusicPlaying.class);
				intent.putExtra(Consts.ITEM_ID, position);
				startActivity(intent);
	
			}});  
	}
}
