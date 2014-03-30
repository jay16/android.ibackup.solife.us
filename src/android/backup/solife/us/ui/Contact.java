package android.backup.solife.us.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import android.backup.solife.us.R;
import android.backup.solife.us.adapter.ContactInfoListViewAdater;
import android.backup.solife.us.adapter.SmsInfoListViewAdapter;
import android.backup.solife.us.db.ContactTb;
import android.backup.solife.us.entity.ContactInfo;
import android.backup.solife.us.util.NetUtils;
import android.backup.solife.us.util.URIs;
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

public class Contact extends BaseActivity {
	ListView listView;
	SharedPreferences            preferences;
	ArrayList<ContactInfo>      contactInfos;
	Context mContext = null;
    /**��ȡ��Phon���ֶ�**/
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
		
		contactInfos = NetUtils.getContacts(mContext);
				//(ArrayList<ContactInfo>) getContacts();
		Toast.makeText(getApplication(), contactInfos.size()+"", 0).show();
		if (contactInfos != null && contactInfos.size() != 0) {
			listView.setAdapter(new ContactInfoListViewAdater(contactInfos,getApplication()));
		} else {
			Toast.makeText(getApplication(), "No Data", 0).show();
		}
		listView.invalidate();
	}
	
	public ArrayList<ContactInfo> getContacts() {
		ArrayList<ContactInfo> ContactInfos = new ArrayList<ContactInfo>();
		ContentResolver resolver = mContext.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION, null, null, null);  
		ByteArrayOutputStream os = new ByteArrayOutputStream();  
	    ContactTb contactTb;
	    contactTb = ContactTb.getContactTb(mContext);
	    
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
				
				 if(contactTb.getContactCountWithIdId(contactId) > 0) continue;
				 
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
				    contactPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.contact_photo);
				}
				contactPhoto.compress(Bitmap.CompressFormat.PNG, 100, os);  
				
 
					ContactInfo contactInfo = new ContactInfo();
					contactInfo.setIdId(contactId);
					contactInfo.setPhoneId(-1);
					contactInfo.setContactId(-1);
					contactInfo.setType("phone");
					contactInfo.setNumber(number);
					contactInfo.setName(name);
					contactInfo.setPhoto(os.toByteArray());
					contactInfo.setSync((long)0);
					contactInfo.setState("create");
					
					contactTb.insertContact(contactInfo);
					ContactInfos.add(contactInfo);
		    }
		    phoneCursor.close();
		}
		return contactTb.getAllContact();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
