package android.backup.solife.us.recevier;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpException;
import org.json.JSONException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.backup.solife.us.entity.ContactInfo;
import android.backup.solife.us.util.NetUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class TimerService extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        //intent.getAction().equals("short")
    	SharedPreferences prefer = context.getSharedPreferences("config", Context.MODE_PRIVATE);
    	Integer phoneId = prefer.getInt("PhoneId", -1);
    	
		//插入新数据
    	NetUtils.insertNewContacts(context,phoneId);
    	if(NetUtils.hasNetWork(context)) {
    	  //插入新数据
		  new Thread() {
			  public void run() {
			    try {
					NetUtils.uploadUnsyncContact(context);
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
		  }.start();
    	} else {
            //Toast.makeText(context, "网络有问题,同步数据失败!", Toast.LENGTH_LONG).show();
    	}

    }

}