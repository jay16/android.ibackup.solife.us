package android.backup.solife.us.util;

import java.io.Serializable;

public class URIs implements Serializable{
	//所有的短信
	public final static String SMS_URI_ALL = "content://sms/";
	//收件箱短信
	public static final String SMS_URI_INBOX = "content://sms/inbox";
	// 发件箱短信
	public static final String SMS_URI_SEND = "content://sms/sent";
	//草稿箱短信
	public static final String SMS_URI_DRAFT = "content://sms/draft";
	
	
	public final static String HOST = "ibackup.solife.us";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";
	
	private final static String URL_SPLITTER = "/";
	private final static String URL_UNDERLINE = "_";
	
	private final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	//users
	public final static String USER_VALIDATE  = URL_API_HOST + "api/users/validate.json";
	public final static String PHONE_VALIDATE  = URL_API_HOST + "api/phones";
	public final static String SMS_VALIDATE  = URL_API_HOST + "api/sms";
	public final static String CONTACT_VALIDATE  = URL_API_HOST + "api/contacts";
	
}
