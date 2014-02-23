package com.example.hellodragdroplistdb;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

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
}
