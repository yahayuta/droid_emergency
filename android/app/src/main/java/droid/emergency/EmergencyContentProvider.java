package droid.emergency;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * EmergencyContentProvider
 * @author yasupong
 */
public class EmergencyContentProvider extends ContentProvider {

	/** DBコントロール */
    private EmergencyDBHelper dbHelper = null;

    public static final String AUTHORITY = "droid.emergency.Emergency";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/");
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + AUTHORITY;

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete(EmergencyDBHelper.DB_TABLE_EMERGENCY, "", null);
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		return CONTENT_TYPE;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.insert(EmergencyDBHelper.DB_TABLE_EMERGENCY, "", arg1);
		return null;
	}

	@Override
	public boolean onCreate() {
		dbHelper = new EmergencyDBHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3, String arg4) {
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from " + EmergencyDBHelper.DB_TABLE_EMERGENCY, null);
		if (c == null) return null;
		c.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}
}
