package com.example.laibomusic.util;

import static com.example.laibomusic.util.Consts.EXTERNAL;
import static com.example.laibomusic.util.Consts.PLAYLIST_NAME_FAVORITES;

import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;

import com.example.laibomusic.IMusicService;
import com.example.laibomusic.R;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.service.ServiceBinder;
import com.example.laibomusic.service.ServiceToken;

public class MusicUtils {

    private final static StringBuilder sFormatBuilder = new StringBuilder();
    public static IMusicService service = null;
    static HashMap<Context,ServiceBinder> sConnectionMap = new HashMap<Context,ServiceBinder>();
    private final static Formatter sFormatter = new Formatter(sFormatBuilder, Locale.getDefault());
    private final static long[] sEmptyList = new long[0];
    private static final Object[] sTimeArgs = new Object[5];
    private static ContentValues[] sContentValuesCache = null;



    public static ServiceToken bindToService (Context act){
        return bindToService(act,null);
    }

    public static ServiceToken bindToService(Context act,ServiceConnection serviceconection){
        Activity realactivity = ((Activity)act).getParent();
        if(realactivity == null){
            realactivity = (Activity) act;
        }
        ContextWrapper cw = new ContextWrapper(realactivity);
        cw.startService(new Intent(cw,MusicService.class));
        ServiceBinder sb  = new ServiceBinder(serviceconection);
        if(cw.bindService(new Intent().setClass(cw, MusicService.class), sb	, 0)){
            sConnectionMap.put(cw, sb);
            return new ServiceToken(cw);
        }
        return null;
    }


    public static void unbindFromService(ServiceToken servicetoken ){
        if(servicetoken == null)return ;
        ContextWrapper cw = servicetoken.mContextWrapper;
        ServiceBinder sb = 	sConnectionMap.remove(cw);
        if(sb == null)return ;
        cw.unbindService(sb);
        if(sConnectionMap.isEmpty())service = null;

    }
    public static  long getDuration(){
        if(service != null){
            try {
                return service.duration();
            } catch (RemoteException e) {
                return 0;
            }
        }
        else return 0;

    }



//	/**
//     * @param context
//     * @param list
//     * @param position
//     * @param force_shuffle
//     */
//    private static void playAll(Context context, long[] list, int position, boolean force_shuffle) { //全部播放
//        if (list.length == 0 || service == null) {
//            return;
//        }
//        try {
////            if (force_shuffle) {
////                service.setShuffleMode(MusicService.SHUFFLE_NORMAL);
////            }
//            long curid = service.getAudioId();
//            int curpos = service.getQueuePosition();
//            if (position != -1 && curpos == position && curid == list[position]) { //被选择的文件是正在播放的文件，判断是否需要重新启动新的播放列表或者只是启动回放的activity
//                long[] playlist = service.getQueue();
//                if (Arrays.equals(list, playlist)) { //无需设置新的列表，但在需要的时候需要恢复回放
//                    service.play();
//                    return;
//                }
//            }
//            if (position < 0) {
//                position = 0;
//            }
//            service.open(list, force_shuffle ? -1 : position);
//            service.play();
//        } catch (RemoteException ex) {
//            ex.printStackTrace();
//        }
//    }
//	
    /**
     * @param context
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
        return query(context, uri, projection, selection, selectionArgs, sortOrder, 0);
    }
    /**
     * @param context
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @param limit
     * @return
     */
    public static Cursor query(Context context, Uri uri, String[] projection, String selection,
                               String[] selectionArgs, String sortOrder, int limit) {
        try {
            ContentResolver resolver = context.getContentResolver();
            if (resolver == null) {
                return null;
            }
            if (limit > 0) {
                uri = uri.buildUpon().appendQueryParameter("limit", "" + limit).build();
            }
            return resolver.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (UnsupportedOperationException ex) {
            return null;
        }
    }


    /**
     * @param context
     * @param cursor
     */
    public static void shuffleAll(Context context, Cursor cursor) { //随机播放所有歌曲
        playAll(context, cursor, 0, true);
    }

    /**
     * @param context
     * @param cursor
     */
    public static void playAll(Context context, Cursor cursor) { //播放所有歌曲
        playAll(context, cursor, 0, false);
    }

    /**
     * @param context
     * @param cursor
     * @param position
     */
    public static void playAll(Context context, Cursor cursor, int position) {
        playAll(context, cursor, position, false);
    }

    /**
     * @param context
     * @param list
     * @param position
     */
    public static void playAll(Context context, long[] list, int position) {
        playAll(context, list, position, false);
    }

    /**
     * @param context
     * @param cursor
     * @param position
     * @param force_shuffle
     */
    private static void playAll(Context context, Cursor cursor, int position, boolean force_shuffle) {

        long[] list = getSongListForCursor(cursor);
        playAll(context, list, position, force_shuffle);
    }

    /**
     * @param cursor
     * @return
     */
    public static long[] getSongListForCursor(Cursor cursor) {
        if (cursor == null) {
            return sEmptyList;
        }
        int len = cursor.getCount();
        long[] list = new long[len];
        cursor.moveToFirst();
        int colidx = -1;
        try {
            colidx = cursor.getColumnIndexOrThrow(Audio.Playlists.Members.AUDIO_ID);
        } catch (IllegalArgumentException ex) {
            colidx = cursor.getColumnIndexOrThrow(BaseColumns._ID);
        }
        for (int i = 0; i < len; i++) {
            list[i] = cursor.getLong(colidx);
            cursor.moveToNext();
        }
        return list;
    }

    /**
     * @param context
     * @param list
     * @param position
     * @param force_shuffle
     */
    private static void playAll(Context context, long[] list, int position, boolean force_shuffle) { //全部播放
        if (list.length == 0 || service == null) {
            return;
        }
        try {
//            if (force_shuffle) {
//                service.setShuffleMode(MusicService.SHUFFLE_NORMAL);
//            }
            long curid = service.getAudioId();
            int curpos = service.getQueuePosition();
            if (position != -1 && curpos == position && curid == list[position]) { //被选择的文件是正在播放的文件，判断是否需要重新启动新的播放列表或者只是启动回放的activity
                long[] playlist = service.getQueue();
                if (Arrays.equals(list, playlist)) { //无需设置新的列表，但在需要的时候需要恢复回放
                    service.play();
                    return;
                }
            }
            if (position < 0) {
                position = 0;
            }
            service.open(list, force_shuffle ? -1 : position);
            service.play();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }

    public static String getArtistName(Context mContext, long artist_id, boolean default_name) { //获取艺术家名
        String where = BaseColumns._ID + "=" + artist_id;
        String[] cols = new String[] {
                ArtistColumns.ARTIST
        };
        Uri uri = Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, cols, where, null, null);
        if (cursor == null){
            return MediaStore.UNKNOWN_STRING;
        }
        if (cursor.getCount() <= 0) {
            if (default_name)
                return mContext.getString(R.string.unknown);
            else
                return MediaStore.UNKNOWN_STRING;
        } else {
            cursor.moveToFirst();
            String name = cursor.getString(0);
            cursor.close();
            if (name == null || MediaStore.UNKNOWN_STRING.equals(name)) {
                if (default_name)
                    return mContext.getString(R.string.unknown);
                else
                    return MediaStore.UNKNOWN_STRING;
            }
            return name;
        }
    }

    /**
     * @param mContext
     * @param album_id
     * @param default_name
     * @return album name
     */
    public static String getAlbumName(Context mContext, long album_id, boolean default_name) { //获取专辑名
        String where = BaseColumns._ID + "=" + album_id;
        String[] cols = new String[] {
                AlbumColumns.ALBUM
        };
        Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor cursor = mContext.getContentResolver().query(uri, cols, where, null, null);
        if (cursor == null){
            return MediaStore.UNKNOWN_STRING;
        }
        if (cursor.getCount() <= 0) {
            if (default_name)
                return mContext.getString(R.string.unknown);
            else
                return MediaStore.UNKNOWN_STRING;
        } else {
            cursor.moveToFirst();
            String name = cursor.getString(0);
            cursor.close();
            if (name == null || MediaStore.UNKNOWN_STRING.equals(name)) {
                if (default_name)
                    return mContext.getString(R.string.unknown);
                else
                    return MediaStore.UNKNOWN_STRING;
            }
            return name;
        }
    }

    /**
     * @param index
     */
    public static void setQueuePosition(int index) {
        if (service == null)
            return;
        try {
            service.setQueuePosition(index);
        } catch (RemoteException e) {
        }
    }

    /**
     * @return
     */
    public static long[] getQueue() {

        if (service == null)
            return sEmptyList;

        try {
            return service.getQueue();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return sEmptyList;
    }


    /**
     * @return current track ID
     */
    public static long getCurrentAudioId() { //获取当前音频id
        if (MusicUtils.service != null) {
            try {
                return service.getAudioId();
            } catch (RemoteException ex) {
            }
        }
        return -1;
    }

    /**
     * @return current album name
     */
    public static String getAlbumName() { //获取专辑名
        if (service != null) {
            try {
                return service.getAlbumName();
            } catch (RemoteException ex) {
            }
        }
        return null;
    }

    /**
     * @return current track name
     */
    public static String getTrackName() {
        if (service != null) {
            try {
                return service.getTrackName();
            } catch (RemoteException ex) {
            }
        }
        return null;
    }

    /**
     * @param context
     * @param secs
     * @return time String
     */
    public static String TransformSecs(Context context, long secs) {
        String durationformat = context.getString(secs < 3600 ? R.string.durationformatshort
                : R.string.durationformatlong);
        /*
         * Provide multiple arguments so the format can be changed easily by
         * modifying the xml.
         */
        sFormatBuilder.setLength(0);
        final Object[] timeArgs = sTimeArgs;
        timeArgs[0] = secs / 3600;
        timeArgs[1] = secs / 60;
        timeArgs[2] = secs / 60 % 60;
        timeArgs[3] = secs;
        timeArgs[4] = secs % 60;
        return sFormatter.format(durationformat, timeArgs).toString();
    }

    public static String getArtistName() {
        if (service != null) {
            try {
                return service.getArtistName();
            } catch (RemoteException e) {
            }
        }
        return null;
    }


    /**
     * @param context
     * @param name
     * @return
     */
    public static long createPlaylist(Context context, String name) { //创建播放列表
        if (name != null && name.length() > 0) {
            ContentResolver resolver = context.getContentResolver();
            String[] cols = new String[] {
                    PlaylistsColumns.NAME
            };
            String whereclause = PlaylistsColumns.NAME + " = '" + name + "'";
            Cursor cur = resolver.query(Audio.Playlists.EXTERNAL_CONTENT_URI, cols, whereclause,
                    null, null);
            if (cur.getCount() <= 0) {
                ContentValues values = new ContentValues(1);
                values.put(PlaylistsColumns.NAME, name);
                Uri uri = resolver.insert(Audio.Playlists.EXTERNAL_CONTENT_URI, values);
                return Long.parseLong(uri.getLastPathSegment());
            }
            return -1;
        }
        return -1;
    }



    public static boolean  isFavorite(Context context,long id){

        long favorite_id;
        if(id < 0){

        }else{

            ContentResolver contentresolver = context.getContentResolver();
            String favorite_where = PlaylistsColumns.NAME +"='" + PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[]{
                    BaseColumns._ID
            };
            Uri favourite_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = contentresolver.query(favourite_uri, favorites_cols, favorite_where, null, null);
            if(cursor.getCount() <= 0){
                favorite_id = createPlaylist(context, PLAYLIST_NAME_FAVORITES);
            }else{
                cursor.moveToFirst();
                favorite_id  = cursor.getLong(0);
                cursor.close();
            }
            //to-do
            String[] cols = new String[] {
                    Playlists.Members.AUDIO_ID
            };
            Uri uri = Playlists.Members.getContentUri(EXTERNAL, favorite_id);
            Cursor cur = contentresolver.query(uri, cols, null, null, null);

            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                if (cur.getLong(0) == id) {
                    cur.close();
                    return true;
                }
                cur.moveToNext();
            }
            cur.close();
            return false;
        }
        return false;
    }


    /**
     * @param context
     * @return
     */
    public static long getFavoritesId(Context context) { //获取喜欢的歌曲的id
        long favorites_id = -1;
        String favorites_where = PlaylistsColumns.NAME + "='" + "Favorites" + "'";
        String[] favorites_cols = new String[] {
                BaseColumns._ID
        };
        Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
        Cursor cursor = query(context, favorites_uri, favorites_cols, favorites_where, null, null);
        if (cursor.getCount() <= 0) {
            favorites_id = createPlaylist(context, "Favorites");
        } else {
            cursor.moveToFirst();
            favorites_id = cursor.getLong(0);
            cursor.close();
        }
        return favorites_id;
    }


    public static void addToFavorites(Context context,long id){
        long favorites_id;
        if (id < 0) {
        } else {
            ContentResolver resolver = context.getContentResolver();
            String favorites_where = PlaylistsColumns.NAME + "='" + PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                    BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }

            String[] cols = new String[] {
                    Playlists.Members.AUDIO_ID
            };
            Uri uri = Playlists.Members.getContentUri(EXTERNAL, favorites_id);
            Cursor cur = resolver.query(uri, cols, null, null, null);

            int base = cur.getCount();
            cur.moveToFirst();
            while (!cur.isAfterLast()) {
                if (cur.getLong(0) == id)
                    return;
                cur.moveToNext();
            }
            cur.close();
            ContentValues values = new ContentValues();
            values.put(Playlists.Members.AUDIO_ID, id);
            values.put(Playlists.Members.PLAY_ORDER, base + 1);
            resolver.insert(uri, values);
        }

    }

    public static void toggleFavouite() {
        if (service == null)
            return;
        try {
            service.toggleFavorite();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param id
     */
    public static void removeFromFavorites(Context context, long id) { //从喜欢列表中移除不喜欢的歌曲
        long favorites_id;
        if (id < 0) {
        } else {
            ContentResolver resolver = context.getContentResolver();
            String favorites_where = PlaylistsColumns.NAME + "='" + PLAYLIST_NAME_FAVORITES + "'";
            String[] favorites_cols = new String[] {
                    BaseColumns._ID
            };
            Uri favorites_uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor cursor = resolver.query(favorites_uri, favorites_cols, favorites_where, null,
                    null);
            if (cursor.getCount() <= 0) {
                favorites_id = createPlaylist(context, PLAYLIST_NAME_FAVORITES);
            } else {
                cursor.moveToFirst();
                favorites_id = cursor.getLong(0);
                cursor.close();
            }
            Uri uri = Playlists.Members.getContentUri(EXTERNAL, favorites_id);
            resolver.delete(uri, Playlists.Members.AUDIO_ID + "=" + id, null);
        }
    }



    /**
     * @param context
     * @param numalbums
     * @param numsongs
     * @param isUnknown
     * @return a string based on the number of albums for an artist or songs for
     *         an album
     */
    public static String makeAlbumsLabel(Context mContext, int numalbums, int numsongs, boolean isUnknown) { //制作专辑标签
        StringBuilder songs_albums = new StringBuilder();
        Resources r = mContext.getResources();
        if (isUnknown) {
            String f = r.getQuantityText(R.plurals.Nsongs, numsongs).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f, Integer.valueOf(numsongs));
            songs_albums.append(sFormatBuilder);
        } else {
            String f = r.getQuantityText(R.plurals.Nalbums, numalbums).toString();
            sFormatBuilder.setLength(0);
            sFormatter.format(f, Integer.valueOf(numalbums));
            songs_albums.append(sFormatBuilder);
            songs_albums.append("\n");
        }
        return songs_albums.toString();
    }


}
