package droid.emergency;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * EmergencyDBHelper
 * @author yasupong
 */
public class EmergencyDBHelper extends SQLiteOpenHelper {

	public static String DB_NAME = "Emergency";
	public static int DB_VERSON = 1;
	public static String DB_TABLE_EMERGENCY = "emergency";

	public EmergencyDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSON);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL( "create table if not exists " + DB_TABLE_EMERGENCY + 
						"(emergency text primary key," +
						"gmail text not null," +
						"gmailpass text not null," +
						"name text  not null," +
						"mail1 text not null," +
						"mail2 text," +
						"mail3 text," +
						"mail4 text," +
						"mail5 text," +
						"cusmessage text" +
						")" );
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		arg0.execSQL( "drop table if exists " + DB_TABLE_EMERGENCY );
		onCreate(arg0);
	}
}
