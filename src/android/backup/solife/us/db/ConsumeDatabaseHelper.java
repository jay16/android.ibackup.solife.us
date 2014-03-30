package android.backup.solife.us.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConsumeDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "backup.db";
	private static final String DATABASE_TABLE_SMS   = "smses"; 
	private static final String DATABASE_TABLE_CONTACT = "contacts"; 
	private static final int DATABASE_VERSION = 1;

	//SMS
	String tb_sms = "create table "+ DATABASE_TABLE_SMS +"(" +
			"id integer primary key autoincrement, " +
			"id_id varchar(100)," +  //phone  sms_id
			"sms_id integer," +      //solife sms_id
			"phone_id integer," +
			"number varchar(100) NOT NULL," +
			"date varchar(100) NOT NULL,"+
			"name varchar(100) DEFAULT ''," +
			"content varchar(500)," +
			"type varchar(100)," +
			"sync boolean DEFAULT false," +
			"state varchar(100) DEFAULT '')";
	//contact
	String tb_contact = "create table "+ DATABASE_TABLE_CONTACT +"(" +
			"id integer primary key autoincrement, " +
			"id_id varchar(100)," +  //phone  contact_id
			"contact_id integer," +  //solife contact_id
			"phone_id integer," +
			"number varchar(100) NOT NULL," +
			"name varchar(100) DEFAULT ''," +
			"photo BLOB," +
			"type varchar(100)," +
			"sync boolean DEFAULT false," +
			"state varchar(100) DEFAULT '')";
	

	public ConsumeDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(tb_sms);
		db.execSQL(tb_contact);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists " + DATABASE_TABLE_SMS);
		db.execSQL("drop table if exists " + DATABASE_TABLE_CONTACT);
	}

}
