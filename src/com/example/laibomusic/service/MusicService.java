package com.example.laibomusic.service;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

import com.example.laibomusic.IMusicService;
import com.example.laibomusic.MyApplication;
import com.example.laibomusic.R;
import com.example.laibomusic.entity.Music;
import com.example.laibomusic.util.Consts;
import com.example.laibomusic.util.MusicUtils;
import com.example.laibomusic.util.Musiclist;


public class MusicService extends Service {

	boolean isLoop = false;
	int PLAYMODE;
	private int mRepeatMode = REPEAT_NONE;
	MediaPlayer mediaplayer;
	Thread my_thread;
	MyApplication myapplication;
	InnerReceiver innerreceiver;
	ArrayList<Music> musicList;
	private boolean mIsSupposedToBePlaying = false;
	private ContentResolver contentresolver;
	private final Shuffler mRand = new Shuffler();
	public static final int NOW = 1; //当前
	public static final int NEXT = 2; //下一个
	public static final int LAST = 3; //最后一个
	public static final int PLAYBACKSERVICE_STATUS = 1; //回放服务的状体
	public static final int SHUFFLE_NONE = 0; //无更新
	public static final int SHUFFLE_NORMAL = 1; //正常刷新
	public static final int SHUFFLE_AUTO = 2; //音频刷新
	public static final int REPEAT_NONE = 0; //无重复
	public static final int REPEAT_CURRENT = 1; //重复当前
	public static final int REPEAT_ALL = 2; //重复所有，即循环
	private final static int IDCOLIDX = 0;
	public static final String KINGSLY_PACKAGE_NAME = "com.fq.kingsly"; //工程包名
	public static final String MUSIC_PACKAGE_NAME = "com.android.music"; //音乐包名
	public static final String PLAYSTATE_CHANGED = "com.fq.kingsly.playstatechanged"; //播放状态改变
	public static final String META_CHANGED = "com.fq.kingsly.metachanged";
	public static final String FAVORITE_CHANGED = "com.fq.kingsly.favoritechanged";//喜欢歌曲设置的改变
	public static final String QUEUE_CHANGED = "com.fq.kingsly.queuechanged"; //改变队列
	public static final String REPEATMODE_CHANGED = "com.fq.kingsly.repeatmodechanged"; //重复模式的改变
	public static final String SHUFFLEMODE_CHANGED = "com.fq.kingsly.shufflemodechanged"; //刷新模式的改变
	public static final String PROGRESSBAR_CHANGED = "com.fq.kingsly.progressbarchnaged"; //进度条的改变
	public static final String REFRESH_PROGRESSBAR = "com.fq.kingsly.refreshprogessbar"; //刷新进度条
	public static final String CYCLEREPEAT_ACTION = "com.fq.kingsly.musicservicecommand.cyclerepeat"; //循环重复
	public static final String TOGGLESHUFFLE_ACTION = "com.fq.kingsly.musicservicecommand.toggleshuffle"; //刷新触发器
	public static final String SERVICECMD = "com.fq.kingsly.musicservicecommand"; //服务命令
	public static final String CMDNAME = "command"; //命令
	public static final String CMDTOGGLEPAUSE = "togglepause"; //暂停触发器
	public static final String CMDSTOP = "stop"; //停止
	public static final String CMDPAUSE = "pause"; //暂停
	public static final String CMDPLAY = "play"; //播放
	public static final String CMDPREVIOUS = "previous"; //上一首
	public static final String CMDNEXT = "next"; //下一首
	public static final String CMDNOTIF = "buttonId"; //按钮id

	boolean mIsInitialized  = false;
	private int mPlayListLen = 0;
	private int mPlayPos = -1;     //main 播放到了哪里
	private int MAX_HISTORY_SIZE = 100;
	private long[] mplaylist ;



	private Vector<Integer> mHistory = new Vector<Integer>(MAX_HISTORY_SIZE);

	private Handler mhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case REPEAT_CURRENT:
					if (mRepeatMode == REPEAT_CURRENT) {
						seekTo(0);
						play();
					}
					else Next();
					break;

			}
		}
	};

	String[] mCursorCols = new String[] {
			"audio._id AS _id", MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.IS_PODCAST,
			MediaStore.Audio.Media.BOOKMARK
	};
	private Cursor mcursor = null;
	private String mFileToPlay;
	private int mNextPlayPos = -1;
	private int mOpenFailedCounter = 0;


	@Override
	public void onCreate() {

		super.onCreate();
		this.mediaplayer = new MediaPlayer();
		this.isLoop = true;
		this.PLAYMODE = Consts.MODE_LOOP;
		innerreceiver = new InnerReceiver();
		this.musicList = new Musiclist(this).getMusicList();
		this.mediaplayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {

				mhandler.sendEmptyMessage(REPEAT_CURRENT);
			}
		});
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(Consts.ACTION_PLAY);
		intentfilter.addAction(Consts.ACTION_PAUSE);
		intentfilter.addAction(Consts.ACTION_ITEM_PLAY);
		intentfilter.addAction(Consts.ACTION_NEXT);
		intentfilter.addAction(Consts.ACTION_SEEK_TO);
		intentfilter.addAction(Consts.ACTION_PREVIOUS);
		registerReceiver(innerreceiver, intentfilter);
		contentresolver = getApplicationContext().getContentResolver();
		getMusicList();



		this.my_thread = new Thread() {
			@Override
			public void run() {

				while (isLoop) {
					while (mediaplayer.isPlaying()) {
						Intent intent = new Intent(Consts.ACTION_UPDATE_PROGRESS);
						intent.putExtra(Consts.EXTRA_PROGRESS,
								mediaplayer.getCurrentPosition());
						sendBroadcast(intent);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
//						synchronized (my_thread) {
//							try {
//								my_thread.wait();
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						}

					}

				}
			}

		};
		this.my_thread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			String action = intent.getAction();
			String cmd = intent.getStringExtra("cmd");
			if(CMDNEXT.equals(cmd)){
			}else if(CMDPREVIOUS.equals(cmd)){

			}
			else if(CMDTOGGLEPAUSE.equals(cmd)){
				if(mIsSupposedToBePlaying){
					pause();
				}else{
					play();
				}
			}else if(CMDPLAY.equals(cmd))
				play();
			else if(CMDPAUSE.equals(cmd))
				pause();
			else return 1;
			return 1;
		}
		return super.onStartCommand(intent, flags, startId);
	}


	public void getMusicList(){
		Cursor c = contentresolver.query(Media.EXTERNAL_CONTENT_URI, null, null, null	, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
		if(c == null || c.getCount() == 0 )
			return  ;
		if(c.moveToFirst())
			do{
				Music music = new Music ()	;
				String title = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.TITLE));
				String id =  c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID));
				String uril = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DATA));

				String singer = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.ARTIST));
				Long time  = c.getLong(c.getColumnIndex(MediaStore.Audio.Media.DURATION));
				String name = c.getString(c
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				String sbr = name.substring(name.length() - 3, name.length());
				if (sbr.equals("mp3")) {
					music.setName(name);
					music.setSinger(singer);
					music.setTitle(title);
					music.setUrl(uril);
					music.setId(id);
					music.setTime(time);
					musicList.add(music);
				}
			}while(c.moveToNext());

	}


//	private boolean setDataSourceImpl(MediaPlayer mediaplayer,String path){
//		
//		mediaplayer.reset();
//		mediaplayer.setOnCompletionListener(new OnCompletionListener(){
//
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				Next();
//			}});
//		 
//			try {
//				if(path.startsWith("content://"))
//					mediaplayer.setDataSource(MusicService.this, Uri.parse(path));
//				else 
//					mediaplayer.setDataSource(path);
////				 Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
////		            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
////		            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
////		            sendBroadcast(i);
//				mIsInitialized = true;
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			} catch (IllegalStateException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		
//		return false;
//	}
	/**
	 * Appends a list of tracks to the current playlist. If nothing is playing
	 * currently, playback will be started at the first track. If the action is
	 * NOW, playback will switch to the first of the new tracks immediately.
	 *
	 * @param list The list of tracks to append.
	 * @param action NOW, NEXT or LAST
	 */
	public void enqueue(long[] list, int action) {
		synchronized (this) {
			if (action == NEXT && mPlayPos + 1 < mPlayListLen) {
				addToPlayList(list, mPlayPos + 1);
				notifyChange(QUEUE_CHANGED);
			} else {
				// action == LAST || action == NOW || mPlayPos + 1 == mPlayListLen
				addToPlayList(list, Integer.MAX_VALUE);
				notifyChange(QUEUE_CHANGED);
				if (action == NOW) {
					mPlayPos = mPlayListLen - list.length;
					openCurrentAndNext();
					play();
					notifyChange(META_CHANGED);
					return;
				}
			}
			if (mPlayPos < 0) {
				mPlayPos = 0;
				openCurrentAndNext();
				play();
				notifyChange(META_CHANGED);
			}
		}
	}


	private void addToPlayList(long[] list, int position) { //在播放列表特殊的位置处插入歌曲列表
		int addlen = list.length;
		if (position < 0) { //覆盖
			mPlayListLen = 0;
			position = 0;
		}
		ensurePlayListCapacity(mPlayListLen + addlen);
		if (position > mPlayListLen) {
			position = mPlayListLen;
		}
		//在插入点后移出一部分列表内容
		int tailsize = mPlayListLen - position;
		for (int i = tailsize; i > 0; i--) {
			mplaylist[position + i] = mplaylist[position + i - addlen];
		}
		for (int i = 0; i < addlen; i++) { //复制列表到播放列表
			mplaylist[position + i] = list[i];
		}
		mPlayListLen += addlen;
		if (mPlayListLen == 0) {
			mcursor.close();
			mcursor = null;
			// updateAlbumBitmap();
			notifyChange(META_CHANGED);
		}
	}


	private void ensurePlayListCapacity(int size) {
		if (mplaylist == null || size > mplaylist.length) {
			// 2x请求大小时重新分配,所以不需要在每组插入时增加和复制数组
			long[] newlist = new long[size * 2];
			int len = mplaylist != null ? mplaylist.length : mPlayListLen;
			for (int i = 0; i < len; i++) {
				newlist[i] = mplaylist[i];
			}
			mplaylist = newlist;
		}
		// FIXME: shrink the array when the needed size is much smaller than the allocated size
	}



	public void setDataSource(String path) { //设置数据资源
		mIsInitialized = setDataSourceImpl(mediaplayer, path);
//          if (mIsInitialized) {
//               setNextDataSource(null);
//          }
	}

	@SuppressLint("NewApi")
	private boolean setDataSourceImpl(MediaPlayer player, String path) {
		try {

			player.reset();
			player.setOnPreparedListener(null);
			if (path.startsWith("content://")) {
				player.setDataSource(MusicService.this, Uri.parse(path));
			} else {
				player.setDataSource(path);
			}
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.prepare();
		} catch (IOException ex) {
			return false;
		} catch (IllegalArgumentException ex) {
			return false;
		}
//	            player.setOnCompletionListener(listener);
//	            player.setOnErrorListener(errorListener);
//	            Intent i = new Intent(AudioEffect.ACTION_OPEN_AUDIO_EFFECT_CONTROL_SESSION);
//	            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, getAudioSessionId());
//	            i.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getPackageName());
		//sendBroadcast(i);
		return true;
	}

	@SuppressLint("NewApi")
	public void setNextDataSource(String path) { //设置下一首数据资源
		mediaplayer.setNextMediaPlayer(null);
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null;
		}
		if (path == null) {
			return;
		}
		mediaplayer = new MediaPlayer();
		mediaplayer.setWakeMode(MusicService.this, PowerManager.PARTIAL_WAKE_LOCK);
		//mediaplayer.setAudioSessionId(getAudioSessionId());
	}

	public void palyAll(Context context ,int position){
//		if(position <= 0 &&   position == mPlayPos　 ){
//		}

		Cursor c = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mCursorCols, null,
				null, null);
		c.moveToFirst();
		String c1 = c.getString(0);
		long d = c.getLong(0);
		if (c != null) {
			c.moveToFirst();
		}
		String uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()+"/" + c1;
		setDataSource(uri + "");
		this.makeAutoShuffle();
		for(int j = 0 ;j < mplaylist.length ; j++){
			Log.d("TAG","j:" + mplaylist[j]);
		}
		mIsInitialized = true;
		mPlayPos = 1;
		play();
	}

	public void play() {

		if(mIsInitialized){
			mediaplayer.start();
		}
		if(!mIsSupposedToBePlaying){
			mIsSupposedToBePlaying = true;
			notifyChange(META_CHANGED);
		}

	}

	/**
	 *   暂停播放
	 */
	public void pause() {
		synchronized (this) {
			if (mIsSupposedToBePlaying) {
				mediaplayer.pause();
				mIsSupposedToBePlaying = false;
			}
		}

	}




//	public void play(Music m) {
//		if (this.isPause) {
//			mediaplayer.start();
//		} else {
//			if (m != null) {
//				try {
//					mediaplayer.reset();
//					mediaplayer.setDataSource(m.getUrl());
//					mediaplayer.prepare();
//					mediaplayer.start();
//					Intent intent = new Intent(
//							Consts.ACTION_CURRENT_MUSIC_CHANGED);
//					sendBroadcast(intent);
//
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				} catch (IllegalStateException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//
//		}
//
//	}

	/**
	 * 上一首
	 */
	private void Previous() {

		switch (PLAYMODE) {
			case Consts.MODE_LOOP:
				if ( -- mPlayPos > 0)
					mPlayPos = mPlayListLen - 1;
				stop(false);
				openCurrentAndNext();
				play();
				break;
			case Consts.MODE_RANDOM:
				break;
		}
		notifyChange(META_CHANGED);
	}


	/**
	 * 下一首
	 */
	protected void Next() {
		switch (PLAYMODE) {
			case Consts.MODE_LOOP:

				if (++ mPlayPos == mPlayListLen)
					mPlayPos = 0;
				stop(false);
				openCurrentAndNext();
				play();
				break;
			case Consts.MODE_RANDOM:
				do {
					mPlayPos = new Random().nextInt(mPlayListLen);
				} while (mPlayPos == mPlayListLen);
				break;
		}
		notifyChange(META_CHANGED);
	}



	/**
	 * 转换下一曲
	 */
	private void openCurrentAndNext() {

		if(mcursor != null){
			mcursor.close();
			mcursor = null;
		}
		if(mPlayListLen == 0 ){
			return ;
		}
//		stop();
		//if(mPlayPos>= mPlayListLen)mPlayPos = mPlayListLen;//preqel
		mcursor =  getCursorForId(this.mplaylist[mPlayPos]);

		if(mcursor == null)return ;
		Long idcolix = mcursor.getLong(IDCOLIDX);

		if(!open(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/"
				+ idcolix.toString())){
			//打不开的时候
			Log.d("TAG","竟然有打不开的时候，真是见了鬼了"+ mOpenFailedCounter +" is mOpenFailedCounter "+mPlayListLen +" is mPlayListLen ");
		};
	}
	/**
	 * Opens the specified file and readies it for playback.
	 *
	 * @param path The full path of the file to be opened.
	 */
	public boolean open(String path) { //打开
		synchronized (this) {
			if (path == null) {
				return false;
			}
			if (mcursor == null) { //如果mCursor是空的，试着用数据库指针联系路径
				ContentResolver resolver = getContentResolver();
				Uri uri;
				String where;
				String selectionArgs[];
				if (path.startsWith("content://media/")) {
					uri = Uri.parse(path);
					where = null;
					selectionArgs = null;
				} else { //在数据库中为搜索移出模式，否则文件不会被找到
					String data = path;
					if( data.startsWith("file://") ){
						data = data.substring(7);
					}
					uri = MediaStore.Audio.Media.getContentUriForPath(path);
					where = MediaColumns.DATA + "=?";
					selectionArgs = new String[] {
							data
					};
				}
				try {
					mcursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
					if (mcursor != null) {
						if (mcursor.getCount() == 0) {
							mcursor.close();
							mcursor = null;
						} else {
							mcursor.moveToNext();
							mPlayListLen = 1;
							mplaylist[0] = mcursor.getLong(IDCOLIDX);
							mPlayPos = 0;
						}
					}
				} catch (UnsupportedOperationException ex) {
				}

			}
			mFileToPlay = path;
			setDataSource(mFileToPlay);
			if (this.mIsInitialized) {
				//  mOpenFailedCounter = 0;
				return true;
			}
			stop(true);
			return false;
		}
	}

	/**
	 * Opens the specified file and readies it for playback.
	 *
	 * @param path The full path of the file to be opened.
	 */
	public boolean open(long[] list,int position) { //打开


		synchronized (this) {
//            if (mShuffleMode == SHUFFLE_AUTO) {
//                mShuffleMode = SHUFFLE_NORMAL;
//            }
			long oldId = getAudioId();
			int listlength = list.length;
			boolean newlist = true;
			if (mPlayListLen == listlength) { //可能的快速路径：列表可能是同一个
				newlist = false;
				for (int i = 0; i < listlength; i++) {
					if (list[i] != mplaylist[i]) {
						newlist = true;
						break;
					}
				}
			}
			if (newlist) {
				addToPlayList(list, -1);
				notifyChange(QUEUE_CHANGED);
			}
			if (position >= 0) {
				mPlayPos = position;
			} else {
				mPlayPos = mRand.nextInt(mPlayListLen);
			}
			mHistory.clear();
			// saveBookmarkIfNeeded();
			openCurrentAndNext();
			if (oldId != getAudioId()) {
				notifyChange(META_CHANGED);
			}
		}
		return true;


	}


	/**
	 * Returns the current play list
	 *
	 * @return An array of integers containing the IDs of the tracks in the play
	 *         list
	 */
	public long[] getQueue() { //获取队列
		synchronized (this) {
			int len = mPlayListLen;
			long[] list = new long[len];
			for (int i = 0; i < len; i++) {
				list[i] = mplaylist[i];
			}
			return list;
		}
	}



	public long seekTo(long pos){
		if(mIsInitialized){
			if(pos < 0  )
				pos = 0;
			if(pos> mediaplayer.getDuration())
				pos = mediaplayer.getDuration();
			mediaplayer.seekTo((int)pos);
		}
		return -1;
	}

	/**
	 * Removes all instances of the track with the given id from the playlist.
	 *
	 * @param id The id to be removed
	 * @return how many instances of the track were removed
	 */
	public int removeTrack(long id) {
		int numremoved = 0;
		synchronized (this) {
			for (int i = 0; i < mPlayListLen; i++) {
				if (mplaylist[i] == id) {
					numremoved += removeTracksInternal(i, i);
					i--;
				}
			}
		}
		if (numremoved > 0) {
			notifyChange(QUEUE_CHANGED);
		}
		return numremoved;
	}

	public int removeTracks(int first, int last) { //移除tracks
		int numremoved = removeTracksInternal(first, last);
		if (numremoved > 0) {
			notifyChange(QUEUE_CHANGED);
		}
		return numremoved;
	}
	private int removeTracksInternal(int first, int last) { //移除内部tracks
		synchronized (this) {
			if (last < first)
				return 0;
			if (first < 0)
				first = 0;
			if (last >= mPlayListLen)
				last = mPlayListLen - 1;

			boolean gotonext = false;
			if (first <= mPlayPos && mPlayPos <= last) {
				mPlayPos = first;
				gotonext = true;
			} else if (mPlayPos > last) {
				mPlayPos -= (last - first + 1);
			}
			int num = mPlayListLen - last - 1;
			for (int i = 0; i < num; i++) {
				mplaylist[first + i] = mplaylist[last + 1 + i];
			}
			mPlayListLen -= last - first + 1;

			if (gotonext) {
				if (mPlayListLen == 0) {
					stop(true);
					mPlayPos = -1;
					if (mcursor != null) {
						mcursor.close();
						mcursor = null;
					}
				} else {
					if (mPlayPos >= mPlayListLen) {
						mPlayPos = 0;
					}
					boolean wasPlaying = mIsSupposedToBePlaying;
					stop(false);
					openCurrentAndNext();
					if (wasPlaying) {
						play();
					}
				}
				// updateAlbumBitmap();
				notifyChange(META_CHANGED);
			}
			return last - first + 1;
		}
	}


	public String getTrackName() {

		synchronized(this){
			if(mcursor != null ) return mcursor.getString( mcursor.getColumnIndexOrThrow(AudioColumns.TITLE));
			return getString(R.string.unknown);
		}
	}

	public long getArtistId() {
		synchronized(this){
			if(mcursor != null) return mcursor.getColumnIndexOrThrow(AudioColumns.ARTIST_ID);
			return -1;
		}
	}

	public long getAudioId(){
		synchronized (this) {
			if (mIsInitialized && mPlayPos >= 0)
				return mplaylist[mPlayPos];
		}
		return -1;
	}

	/**
	 *  getCursorForId
	 * @return
	 */
	private Cursor getCursorForId(long lid) { //获取id的指针
		String id = String.valueOf(lid);
		Cursor c = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				mCursorCols, "_id=" + id , null, null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	/**
	 * @param boolean b
	 */
	private void stop(boolean b) {
		if(mIsInitialized && mediaplayer.isPlaying()){
			mediaplayer.stop();
			if (mcursor != null) {
				mcursor.close();
				mcursor = null;
			}
		}
		mIsSupposedToBePlaying = false;

	}


	public void notifyChange(String what){
		Intent i = new Intent(what);
		i.putExtra("id",  Long.valueOf(getAudioId()));
		i.putExtra("track", getTrackName());
		i.putExtra("artist",getArtistName() );
		i.putExtra("album", getAlbumName());
		i.putExtra("isfavorite", isFavorite());
		sendStickyBroadcast(i);
	}


	private String getAlbumName() {
		if(mcursor != null) return mcursor.getString(mcursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
		return "unknown";
	}

	private String getArtistName() {
		if(mcursor != null) return mcursor.getString(mcursor.getColumnIndexOrThrow(AudioColumns.ARTIST))	;
		return "unknown";
	}

	public Music getMusicById(String id ){
		Music result= null;
		for(int i=0;i< musicList.size();i++)
		{
			if(musicList.get(i).getId().equals(id)){
				result = musicList.get(i);
				mPlayPos = i;
				break;
			}
		}
		return result;
	}

	/**
	 * Returns the position in the queue
	 *
	 * @return the position in the queue
	 */
	public int getQueuePosition() { //获取队列位置
		synchronized (this) {
			return mPlayPos;
		}
	}

	/**
	 * Starts playing the track at the given position in the queue.
	 *
	 * @param pos The position in the queue of the track that will be played.
	 */
	public void setQueuePosition(int pos) { //设置队列位置
		synchronized (this) {
			stop(false);
			mPlayPos = pos;
			openCurrentAndNext();
			play();
			notifyChange(META_CHANGED);
//            if (mShuffleMode == SHUFFLE_AUTO) {
//                doAutoShuffleUpdate();
//            }
		}
	}

	private int getNextPosition() {
		if(mRepeatMode == REPEAT_CURRENT ){
			if(mPlayPos <= 0) return 0;
			else return mPlayPos;
		}
		else{
			if(mPlayPos > 0 ) mHistory.add(mPlayPos);
			if(mHistory.size() > MAX_HISTORY_SIZE)mHistory.removeElementAt(0);
		}
		return 0;
	}



	public long duration(){
		if(mIsInitialized) return mediaplayer.getDuration();
		else return 0 ;
	}

	private boolean isplaying(){
		return mIsSupposedToBePlaying;
	}

	private void StopSelf() {
		this.StopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	/**
	 * 广播接收者
	 * @author 王康
	 *
	 */
	public class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			if (action.equals(Consts.ACTION_PLAY)) {   //ready to -depreted 
				palyAll(MusicService.this, 1);
			} else if (action.equals(Consts.ACTION_PAUSE)) {
				ActionPause();
			} else if (action.equals(Consts.ACTION_ITEM_PLAY)) {
				int id = arg1.getIntExtra(Consts.ITEM_ID, 0);
				//myapplication.setMposition (id);
				play();
			} else if (action.equals(Consts.ACTION_ITEM_PLAY)) {

			}else if(action.equals(Consts.ACTION_CURRENTMUSIC_CHANGE)){

			}
			else if(action.equals(Consts.ACTION_NEXT)){
				Next();
			}
			else if(action.equals(Consts.ACTION_PREVIOUS)){
				Previous();
			}
			else if(action.equals(Consts.ACTION_SEEK_TO)){
				int process = arg1.getIntExtra(Consts.EXTRA_PROGRESS, 0);
				//this.seekTo(process);
			} else if (action.equals(Consts.ACTION_EXIT)) {
				StopSelf();
			}
		}



	}

	/**
	 * 自动刷新
	 */
	public boolean makeAutoShuffle(){
		ContentResolver cr = getContentResolver();

		try{
			Cursor c = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[]{BaseColumns._ID}, AudioColumns.IS_MUSIC + "=1", null	, null);
			if(c == null||c.getCount() == 0){
				return false;
			}
			int len  = c.getCount();
			long[] list = new long[len];
			for (int i = 0; i < len; i++) {
				c.moveToNext();
				list[i] = c.getLong(0);
			}
			mplaylist  = list;
			mPlayListLen = len; //这里是我自己加的
			c.close();
			return true;
		}catch(RuntimeException e ){
			return false;
		}


	}

	public void setReapeatMode(int repeatmode){
		synchronized(this){
			mRepeatMode = repeatmode;
			//setNextTrack();
			notifyChange(REPEATMODE_CHANGED);
			//saveQueen(fasle);
		}
	}

	public int getRepeatMode() {
		return mRepeatMode;
	}

	private void setNextTrack() { //设置下一个track
		mNextPlayPos = getNextPosition();
		if (mNextPlayPos >= 0 && mplaylist != null) {
			long id = mplaylist[mNextPlayPos];
			setNextDataSource(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/" + id);
		}
	}

	/**
	 * Method that query the media database for search a path an translate
	 * to the internal media id
	 *
	 * @param path The path to search
	 * @return long The id of the resource, or -1 if not found
	 */
	public long getIdFromPath(String path) {
		try { ////在数据库中为搜索移出模式，否则文件不会被找到
			String data = path;
			if( data.startsWith("file://") ){
				data = data.substring(7);
			}
			ContentResolver resolver = getContentResolver();
			String where = MediaColumns.DATA + "=?";
			String selectionArgs[] = new String[] {
					data
			};
			Cursor cursor =
					resolver.query(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							mCursorCols, where, selectionArgs, null);
			try {
				if (cursor == null || cursor.getCount() == 0) {
					return -1;
				}
				cursor.moveToNext();
				return cursor.getLong(0);
			} finally {
				try {
					if( cursor != null )
						cursor.close();
				} catch (Exception ex) {
				}
			}
		} catch (UnsupportedOperationException ex) {
		}
		return -1;
	}

	public void toggleFavorite() { //喜欢列表的触发器
		if (!isFavorite()) {
			addToFavorites();
		} else {
			removeFromFavorites();
		}
	}

	public boolean isFavorite() { //判断是否为喜欢列表中的歌曲
		if (getAudioId() >= 0)
			return isFavorite(getAudioId());
		return false;
	}

	public boolean isFavorite(long id) {
		return MusicUtils.isFavorite(this, id);
	}

	public void addToFavorites() { //添加到喜欢列表
		if (getAudioId() >= 0) {
			addToFavorites(getAudioId());
		}
	}

	public void addToFavorites(long id) {
		MusicUtils.addToFavorites(this, id);
		notifyChange(FAVORITE_CHANGED);
	}

	public void removeFromFavorites() { //从喜欢列表中移除
		if (getAudioId() >= 0) {
			removeFromFavorites(getAudioId());
		}
	}

	public void removeFromFavorites(long id) {
		MusicUtils.removeFromFavorites(this, id);
		notifyChange(FAVORITE_CHANGED);
	}

	private static class Shuffler { //一个简单的随机变化,确保它返回的值不等于它之前返回的值,除非间隔是1
		private int mPrevious;
		private final Random mRandom = new Random();

		public int nextInt(int interval) {
			int ret;
			do {
				ret = mRandom.nextInt(interval);
			} while (ret == mPrevious && interval > 1);
			mPrevious = ret;
			return ret;
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.isLoop = false;
		synchronized (this.my_thread) {
			my_thread.notify();
		}
		if (this.mediaplayer.isPlaying())
			this.mediaplayer.stop();
		this.mediaplayer.release();
		unregisterReceiver(innerreceiver);
	}

	public void ActionPause() {
		if(this.mediaplayer.isPlaying() ){

			mediaplayer.pause();
		}

	}

	static class ServiceStub extends IMusicService.Stub{

		WeakReference<MusicService> mService;


		public ServiceStub(MusicService service){
			this.mService = new WeakReference<MusicService>(service);
		}

		@Override
		public void stop() throws RemoteException {
			mService.get().stop(true);
		}

		@Override
		public void pause() throws RemoteException {
			mService.get().pause();
		}

		@Override
		public void play() throws RemoteException {
			mService.get().play();
		}

		@Override
		public void prev() throws RemoteException {
			mService.get().Previous();
		}

		@Override
		public void next() throws RemoteException {
			mService.get().Next();
		}

		@Override
		public boolean isplaying() throws RemoteException {
			return mService.get().isplaying();
		}

		@Override
		public long duration() throws RemoteException {
			return mService.get().duration();
		}

		@Override
		public void openFile(String path) throws RemoteException {
			mService.get().open(path);
		}


		@Override
		public void open(long[] list, int position) throws RemoteException {
			mService.get().open(list,position);

		}

		@Override
		public void enqueue(long[] list, int action) throws RemoteException {
			mService.get().enqueue(list,action);

		}

		@Override
		public long[] getQueue() throws RemoteException {
			return mService.get().getQueue();
		}

		@Override
		public void setQueuePosition(int index) throws RemoteException {
			mService.get().setQueuePosition(index);
		}

		@Override
		public long seekTo(long position) throws RemoteException {
			return mService.get().seekTo(position);
		}

		@Override
		public long getArtistId() throws RemoteException {
			return mService.get().getArtistId();
		}

		@Override
		public long getAudioId() throws RemoteException {
			return mService.get().getAudioId();
		}

		@Override
		public int getQueuePosition() throws RemoteException {
			return mService.get().getQueuePosition();
		}

		@Override
		public String getArtistName() throws RemoteException {
			return mService.get().getArtistName();
		}

		@Override
		public String getAlbumName() throws RemoteException {
			return mService.get().getAlbumName();
		}



		@Override
		public int removeTrack(long trackid) throws RemoteException {
			return  mService.get().removeTrack(trackid);
		}

		@Override
		public String getTrackName() throws RemoteException {

			return mService.get().getTrackName();
		}

		@Override
		public void setRepeatMode(int repeatmode) throws RemoteException {
			mService.get().setReapeatMode(repeatmode);
		}

		@Override
		public int getRepeatMode() throws RemoteException {
			return mService.get().getRepeatMode();
		}

		@Override
		public void toggleFavorite() throws RemoteException {
			mService.get().toggleFavorite();
		}

		@Override
		public boolean isFavorite(long id) throws RemoteException {
			return mService.get().isFavorite();
		}

		@Override
		public void addToFavorites() throws RemoteException {
			mService.get().addToFavorites();
		}

		@Override
		public void removeFromFavorites() throws RemoteException {
			mService.get().removeFromFavorites();
		}};


	private final IBinder mBinder = new ServiceStub ( this);







}
