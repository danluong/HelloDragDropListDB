package com.example.hellodragdroplistdb;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import com.example.hellodragdroplistdb.contentprovider.MyItemContentProvider;
import com.example.hellodragdroplistdb.database.Items;

public class ItemCreateActivity extends Activity {
	private Uri mItemUri;
	private EditText mItemNameEditText;
	private EditText mItemDetailEditText;

	private String mItemName;
	private String mItemDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_item_new);

		mItemNameEditText = (EditText) findViewById(R.id.new_item_name_editText);
		mItemDetailEditText = (EditText) findViewById(R.id.new_item_detail_editText);

		Bundle extras = getIntent().getExtras();

		// check from the saved Instance
		mItemUri = (savedInstanceState == null) ? null
				: (Uri) savedInstanceState
						.getParcelable(MyItemContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity
//		if (extras != null) {
//			mItemUri = extras
//					.getParcelable(MyItemContentProvider.CONTENT_ITEM_TYPE);
//
//			fillData(mItemUri);
//		}

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveState();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(MyItemContentProvider.CONTENT_ITEM_TYPE,
				mItemUri);
	}

	private void saveState() {
		String name = mItemNameEditText.getText().toString();
		String itemDetail = mItemDetailEditText.getText().toString();

		// only save if either summary or description
		// is available

		if (name.length() == 0) {
			if (itemDetail.length() == 0) {
				return;
			} else if (itemDetail.length() != 0) {
				name = "Untitled";
			}
		}

		ContentValues values = new ContentValues();
		values.put(Items.COLUMN_NAME, name);
		values.put(Items.COLUMN_ITEM_DETAIL, itemDetail);

		if (mItemUri == null) {
			// New item
			mItemUri = getContentResolver().insert(
					MyItemContentProvider.CONTENT_URI, values);
		} else {
			// Update item
			getContentResolver().update(mItemUri, values, null, null);
		}
	}

	private void fillData(Uri uri) {
		String[] projection = { Items.COLUMN_NAME, Items.COLUMN_ITEM_DETAIL };
		Cursor cursor = getContentResolver().query(uri, projection, null, null,
				null);

		if (cursor != null) {
			cursor.moveToFirst();

			mItemName = cursor.getString(cursor
					.getColumnIndex(Items.COLUMN_NAME));
			mItemDetail = cursor.getString(cursor
					.getColumnIndex(Items.COLUMN_ITEM_DETAIL));

			if (mItemName != null) {
				mItemNameEditText.setText(mItemName);
			}
			if (mItemDetail != null) {
				mItemDetailEditText.setText(mItemName);
			}
		}

		// always close the cursor
		cursor.close();
	}
}
