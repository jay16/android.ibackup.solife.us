package android.backup.solife.us.ui;

import java.lang.reflect.Method;

import android.backup.solife.us.R;
import android.backup.solife.us.db.ContactTb;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Main extends BaseActivity {
	private Button sms_btn;
	private Button phone_contact_btn;
	private TextView textview1;
    private static final String[] PHONES_PROJECTION = new String[] {
	    Phone._ID,Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initControls();
	}

    @Override
    protected void onResume() {
    	super.onResume();
		initControls();
    }
	public void initControls() {
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
	    ContactTb contactTb;
	    contactTb = ContactTb.getContactTb(getApplication());
	    Long maxId = (long)0;
	    Integer count = 0;
	    Integer count1 = 0;
	    try {
	    	maxId = contactTb.getMaxId();
	    	count = contactTb.getCount();
			ContentResolver resolver = getApplication().getContentResolver();
			Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null); 
			
			count1 = phoneCursor.getCount();
	    } catch (IllegalStateException e){
	    	Log.w("Contact",e.toString());
	    }
	    os += "\nid_id:" + maxId;
	    os += "\ncount:" + count;
	    os += "\ncount1:" + count1;
		textview1.setText(os);
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
