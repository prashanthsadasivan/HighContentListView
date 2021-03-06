package com.HighContentListView;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class HighContentListView extends ListView {

	private ScrollHandler mScrollHandler;
	private ContentDisplayer mDisplayer;
	private boolean mLoggingEnabled = false;


	private class ScrollHandler implements OnScrollListener {
		private int startLastLoad = -1;
		private int endLastLoad = -1;

		OnScrollListener mWrappedListener;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (mWrappedListener != null) {
				mWrappedListener.onScroll(view, firstVisibleItem,
						visibleItemCount, totalItemCount);
			}
		}

		public void wrapListener(OnScrollListener l) {
			mWrappedListener = l;
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mWrappedListener != null) {
				mWrappedListener.onScrollStateChanged(view, scrollState);
			}

			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				displayContentOnIdle(view);
			} else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
				logMsg("TOUCH_SCROLL");
			} else {
				logMsg("FLING");
			}
		}

		private void displayContentOnIdle(AbsListView view) {
			int firstVisible = view.getFirstVisiblePosition();
			int lastVisible = view.getLastVisiblePosition();

			for (int i = firstVisible; i <= lastVisible; i++) {
				if (i < startLastLoad || i > endLastLoad) {

					if (mDisplayer != null) {
						logMsg("displaying high res content for position: " + i);
						mDisplayer.displayHighResContent(
								view.getChildAt(i - firstVisible), i,
								view.getAdapter());

					}
				}
			}
			startLastLoad = firstVisible;
			endLastLoad = lastVisible;
		}
	}

	public static class HighContentArrayAdapter<T> extends ArrayAdapter<T> {

		private ContentDisplayer mContentDisplayer;

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

		public void setDisplayer(ContentDisplayer displayer) {
			this.mContentDisplayer = displayer;
		}

		public void addAll(Collection<T> collection) {
			for (T o : collection) {
				add(o);
			}
		}
		

		@Override
		public final View getView(int position, View convertView,
				ViewGroup parent) {
			View toReturn = null;
			if (convertView == null) {
				toReturn = this.mContentDisplayer.setUpHolders(position,
						convertView, parent, LayoutInflater.from(getContext()));
				toReturn = this.mContentDisplayer.displayHighResContent(
						toReturn, position, this);

			} else {
				toReturn = convertView;
				toReturn = this.mContentDisplayer.displayLowResContent(
						toReturn, position, this);
			}
			return toReturn;
		}

	}

	public HighContentListView(Context context) {
		super(context);
		init();
	}

	public HighContentListView(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	public HighContentListView(Context context, AttributeSet set, int defStyle) {
		super(context, set, defStyle);
		init();
	}

	private void init() {
		mScrollHandler = new ScrollHandler();
	}

	@Override
	public final void setOnScrollListener(OnScrollListener l) {
		mScrollHandler.wrapListener(l);
	}

	@SuppressWarnings("rawtypes")
	public void setContentDisplayer(ContentDisplayer displayer) {
		mDisplayer = displayer;
		HighContentArrayAdapter adapter = (HighContentArrayAdapter) getAdapter();
		if (adapter != null) {
			super.setOnScrollListener(mScrollHandler);
			adapter.setDisplayer(displayer);
		}
	}

	@Override
	@SuppressWarnings("rawtypes")
	public final void setAdapter(ListAdapter adapter) {
		HighContentArrayAdapter HCAdapter = null;
		if (adapter != null) {
			try {
				HCAdapter = (HighContentArrayAdapter) adapter;
			} catch (ClassCastException e) {
				throw new RuntimeException(
						"You must set a HighContentAdapter as the adapter for a HighContentListView!");
			}
		}
		super.setAdapter(adapter);
	}

	public void enableLogging(boolean loggingEnabled) {
		mLoggingEnabled = loggingEnabled;
	}

	private void logMsg(String message) {
		if (mLoggingEnabled)
			Log.d("HighContentListView", message);
	}

}
