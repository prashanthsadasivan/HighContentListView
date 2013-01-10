HighContentListView
===================

Want to speed up the loading of images in a listview?  Load low res on scrolling and load higher res when it stops scrolling!

This puts all your "getView" code in one place, rather than both in your ListAdapter and your ScrollListener.  

To get started using it, put a HighContentListView in your xml layout

````xml
<com.HighContentListView.HighContentListView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:id="@+id/yourid"
/>
````

Then, make a class that implements the ContentDisplayer Interface.  The interface has three methods, one for displaying the low res content, one for displaying the high res content, and one for setting up the view holders, as you normally would in an adapter's getView method.

Finally, in the Activity that hosts this list view, simply do the following:

````java
mList = (HighContentListView) findViewById(R.id.yourid);
mAdapter = new HighContentListView<YourObject>(this, R.layout.your_layout, mYourDataList);
mList.setAdapter(mAdapter);
mList.setContentDisplayer(new YourContentDisplayer());
````

and thats it!  When your list view is scrolling, it will create your list items using displayLowResContent, and once the scroll state is idle, it will use displayHighResContent.



