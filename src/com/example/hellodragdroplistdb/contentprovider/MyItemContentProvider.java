package com.example.hellodragdroplistdb.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.example.hellodragdroplistdb.database.ItemSQLiteOpenHelper;
import com.example.hellodragdroplistdb.database.Items;


public class MyItemContentProvider extends ContentProvider {

	// database
	private ItemSQLiteOpenHelper database;

	// used for the UriMacher
	private static final int URI_ITEMS = 10;
	private static final int URI_ITEMS_ID = 20;

	private static final String AUTHORITY = "com.example.hellodragdroplistdb.contentprovider";

	private static final String BASE_PATH = "items";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/items";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/item";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, URI_ITEMS);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", URI_ITEMS_ID);
	}

	@Override
	public boolean onCreate() {
		database = new ItemSQLiteOpenHelper(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		// Uisng SQLiteQueryBuilder instead of query() method
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// check if the caller has requested a column which does not exists
		checkColumns(projection);

		// Set the table
		queryBuilder.setTables(Items.TABLE_ITEM);

		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case URI_ITEMS:
			break;
		case URI_ITEMS_ID:
			// adding the COLUMN_ID to the original query
			queryBuilder.appendWhere(Items.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();
		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case URI_ITEMS:
			id = sqlDB.insert(Items.TABLE_ITEM, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(CONTENT_URI + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case URI_ITEMS:
			rowsDeleted = sqlDB.delete(Items.TABLE_ITEM, selection,
					selectionArgs);
			break;
		case URI_ITEMS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = sqlDB.delete(Items.TABLE_ITEM,
						Items.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = sqlDB.delete(Items.TABLE_ITEM,
						Items.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {

		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case URI_ITEMS:
			rowsUpdated = sqlDB.update(Items.TABLE_ITEM, values, selection,
					selectionArgs);
			break;
		case URI_ITEMS_ID:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(Items.TABLE_ITEM, values,
						Items.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(Items.TABLE_ITEM, values,
						Items.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	private void checkColumns(String[] projection) {
		String[] available = { Items.COLUMN_NAME, Items.COLUMN_ITEM_DETAIL, Items.COLUMN_ID };
		if (projection != null) {
			HashSet<String> requestedColumns = new HashSet<String>(
					Arrays.asList(projection));
			HashSet<String> availableColumns = new HashSet<String>(
					Arrays.asList(available));
			// check if all columns which are requested are available
			if (!availableColumns.containsAll(requestedColumns)) {
				throw new IllegalArgumentException(
						"Unknown columns in projection");
			}
		}
	}

}
