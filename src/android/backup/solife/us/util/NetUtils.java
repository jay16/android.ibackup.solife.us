package android.backup.solife.us.util;

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
import java.util.ArrayList;
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
import android.backup.solife.us.db.ContactTb;
import android.backup.solife.us.db.SmsTb;
import android.backup.solife.us.entity.ContactInfo;
import android.backup.solife.us.entity.SmsInfo;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

/**
 * 网络相关操作
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class NetUtils {
	
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
	


	public static void validUserInfo(SharedPreferences preferences,String email,String pwd) {
        Integer n2 = email.length();
        Integer n1 = n2.toString().length();
        String str = n1.toString()+n2.toString()+email+pwd;
        String token = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        
	    Editor Editor = preferences.edit();
	    try {
	         HashMap<String, Object> hm = ApiClient._Get(URIs.USER_VALIDATE+"?token="+token);
	         Log.e("ValidUser",(Integer)hm.get("statusCode")+"-"+(String)hm.get("jsonStr"));
	         
            //请求成功
            if((Integer)hm.get("statusCode")==HttpStatus.SC_OK) {
			     JSONObject jsonObject = new JSONObject((String)hm.get("jsonStr")) ;
			     
			         int userId = jsonObject.getInt("id");
			         String userName      = jsonObject.getString("name");
			         String userEmail     = jsonObject.getString("email");
			         String userCreatedAt = jsonObject.getString("created_at");
			         
			        if(userId > 0 && userEmail.equals(email)) {
						Editor.putInt("UserId", userId);
						Editor.putString("UserName", userName);
						Editor.putString("UserEmail", userEmail);
						Editor.putString("UserCreatedAt", userCreatedAt);
						Editor.putBoolean("LoginState", true);
						Editor.putString("UserToken",token);
						Editor.commit();
						
						validPhoneInfo(preferences,token);
			        } else {
			        	Log.e("VaidatePhone","userId < 0");
			        }
			     } 
            }catch(Exception e) {
				Editor.commit();
	        }   
	}
	public static void validPhoneInfo(SharedPreferences preferences,String token) 
			throws HttpException, IOException, JSONException {
		String serialNum = "";
        try {
            Class<?> classZ = Class.forName("android.os.SystemProperties");
            Method get = classZ.getMethod("get", String.class);
            serialNum = (String) get.invoke(classZ, "ro.serialno");
        } catch (Exception e) {
        }
		org.apache.commons.httpclient.NameValuePair[] params = new org.apache.commons.httpclient.NameValuePair[] {
		  new org.apache.commons.httpclient.NameValuePair("token", token),
		  new org.apache.commons.httpclient.NameValuePair("phone[serial]", serialNum),
		  new org.apache.commons.httpclient.NameValuePair("phone[brand]", Build.BRAND),
		  new org.apache.commons.httpclient.NameValuePair("phone[host]", Build.HOST),
		  new org.apache.commons.httpclient.NameValuePair("phone[fingerprint]", Build.FINGERPRINT),
		  new org.apache.commons.httpclient.NameValuePair("phone[manufacturer]", Build.MANUFACTURER),
		  new org.apache.commons.httpclient.NameValuePair("phone[product]", Build.PRODUCT),
		  new org.apache.commons.httpclient.NameValuePair("phone[device]", Build.DEVICE),
		  new org.apache.commons.httpclient.NameValuePair("phone[model]", Build.MODEL),
		  new org.apache.commons.httpclient.NameValuePair("phone[incremental]", Build.VERSION.INCREMENTAL),
		  new org.apache.commons.httpclient.NameValuePair("phone[release]", Build.VERSION.RELEASE)
		};

		HashMap<String, Object> hash_map = ApiClient._Post(URIs.PHONE_VALIDATE, params);
		int statusCode  = (Integer)hash_map.get("statusCode");
		String response = (String)hash_map.get("response");
			// 请求成功
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			JSONObject jsonObject = new JSONObject(response);
		    Editor Editor = preferences.edit();
	         int phoneId = jsonObject.getInt("id");
			 Editor.putInt("PhoneId", phoneId);
			 if (phoneId > 0) {
				 Editor.putBoolean("loginState", true);
			 }
			 Editor.commit();
		}
	}
	public static Integer postContact(String token,ContactInfo contactInfo,Integer phoneId) 
			throws HttpException, IOException, JSONException {
		org.apache.commons.httpclient.NameValuePair[] params = new org.apache.commons.httpclient.NameValuePair[] {
		  new org.apache.commons.httpclient.NameValuePair("token", token),
		  new org.apache.commons.httpclient.NameValuePair("phone_id", phoneId+""),
		  new org.apache.commons.httpclient.NameValuePair("contact[id_id]", contactInfo.getIdId()+""),
		  new org.apache.commons.httpclient.NameValuePair("contact[number]", contactInfo.getNumber()),
		  new org.apache.commons.httpclient.NameValuePair("contact[name]", contactInfo.getName()),
		  new org.apache.commons.httpclient.NameValuePair("contact[contact_type]", contactInfo.getType()),
		};
        Integer contactId = -1;
		HashMap<String, Object> hash_map = ApiClient._Post(URIs.CONTACT_VALIDATE, params);
		int statusCode  = (Integer)hash_map.get("statusCode");
		String response = (String)hash_map.get("response");
		// 请求成功
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			JSONObject jsonObject = new JSONObject(response);
	         contactId = jsonObject.getInt("id");
		}
		return contactId;
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
	public static void inserNewSms(Activity activity,Context context) {
		Uri uri = Uri.parse(URIs.SMS_URI_INBOX);
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
		Cursor cusor = activity.managedQuery(uri, projection, null, null, "date desc");
		int idColumn = cusor.getColumnIndex("_id");
		int nameColumn = cusor.getColumnIndex("person");
		int numberColumn = cusor.getColumnIndex("address");
		int smsbodyColumn = cusor.getColumnIndex("body");
		int dateColumn = cusor.getColumnIndex("date");
		int typeColumn = cusor.getColumnIndex("type");
		//ArrayList<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		SmsTb smsTb = SmsTb.getSmsTb(context);
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
				smsInfo.setSync((long)0);
				smsInfo.setState("create");
				if(smsTb.getSmsCountWithIdId(smsInfo.getIdId()) > 0) continue;
				smsTb.insertSms(smsInfo);
			}
			cusor.close();
		}
	}
	public static ArrayList<ContactInfo> getContacts(Context context) {
	    String[] PHONES_PROJECTION = new String[] {
		    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	    /**联系人显示名称**/
	    int PHONES_DISPLAY_NAME_INDEX = 0;
	    /**电话号码**/
	    int PHONES_NUMBER_INDEX = 1;
	    /**头像ID**/
	    int PHONES_PHOTO_ID_INDEX = 2;
	    /**联系人的ID**/
	    int PHONES_CONTACT_ID_INDEX = 3;
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
	public static void insertNewContacts(Context context,Integer phoneId) {
	    String[] PHONES_PROJECTION = new String[] {
		    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	    /**联系人显示名称**/
	    int PHONES_DISPLAY_NAME_INDEX = 0;
	    /**电话号码**/
	    int PHONES_NUMBER_INDEX = 1;
	    /**头像ID**/
	    int PHONES_PHOTO_ID_INDEX = 2;
	    /**联系人的ID**/
	    int PHONES_CONTACT_ID_INDEX = 3;
		//ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ContactTb contactTb;
	    contactTb = ContactTb.getContactTb(context);

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
				if(contactTb.getContactCountWithIdId(contactId) > 0) continue;
				 
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
					contactInfo.setPhoto(os.toByteArray());
					contactInfo.setSync((long)0);
					contactInfo.setState("create");

					contactTb.insertContact(contactInfo);
					//contactInfos.add(contactInfo);
		    }
		    phoneCursor.close();
		}
		//return contactInfos;
	}

  public static void uploadUnsyncContact(Context mContext) throws HttpException, IOException, JSONException {
  	    SharedPreferences prefer = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
  	    String token = prefer.getString("UserToken", "notoken");
  	    Integer phoneId = prefer.getInt("PhoneId", -1);
	    ContactTb contactTb = ContactTb.getContactTb(mContext);
	    ArrayList<ContactInfo> contactInfos = contactTb.getUnsyncContact();
	    if(contactInfos.size()>0) {
			for(int i = 0; i < contactInfos.size(); i++) {
			  ContactInfo contactInfo = contactInfos.get(i);
			  Integer contactId = postContact(token,contactInfo,phoneId);
			  if(contactId>0) {
				  contactTb.updateContact(contactInfo.getId(), contactId);
			  }
			}
	    }
  }
}
