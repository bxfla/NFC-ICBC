package com.nohttp.activity;

import com.nohttp.Application;
import com.sdhy.common.DBhelpersql;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivityse extends Activity {
	private Application application;
	private BaseActivityse oContext;
	//数据库
	protected DBhelpersql database=null;
	protected SQLiteDatabase dbr=null;
	protected SQLiteDatabase dbw=null;

	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	if (application == null) {
	    // 得到Application对象
	    application = (Application) getApplication();
	}
	oContext = this;// 把当前的上下文对象赋值给BaseActivity
	addActivity();// 调用添加方法
	}

	// 添加Activity方法
	public void addActivity() {
	application.addActivity_(oContext);// 调用myApplication的添加Activity方法
	}
	//销毁当个Activity方法
	public void removeActivity() {
	application.removeActivity_(oContext);// 调用myApplication的销毁单个Activity方法
	}
	//销毁所有Activity方法
	public void removeALLActivity() {
	application.removeALLActivity_();// 调用myApplication的销毁所有Activity方法
	}

	/* 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可*/
	public void show_Toast(String text) {
	Toast.makeText(oContext, text, Toast.LENGTH_SHORT).show();
	}
	}