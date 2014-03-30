package android.backup.solife.us.db;

import java.util.ArrayList;
import android.backup.solife.us.entity.ContactInfo;
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
public class ContactTb {
	public static final String KEY_ROWID = "id";
	private static final String TABLE = "contacts";

	private Context context;
	public ConsumeDatabaseHelper consumeDatabaseHelper;
	static ContactTb contactTb;

	private ContactTb(Context context) {
		this.context = context;
		this.consumeDatabaseHelper = new ConsumeDatabaseHelper(context);
	}

	public static ContactTb getContactTb(Context context) {
		if (contactTb != null) {
		} else {
			contactTb = new ContactTb(context);
		}
		return contactTb;
	}
	
	public Integer getCount() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		String sql = "select * from " + TABLE;
		Cursor cursor = database.rawQuery(sql, null);
	    Integer count = cursor.getCount();
		cursor.close();
		database.close();
		return count;
	}
	public ArrayList<ContactInfo> getAllContact() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		String sql = "select * from " + TABLE;
		Cursor cursor = database.rawQuery(sql, null);
	
		ArrayList<ContactInfo> smsInfos = new ArrayList<ContactInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				smsInfos.add(getContactFromCursor(cursor));
			}
		}
		cursor.close();
		database.close();
		return smsInfos;
	}
	// 插入一笔记录
	public long insertContact(ContactInfo contactInfo) {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		database.beginTransaction();

		ContentValues values = new ContentValues();
		if(contactInfo.getIdId()>0)    values.put("id_id", contactInfo.getIdId());
		if(contactInfo.getPhoneId()>0) values.put("phone_id", contactInfo.getPhoneId());
		if(contactInfo.getContactId()>0) values.put("contact_id", contactInfo.getContactId());
		if(contactInfo.getNumber().length()>0) values.put("number", contactInfo.getNumber());
		if(contactInfo.getName().length()>0) values.put("name", contactInfo.getName());
		values.put("type", contactInfo.getType());
		values.put("photo", contactInfo.getPhoto());
		//是否与服务器数据已同步
		values.put("sync", contactInfo.getSync());
		values.put("state", contactInfo.getState());

		//log调试用
        Log.w(TABLE,"插入前:" + contactInfo.toStr());
		long rowid = database.insert(TABLE, null, values);
		database.setTransactionSuccessful();
		database.endTransaction();
		database.close();

		//log调试用
        Log.w(TABLE,"插入完成id:["+rowid+"]");// + getCount() + "-" + getContactWithId(rowid).toStr());
		return rowid;
	}
	
	//取得所有未同步至服务器的记录
	public ArrayList<ContactInfo> getUnsyncContact() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from "+TABLE+" where sync = 0", null);
		
		ArrayList<ContactInfo> consumeInfos = new ArrayList<ContactInfo>();
		if (cursor.getCount() > 0) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				consumeInfos.add(getContactFromCursor(cursor));
			}
		}
		cursor.close();
		database.close();
		return consumeInfos;
	}
	
	public ContactInfo getContactFromCursor(Cursor cursor){
		ContactInfo contactInfo = new ContactInfo();
	    
		try {
		contactInfo.setIdId(cursor.getLong(cursor.getColumnIndex("id_id")));
		contactInfo.setPhoneId(cursor.getInt(cursor.getColumnIndex("phone_id")));
		contactInfo.setContactId(cursor.getInt(cursor.getColumnIndex("contact_id")));
		contactInfo.setName(cursor.getString(cursor.getColumnIndex("name")));
		contactInfo.setNumber(cursor.getString(cursor.getColumnIndex("number")));
		contactInfo.setPhoto(cursor.getBlob(cursor.getColumnIndex("photo")));
		contactInfo.setType(cursor.getString(cursor.getColumnIndex("type")));
		contactInfo.setSync(cursor.getLong(cursor.getColumnIndex("sync")));
		contactInfo.setState(cursor.getString(cursor.getColumnIndex("state")));
		} catch(IllegalStateException e) {
			Log.w("ContactTb",e.toString());
		}
		
		return contactInfo;
	}
	
	public Long getMaxId() {
		SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		String sql = "select max(id_id) from " + TABLE;
		Cursor cursor = database.rawQuery(sql, null);
	    Long rowId;
	    
		if (cursor != null && cursor.getCount() > 0) {
		  cursor.moveToFirst(); 
		  rowId = cursor.getLong(cursor.getColumnIndex("id_id"));
		} else {
			rowId = (long)0;
		}
		cursor.close();
		database.close();
		return rowId;
	}
	public ContactInfo getContactWithId(long rowId) {
	    SQLiteDatabase database = consumeDatabaseHelper.getWritableDatabase();
		Cursor cursor = database.rawQuery("select * from " + TABLE +" where id= "+rowId, null);
		ContactInfo contactInfo = new ContactInfo();
		if (cursor != null) {
			cursor.moveToFirst();
			contactInfo = getContactFromCursor(cursor);
		}
		cursor.close();
		database.close();
		return contactInfo;
	}
	public Integer getContactCountWithIdId(long idId) {
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

