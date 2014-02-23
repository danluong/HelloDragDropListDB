package com.example.hellodragdroplistdb.database;

//import android.graphics.drawable.Drawable;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * @author Dan
 * 
 */

public class Items implements BaseColumns {

	// Database table
	public static final String TABLE_ITEM = "items";
	public static final String COLUMN_ID = BaseColumns._ID;

	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_ITEM_DETAIL = "detail";

	// Database creation SQL statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_ITEM + "(" + COLUMN_ID
			+ " integer primary key autoincrement, " + COLUMN_NAME
			+ " text not null, " + COLUMN_ITEM_DETAIL + " text not null"
			+ ");";

	public static void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	public static void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(Items.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
		onCreate(database);
	}
}