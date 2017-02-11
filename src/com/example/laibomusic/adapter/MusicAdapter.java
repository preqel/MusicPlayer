package com.example.laibomusic.adapter;

import java.util.List;

import com.example.laibomusic.R;
import com.example.laibomusic.entity.Music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MusicAdapter extends BaseAdapter { 
	private List<Music>  lists;  
	LayoutInflater layoutinflater; 
	
	public MusicAdapter(Context context,List<Music> list)
	{
		this.lists= list;
		this.layoutinflater= LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return lists.size();
	} 
	@Override
	public Object getItem(int arg0) {
		return lists.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
        ViewHolder 		viewholder =null;
		if( convertView   == null){
			viewholder = new ViewHolder();
			convertView = this.layoutinflater
					.inflate(R.layout.music_item, null);
			viewholder.musicName = (TextView) convertView
					.findViewById(R.id.music_name);
			viewholder.musicSinger = (TextView) convertView
					.findViewById(R.id.music_singer);
			viewholder.musicTime = (TextView) convertView
					.findViewById(R.id.music_item_name);
			convertView.setTag(viewholder);
		}else
			viewholder= (ViewHolder) convertView.getTag(); 
		Music m = this.lists.get(position);
		viewholder.musicSinger.setText(m.getSinger());
		viewholder.musicName.setText(m.getName());
		viewholder.musicTime.setText("40");  
		return convertView;
	}

	class ViewHolder {
		TextView musicName;
		TextView musicSinger;
		TextView musicTime;

	}
}
