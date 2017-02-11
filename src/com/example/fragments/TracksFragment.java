package com.example.fragments;
 
import com.example.laibomusic.R;
import com.example.laibomusic.adapter.TracksAdapter;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TracksFragment extends Fragment {

	private TracksAdapter trackadapter;
	private ListView listview;

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		trackadapter= new TracksAdapter(getActivity(), R.layout.listview_items, null, null, null, 0);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.listview, container, false);
		listview = (ListView) view.findViewById(R.id.listview_playlist);
		return view;
	}

	
}
