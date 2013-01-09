package com.boostedlistview;

import java.lang.reflect.Array;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HighContentListView extends ListView {

	private ScrollHandler mScrollHandler = new ScrollHandler();
	private ContentDisplayer mDisplayer;

	public interface ContentDisplayer {
		public void displayLowResContent(View v, int position);

		public void displayHighResContent(View v, int position);

		public View setUpHolders(int position, View convertView,
				ViewGroup parent);
	}

	private class ScrollHandler implements OnScrollListener {
		private int startLastLoad;
		private int endLastLoad;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				displayContentOnIdle(view);
			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				System.out.println("TOUCH_SCROLL");
			} else {
				System.out.println("FLING");
			}
		}

		private void displayContentOnIdle(AbsListView view) {

			int firstVisible = view.getFirstVisiblePosition();
			int lastVisible = view.getLastVisiblePosition();

			for (int i = firstVisible; i <= lastVisible; i++) {
				if (i < startLastLoad || i > endLastLoad) {

					if (mDisplayer != null) {
						mDisplayer.displayHighResContent(
								view.getChildAt(i - firstVisible), i);

					}
				}
			}
			startLastLoad = firstVisible;
			endLastLoad = lastVisible;
		}
	}

	public static class HighContentArrayAdapter<T> extends ArrayAdapter<T> {
		
		private ContentDisplayer mDisplayer;

		public HighContentArrayAdapter(Context context, int textViewResourceId,
				T[] objects) {
			super(context, textViewResourceId, objects);
		}

		public HighContentArrayAdapter(Context context, int textViewResourceId,
				List<T> objects) {
			super(context, textViewResourceId, objects);
		}

		public HighContentArrayAdapter(Context context, int resource,
				int textViewResourceId, T[] objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public HighContentArrayAdapter(Context context, int resource,
				int textViewResourceId, List<T> objects) {
			super(context, resource, textViewResourceId, objects);
		}

		public HighContentArrayAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
		}

		public HighContentArrayAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		private void setDisplayer(ContentDisplayer displayer) {
			mDisplayer = displayer;
		}

		@Override
		public final View getView(int position, View convertView,
				ViewGroup parent) {
			View toReturn = null;
			if (convertView == null) {
				toReturn = mDisplayer.setUpHolders(position, convertView, parent);
			} else {
				toReturn = convertView;
			}
			
			mDisplayer.displayLowResContent(toReturn, position);
			return toReturn;
		}

	}

	public HighContentListView(Context context) {
		super(context);
		setOnScrollListener(mScrollHandler);
	}

	@Override
	public final void setOnScrollListener(OnScrollListener l) {
		super.setOnScrollListener(l);
	}

	public void setContentDisplayer(ContentDisplayer displayer) {
		mDisplayer = displayer;
	}

	@Override
	public final void setAdapter(ListAdapter adapter) {
		HighContentArrayAdapter HCAdapter = null;
		if (adapter != null) {
			try {
			HCAdapter = (HighContentArrayAdapter) adapter;
			} catch (ClassCastException e) {
				throw new RuntimeException("You must set a HighContentAdapter as the adapter for a HighContentListView!");
			}
		}
		HCAdapter.setDisplayer(mDisplayer);
		super.setAdapter(adapter);
	}

}
