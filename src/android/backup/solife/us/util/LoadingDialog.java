package android.backup.solife.us.util;


import android.app.Dialog;
import android.backup.solife.us.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * ���ضԻ���ؼ�
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class LoadingDialog extends Dialog {

	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;
	private TextView loadtext;

	public LoadingDialog(Context context) {
		super(context, R.style.Dialog);
		
		this.mContext = context;
		
		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loading_dialog, null);
		loadtext = (TextView) layout.findViewById(R.id.loading_text);
		setContentView(layout);
		
		// ����window����
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = 0; // ȥ�����ڸ�
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);

	}

	public void setLoadText(String content){
		loadtext.setText(content);
	}
}