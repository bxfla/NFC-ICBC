package com.sdhy.cpucardoper.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

import com.alpha.live.R;
import com.nohttp.activity.BaseActivityse;
import com.sdhy.common.Constant;
import com.sdhy.common.HYENCRY;
import com.sdhy.common.Utils;

/**
 * 建行支付页面
 * 
 * @author GPX
 *
 */
public class CCBWebActivity extends BaseActivityse {
	private WebView mWebView;
	private ProgressBar mProgress;
	private Button show;
	private String mURL;
	private static ArrayList<Object> homeDatas = new ArrayList<Object>();
	
	private int success=1;
	private int falsess=2;
	private String status="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ccb_web_view);
		show = (Button) findViewById(R.id.show);

		Intent intent = getIntent();
		Bundle bunde = intent.getExtras();

		mURL = bunde.getString("URL");
		status = bunde.getString("ssss");
		if (mURL.indexOf("SUCCESS=Y") != -1) {
			show.setVisibility(View.VISIBLE);

		} else {
			show.setVisibility(View.GONE);
		}

		mWebView = (WebView) findViewById(R.id.ccb_webview);
		// WebView加载web资源
		mProgress = ((ProgressBar) findViewById(R.id.helpWebProgress));

		WebSettings webSettings = mWebView.getSettings();
		// webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		// 设置网页支持缩放
		webSettings.setBuiltInZoomControls(true);
		webSettings.setSupportZoom(true);

		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, final String url) {
			
				new Thread() {
					Message msg = new Message();
					@Override
					public void run() {
						// TODO Auto-generated method stub
						super.run();

						try {
							sleep(1500);
							
							// 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
							if (url.indexOf("SUCCESS=Y") != -1) {
								msg.what=success;
								handler.sendMessage(msg);
								
							} else {
								msg.what=falsess;
								handler.sendMessage(msg);
								
								
							}

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();

				if (url.startsWith("mbspay:")) {
					PackageManager pm = getPackageManager();
					Intent checkIntent = pm.getLaunchIntentForPackage("com.chinamworld.main");
					Log.e(null, "``````````````~~~~~~~~~~~~~~~~~~~~~~~~~" + checkIntent);
					if (checkIntent != null) {
						Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

						startActivity(intent);
					}
					return true;
				} else {
					return false;
				}
			}
		});

		mWebView.loadUrl(mURL);

		mWebView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					// 网页加载完成
					mProgress.setVisibility(View.GONE);
				} else {
					// 加载中
					mProgress.setProgress(newProgress);
				}
			}
		});
	}
	
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if(status.equals("1")){
					
					String[] sourceStrArray = mURL.split("&");
					for (int i = 0; i < sourceStrArray.length; i++) {
						homeDatas.add(sourceStrArray[i]);
					//	transStringToMap(sourceStrArray[i]);
						
						
					//	list.add(transStringToMap(sourceStrArray[i]));
					}
					
					
					Log.e(null, "********5555*********************"+homeDatas);
					String order = homeDatas.get(3).toString()
							.substring(homeDatas.get(3).toString().indexOf("=") + 1);
					String card = homeDatas.get(7).toString()
							.substring(homeDatas.get(7).toString().indexOf("=") + 1);
					String money = homeDatas.get(4).toString()
							.substring(homeDatas.get(4).toString().indexOf("=") + 1);
				//	Toast.makeText(CCBWebActivity.this, order, 3).show();
					/*Intent intent = new Intent();
					Log.e(null, "********555544*********************"+order);
					Log.e(null, "********555566*********************"+card);
					Log.e(null, "********555577*********************"+money);
					intent.putExtra("action", "readccb");
					intent.putExtra("orderId", order);
					intent.putExtra("cardNo", card);
					intent.putExtra("balance", money);*/
					if(card.length()>10){
						Intent intent = new Intent();
						Log.e(null, "********555544*********************"+order);
						Log.e(null, "********555566*********************"+card);
						Log.e(null, "********555577*********************"+money);
						intent.putExtra("action", "readccb");
						intent.putExtra("orderId", order);
						intent.putExtra("cardNo", card);
						intent.putExtra("balance", money);
						intent.putExtra("type", "cpu");	
						homeDatas.clear();;
						intent.setClass(CCBWebActivity.this, ChargeActivity.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent();
						/*Log.e(null, "********555544*********************"+order);
						Log.e(null, "********555566*********************"+card);
						Log.e(null, "********555577*********************"+money);
						intent.putExtra("action", "readmm");
						intent.putExtra("orderId", order);
						intent.putExtra("cardNo", card);
						intent.putExtra("balance", money);
						intent.putExtra("type", "m1");	*/
						homeDatas.clear();
						intent.setClass(CCBWebActivity.this, MainActivity.class);
						startActivity(intent);
					}
					
					
//					show.setVisibility(View.VISIBLE);
//					show.setOnClickListener(new OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//
//							String[] sourceStrArray = mURL.split("&");
//							for (int i = 0; i < sourceStrArray.length; i++) {
//								homeDatas.add(sourceStrArray[i]);
//							//	transStringToMap(sourceStrArray[i]);
//								
//								
//							//	list.add(transStringToMap(sourceStrArray[i]));
//							}
//							
//							
//							Log.e(null, "********5555*********************"+homeDatas);
//							String order = homeDatas.get(4).toString()
//									.substring(homeDatas.get(4).toString().indexOf("=") + 1);
//							String card = homeDatas.get(8).toString()
//									.substring(homeDatas.get(8).toString().indexOf("=") + 1);
//							String money = homeDatas.get(5).toString()
//									.substring(homeDatas.get(5).toString().indexOf("=") + 1);
//						//	Toast.makeText(CCBWebActivity.this, order, 3).show();
//							Intent intent = new Intent();
//							Log.e(null, "********555544*********************"+order);
//							Log.e(null, "********555566*********************"+card);
//							Log.e(null, "********555577*********************"+money);
//							intent.putExtra("orderId", order);
//							intent.putExtra("cardNo", card);
//							intent.putExtra("balance", money);
//							intent.setClass(CCBWebActivity.this, MainActivity.class);
//							startActivity(intent);
//
//						}
//					});
				}else if (status.equals("2")){
					
					show.setVisibility(View.VISIBLE);
					show.setText("返回首页");
					show.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

						
							Intent intent = new Intent();
							
							intent.setClass(CCBWebActivity.this, MainActivity.class);
							startActivity(intent);
							
						}
					});
				}
				
				

				break;
			case 2:
				show.setVisibility(View.GONE);
				/*Intent intent = new Intent();
				intent.setClass(CCBWebActivity.this, MainActivity.class);
				startActivity(intent);*/
			//	222
				break;
			
			}

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
				// 表示按返回键时的操作
				mWebView.goBack(); // 后退
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}
}
