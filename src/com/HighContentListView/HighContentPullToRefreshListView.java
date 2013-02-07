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
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class HighContentPullToRefreshListView extends PullToRefreshListView {

	private ScrollHandler mScrollHandler;
	private static boolean mLoggingEnabled = false;

	public static class ScrollHandler implements OnScrollListener {
		private int startLastLoad = -1;
		private int endLastLoad = -1;
		private ContentDisplayer mDisplayer;
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
				if (firstVisible != 0) {
					if (i < startLastLoad || i > endLastLoad) {

						if (mDisplayer != null) {
							logMsg("displaying high res content for position: "
									+ i);
							mDisplayer.displayHighResContent(
									view.getChildAt(i - firstVisible), i,
									view.getAdapter());

						}
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
				toReturn = mDisplayer.setUpHolders(position, convertView,
						parent, LayoutInflater.from(getContext()));
			} else {
				toReturn = convertView;
			}
			toReturn = mDisplayer
					.displayLowResContent(toReturn, position, this);
			return toReturn;
		}
	}

	public HighContentPullToRefreshListView(Context context) {
		super(context);
		init();
	}

	public HighContentPullToRefreshListView(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	private void init() {
		mScrollHandler = new ScrollHandler();
		setOnScrollListener(mScrollHandler);
	}
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		throw new RuntimeException("You should be calling setContentDisplayerForAdapter instead!");
	}

	/**
	 * Use this instead of setAdapter!  
	 * @param displayer
	 * @param adapter
	 */
	public void setContentDisplayerForAdapter(ContentDisplayer displayer,
			HighContentArrayAdapter<?> adapter) {
		adapter.setDisplayer(displayer);
		mScrollHandler = new ScrollHandler();
		mScrollHandler.mDisplayer = displayer;
		super.setAdapter(adapter);
		setOnScrollListener(mScrollHandler);
	}

	public void enableLogging(boolean loggingEnabled) {
		mLoggingEnabled = loggingEnabled;
	}

	private static void logMsg(String message) {
		if (mLoggingEnabled)
			Log.d("HighContentListView", message);
	}
}
