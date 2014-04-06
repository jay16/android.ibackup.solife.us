package android.ibackup.solife.us.api;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.json.JSONException;
import org.json.JSONObject;
import android.backup.solife.us.R;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.ibackup.solife.us.db.ContactTb;
import android.ibackup.solife.us.db.SmsTb;
import android.ibackup.solife.us.entity.ContactInfo;
import android.ibackup.solife.us.entity.SmsInfo;
import android.ibackup.solife.us.util.AppException;
import android.ibackup.solife.us.util.NetUtils;
import android.ibackup.solife.us.util.URIs;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;


/**
 * API客户端接口：用于访问网络数据
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {

	public static final String UTF_8 = "UTF-8";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";
	
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	private final static int RETRY_TIME = 3;

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

	
	public static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// 设置 连接超时时间
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// 设置 读数据超时时间 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// 设置 字符集
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	

	private static GetMethod getHttpGet(String url) {
		GetMethod httpGet = new GetMethod(url);
		// 设置 请求超时时间
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URIs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url) {
		PostMethod httpPost = new PostMethod(url);
		// 设置 请求超时时间
		httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpPost.setRequestHeader("Host", URIs.HOST);
		httpPost.setRequestHeader("Connection","Keep-Alive");
		return httpPost;
	}
	
	public static HashMap<String, Object> Get(String url) 
			throws AppException{	
		HashMap<String, Object> hashMap = new HashMap<String, Object>();

		HttpClient httpClient = null;
		GetMethod httpGet = null;
		String responseBody = "";
		int statusCode = -1;
		int time = 0;
		do{
			try {
				httpClient = getHttpClient();	
				httpGet = getHttpGet(url);	
				statusCode = httpClient.executeMethod(httpGet);
				if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
					throw AppException.http(statusCode);
				}
				responseBody = httpGet.getResponseBodyAsString();
				break;				
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally { // 释放连接
				httpGet.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);

        hashMap.put("statusCode", statusCode);
        hashMap.put("response", responseBody);
		return hashMap;
	}
	public static HashMap<String, Object>  Post(String url, org.apache.commons.httpclient.NameValuePair[] params) 
			throws AppException {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
		HttpClient httpClient = null;
		PostMethod httpPost = null;
		
		String responseBody = "";
		int statusCode = -1;
		int time = 0;
		do{
			try 
			{
				httpClient = getHttpClient();
				httpPost = getHttpPost(url);	
				httpPost.setRequestBody(params);        	        
		        statusCode = httpClient.executeMethod(httpPost);
		        if(statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_CREATED) {
		        	throw AppException.http(statusCode);
		        }
		     	responseBody = httpPost.getResponseBodyAsString();
		     	break;	     	
			} catch (HttpException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生致命的异常，可能是协议不对或者返回的内容有问题
				e.printStackTrace();
				throw AppException.http(e);
			} catch (IOException e) {
				time++;
				if(time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {} 
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);

        hashMap.put("statusCode", statusCode);
        hashMap.put("response", responseBody);
        return hashMap;
	}

    /*
     * 用户登陆
     */
	public static void validUser(SharedPreferences preferences,String email,String pwd) {
        Integer n2 = email.length();
        Integer n1 = n2.toString().length();
        String str = n1.toString()+n2.toString()+email+pwd;
        String token = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
        
	    Editor Editor = preferences.edit();
	    try {
	         HashMap<String, Object> hm = ApiClient.Get(URIs.USER_VALIDATE+"?token="+token);
	         Log.i("ValidUser:",(Integer)hm.get("statusCode")+"-"+(String)hm.get("jsonStr"));
	         
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
						
						validPhone(preferences,token);
			        } else {
			        	Log.e("VaidatePhone","userId < 0");
			        }
			     } 
            }catch(Exception e) {
				Editor.commit();
	        }   
	}
	/*
	 * 验证手机
	 */
	public static void validPhone(SharedPreferences preferences,String token) 
			throws HttpException, IOException, JSONException, AppException {
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

		HashMap<String, Object> hash_map = ApiClient.Post(URIs.PHONE_VALIDATE, params);
		int statusCode  = (Integer)hash_map.get("statusCode");
		String response = (String)hash_map.get("response");
		Log.i("validPhone:",response);
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
	/*
	 * 提交通讯录
	 */
	public static Integer postContact(String token,ContactInfo contactInfo,Integer phoneId) 
			throws HttpException, IOException, JSONException, AppException {
		org.apache.commons.httpclient.NameValuePair[] params = new org.apache.commons.httpclient.NameValuePair[] {
		  new org.apache.commons.httpclient.NameValuePair("token", token),
		  new org.apache.commons.httpclient.NameValuePair("phone_id", phoneId+""),
		  new org.apache.commons.httpclient.NameValuePair("contact[id_id]", contactInfo.getIdId()+""),
		  new org.apache.commons.httpclient.NameValuePair("contact[number]", contactInfo.getNumber()),
		  new org.apache.commons.httpclient.NameValuePair("contact[name]", contactInfo.getName()),
		  new org.apache.commons.httpclient.NameValuePair("contact[contact_type]", contactInfo.getType()),
		};
        Integer contactId = -1;
		HashMap<String, Object> hash_map = ApiClient.Post(URIs.CONTACT_VALIDATE, params);
		int statusCode  = (Integer)hash_map.get("statusCode");
		String response = (String)hash_map.get("response");
		Log.i("postContact:",response);
		// 请求成功
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			try { //防止服务器创建失败返回null
			JSONObject jsonObject = new JSONObject(response);
	         contactId = jsonObject.getInt("id");
			} catch(JSONException e) {
				contactId = -1;
			}
		}
		return contactId;
	}
	/*
	 * 提交短信
	 */
	public static Integer postSms(String token,SmsInfo smsInfo,Integer phoneId) 
			throws HttpException, IOException, JSONException, AppException {
		org.apache.commons.httpclient.NameValuePair[] params = new org.apache.commons.httpclient.NameValuePair[] {
		  new org.apache.commons.httpclient.NameValuePair("token", token),
		  new org.apache.commons.httpclient.NameValuePair("phone_id", phoneId+""),
		  new org.apache.commons.httpclient.NameValuePair("sms[id_id]", smsInfo.getIdId()+""),
		  new org.apache.commons.httpclient.NameValuePair("sms[number]", smsInfo.getNumber()),
		  new org.apache.commons.httpclient.NameValuePair("sms[content]", smsInfo.getContent()),
		  new org.apache.commons.httpclient.NameValuePair("sms[date]", smsInfo.getDate()),
		  new org.apache.commons.httpclient.NameValuePair("sms[sms_type]", smsInfo.getType()),
		};
        Integer smsId = -1;
		HashMap<String, Object> hash_map = ApiClient.Post(URIs.SMS_VALIDATE, params);
		int statusCode  = (Integer)hash_map.get("statusCode");
		String response = (String)hash_map.get("response");
		Log.i("postSms:",response);
		// 请求成功
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			try { //防止服务器创建失败返回null
			JSONObject jsonObject = new JSONObject(response);
			smsId = jsonObject.getInt("id");
			} catch(JSONException e) {
				smsId = -1;
			}
		}
		return smsId;
	}
	/**
	 * 短信入库
	 * @throws InterruptedException 
	 */
	public static void insertSms(Context context,Integer phoneId) 
			throws InterruptedException {
		Uri uri = Uri.parse(URIs.SMS_URI_ALL);
		String[] projection = new String[] { "_id", "address", "person", "body", "date", "type" };
        Cursor cursor = context.getContentResolver().query(uri,projection, null, null, "date desc");
        
		int idColumn = cursor.getColumnIndex("_id");
		int nameColumn = cursor.getColumnIndex("person");
		int numberColumn = cursor.getColumnIndex("address");
		int smsbodyColumn = cursor.getColumnIndex("body");
		int dateColumn = cursor.getColumnIndex("date");
		int typeColumn = cursor.getColumnIndex("type");
		SmsTb smsTb = SmsTb.getSmsTb(context);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
		int index = 0;
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
				
				Integer t = cursor.getInt(typeColumn);
				String type = (t==1 ? "1" : (t==2 ? "2" : "null"));
				
				smsInfo.setType(type);
				
				smsInfo.setSync((long)0);
				smsInfo.setState("create");
				if(smsTb.getSmsCountWithIdId(smsInfo.getIdId()) > 0) continue;
				smsTb.insertSms(smsInfo);
				
				//每插入指定数据，休息1s
				if(index%66==0) Thread.sleep(1000);
			}
			cursor.close();
		}
	}
	/*
	 * 手机通讯录入库
	 */
	public static void insertContacts(Context context,Integer phoneId) {
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ContactTb contactTb = ContactTb.getContactTb(context);

		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);//得到手机号码
				//当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(number)) continue;
				
				String name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);//得到联系人名称
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);//得到联系人ID
				//已存入库直接跳过
				if(contactTb.getContactCountWithIdId(contactId, "phone") > 0) continue;
				 
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);//得到联系人头像ID
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
		Uri uri = Uri.parse(URIs.CONTACT_SIM_URI);
		Cursor simCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);
	
		if (simCursor != null) {
		    while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = simCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber)) continue;
				// 得到联系人名称
				String contactName = simCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//得到联系人ID
				Long contactId = simCursor.getLong(PHONES_CONTACT_ID_INDEX);
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
	
		    simCursor.close();
		}
	}
	/*
	 * SIM通讯录入库
	 */
	 public static void insertSIMContacts(Context mContext,Integer phoneId) {
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
	/*
	 * 上传通讯录
	 */
     public static void postContact(Context mContext) 
    		 throws HttpException, IOException, JSONException, AppException {
	  	    SharedPreferences prefer = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
	  	    String token = prefer.getString("UserToken", "notoken");
	  	    Integer phoneId = prefer.getInt("PhoneId", -1);
		    ContactTb contactTb = ContactTb.getContactTb(mContext);
		    ArrayList<ContactInfo> contactInfos = contactTb.getUnsyncContact();
		    if(contactInfos.size()>0) {
				for(int i = 0; i < contactInfos.size(); i++) {
				  ContactInfo contactInfo = contactInfos.get(i);
				  if(NetUtils.hasNetWork(mContext) 
					 && contactInfo.getNumber() != null && contactInfo.getType() != null
				     && contactInfo.getNumber().length()>0 && contactInfo.getType().length()>0) {
					  Integer contactId = postContact(token,contactInfo,phoneId);
					  if(contactId>0) {
						  contactTb.updateContact(contactInfo.getId(), contactId);
					  }
				  } else {
					  continue;
				  }
				}
		    }
	  }
      /*
       * 上传短信
       */
	  public static void postSms(Context mContext) 
			  throws HttpException, IOException, JSONException, AppException {
	  	    SharedPreferences prefer = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
	  	    String token = prefer.getString("UserToken", "notoken");
	  	    Integer phoneId = prefer.getInt("PhoneId", -1);
		    SmsTb smsTb = SmsTb.getSmsTb(mContext);
		    ArrayList<SmsInfo> smsInfos = smsTb.getUnsyncSms();
		    if(smsInfos.size()>0) {
				for(int i = 0; i < smsInfos.size(); i++) {
				  SmsInfo smsInfo = smsInfos.get(i);
				  Log.i("BeforePost",smsInfo.toStr()+"-");
				  
				  if(NetUtils.hasNetWork(mContext) 
				    && smsInfo.getNumber() != null && smsInfo.getContent() != null && smsInfo.getType() != null
				    && smsInfo.getNumber().length()>0 && smsInfo.getContent().length()>0 
				    && smsInfo.getType().length()>0) {
					  Integer smsId = postSms(token,smsInfo,phoneId);
					  if(smsId > 0) smsTb.updateSms(smsInfo.getId(), smsId); 
				  } else {
					  continue;
				  }
				}
		    }
	  }

}
