package com.example.laibomusic.adapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.example.laibomusic.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class ScrollingTabsAdapter implements TabAdapter {

	private final Activity activity;

	public ScrollingTabsAdapter(Activity activity) {
		this.activity = activity;
	}

	@Override
	public View getView(int position) {
		LayoutInflater inflater = activity.getLayoutInflater();
		
		final Button tab = (Button) inflater.inflate(R.layout.tab, null);
		
		final String[] mtitles = activity.getResources().getStringArray(R.array.tab_titles);
       Set<String > defaults = new HashSet<String>(Arrays.asList(mtitles));
       
       if(position<defaults.size()){
    	   tab.setText(mtitles[position].toUpperCase());
       }
	    return tab;
	}

}
