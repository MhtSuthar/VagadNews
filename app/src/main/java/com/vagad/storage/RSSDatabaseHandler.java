package com.vagad.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vagad.model.RSSItem;
import com.vagad.utils.Constants;
import com.vagad.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class RSSDatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "rssReader";

	// Contacts table name
	private static final String TABLE_RSS = "news";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_LINK = "link";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_IMAGE = "image";
	private static final String KEY_DATE = "date";
	private static final String KEY_FAV = "fav";
	private static final String KEY_NEWS_TYPE = "news_type";

	public RSSDatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_RSS_TABLE = "CREATE TABLE " + TABLE_RSS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY," + KEY_TITLE + " TEXT,"+ KEY_IMAGE +" TEXT," + KEY_LINK
				+ " TEXT," + KEY_DESCRIPTION + " TEXT," + KEY_DATE +" TEXT, " +KEY_FAV +" BOOLEAN NOT NULL, "
				+ KEY_NEWS_TYPE +" TEXT "+ ")";
		db.execSQL(CREATE_RSS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_RSS);

		// Create tables again
		onCreate(db);
	}

	/**
	 * Adding a new website in websites table Function will check if a site
	 * already existed in database. If existed will update the old one else
	 * creates a new row
	 * */
	public void addFeed(RSSItem site) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, site.getTitle()); // site title
		values.put(KEY_LINK, site.getLink()); // site url
		values.put(KEY_IMAGE, site.getImage()); // rss link url
		values.put(KEY_DESCRIPTION, site.getDescription()); // site description
		values.put(KEY_DATE, DateUtils.convertTimestamp(site.getPubdate())); // site description
		values.put(KEY_FAV, 0);
		values.put(KEY_NEWS_TYPE, site.get_news_type());

		// Check if row already existed in database
		if (!isSiteExists(db, site.getTitle())) {
			// site not existed, create a new row
			db.insert(TABLE_RSS, null, values);
			db.close();
		} else {
			// site already existed update the row
			updateSite(site);
			db.close();
		}
	}

	/**
	 * Reading all rows from database
	 * */
	public List<RSSItem> getAllSites() {
		List<RSSItem> siteList = new ArrayList<RSSItem>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RSS +" WHERE "+KEY_NEWS_TYPE+ " != '"+ Constants.NEWS_TYPE_LATEST+"' ORDER BY "+KEY_DATE+" DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RSSItem site = new RSSItem();
				site.setId(Integer.parseInt(cursor.getString(0)));
				site.setTitle(cursor.getString(1));
				site.setImage(cursor.getString(2));
				site.setLink(cursor.getString(3));
				site.setDescription(cursor.getString(4));
				site.setPubdate(cursor.getString(5));
				site.setFav(cursor.getInt(6) > 0);
				site.set_news_type(cursor.getString(7));
				// Adding contact to list
				siteList.add(site);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return siteList;
	}


	public List<RSSItem> getLatestNews() {
		List<RSSItem> siteList = new ArrayList<RSSItem>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_RSS +" WHERE "+ KEY_NEWS_TYPE + " = '"+ Constants.NEWS_TYPE_LATEST +"' ORDER BY "+ KEY_DATE +" DESC";

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				RSSItem site = new RSSItem();
				site.setId(Integer.parseInt(cursor.getString(0)));
				site.setTitle(cursor.getString(1));
				site.setImage(cursor.getString(2));
				site.setLink(cursor.getString(3));
				site.setDescription(cursor.getString(4));
				site.setPubdate(cursor.getString(5));
				site.setFav(cursor.getInt(6) > 0);
				site.set_news_type(cursor.getString(7));
				// Adding contact to list
				siteList.add(site);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return siteList;
	}

	/**
	 * Updating a single row row will be identified by rss link
	 * */
	public int updateSite(RSSItem site) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, site.getTitle());
		values.put(KEY_LINK, site.getLink());
		values.put(KEY_IMAGE, site.getImage());
		values.put(KEY_DESCRIPTION, site.getDescription());
		values.put(KEY_DATE, DateUtils.convertTimestamp(site.getPubdate()));
		values.put(KEY_NEWS_TYPE, site.get_news_type());

		// updating row return
		int update = db.update(TABLE_RSS, values, KEY_TITLE + " = ?",
				new String[] { String.valueOf(site.getTitle()) });
		db.close();
		return update;

	}

	/**
	 * Reading a row (website) row is identified by row id
	 * */
	/*public RSSItem getSite(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_RSS, new String[] { KEY_ID, KEY_TITLE,
				KEY_LINK, KEY_IMAGE, KEY_DESCRIPTION }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		RSSItem site = new RSSItem(cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4));

		site.setId(Integer.parseInt(cursor.getString(0)));
		site.setTitle(cursor.getString(1));
		site.setLink(cursor.getString(2));
		site.setImage(cursor.getString(3));
		site.setDescription(cursor.getString(4));
		cursor.close();
		db.close();
		return site;
	}*/

	/**
	 * Deleting single row
	 * */
	public void deleteSite(RSSItem site) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RSS, KEY_ID + " = ?",
				new String[] { String.valueOf(site.getId())});
		db.close();
	}

	/**
	 * Checking whether a site is already existed check is done by matching rss
	 * link
	 * */
	public boolean isSiteExists(SQLiteDatabase db, String title) {

		Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_RSS
				+ " WHERE title = '" + title + "'", new String[] {});
		boolean exists = (cursor.getCount() > 0);
		return exists;
	}

	public boolean isDataAvailable() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RSS, new String[] {});
		boolean exists = (cursor.getCount() > 0);
		return exists;
	}

	public int setFav(int isFav, String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FAV, isFav);

		// updating row return
		int update = db.update(TABLE_RSS, values, KEY_ID + " = ?",
				new String[] { id });
		db.close();
		return update;
	}

	public List<RSSItem> getFavList() {
		List<RSSItem> siteList = new ArrayList<RSSItem>();
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_RSS, null, KEY_FAV +"= ?", new String[]{ "1" }, null, null, null);
		/*Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_RSS
				+ " WHERE "+ KEY_FAV +" = '1'", new String[] {});*/
		if (cursor.moveToFirst()) {
			do {
				RSSItem site = new RSSItem();
				site.setId(Integer.parseInt(cursor.getString(0)));
				site.setTitle(cursor.getString(1));
				site.setImage(cursor.getString(2));
				site.setLink(cursor.getString(3));
				site.setDescription(cursor.getString(4));
				site.setPubdate(cursor.getString(5));
				site.setFav(cursor.getInt(6) > 0);
				// Adding contact to list
				siteList.add(site);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();
		return siteList;
	}
}