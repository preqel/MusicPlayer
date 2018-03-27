package com.example.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.laibomusic.NowPlayingCursor;
import com.example.laibomusic.R;
import com.example.laibomusic.adapter.AlllistAdapter;
import com.example.laibomusic.service.MusicService;
import com.example.laibomusic.util.MusicUtils;
import com.example.widget.Sidebar;


@SuppressLint("ValidFragment")
public class AllSongFragment extends Fragment implements LoaderCallbacks<Cursor>, OnItemClickListener {
    private ListView listview;
    private AlllistAdapter alllistadapter;
    private String mCurrentPlaylistMusicId;
    private Sidebar sidebar;
    private int mPlaylistIdIndex, mPlaylistNameIndex;
    private Cursor mCursor;

    public AllSongFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.listview, null);
        this.listview = (ListView) root.findViewById(R.id.listview_playlist);
        this.sidebar = (Sidebar) root.findViewById(R.id.sidebar);
        sidebar.setVisibility(View.VISIBLE);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Cursor cursor = null;
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;//媒体扫描音乐
        String[] projection = new String[]{
                BaseColumns._ID, Audio.Media.TITLE, Audio.Media.ARTIST
        };
        String selection = AudioColumns.IS_MUSIC + "=1 AND "
                + AudioColumns.DURATION + " > 10000";
        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        initHeadView();
        alllistadapter = new AlllistAdapter(getActivity(),
                R.layout.listview_items, null, new String[]{}, new int[]{},
                0);
        listview.setAdapter(alllistadapter);
        listview.setOnItemClickListener(this);
        sidebar.setListView(listview);

        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onStart() {
        IntentFilter intentfliter = new IntentFilter();
        intentfliter.addAction(MusicService.META_CHANGED);
        getActivity().registerReceiver(mMediaStatusReceiver, intentfliter);
        super.onStart();
    }


    public BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            alllistadapter.notifyDataSetChanged();
        }

    };

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(mMediaStatusReceiver);
        super.onStop();
    }

    /**
     * listview增加头
     */
    private void initHeadView() {
        LinearLayout headerview = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.listview_header, null);
        listview.addHeaderView(headerview);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.listview_header_shuffle, null);
        listview.addHeaderView(view);
        view.findViewById(R.id.imageView1).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shuffleAll();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        position = position - 2;   //to-do
        if (position <= 0) position = 0;
        long[] test = MusicUtils.getQueue();
//		for(int i = 0 ;i < test.length; i ++)
//			Log.d("TAG","i __ " + test[i]);
        if (mCursor instanceof NowPlayingCursor) {
            if (MusicUtils.service != null)
                MusicUtils.setQueuePosition(position);
        }
        MusicUtils.playAll(getActivity(), mCursor, position);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                BaseColumns._ID, Audio.Media.TITLE, Audio.Media.ARTIST
        };
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = AudioColumns.IS_MUSIC + " = 1 AND "
                + AudioColumns.DURATION + " > 10000";
        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        return new CursorLoader(getActivity(), uri, projection, selection, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null) {
            return;
        }
        // mPlaylistIdIndex = data.getColumnIndexOrThrow(BaseColumns._ID);
        //  mPlaylistNameIndex = data.getColumnIndexOrThrow(PlaylistsColumns.NAME);
        mCursor = data;
        alllistadapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        alllistadapter.changeCursor(null);
    }

    class MylistView extends ExpandableListView {

        public MylistView(Context context) {
            super(context);
        }
    }

    /**
     * Shuffle all the tracks
     */
    public void shuffleAll() {
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                BaseColumns._ID
        };
        String selection = AudioColumns.IS_MUSIC + "=1";
        String sortOrder = Audio.Media.DEFAULT_SORT_ORDER;
        Cursor cursor = MusicUtils.query(this.getActivity(), uri, projection, selection, null, sortOrder);
        if (cursor != null) {
            MusicUtils.shuffleAll(this.getActivity(), cursor);
            cursor.close();
            cursor = null;
        }
    }

}
