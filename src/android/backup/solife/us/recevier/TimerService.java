package android.backup.solife.us.recevier;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.backup.solife.us.util.NetUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class TimerService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        //intent.getAction().equals("short")
    	
    	if(NetUtils.hasNetWork(context)) {
            //Toast.makeText(context, ToolUtils.getStandardDetailDate()+"\n�ɹ�ͬ�����ݣ�", Toast.LENGTH_LONG).show();
    	} else {
            //Toast.makeText(context, "����������,ͬ������ʧ��!", Toast.LENGTH_LONG).show();
    	}

    }

}