package android.ibackup.solife.us.adapter;

import java.util.ArrayList;

import android.backup.solife.us.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.ibackup.solife.us.adapter.SmsInfoListViewAdapter.ViewHolder;
import android.ibackup.solife.us.entity.ContactInfo;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactInfoListViewAdater extends BaseAdapter {
	ArrayList<ContactInfo> ContactInfos;
	private Context        context;

	public ContactInfoListViewAdater(ArrayList<ContactInfo> ContactInfos, Context context) {
		this.ContactInfos  = ContactInfos;
		this.context            = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ContactInfos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ContactInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ContactInfo contactInfo = ContactInfos.get(position);
					
		ViewHolder holder;
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			holder      = new ViewHolder();
			convertView = View.inflate(context, R.layout.contact_listview_item, null);
			holder.photo = (ImageView) convertView.findViewById(R.id.phoneContactPhoto);
			holder.name = (TextView) convertView.findViewById(R.id.phoneContactName);
			holder.number = (TextView) convertView.findViewById(R.id.phoneContactNumber);
			
			convertView.setTag(holder);
		}
 
		try {
			byte[] in = contactInfo.getPhoto();
			Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);  
			holder.photo.setImageBitmap(bmpout);
			holder.name.setText(contactInfo.getIdId() + ":" + contactInfo.getName() + "-" +contactInfo.getSync());
			holder.number.setText(contactInfo.getNumber());
		} catch (NullPointerException e) {
          Log.w("phoneContact",e.toString());
		}

		return convertView;
	}

	class ViewHolder {
		private TextView name,number;
		private ImageView photo;
	}
}
