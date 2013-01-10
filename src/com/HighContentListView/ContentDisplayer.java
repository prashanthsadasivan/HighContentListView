package com.HighContentListView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public interface ContentDisplayer {
	public View displayLowResContent(View v, int position,
			ListAdapter adapter);

	public View displayHighResContent(View v, int position,
			ListAdapter adapter);

	public View setUpHolders(int position, View convertView,
			ViewGroup parent, LayoutInflater i);
}