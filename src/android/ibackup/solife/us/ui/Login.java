package android.ibackup.solife.us.ui;

import java.util.regex.Pattern;
import android.backup.solife.us.R;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.ibackup.solife.us.api.ApiClient;
import android.ibackup.solife.us.util.LoadingDialog;
import android.ibackup.solife.us.util.NetUtils;
import android.ibackup.solife.us.util.UIHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ProgressBar;

public class Login extends BaseActivity {
	SharedPreferences sharedPreferences;

	private ProgressBar loadingProgressBar;
	private LoadingDialog loadingDialog;
	private Button buttonSubmit;
	private EditText editTextLoginEmail;
	private EditText editTextLoginPwd ;
	private TextView warningText ;
	private final static Pattern emailer = Pattern.compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
	    //login
		buttonSubmit =(Button)findViewById(R.id.login_login_btn); 
		buttonSubmit.setOnClickListener(loginSubmit_listener); 
    	buttonSubmit.setEnabled(false);
    	buttonSubmit.setClickable(false);

    	warningText = (TextView) findViewById(R.id.warningText);
		editTextLoginEmail = (EditText) findViewById(R.id.user_info_email);
		editTextLoginPwd   = (EditText) findViewById(R.id.user_info_pwd);
		editTextLoginEmail.addTextChangedListener(text_watcher);
		editTextLoginPwd.addTextChangedListener(text_watcher);
        
        if(!NetUtils.hasNetWork(getApplicationContext())) warningText.setText("��ܰ��ʾ:\n    ��ǰ���粻����");
        
		loadingProgressBar = (ProgressBar) findViewById(R.id.login_form_loading_progress);
		if(loadingDialog != null) loadingDialog.dismiss();
	}
	
	/*�û���½
     * ����������û����˺ź������Ƿ�һ�£������ؽ��
     * */
	Button.OnClickListener loginSubmit_listener = new Button.OnClickListener(){//������������  
		public void onClick(View v){  
        	buttonSubmit.setEnabled(false);
        	buttonSubmit.setClickable(false);
	    	loadingProgressBar.setVisibility(View.VISIBLE);
	    	
            loadingDialog = new LoadingDialog(Login.this);	
			loadingDialog.setLoadText("����������...");	
			loadingDialog.show();
	    	
			//��½�û����뼰����
			final String loginEmail = editTextLoginEmail.getText().toString();
			final String loginPwd   = editTextLoginPwd.getText().toString();
			
    	    login(loginEmail, loginPwd);

			if(loadingDialog != null) loadingDialog.dismiss();
	    	loadingProgressBar.setVisibility(View.GONE);
        	buttonSubmit.setEnabled(true);
        	buttonSubmit.setClickable(true);
		}
	};
	
    //��¼��֤
    private void login(final String email, final String password) {
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if(msg.what == 1){
					//����֪ͨ�㲥
					//UIHelper.sendBroadCast(LoginDialog.this, "user.getNotice()");
					//��ʾ��½�ɹ�
					UIHelper.ToastMessage(Login.this, "��½�ɹ�");
					startActivity(new Intent(Login.this,Main.class));
					finish();
				}else if(msg.what == 0){
					UIHelper.ToastMessage(Login.this, "��½ʧ��");
				}
			}
		};
		new Thread(){
			public void run() {
				Message msg =new Message();
				sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
				ApiClient.validUser(sharedPreferences,email,password);

				if (sharedPreferences.contains("loginState")
				  && sharedPreferences.getBoolean("loginState", false)) {
					msg.what = 1;//�ɹ�
					msg.obj = null;
				}else{
					msg.what = 0;//ʧ��
					msg.obj = null;
				}
				handler.sendMessage(msg);
			}
		}.start();
    }
    /*
     * ��֤���硢��������
     */
	private TextWatcher text_watcher = new TextWatcher(){
		 
	    @Override
	    public void afterTextChanged(Editable s) {
	        // TODO Auto-generated method stub
	    }
	 
	    @Override
	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        // TODO Auto-generated method stub
	    }
	 
	    @Override
	    public void onTextChanged(CharSequence s, int start, int before, int count) {
	    	chkLoginState();
	    }
		 
	    public void chkLoginState() {
	    	Boolean email,pwd,network;
	    	String emailText,pwdText;
	    	String warnText = "��ܰ��ʾ:";
	    	emailText = editTextLoginEmail.getText().toString();
            pwdText = editTextLoginPwd.getText().toString();

            network = NetUtils.hasNetWork(getApplicationContext());

            	
            email = (emailText.length()>0 && emailer.matcher(emailText).matches() ? true : false);
            pwd = (pwdText.length() >= 6 ? true : false);
            
            if(!email) warnText += "\n    �����ʽ����ȷ";
            if(!pwd) warnText += "\n    ���볤��������λ";
            if(!network) warnText += "\n    ��ǰ���粻����";
	        //Integer n2 = emailText.length();
	        //Integer n1 = n2.toString().length();
	        //String str = n1.toString()+n2.toString()+emailText+pwdText;
	        //String token = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
            //warnText += "\n"+token;
            //warnText += "\n"+str;
            
            if(network && email && pwd) {
            	buttonSubmit.setEnabled(true);
            	buttonSubmit.setClickable(true);
            } else {
            	buttonSubmit.setEnabled(false);
            	buttonSubmit.setClickable(false);
            	warningText.setText(warnText);
            }
	    }
	};

}
