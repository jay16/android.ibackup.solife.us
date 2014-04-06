package android.ibackup.solife.us.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.ibackup.solife.us.entity.SmsInfo;
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
	public IbackupDatabaseHelper ibackupDatabaseHelper;
	static SmsTb smsTb;

	private SmsTb(Context context) {
		this.context = context;
		this.ibackupDatabaseHelper = new IbackupDatabaseHelper(context);
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
		SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
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
		long rowId = -1;
		SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
		database.beginTransaction();
        Log.w(TABLE,"插入前:" + smsInfo.toStr());
		ContentValues values = new ContentValues();
		if(smsInfo.getIdId() > 0) values.put("id_id", smsInfo.getIdId());
		if(smsInfo.getPhoneId() > 0) values.put("phone_id", smsInfo.getPhoneId());
		if(smsInfo.getSmsId() > 0) values.put("sms_id", smsInfo.getSmsId());
		if(smsInfo.getNumber().length() > 0) values.put("number", smsInfo.getNumber());
		//if(smsInfo.getName().length() > 0) values.put("name", smsInfo.getName());
		if(smsInfo.getContent().length() > 0) values.put("content", smsInfo.getContent());
		if(smsInfo.getType().length() > 0) values.put("type", smsInfo.getType());
		if(smsInfo.getDate().length() > 0) values.put("date", smsInfo.getDate());
		//是否与服务器数据已同步
		values.put("sync", smsInfo.getSync());
		values.put("state", smsInfo.getState());
		rowId = database.insert(TABLE, null, values);
		//log调试用
        Log.w(TABLE,"插入完成id:["+rowId+"]");

        
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();

        Log.w("SMS","AfterInsert:"+rowId);
        
		return rowId;
	}
	
	public long updateSms(long rowId,Integer smsId){
		SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("sms_id", smsId);
		values.put("sync", true);
		String[] args = {String.valueOf(rowId)};
        rowId = database.update(TABLE, values, "id=?",args);
        //database.close();
        
		//SmsInfo smsInfo = getSmsWithId(rowId);
        //Log.w("SMSTB","更新SMS:"+smsInfo.toStr());
        
        return rowId;
	}
	public SmsInfo getSmsWithId(long rowId) {
	    SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
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
	
	//取得所有未同步至服务器的记录
	public ArrayList<SmsInfo> getUnsyncSms() {
		SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from "+TABLE+" where sync = 0", null);
		
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
	
	public SmsInfo getSmsFromCursor(Cursor cursor){
		SmsInfo smsInfo = new SmsInfo();

		smsInfo.setId(cursor.getLong(cursor.getColumnIndex("id")));
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
	public Integer getSmsCountWithIdId(long idId) {
	    SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from " + TABLE +" where id_id= "+idId, null);
		Integer count = 0;
		try{
		  if(cursor != null) count = cursor.getCount();
		} catch(IllegalStateException e){
			Log.e("getSMSWithIdId",e.getMessage());
		} finally {
			cursor.close();
			database.close();
		}
		cursor.close();
		database.close();
		return count;
	}
	
	public Integer getCount(String type) {
		SQLiteDatabase database = ibackupDatabaseHelper.getWritableDatabase();
		String sql = "select * from " + TABLE;
		if(type.equals("all")) {
			
		} else if(type.equals("yes")) {
			sql += " where sync = 1";
		} else if(type.equals("no")) {
			sql += " where sync = 0";
		}
		Cursor cursor = database.rawQuery(sql, null);
	    Integer count = cursor.getCount();
		cursor.close();
		database.close();
		return count;
	}
}
