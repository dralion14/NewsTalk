/**
 * Author: Ravi Tamada
 * URL: www.androidhive.info
 * twitter: http://twitter.com/ravitamada
 * 
 * Modified by: Gabriel Salazar
 * */
package cl.newstalk.library;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "newstalk";

	// Login table name
	public static final String TABLE_LOGIN = "login";
	public static final String TABLE_SOURCE = "source";
	public static final String TABLE_FEED = "feed";

	// Login Table Columns names
	public static final String KEY_ID = "_id";
	public static final String KEY_UID = "uid";
	public static final String KEY_NAME = "name";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_CREATED_AT = "created_at";

	public static final String KEY_URL = "url";

	public static final String KEY_SOURCE_ID = "source_id";
	public static final String KEY_ACTIVE = "active";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + " ( "
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_UID
					+ " TEXT, " + KEY_NAME + " TEXT," + KEY_EMAIL
					+ " TEXT UNIQUE," + KEY_CREATED_AT + " TEXT" + " );";
			db.execSQL(CREATE_LOGIN_TABLE);
			// Log.i(cl.newstalk.MainActivity.TAG, "CREATE LOGIN");

			String CREATE_SOURCE_TABLE = "CREATE TABLE " + TABLE_SOURCE + " ( "
					+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ KEY_NAME + " TEXT, " + KEY_URL + " TEXT" + " );";
			db.execSQL(CREATE_SOURCE_TABLE);
			// Log.i(cl.newstalk.MainActivity.TAG, "CREATE SOURCE");

			String CREATE_FEED_TABLE = "CREATE TABLE " + TABLE_FEED + " ( "
					+ KEY_ID + " INTEGER PRIMARY KEY, " + KEY_SOURCE_ID
					+ " INTEGER, " + KEY_NAME + " TEXT, " + KEY_URL + " TEXT, "
					+ KEY_ACTIVE + " INTEGER" + " );";
			db.execSQL(CREATE_FEED_TABLE);
			// Log.i(cl.newstalk.MainActivity.TAG, "CREATE FEED");
		} catch (Exception e) {
			Log.e(cl.newstalk.MainActivity.TAG, e.getMessage());
		}

	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN + ";");
		// Log.i(cl.newstalk.MainActivity.TAG, "DROP LOGIN");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE + ";");
		// Log.i(cl.newstalk.MainActivity.TAG, "DROP SOURCE");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FEED + ";");
		// Log.i(cl.newstalk.MainActivity.TAG, "DROP FEED");

		// Create tables again
		onCreate(db);
	}

	/**
	 * Storing user details in database
	 * */
	public void addUser(String name, String email, String uid, String created_at) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name); // Name
		values.put(KEY_EMAIL, email); // Email
		values.put(KEY_UID, uid); // Email
		values.put(KEY_CREATED_AT, created_at); // Created At

		// Inserting Row
		db.insert(TABLE_LOGIN, null, values);
		db.close(); // Closing database connection
	}

	public void addUser(String email) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_EMAIL, email); // Email

		// Inserting Row
		db.insert(TABLE_LOGIN, null, values);
		db.close(); // Closing database connection
	}

	/**
	 * Getting user data from database
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("uid", cursor.getString(1));
			user.put("name", cursor.getString(2));
			user.put("email", cursor.getString(3));
			user.put("created_at", cursor.getString(4));
		}
		cursor.close();
		db.close();
		// return user
		return user;
	}

	/**
	 * Getting user data from database
	 * */
	public String getUserDetails(String key) {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT * FROM " + TABLE_LOGIN;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("uid", cursor.getString(1));
			user.put("name", cursor.getString(2));
			user.put("email", cursor.getString(3));
			user.put("created_at", cursor.getString(4));
		}
		cursor.close();
		db.close();
		String value = user.get(key);
		user.clear();
		return value;
	}

	/**
	 * Getting user login status return true if rows are there in table
	 * */
	public int getRowCount() {
		String countQuery = "SELECT  * FROM " + TABLE_LOGIN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	public int getRowSource() {
		String countQuery = "SELECT  * FROM " + TABLE_SOURCE;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	public int getRowFeed() {
		String countQuery = "SELECT  * FROM " + TABLE_FEED;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	public int getRowFeedActive() {
		String countQuery = "SELECT  * FROM " + TABLE_FEED + " WHERE "
				+ KEY_ACTIVE + " = 1";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int rowCount = cursor.getCount();
		db.close();
		cursor.close();

		// return row count
		return rowCount;
	}

	/**
	 * Re crate database Delete all tables and create them again
	 * */
	public void resetTables() {
		SQLiteDatabase db = this.getWritableDatabase();
		// Delete All Rows
		db.delete(TABLE_LOGIN, null, null);
		// Log.i(cl.newstalk.MainActivity.TAG, "DELETE LOGIN");

		db.delete(TABLE_SOURCE, null, null);
		// Log.i(cl.newstalk.MainActivity.TAG, "DELETE SOURCE");

		db.delete(TABLE_FEED, null, null);
		// Log.i(cl.newstalk.MainActivity.TAG, "DELETE FEED");

		db.close();
	}

	public void addSource(int id, String name, String url) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id); // Email
		values.put(KEY_NAME, name); // Name
		values.put(KEY_URL, url); // Email

		// Inserting Row
		db.insert(TABLE_SOURCE, null, values);
		db.close(); // Closing database connection
	}

	public void addFeed(int id, int source_id, String name, String url) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, id); // Email
		values.put(KEY_SOURCE_ID, source_id); // Name
		values.put(KEY_NAME, name); // Name
		values.put(KEY_URL, url); // Email
		values.put(KEY_ACTIVE, 0); // Email

		// Inserting Row
		db.insert(TABLE_FEED, null, values);
		db.close(); // Closing database connection
	}

	public Cursor getItemsListView(String table) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "";

		if (table.equals(TABLE_SOURCE)) {
			query = "SELECT " + KEY_ID + ", " + KEY_NAME + ", " + KEY_URL + " FROM " + TABLE_SOURCE;
		} else if (table.equals(TABLE_FEED)) {
			query = "SELECT " + KEY_ID + ", " + KEY_SOURCE_ID + ", " + KEY_NAME + ", " + KEY_URL + ", "
					+ KEY_ACTIVE + " FROM " + TABLE_FEED;
		}

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}
		
		
		return cursor;
	}
	
	public Cursor getItemsListView(String table, String id) {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT " + KEY_ID + ", " + KEY_SOURCE_ID + ", " + KEY_NAME + ", " + KEY_URL + ", "
					+ KEY_ACTIVE + " FROM " + TABLE_FEED + " WHERE source_id = "+ id;

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst();
		}		
		db.close();
		
		return cursor;
	}

	public void activeFeed(int id) {
		SQLiteDatabase db = getWritableDatabase();
		if (db != null) {
			ContentValues values = new ContentValues();
			values.put(KEY_ACTIVE, 1);
			db.update(TABLE_FEED, values, "_id = ?",
					new String[] { String.valueOf(id) });
		}
		db.close();
	}

	public String getURLFeed(String id) {
		HashMap<String, String> user = new HashMap<String, String>();
		String selectQuery = "SELECT url FROM " + TABLE_FEED + " WHERE _id=" + id;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		// Move to first row
		cursor.moveToFirst();
		if (cursor.getCount() > 0) {
			user.put("url", cursor.getString(1));
		}
		cursor.close();
		db.close();
		String value = user.get("url");
		user.clear();
		return value;
	}

}
