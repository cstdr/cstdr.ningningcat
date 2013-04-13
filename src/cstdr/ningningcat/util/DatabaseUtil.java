package cstdr.ningningcat.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库工具类
 * 
 * @author cstdingran@gmail.com
 */
public class DatabaseUtil extends SQLiteOpenHelper {

	public static final String mDatabaseName = "favorites";

	public static final String mTableName = "favorite";

	public static final String COLUMN_TITLE = "title";

	public static final String COLUMN_URL = "url";

	public static final String COLUMN_DATE = "date";

	public static final String COLUMN_PAGEVIEW = "pageview";

	public DatabaseUtil(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table favorite(title varchar(20) not null, url varchar(100) not null, date TIMESTAMP default CURRENT_TIMESTAMP, pageview INTEGER default 0)");
		db.execSQL("create unique index INDEX_URL on favorite(url)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * 使用完游标后及时关闭
	 * 
	 * @param cursor
	 */
	public static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

}
