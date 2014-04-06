package android.ibackup.solife.us.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.backup.solife.us.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.ibackup.solife.us.adapter.SmsInfoListViewAdapter;
import android.ibackup.solife.us.db.ContactTb;
import android.ibackup.solife.us.db.SmsTb;
import android.ibackup.solife.us.entity.SmsInfo;
import android.ibackup.solife.us.util.NetUtils;
import android.ibackup.solife.us.util.URIs;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class Sms extends BaseActivity {
	ListView listView;
	SharedPreferences      preferences;
	ArrayList<SmsInfo>     smsInfos;
	Context mContext = null;
	private Activity activity;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms);
        mContext = this;
        activity = this;
    	setViewList();
    }

    
    @Override
    protected void onResume() {
    	super.onResume();
        mContext = this;
        activity = this;
    	setViewList();
    }

	public void setViewList() {
		listView = (ListView) findViewById(R.id.smsListView);
		smsInfos = NetUtils.getSmsInfo(activity);
				//(ArrayList<SmsInfo>) getSmsInfo();
		Toast.makeText(getApplication(), smsInfos.size()+"", 0).show();
		if (smsInfos != null && smsInfos.size() != 0) {
			listView.setAdapter(new SmsInfoListViewAdapter(smsInfos,getApplication()));
		} else {
			Toast.makeText(getApplication(), "No Data", 0).show();
		}
		listView.invalidate();
	}
	/**
	 * Role:获取短信的各种信息
	 */
	public ArrayList<SmsInfo> getSmsInfo() {
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
		SmsTb smsTb = SmsTb.getSmsTb(mContext);
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

				Log.w("SMS",smsInfo.toStr());
				if(smsTb.getSmsCountWithIdId(smsInfo.getIdId()) > 0) continue;
				smsTb.insertSms(smsInfo);
				smsInfos.add(smsInfo);
			}
			cusor.close();
		}
		return smsTb.getAllSms();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
