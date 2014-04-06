package android.ibackup.solife.us.ui;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.backup.solife.us.R;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.ibackup.solife.us.api.ApiClient;
import android.ibackup.solife.us.db.ContactTb;
import android.ibackup.solife.us.db.SmsTb;
import android.ibackup.solife.us.util.AppException;
import android.ibackup.solife.us.util.NetUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("NewApi")
public class Main extends BaseActivity {
	private Context context;
	private Button sms_btn;
	private Button phone_contact_btn;
	private TextView textview1;
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone._ID,Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		if (android.os.Build.VERSION.SDK_INT > 9) {
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		    StrictMode.setThreadPolicy(policy);
		}
		context = this;
		try {
			initControls();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    @Override
    protected void onResume() {
    	super.onResume();
		context = this;
		try {
			initControls();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	public void initControls() throws HttpException, IOException, JSONException {
		textview1 = (TextView)findViewById(R.id.warningText);
		sms_btn = (Button) findViewById(R.id.button1);
		sms_btn.setOnClickListener(sms_btn_listener);

		phone_contact_btn = (Button) findViewById(R.id.button2);
		phone_contact_btn.setOnClickListener(contact_btn_listener);
		
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
		//os += "\nID:" +Build.ID;
		//os += "\nBOARD:" +Build.BOARD;
		os += "\nBRAND:" +Build.BRAND;
		os += "\nHOST:" +Build.HOST;
		os += "\nFINGERPRINT:" +Build.FINGERPRINT;
		os += "\nMANUFACTURER:" +Build.MANUFACTURER;
		os += "\nPRODUCT:" + Build.PRODUCT;
		os += "\nHARDWARE:" + Build.HARDWARE;
		os += "\nDISPLAY:" + Build.DISPLAY;
		os += "\nDEVICE:" + Build.DEVICE;
		os += "\nMODEL:" + Build.MODEL;
	    // 手机型号
		os += "\nCODENAME:" + Build.VERSION.CODENAME; 
		  // 本机电话号码
		  os += "\nINCREMENTAL:" + Build.VERSION.INCREMENTAL;
		  //SDK 版本号
		  os += "\nSDK_INT:" + Build.VERSION.SDK_INT;
		  //Firmware/OS 版本号
		  os += "\nRELEASE:" + Build.VERSION.RELEASE; 
	    //ContactTb contactTb = ContactTb.getContactTb(getApplication());
	    //SmsTb smsTb = SmsTb.getSmsTb(getApplication());
	    Integer count1 = 0,count2 = 0, count3 = 0, count4 = 0;
	    try {
	    	//count1 = contactTb.getCount("all");
	    	//count2 = contactTb.getCount("yes");
	    	//count3 = smsTb.getCount("all");
	    	//count4 = smsTb.getCount("yes");
	    } catch (IllegalStateException e){
	    	Log.w("Contact",e.toString());
	    }
	    os += "\nContactCount:" + count1 + "/" +count2;
	    os += "\nSMSCount:" + count3 + "/" + count4;
		textview1.setText(os);

       new Thread() {
    	   public void run(){
    		   SharedPreferences prefer = getSharedPreferences("config", Context.MODE_PRIVATE);
			   Integer phoneId = prefer.getInt("PhoneId", -1);
			   ApiClient.insertContacts(context, phoneId);
			//ApiClient.insertSms(context, phoneId);
			//ApiClient.postContact(context);
			//ApiClient.postSms(context);
    	   }
       }.start();
	}

	Button.OnClickListener sms_btn_listener = new Button.OnClickListener(){//创建监听对象  
		public void onClick(View v){ 
			Intent intent = new Intent (getApplication(),Sms.class);			
			startActivity(intent);
		};
	};

	Button.OnClickListener contact_btn_listener = new Button.OnClickListener(){//创建监听对象  
		public void onClick(View v){ 
			Intent intent = new Intent (getApplication(),Contact.class);			
			startActivity(intent);
		};
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
