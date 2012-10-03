package cstdr.ningningcat.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseUtil extends SQLiteOpenHelper {

	public static final String mDatabaseName = "favorites";

	public static final String mTableName = "favorite";

	// public static final String COLUMN_FAVICON = "favicon";

	public static final String COLUMN_TITLE = "title";

	public static final String COLUMN_URL = "url";

	// public static final String COLUMN_COUNT = "_COUNT";

	public DatabaseUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table favorite(title varchar(20), url varchar(40))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public static void insert(DatabaseUtil dbHelper, String sql) {
		if (LOG.DEBUG) {
			LOG.cstdr("insert sql = " + sql);
		}
		dbHelper.getWritableDatabase().execSQL(sql);

		dbHelper.close();
	}

	public static void query(DatabaseUtil dbHelper, String sql) {
		if (LOG.DEBUG) {
			LOG.cstdr("query sql = " + sql);
		}
		dbHelper.getWritableDatabase().execSQL(sql);

		dbHelper.close();
	}

	public static void delete(DatabaseUtil dbHelper, String sql) {
		if (LOG.DEBUG) {
			LOG.cstdr("delete sql = " + sql);
		}
		dbHelper.getWritableDatabase().execSQL(sql);

		dbHelper.close();
	}

}
