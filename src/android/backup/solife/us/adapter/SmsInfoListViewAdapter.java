package android.backup.solife.us.adapter;

import java.util.ArrayList;

import android.backup.solife.us.R;
import android.backup.solife.us.entity.SmsInfo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class SmsInfoListViewAdapter  extends BaseAdapter{
	ArrayList<SmsInfo> smsInfos;
	private Context        context;

	public SmsInfoListViewAdapter(ArrayList<SmsInfo> smsInfos, Context context) {
		this.smsInfos  = smsInfos;
		this.context      = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return smsInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return smsInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		SmsInfo smsInfo = smsInfos.get(position);
					
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder        = new ViewHolder();
			convertView   = View.inflate(context, R.layout.sms_listview_item, null);
			
			holder.name   = (TextView) convertView.findViewById(R.id.smsName);
			holder.content   = (TextView) convertView.findViewById(R.id.smsContent);
			
			convertView.setTag(holder);
		}

		holder.name.setText(smsInfo.getIdId()+":"+smsInfo.getNumber());
		holder.content.setText(smsInfo.getContent());

		return convertView;
	}

	class ViewHolder {
		private TextView name,content;
	}

}
