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

/**
 * ������ز���
 * @author jay (http://solife.us/resume)
 * @version 1.0
 * @created 2014-02-25
 */
public class NetUtils {
	
	/**
	 * ��������������󣬵õ�����
	 * @param url
	 * @return
	 */
	public static Object post(URL url) {
		return new Object();
	}
	/**
	 * �ж��Ƿ�������
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
	


	public static String [] validateUserInfo(SharedPreferences preferences,String email,String pwd) {
    	String [] ret_array = {"0","���³ɹ�"};
        Integer ret     = 0; 
        String ret_info = "no return";
        Integer n1 = email.length();
        Integer n2 = n1.toString().length();
        String str = n1.toString()+n2.toString()+email+pwd;
        String token = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        
	    Editor Editor = preferences.edit();
        //httpGet ���Ӷ���
        HttpGet httpRequest =new HttpGet(URIs.USR_VALIDATE+"?token="+token);

        try {
            //ȡ��HttpClinet����
            HttpClient httpclient = new DefaultHttpClient();
            // ����HttpClient,ȡ��HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //����ɹ�
            if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
            {
			     //ȡ�÷��ص��ַ���
			     String strResult=EntityUtils.toString(httpResponse.getEntity());
			     
			     JSONObject jsonObject = new JSONObject(strResult) ;
			     //��ȡ����ֵ,���ж��Ƿ���ȷ
			     //actionResult=jsonObject.getBoolean("ActionResult");
			     ret      = jsonObject.getInt("ret");
			     ret_info = jsonObject.getString("ret_info");
			     ret_array[0] = ret.toString();
			     ret_array[1] = ret_info;
			     if(ret.toString().equals("1")) {
			         //Integer user_id      = jsonObject.getInt("user_id");
			         String user_name     = jsonObject.getString("user_name");
			         String user_email    = jsonObject.getString("user_email");
			         String user_province = jsonObject.getString("user_province");
			         String user_register = jsonObject.getString("user_register");
			         String user_gravatar = jsonObject.getString("user_gravatar");
			         int user_id = jsonObject.getInt("user_id");
			         
					//Editor.putInt("current_user_id", user_id);
					Editor.putString("UserName", user_name);
					Editor.putString("UserEmail", user_email);
					Editor.putLong("UserId", user_id);
					Editor.putBoolean("LoginState", true);
					
					String serialNum = "";
			        try {
			            Class<?> classZ = Class.forName("android.os.SystemProperties");
			            Method get = classZ.getMethod("get", String.class);
			            serialNum = (String) get.invoke(classZ, "ro.serialno");
			        } catch (Exception e) {
			        }
			        
					String os = "";
					os += "uniq:"+Build.MANUFACTURER + "-" + Build.MODEL + "-" + serialNum;
					os += "\nserial:" + serialNum;
					os += "\nBRAND:" +Build.BRAND;
					os += "\nHOST:" +Build.HOST;
					os += "\nFINGERPRINT:" +Build.FINGERPRINT;
					os += "\nMANUFACTURER:" +Build.MANUFACTURER;
					os += "\nPRODUCT:" + Build.PRODUCT;
					os += "\nHARDWARE:" + Build.HARDWARE;
					os += "\nDISPLAY:" + Build.DISPLAY;
					os += "\nDEVICE:" + Build.DEVICE;
					os += "\nMODEL:" + Build.MODEL;
				    // �ֻ��ͺ�
					os += "\nCODENAME:" + Build.VERSION.CODENAME; 
				    // �����绰����
				    os += "\nINCREMENTAL:" + Build.VERSION.INCREMENTAL;
				    //Firmware/OS �汾��
				    os += "\nRELEASE:" + Build.VERSION.RELEASE; 
					
					//download_image_with_url(user_email);
					Editor.commit();
			     }
			            }
			        }
			        catch(Exception e) {
			        	ret_array[1] = e.getMessage().toString();
						Editor.commit();
			        }
			        
				    return ret_array;
	}
	public static String [] chkPhoneInfo(SharedPreferences preferences,String email,String pwd) {
    	String [] ret_array = {"0","���³ɹ�"};
        Integer ret     = 0; 
        String ret_info = "no return";
        
	    Editor Editor = preferences.edit();
        //httpGet ���Ӷ���
        HttpGet httpRequest =new HttpGet(URIs.USR_VALIDATE+"?email="+email);

        try {
            //ȡ��HttpClinet����
            HttpClient httpclient = new DefaultHttpClient();
            // ����HttpClient,ȡ��HttpResponse
            HttpResponse httpResponse = httpclient.execute(httpRequest);
            //����ɹ�
            if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
            {
			     //ȡ�÷��ص��ַ���
			     String strResult=EntityUtils.toString(httpResponse.getEntity());
			     
			     JSONObject jsonObject = new JSONObject(strResult) ;
			     //��ȡ����ֵ,���ж��Ƿ���ȷ
			     //actionResult=jsonObject.getBoolean("ActionResult");
			     ret      = jsonObject.getInt("ret");
			     ret_info = jsonObject.getString("ret_info");
			     ret_array[0] = ret.toString();
			     ret_array[1] = ret_info;
			     if(ret.toString().equals("1")) {
			         //Integer user_id      = jsonObject.getInt("user_id");
			         String user_name     = jsonObject.getString("user_name");
			         String user_email    = jsonObject.getString("user_email");
			         String user_province = jsonObject.getString("user_province");
			         String user_register = jsonObject.getString("user_register");
			         String user_gravatar = jsonObject.getString("user_gravatar");
			         int user_id = jsonObject.getInt("user_id");
			         
					//Editor.putInt("current_user_id", user_id);
					Editor.putString("UserName", user_name);
					Editor.putString("UserEmail", user_email);
					Editor.putLong("UserId", user_id);
					Editor.putBoolean("LoginState", true);
					
					String serialNum = "";
			        try {
			            Class<?> classZ = Class.forName("android.os.SystemProperties");
			            Method get = classZ.getMethod("get", String.class);
			            serialNum = (String) get.invoke(classZ, "ro.serialno");
			        } catch (Exception e) {
			        }
			        
					String os = "";
					os += "uniq:"+Build.MANUFACTURER + "-" + Build.MODEL + "-" + serialNum;
					os += "\nserial:" + serialNum;
					os += "\nBRAND:" +Build.BRAND;
					os += "\nHOST:" +Build.HOST;
					os += "\nFINGERPRINT:" +Build.FINGERPRINT;
					os += "\nMANUFACTURER:" +Build.MANUFACTURER;
					os += "\nPRODUCT:" + Build.PRODUCT;
					os += "\nHARDWARE:" + Build.HARDWARE;
					os += "\nDISPLAY:" + Build.DISPLAY;
					os += "\nDEVICE:" + Build.DEVICE;
					os += "\nMODEL:" + Build.MODEL;
				    // �ֻ��ͺ�
					os += "\nCODENAME:" + Build.VERSION.CODENAME; 
				    // �����绰����
				    os += "\nINCREMENTAL:" + Build.VERSION.INCREMENTAL;
				    //Firmware/OS �汾��
				    os += "\nRELEASE:" + Build.VERSION.RELEASE; 
					
					//download_image_with_url(user_email);
					Editor.commit();
			     }
			            }
			        }
			        catch(Exception e) {
			        	ret_array[1] = e.getMessage().toString();
						Editor.commit();
			        }
			        
				    return ret_array;
	}	/**
	 * Role:��ȡ���ŵĸ�����Ϣ
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
	public static ArrayList<ContactInfo> getContacts(Context context) {
	    String[] PHONES_PROJECTION = new String[] {
		    Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	    /**��ϵ����ʾ����**/
	    int PHONES_DISPLAY_NAME_INDEX = 0;
	    /**�绰����**/
	    int PHONES_NUMBER_INDEX = 1;
	    /**ͷ��ID**/
	    int PHONES_PHOTO_ID_INDEX = 2;
	    /**��ϵ�˵�ID**/
	    int PHONES_CONTACT_ID_INDEX = 3;
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				//�õ��ֻ�����
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);
				//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(number)) continue;
				
				//�õ���ϵ������
				String name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//�õ���ϵ��ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				 
				//�õ���ϵ��ͷ��ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				//�õ���ϵ��ͷ��Bitamp
				Bitmap contactPhoto = null;
				//photoid ����0 ��ʾ��ϵ����ͷ�� ���û�и���������ͷ�������һ��Ĭ�ϵ�
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
}