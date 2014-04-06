package android.ibackup.solife.us.util;

import java.io.BufferedOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;

import android.app.Activity;
import android.backup.solife.us.R;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.ibackup.solife.us.api.ApiClient;
import android.ibackup.solife.us.db.ContactTb;
import android.ibackup.solife.us.db.SmsTb;
import android.ibackup.solife.us.entity.ContactInfo;
import android.ibackup.solife.us.entity.SmsInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * 网络相关操作
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class NetUtils {

    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
    /**联系人显示名称**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**电话号码**/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**头像ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    /**联系人的ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;
	/**
	 * 与服务器发送请求，得到数据
	 * @param url
	 * @return
	 */
	public static Object post(URL url) {
		return new Object();
	}
	/**
	 * 判断是否有网络
	 * @param context
	 * @return
	 */
	public static boolean hasNetWork(Context context) {
		ConnectivityManager con = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo workinfo = con.getActiveNetworkInfo();
		if (workinfo == null || !workinfo.isAvailable()) {
			return false;
		}
		return true;
	}
	


	/**
	 * Role:获取短信的各种信息
	 */
	public static ArrayList<SmsInfo> getSmsInfo(Activity activity) {
		Uri uri = Uri.parse(URIs.SMS_URI_INBOX);
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
		Cursor cusor = activity.managedQuery(uri, projection, null, null, "date desc");
		int idColumn = cusor.getColumnIndex("_id");
		int nameColumn = cusor.getColumnIndex("person");
		int numberColumn = cusor.getColumnIndex("address");
		int smsbodyColumn = cusor.getColumnIndex("body");
		int dateColumn = cusor.getColumnIndex("date");
		int typeColumn = cusor.getColumnIndex("type");
		ArrayList<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		if (cusor != null) {
			while (cusor.moveToNext()) {
				SmsInfo smsInfo = new SmsInfo();
				smsInfo.setPhoneId(-1);
				smsInfo.setSmsId(-1);
				smsInfo.setIdId(cusor.getLong(idColumn));
				smsInfo.setName(cusor.getString(nameColumn));
				smsInfo.setDate(cusor.getString(dateColumn));
				smsInfo.setNumber(cusor.getString(numberColumn));
				smsInfo.setContent(cusor.getString(smsbodyColumn));
				smsInfo.setType(cusor.getString(typeColumn));
				//smsInfo.setSync((long)0);
				//smsInfo.setState("create");
				smsInfos.add(smsInfo);
			}
			cusor.close();
		}
		return smsInfos;
	}
	/**
	 * Role:获取短信的各种信息
	 */
	public static void insertNewSms(Context context,Integer phoneId) {
		Uri uri = Uri.parse(URIs.SMS_URI_ALL);
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
        Cursor cursor = context.getContentResolver().query(uri,projection, null, null, "date desc");
        
		int idColumn = cursor.getColumnIndex("_id");
		int nameColumn = cursor.getColumnIndex("person");
		int numberColumn = cursor.getColumnIndex("address");
		int smsbodyColumn = cursor.getColumnIndex("body");
		int dateColumn = cursor.getColumnIndex("date");
		int typeColumn = cursor.getColumnIndex("type");
		//ArrayList<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		SmsTb smsTb = SmsTb.getSmsTb(context);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");   
		if (cursor != null) {
			while (cursor.moveToNext()) {
				SmsInfo smsInfo = new SmsInfo();
				smsInfo.setPhoneId(phoneId);
				smsInfo.setSmsId(-1);
				smsInfo.setIdId(cursor.getLong(idColumn));
				smsInfo.setName(cursor.getString(nameColumn));

		        Date d = new Date(Long.parseLong(cursor.getString(dateColumn)));   
                String date = dateFormat.format(d);  
				smsInfo.setDate(date);
				
				String number = cursor.getString(numberColumn);
				if(number == null || number.length()==0) number = "nonumber";
				smsInfo.setNumber(number);
				smsInfo.setContent(cursor.getString(smsbodyColumn));
				String type = "null";
				Integer t = cursor.getInt(typeColumn);
				if(t==1) {
					type = "1";
				} else if(t==2) {
					type = "2";
				}
				smsInfo.setType(type);
				
				smsInfo.setSync((long)0);
				smsInfo.setState("create");
				if(smsTb.getSmsCountWithIdId(smsInfo.getIdId()) > 0) continue;
				smsTb.insertSms(smsInfo);
			}
			cursor.close();
		}
	}
	public static ArrayList<ContactInfo> getContacts(Context context) {
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				//得到手机号码
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(number)) continue;
				
				//得到联系人名称
				String name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//得到联系人ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				 
				//得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if(photoid > 0 ) {
				    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactId);
				    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
				    contactPhoto = BitmapFactory.decodeStream(input);
				}else {
				    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.contact_photo);
				}
				contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, os);  
					ContactInfo contactInfo = new ContactInfo();
					contactInfo.setIdId(contactId);
					//contactInfo.setPhoneId(-1);
					//contactInfo.setContactId(-1);
					contactInfo.setType("phone");
					contactInfo.setNumber(number);
					contactInfo.setName(name);
					contactInfo.setPhoto(os.toByteArray());
					//contactInfo.setSync((long)0);
					//contactInfo.setState("create");
					
					contactInfos.add(contactInfo);
		    }
		    phoneCursor.close();
		}
		return contactInfos;
	}
	
	 /**得到手机SIM卡联系人人信息**/
    private ArrayList<ContactInfo> getSIMContacts(Context mContext) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URIs.CONTACT_SIM_URI);
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
	
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) continue;
				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//得到联系人ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
	
				ContactInfo contactInfo = new ContactInfo();
				contactInfo.setIdId(contactId);
				contactInfo.setName(contactName);
				contactInfo.setNumber(phoneNumber);
				contactInfo.setType("sim");
				contactInfos.add(contactInfo);
				//Sim卡中没有联系人头像
		    }
	
		    phoneCursor.close();
		}
		return contactInfos;
    }
    
	 /**得到手机SIM卡联系人人信息**/
    private void insertSIMContacts(Context mContext,Integer phoneId) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URIs.CONTACT_SIM_URI);
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
	    ContactTb contactTb = ContactTb.getContactTb(mContext);
	
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) continue;
				// 得到联系人名称
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//得到联系人ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				//已存入库直接跳过
				if(contactTb.getContactCountWithIdId(contactId,"sim") > 0) continue;
				//Sim卡中没有联系人头像  
				
				ContactInfo contactInfo = new ContactInfo();
				contactInfo.setIdId(contactId);
				contactInfo.setPhoneId(phoneId);
				contactInfo.setContactId(-1);
				contactInfo.setType("sim");
				contactInfo.setNumber(phoneNumber);
				contactInfo.setName(contactName);
				//contactInfo.setPhoto(os.toByteArray());
				contactInfo.setSync((long)0);
				contactInfo.setState("create");

				contactTb.insertContact(contactInfo);
		    }
	
		    phoneCursor.close();
		}
    }
	public static void insertNewContacts(Context context,Integer phoneId) {
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ContactTb contactTb = ContactTb.getContactTb(context);

		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				//得到手机号码
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(number)) continue;
				
				//得到联系人名称
				String name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//得到联系人ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				//已存入库直接跳过
				if(contactTb.getContactCountWithIdId(contactId,"phone") > 0) continue;
				 
				//得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if(photoid > 0 ) {
				    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactId);
				    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
				    contactPhoto = BitmapFactory.decodeStream(input);
				}else {
				    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.contact_photo);
				}
				contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, os);  
					ContactInfo contactInfo = new ContactInfo();
					contactInfo.setIdId(contactId);
					contactInfo.setPhoneId(phoneId);
					contactInfo.setContactId(-1);
					contactInfo.setType("phone");
					contactInfo.setNumber(number);
					contactInfo.setName(name);
					//contactInfo.setPhoto(os.toByteArray());
					contactInfo.setSync((long)0);
					contactInfo.setState("create");

					contactTb.insertContact(contactInfo);
					//contactInfos.add(contactInfo);
		    }
		    phoneCursor.close();
		}
		//return contactInfos;
	}


}
