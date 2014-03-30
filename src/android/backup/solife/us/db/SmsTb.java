package android.backup.solife.us.db;

import java.util.ArrayList;

import android.backup.solife.us.entity.ContactInfo;
import android.backup.solife.us.entity.SmsInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
/**
 * 消费记录实体表
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class SmsTb {
	public static final String KEY_ROWID = "id";
	private static final String TABLE = "smses";

	private Context context;
	public ConsumeDatabaseHelper consumeDatabaseHelper;
	static SmsTb smsTb;

	private SmsTb(Context context) {
		this.context = context;
		this.consumeDatabaseHelper = new ConsumeDatabaseHelper(context);
	}

	public static SmsTb getSmsTb(Context context) {
		if (smsTb != null) {
		} else {
			smsTb = new SmsTb(context);
		}
		return smsTb;
	}
	
	//取得所有消费记录
	public ArrayList<SmsInfo> getAllSms() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		String sql = "select * from " + TABLE + " order by date desc";
		Cursor cursor = database.rawQuery(sql, null);
	
		ArrayList<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				smsInfos.add(getSmsFromCursor(cursor));
			}
		}
		cursor.close();
		database.close();
		return smsInfos;
	}
	
	// 插入一笔记录
	public long insertSms(SmsInfo smsInfo) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		database.beginTransaction();
        try {
		ContentValues values = new ContentValues();
		if(smsInfo.getIdId() > 0) values.put("id_id", smsInfo.getIdId());
		if(smsInfo.getPhoneId() > 0) values.put("phone_id", smsInfo.getPhoneId());
		if(smsInfo.getSmsId() > 0) values.put("sms_id", smsInfo.getSmsId());
		if(smsInfo.getNumber().length() > 0) values.put("number", smsInfo.getNumber());
		//if(smsInfo.getName().length() > 0) values.put("name", smsInfo.getName());
		if(smsInfo.getContent().length() > 0) values.put("content", smsInfo.getContent());
		if(smsInfo.getDate().length() > 0) values.put("date", smsInfo.getDate());
		//是否与服务器数据已同步
		values.put("sync", smsInfo.getSync());
		values.put("state", smsInfo.getState());
		long rowid = database.insert(TABLE, null, values);
		//log调试用
        Log.w(TABLE,"插入数据库动作完成id:["+rowid+"]");
        } catch(NullPointerException e) {
        	Log.e("SMSTB","e.getMessage():"+e.getMessage());
        }
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();

		return -1;
	}
	
	//取得所有未同步至服务器的记录
	public ArrayList<SmsInfo> getUnsyncSms() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from "+TABLE+" where sync = 0", null);
		
		ArrayList<SmsInfo> consumeInfos = new ArrayList<SmsInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				consumeInfos.add(getSmsFromCursor(cursor));
			}
		}
		cursor.close();
		database.close();
		return consumeInfos;
	}
	
	public SmsInfo getSmsFromCursor(Cursor cursor){
		SmsInfo smsInfo = new SmsInfo();

		smsInfo.setPhoneId(cursor.getInt(cursor.getColumnIndex("phone_id")));
		smsInfo.setSmsId(cursor.getInt(cursor.getColumnIndex("sms_id")));
		smsInfo.setIdId(cursor.getLong(cursor.getColumnIndex("id_id")));
		smsInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
		smsInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
		smsInfo.setDate(cursor.getString(cursor.getColumnIndex("date")));
		smsInfo.setType(cursor.getString(cursor.getColumnIndex("type")));
		smsInfo.setContent(cursor.getString(cursor.getColumnIndex("content")));
		smsInfo.setSync(cursor.getLong(cursor.getColumnIndex("sync")));
		smsInfo.setState(cursor.getString(cursor.getColumnIndex("state")));
		
		return smsInfo;
	}
	public SmsInfo getContactWithId(long rowId) {
	    SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from " + TABLE +" where id= "+rowId, null);
		SmsInfo smsInfo = new SmsInfo();
		if (cursor != null) {
			cursor.moveToFirst();
			smsInfo = getSmsFromCursor(cursor);
		}
		cursor.close();
		database.close();
		return smsInfo;
	}
	public Integer getSmsCountWithIdId(long idId) {
	    SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from " + TABLE +" where id_id= "+idId, null);
		Integer count = 0;
		try{
		  if(cursor != null) count = cursor.getCount();
		} catch(IllegalStateException e){
			Log.e("getContactWithIdId",e.getMessage());
		}
		cursor.close();
		database.close();
		return count;
	}
}
