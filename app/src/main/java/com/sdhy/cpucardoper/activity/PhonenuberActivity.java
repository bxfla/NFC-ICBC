package com.sdhy.cpucardoper.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alpha.live.R;
import com.alpha.live.R.id;
import com.alpha.live.R.layout;
import com.alpha.live.R.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PhonenuberActivity extends Activity {
	private String IMEI;
	private EditText username;
	String phoneNums="";
	private Button loginff;
	private ImageView loghome_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonenuber);
		username=(EditText) findViewById(R.id.username);
		loginff=(Button) findViewById(R.id.loginff);
		loghome_back=(ImageView) findViewById(R.id.loghome_back);
		IMEI=getIntent().getStringExtra("IMEI");
		loghome_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PhonenuberActivity.this.finish();
				
			}
		});
		loginff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				phoneNums=username.getText().toString();
				if (!judgePhoneNums(phoneNums)) {
					return;
				}else{
					Toast.makeText(PhonenuberActivity.this, "正确的手机号", Toast.LENGTH_LONG).show();
					
					SharedPreferences sp = PhonenuberActivity.this.getSharedPreferences("Session",
							Activity.MODE_PRIVATE);
					// 获取Editor对象
					Editor editor = sp.edit();
					

					editor.putString("username", phoneNums);
					editor.putString("IMEI", IMEI);
					editor.commit();
					Intent in =new Intent(PhonenuberActivity.this,MainActivity.class);
					startActivity(in);
					PhonenuberActivity.this.finish();
				}
			}
		});
		
		
	}
	/**
	 * 判断手机号码是否合理
	 * 
	 * @param phoneNums
	 */
	private boolean judgePhoneNums(String phoneNums) {
		if (isMatchLength(phoneNums, 11) && isMobileNO(phoneNums)) {
			return true;
		}else{
		Toast.makeText(PhonenuberActivity.this, "手机号码输入有误！"+isMobileNO(phoneNums), Toast.LENGTH_SHORT).show();
		return false;
		}
	}
	/**
	 * 判断一个字符串的位数
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
		}
	}
	
	/**
	 * 验证手机格式
	 */
	public static boolean isMobileNO(String mobileNums) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		
		
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");  
		Matcher m = p.matcher(mobileNums);   //此处参数为String的字符串
		if(m.matches()){
			return true;
		}else{
			return false;
		}
		
		
	
	}
}
