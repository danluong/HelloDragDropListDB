package com.example.hellodragdroplistdb;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mobeta.android.dslv.DragSortController;

public class TestBedDSLV extends Activity {

	private int mNumHeaders = 0;
	private int mNumFooters = 0;

	private int mDragStartMode = DragSortController.ON_DRAG;
	private boolean mRemoveEnabled = true;
	private int mRemoveMode = DragSortController.FLING_REMOVE;
	private boolean mSortEnabled = true;
	private boolean mDragEnabled = true;

	private String mTag = "dslvTag";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_bed_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.test_bed, getNewDslvFragment(), mTag).commit();
		}
	}

	private Fragment getNewDslvFragment() {
		DSLVFragmentClicks f = DSLVFragmentClicks.newInstance(mNumHeaders,
				mNumFooters);
		f.removeMode = mRemoveMode;
		f.removeEnabled = mRemoveEnabled;
		f.dragStartMode = mDragStartMode;
		f.sortEnabled = mSortEnabled;
		f.dragEnabled = mDragEnabled;
		return f;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.main_activity_actions, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_add:
			createItem();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void createItem() {

		// start new Intent for result, which gets name and details
		Intent i = new Intent(getApplicationContext(), ItemCreateActivity.class);
		startActivity(i);

	}
	
}
