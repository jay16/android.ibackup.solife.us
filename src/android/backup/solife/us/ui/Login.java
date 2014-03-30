package android.backup.solife.us.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.HashMap;

import android.backup.solife.us.R;
import android.backup.solife.us.util.LoadingDialog;
import android.backup.solife.us.util.NetUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

//import org.apache.http.client.methods.HttpPost; 
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

//import org.json.JSONException;

//import us.solife.consumes.BaseActivity.DataCallback;
//import us.solife.consumes.db.ConsumeDao;
//import us.solife.consumes.entity.ConsumeInfo;


import android.widget.Toast;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
//import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
//import android.widget.TextView;
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
		buttonSubmit.setOnClickListener(button_login_listener); 
    	buttonSubmit.setEnabled(false);
    	buttonSubmit.setClickable(false);

    	warningText = (TextView) findViewById(R.id.warningText);
		editTextLoginEmail = (EditText) findViewById(R.id.user_info_email);
		editTextLoginPwd   = (EditText) findViewById(R.id.user_info_pwd);
		editTextLoginEmail.addTextChangedListener(text_watcher);
		editTextLoginPwd.addTextChangedListener(text_watcher);
        
        if(!NetUtils.hasNetWork(getApplicationContext())) warningText.setText("温馨提示:\n    当前网络不可用");
        
		loadingProgressBar = (ProgressBar) findViewById(R.id.login_form_loading_progress);
		if(loadingDialog != null) loadingDialog.dismiss();
	}
	
	/*用户登陆
     * 服务器检测用户的账号和密码是否一致，并返回结果
     * */
	Button.OnClickListener button_login_listener = new Button.OnClickListener(){//创建监听对象  
		public void onClick(View v){  
        	buttonSubmit.setEnabled(false);
        	buttonSubmit.setClickable(false);
	    	loadingProgressBar.setVisibility(View.VISIBLE);
	    	
            loadingDialog = new LoadingDialog(Login.this);	
			loadingDialog.setLoadText("下载数据中...");	
			loadingDialog.show();
	    	
			//登陆用户密码及密码
			String loginEmail = editTextLoginEmail.getText().toString();
			String loginPwd   = editTextLoginPwd.getText().toString();
			
    		sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);	
            Editor Editor = sharedPreferences.edit();
            
			String [] ret_array = {"0",""};
			if(NetUtils.hasNetWork(getApplicationContext())) {
					//NetUtils.validateUserInfo(sharedPreferences,loginEmail,loginPwd);
			} else {
				ret_array[0] = "0";
				ret_array[1] = "\n当前网络不可用";
			}
			
	        String ret_str;
	        if(ret_array[0].equals("1")){
	            ret_str = "登陆成功";	
				startActivity(new Intent(Login.this,Main.class));
	        } else {
	            ret_str = "登陆失败:" +ret_array[1];
				if(loadingDialog != null) loadingDialog.dismiss();
		    	loadingProgressBar.setVisibility(View.GONE);
	        }
			startActivity(new Intent(Login.this,Main.class));
			Toast.makeText(Login.this, ret_str, 0).show();
			
        	buttonSubmit.setEnabled(true);
        	buttonSubmit.setClickable(true);
		}
	};
	
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
	    	String warnText = "温馨提示:";
	    	emailText = editTextLoginEmail.getText().toString();
            pwdText = editTextLoginPwd.getText().toString();

            network = NetUtils.hasNetWork(getApplicationContext());

	        Integer n2 = emailText.length();
	        Integer n1 = n2.toString().length();
	        String str = n1.toString()+n2.toString()+emailText+pwdText;
	        String token = Base64.encodeToString(str.getBytes(), Base64.DEFAULT);
            	
            if(emailText.length()>0 && emailer.matcher(emailText).matches()) {
               email = true;
            } else {
               email = false;
            }
            
            if(pwdText.length()>=6) {
            	pwd = true;
            } else {
            	pwd = false;
            }
            
            if(!email) warnText += "\n    邮箱格式不正确";
            if(!pwd) warnText += "\n    密码长度至少六位";
            if(!network) warnText += "\n    当前网络不可用";
            warnText += "\n"+token;
            warnText += "\n"+str;
            
            if(network && email && pwd) {
            	buttonSubmit.setEnabled(true);
            	buttonSubmit.setClickable(true);
            } else {
            	buttonSubmit.setEnabled(false);
            	buttonSubmit.setClickable(false);
            	warningText.setText(warnText);
            	buttonSubmit.setEnabled(true);
            	buttonSubmit.setClickable(true);
            }
	    }
	};

}
