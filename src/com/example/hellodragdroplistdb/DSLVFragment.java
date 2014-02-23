package com.example.hellodragdroplistdb;

import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.hellodragdroplistdb.contentprovider.MyItemContentProvider;
import com.example.hellodragdroplistdb.database.Items;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

public class DSLVFragment extends ListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	ArrayAdapter<String> adapter;

	private DragSortListView mDslv;
	private DragSortController mController;

	public int dragStartMode = DragSortController.ON_DOWN;
	public boolean removeEnabled = false;
	public int removeMode = DragSortController.FLING_REMOVE;
	public boolean sortEnabled = true;
	public boolean dragEnabled = true;

	private static final String[] PROJECTION = { Items.COLUMN_NAME,
			Items.COLUMN_ID };

	// The loader's unique id. Loader ids are specific to the Activity or
	// Fragment in which they reside.
	private static final int LOADER_ID = 1;

	// The callbacks through which we will interact with the LoaderManager.
	private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;

	// The adapter that binds our data to the ListView
	private SimpleCursorAdapter mAdapter;

	private DragSortListView.DropListener onDrop = new DragSortListView.DropListener() {
		@Override
		public void drop(int from, int to) {
			if (from != to) {
				// String item = adapter.getItem(from);
				// adapter.remove(item);
				// adapter.insert(item, to);
			}
		}
	};

	private DragSortListView.RemoveListener onRemove = new DragSortListView.RemoveListener() {
		@Override
		public void remove(int which) {
			// deleting item based on database element ID
			Uri mItemUri = Uri.parse(MyItemContentProvider.CONTENT_URI + "/"
					+ mDslv.getItemIdAtPosition(which));
			Log.w(this.getClass().toString(),
					"deleted item: " + mItemUri.toString());
			getActivity().getContentResolver().delete(mItemUri, null, null);
		}
	};

	public static DSLVFragment newInstance(int headers, int footers) {
		DSLVFragment f = new DSLVFragment();

		Bundle args = new Bundle();
		args.putInt("headers", headers);
		args.putInt("footers", footers);
		f.setArguments(args);

		return f;
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDslv = (DragSortListView) inflater.inflate(getLayout(), container,
				false);

		mController = buildController(mDslv);
		mDslv.setFloatViewManager(mController);
		mDslv.setOnTouchListener(mController);
		mDslv.setDragEnabled(dragEnabled);

		return mDslv;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mDslv = (DragSortListView) getListView();

		mDslv.setDropListener(onDrop);
		mDslv.setRemoveListener(onRemove);

		Bundle args = getArguments();
		int headers = 0;
		int footers = 0;
		if (args != null) {
			headers = args.getInt("headers", 0);
			footers = args.getInt("footers", 0);
		}

		for (int i = 0; i < headers; i++) {
			addHeader(getActivity(), mDslv);
		}
		for (int i = 0; i < footers; i++) {
			addFooter(getActivity(), mDslv);
		}

		setListAdapter();
	}

	/**
	 * Called in onCreateView. Override this to provide a custom
	 * DragSortController.
	 */
	public DragSortController buildController(DragSortListView dslv) {
		// defaults are
		// dragStartMode = onDown
		// removeMode = flingRight
		DragSortController controller = new DragSortController(dslv);
		controller.setDragHandleId(R.id.drag_handle);
		controller.setClickRemoveId(R.id.click_remove);
		controller.setRemoveEnabled(removeEnabled);
		controller.setSortEnabled(sortEnabled);
		controller.setDragInitMode(dragStartMode);
		controller.setRemoveMode(removeMode);
		return controller;
	}

	/**
	 * Called from DSLVFragment.onActivityCreated(). Override to set a different
	 * adapter.
	 */
	public void setListAdapter() {
		// Columns from the data to bind
		String[] from = new String[] { Items.COLUMN_NAME };

		// The views to which the data will be bound
		int[] to = new int[] { R.id.text };

		// Initialize the adapter. Note that we pass a 'null' Cursor as the
		// third argument. We will pass the adapter a Cursor only when the
		// data has finished loading for the first time (i.e. when the
		// LoaderManager delivers the data to onLoadFinished). Also note
		// that we have passed the '0' flag as the last argument. This
		// prevents the adapter from registering a ContentObserver for the
		// Cursor (the CursorLoader will do this for us!).
		mAdapter = new SimpleCursorAdapter(getActivity(), getItemLayout(),
				null, from, to, 0);
		// list = new ArrayList<String>(Arrays.asList(array));
		//
		// adapter = new ArrayAdapter<String>(getActivity(), getItemLayout(),
		// R.id.text, list);

		// Associate the (now empty) adapter with the ListView.
		setListAdapter(mAdapter);

		// The Activity (which implements the LoaderCallbacks<Cursor>
		// interface) is the callbacks object through which we will interact
		// with the LoaderManager. The LoaderManager uses this object to
		// instantiate the Loader and to notify the client when data is made
		// available/unavailable.
		mCallbacks = this;

		// Initialize the Loader with id '1' and callbacks 'mCallbacks'.
		// If the loader doesn't already exist, one is created. Otherwise,
		// the already created Loader is reused. In either case, the
		// LoaderManager will manage the Loader across the Activity/Fragment
		// lifecycle, will receive any new loads once they have completed,
		// and will report this new data back to the 'mCallbacks' object.
		LoaderManager lm = getLoaderManager();
		lm.initLoader(LOADER_ID, null, mCallbacks);

		// array = getResources().getStringArray(R.array.jazz_artist_names);
		// list = new ArrayList<String>(Arrays.asList(array));
		//
		// adapter = new ArrayAdapter<String>(getActivity(), getItemLayout(),
		// R.id.text, list);
		// setListAdapter(adapter);
	}

	protected int getLayout() {
		// this DSLV xml declaration does not call for the use
		// of the default DragSortController; therefore,
		// DSLVFragment has a buildController() method.
		return R.layout.dslv_fragment_main;
	}

	/**
	 * Return list item layout resource passed to the ArrayAdapter.
	 */
	protected int getItemLayout() {
		/*
		 * if (removeMode == DragSortController.FLING_LEFT_REMOVE || removeMode
		 * == DragSortController.SLIDE_LEFT_REMOVE) { return
		 * R.layout.list_item_handle_right; } else
		 */
		if (removeMode == DragSortController.CLICK_REMOVE) {
			return R.layout.list_item_click_remove;
		} else {
			return R.layout.list_item_handle_left;
		}
	}

	public DragSortController getController() {
		return mController;
	}

	public static void addHeader(Activity activity, DragSortListView dslv) {
		LayoutInflater inflater = activity.getLayoutInflater();
		int count = dslv.getHeaderViewsCount();

		TextView header = (TextView) inflater.inflate(R.layout.header_footer,
				null);
		header.setText("Header #" + (count + 1));

		dslv.addHeaderView(header, null, false);
	}

	public static void addFooter(Activity activity, DragSortListView dslv) {
		LayoutInflater inflater = activity.getLayoutInflater();
		int count = dslv.getFooterViewsCount();

		TextView footer = (TextView) inflater.inflate(R.layout.header_footer,
				null);
		footer.setText("Footer #" + (count + 1));

		dslv.addFooterView(footer, null, false);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// Create a new CursorLoader with the following query parameters.
		return new CursorLoader(getActivity(),
				MyItemContentProvider.CONTENT_URI, PROJECTION, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		// A switch-case is useful when dealing with multiple Loaders/IDs
		switch (loader.getId()) {
		case LOADER_ID:
			// The asynchronous load is complete and the data
			// is now available for use. Only now can we associate
			// the queried Cursor with the SimpleCursorAdapter.
			mAdapter.swapCursor(cursor);
			break;
		}
		// The listview now displays the queried data.
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// For whatever reason, the Loader's data is now unavailable.
		// Remove any references to the old data by replacing it with
		// a null Cursor.
		mAdapter.swapCursor(null);
	}

}
