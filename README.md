HighContentListView
===================

Want to speed up the loading of images?  Load low res on scrolling and load higher res when it stops scrolling!

This puts all your "getView" code in one place, rather than both in your ListAdapter and your ScrollListener.  

To get started using it, put a HighContentListView in your xml layout

````
<com.HighContentListView.HighContentListView
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:id="@+id/yourid"
/>
````

Then, make a class that implements 
