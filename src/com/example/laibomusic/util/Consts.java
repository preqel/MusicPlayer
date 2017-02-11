package com.example.laibomusic.util;

import java.io.File;

import android.os.Environment;

public final class Consts {

	public final static String LASTFM_API_KEY = "0bec3f7ec1f914d7c960c12a916c8fb3";


	public static final int MODE_LOOP = 1;
	public static final int MODE_SINGER = 2;
	public static final int MODE_RANDOM = 3;


	public static final int TYPE_LSIT = 1;
	public static final int TYPE_ALUM = 2;


	public static final String ACTION_PLAY="com.example.laibomusic.play";
	public static final String ACTION_NEXT="com.example.laibomusic.next";
	public static final String ACTION_PAUSE= "com.example.laibomusic.pause";
	public static final String ACTION_PREVIOUS="com.example.laibomusic.previous";
	public static final String ACTION_EXIT="com.example.laibomusic.exit";

	public static final String ACTION_CURRENTMUSIC_CHANGE = "com.example.laibomusic.currentmusicchange" ;
	public static final String ACTION_ITEM_PLAY = "com.example.laibomusic.item_play";

	public static final String EXTRA_PROGRESS = "progress";
	public static final String ITEM_ID = "item_id";

	public static final String Acton_UPDATE_PROGRESS="com.example.laibomusic.updateprogress";





	//播放状态
	public static final int PLAY_STATE_ISPLAYING=1;
	public static final int PLAY_STATE_PAUSE=2;
	public static final int PLAY_STATE_STOP=3;
	public static final String POST_ID="id";
	public static final String BACK_ID="back_id";
	public static final String ACTION_CURRENT_MUSIC_CHANGED = "com.example.MUSIC_CHANGED";
	public static final String ACTION_UPDATE_PROGRESS = "com.example.MUSIC_PROGRESS";
	public static final String ACTION_RESPONSE_PLAY_STATE = "com.example.RESPONSE_PLAY_STATE";
	public static final String ACTION_CURRENT_MUSIC_CHANGED_ITEM = "com.example.MUSIC_CHANGE_ITEM";
	public static final String ACTION_SEEK_TO = "com.example.ACTION_SEEK_TO";
	public static final String ACTION_START_ALREADY = "com.example.start_already";

	public final static long PLAYLIST_UNKNOWN = -1, PLAYLIST_ALL_SONGS = -2, PLAYLIST_QUEUE = -3,
			PLAYLIST_NEW = -4, PLAYLIST_FAVORITES = -5, PLAYLIST_RECENTLY_ADDED = -6;
	// SharedPreferences 共享参数
	public final static String KINGSLY = "Kingsly", KINGSLY_PREFERENCES = "kingslypreferences",
			ARTIST_KEY = "artist", ALBUM_KEY = "album", ALBUM_ID_KEY = "albumid", NUMALBUMS = "num_albums",
			GENRE_KEY = "genres", ARTIST_ID = "artistid", NUMWEEKS = "numweeks",
			PLAYLIST_NAME_FAVORITES = "Favorites", PLAYLIST_NAME = "playlist", WIDGET_STYLE="widget_type",
			THEME_PACKAGE_NAME = "themePackageName", THEME_DESCRIPTION = "themeDescription",
			THEME_PREVIEW = "themepreview", THEME_TITLE = "themeTitle", VISUALIZATION_TYPE="visualization_type",
			UP_STARTS_ALBUM_ACTIVITY = "upStartsAlbumActivity", TABS_ENABLED = "tabs_enabled";


	public static final String MIME_TYPE="mimetype",Intent_Action="action",SCHEME_DATA="data";

	//Image Loading Constants 图像载入常量
	public final static String TYPE_ARTIST = "artist", TYPE_ALBUM = "album", TYPE_GENRE = "genre",
			TYPE_PLAYLIST  = "playlist", ALBUM_SUFFIX = "albartimg", ARTIST_SUFFIX = "artstimg",
			PLAYLIST_SUFFIX = "plylstimg", GENRE_SUFFIX = "gnreimg", SRC_FIRST_AVAILABLE = "first_avail",
			SRC_LASTFM = "last_fm", SRC_FILE = "from_file", SRC_GALLERY = "from_gallery",
			SIZE_NORMAL = "normal", SIZE_THUMB = "thumb";
	// Storage Volume 存储音量
	public final static String EXTERNAL = "external";

	/**
	 * 临时目录
	 */
	public final static String PATH_TEMP = Environment
			.getExternalStorageDirectory() + File.separator + "haplayer";

	/**
	 * 专辑图
	 */
	public final static String PATH_ALBUM = PATH_TEMP + File.separator
			+ "album";


	public final static String[] imageThumbUrls = new String[] {
			"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383248_3693.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383243_5120.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_3127.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_9576.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383242_1721.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383219_5806.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383214_7794.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_4418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383213_3557.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383210_8779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383172_4577.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_3407.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_2224.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383166_7301.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383165_7197.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383150_8410.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383131_3736.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_5094.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383130_7393.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383129_8813.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383100_3554.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383093_7894.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_2432.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383092_3071.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383091_3119.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_6589.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_8814.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383059_2237.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383058_4330.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383038_3602.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_3079.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_8125.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382942_4881.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_4559.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382941_3845.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382924_8955.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_2141.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382923_8437.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_6166.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382922_4843.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382905_5804.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_3362.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_2312.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382904_4960.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382900_2418.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_4490.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382881_5935.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_3865.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382880_4662.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382879_2553.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_5375.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382862_1748.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_7618.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8606.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382861_8949.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382841_9821.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6603.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_2405.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382840_6354.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382839_5779.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_7578.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382810_2436.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_3883.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382809_6269.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382808_4179.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382790_8326.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_7174.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_5170.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382789_4118.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382788_9532.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_3184.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382767_4772.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_4924.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382766_5762.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406382765_7341.jpg" };

}
