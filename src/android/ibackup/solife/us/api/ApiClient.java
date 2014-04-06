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
 * API�ͻ��˽ӿڣ����ڷ�����������
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
    /**��ϵ����ʾ����**/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;
    /**�绰����**/
    private static final int PHONES_NUMBER_INDEX = 1;
    /**ͷ��ID**/
    private static final int PHONES_PHOTO_ID_INDEX = 2;
    /**��ϵ�˵�ID**/
    private static final int PHONES_CONTACT_ID_INDEX = 3;

	
	public static HttpClient getHttpClient() {        
        HttpClient httpClient = new HttpClient();
		// ���� HttpClient ���� Cookie,���������һ���Ĳ���
		httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // ���� Ĭ�ϵĳ�ʱ���Դ������
		httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		// ���� ���ӳ�ʱʱ��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
		// ���� �����ݳ�ʱʱ�� 
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
		// ���� �ַ���
		httpClient.getParams().setContentCharset(UTF_8);
		return httpClient;
	}	

	private static GetMethod getHttpGet(String url) {
		GetMethod httpGet = new GetMethod(url);
		// ���� ����ʱʱ��
		httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
		httpGet.setRequestHeader("Host", URIs.HOST);
		httpGet.setRequestHeader("Connection","Keep-Alive");
		return httpGet;
	}
	
	private static PostMethod getHttpPost(String url) {
		PostMethod httpPost = new PostMethod(url);
		// ���� ����ʱʱ��
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
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
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
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally { // �ͷ�����
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
				// �����������쳣��������Э�鲻�Ի��߷��ص�����������
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
				// ���������쳣
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// �ͷ�����
				httpPost.releaseConnection();
				httpClient = null;
			}
		}while(time < RETRY_TIME);

        hashMap.put("statusCode", statusCode);
        hashMap.put("response", responseBody);
        return hashMap;
	}

    /*
     * �û���½
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
	         
            //����ɹ�
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
	 * ��֤�ֻ�
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
		// ����ɹ�
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
	 * �ύͨѶ¼
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
		// ����ɹ�
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			try { //��ֹ����������ʧ�ܷ���null
			JSONObject jsonObject = new JSONObject(response);
	         contactId = jsonObject.getInt("id");
			} catch(JSONException e) {
				contactId = -1;
			}
		}
		return contactId;
	}
	/*
	 * �ύ����
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
		// ����ɹ�
		if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
			try { //��ֹ����������ʧ�ܷ���null
			JSONObject jsonObject = new JSONObject(response);
			smsId = jsonObject.getInt("id");
			} catch(JSONException e) {
				smsId = -1;
			}
		}
		return smsId;
	}
	/**
	 * �������
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
				
				//ÿ����ָ�����ݣ���Ϣ1s
				if(index%66==0) Thread.sleep(1000);
			}
			cursor.close();
		}
	}
	/*
	 * �ֻ�ͨѶ¼���
	 */
	public static void insertContacts(Context context,Integer phoneId) {
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ContactTb contactTb = ContactTb.getContactTb(context);

		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				String number = phoneCursor.getString(PHONES_NUMBER_INDEX);//�õ��ֻ�����
				//���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(number)) continue;
				
				String name = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);//�õ���ϵ������
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);//�õ���ϵ��ID
				//�Ѵ����ֱ������
				if(contactTb.getContactCountWithIdId(contactId, "phone") > 0) continue;
				 
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);//�õ���ϵ��ͷ��ID
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
				// �õ��ֻ�����
				String phoneNumber = simCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber)) continue;
				// �õ���ϵ������
				String contactName = simCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//�õ���ϵ��ID
				Long contactId = simCursor.getLong(PHONES_CONTACT_ID_INDEX);
				//�Ѵ����ֱ������
				if(contactTb.getContactCountWithIdId(contactId,"sim") > 0) continue;
				//Sim����û����ϵ��ͷ��  
				
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
	 * SIMͨѶ¼���
	 */
	 public static void insertSIMContacts(Context mContext,Integer phoneId) {
		ContentResolver resolver = mContext.getContentResolver();
		Uri uri = Uri.parse(URIs.CONTACT_SIM_URI);
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,null);
		ArrayList<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
	    ContactTb contactTb = ContactTb.getContactTb(mContext);
	
		if (phoneCursor != null) {
		    while (phoneCursor.moveToNext()) {
				// �õ��ֻ�����
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// ���ֻ�����Ϊ�յĻ���Ϊ���ֶ� ������ǰѭ��
				if (TextUtils.isEmpty(phoneNumber)) continue;
				// �õ���ϵ������
				String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX);
				//�õ���ϵ��ID
				Long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				//�Ѵ����ֱ������
				if(contactTb.getContactCountWithIdId(contactId,"sim") > 0) continue;
				//Sim����û����ϵ��ͷ��  
				
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
	 * �ϴ�ͨѶ¼
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
       * �ϴ�����
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
