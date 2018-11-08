package com.sdhy.cpucardoper.activity;

import com.alpha.live.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class FirstActivity extends Activity implements android.view.View.OnClickListener{
	private RadioButton rbNFC;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
		rbNFC = (RadioButton) findViewById(R.id.rbNFC);
		
		rbNFC.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		Intent intent = new Intent(this,MainActivity.class);
		startActivity(intent);
		
	}

}
