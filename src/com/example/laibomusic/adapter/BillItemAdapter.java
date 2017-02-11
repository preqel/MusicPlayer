package com.example.laibomusic.adapter;

import java.util.ArrayList;
import java.util.List;

import com.example.laibomusic.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BillItemAdapter extends BaseAdapter {

	ArrayList<String> lists;
	private Context context;
	public BillItemAdapter(Context context, ArrayList<String> lists){
		this.context = context;
		this.lists = lists;
	}
	
	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(
				R.layout.listview_items, null);
		return view;
	}

}
