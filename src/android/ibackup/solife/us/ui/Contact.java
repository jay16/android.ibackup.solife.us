package android.ibackup.solife.us.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import android.backup.solife.us.R;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.ContentResolver;  
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.ibackup.solife.us.adapter.ContactInfoListViewAdater;
import android.ibackup.solife.us.adapter.SmsInfoListViewAdapter;
import android.ibackup.solife.us.db.ContactTb;
import android.ibackup.solife.us.entity.ContactInfo;
import android.ibackup.solife.us.util.NetUtils;
import android.ibackup.solife.us.util.URIs;

public class Contact extends BaseActivity {
	ListView listView;
	SharedPreferences            preferences;
	ArrayList<ContactInfo>      contactInfos;
	Context mContext = null;
    /**获取库Phon表字段**/
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
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);
        mContext = this;
    	setViewList();
    }

    
    @Override
    protected void onResume() {
    	super.onResume();
        mContext = this;
    	setViewList();
    }

	public void setViewList() {
		listView = (ListView) findViewById(R.id.phoneContactListView);

	    ContactTb contactTb = ContactTb.getContactTb(mContext);
		contactInfos = contactTb.getAllContact();
				//NetUtils.getContacts(mContext);
				//(ArrayList<ContactInfo>) getContacts();
		Toast.makeText(getApplication(), contactInfos.size()+"", 0).show();
		if (contactInfos != null && contactInfos.size() != 0) {
			listView.setAdapter(new ContactInfoListViewAdater(contactInfos,getApplication()));
		} else {
			Toast.makeText(getApplication(), "No Data", 0).show();
		}
		listView.invalidate();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
