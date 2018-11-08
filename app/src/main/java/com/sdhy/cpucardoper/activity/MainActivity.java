package com.sdhy.cpucardoper.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alpha.live.R;
import com.nohttp.HttpListener;
import com.nohttp.activity.BaseActivityse;
import com.nohttp.dialog.CProgressDialog;
import com.nohttp.util.AlertDialog;
import com.nohttp.util.SIMCardInfo;
import com.nohttp.util.SnackBar.OnMessageClickListener;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.yolanda.nohttp.rest.Response;

public class MainActivity extends BaseActivityse implements
		HttpListener<String>, OnClickListener {
	private LinearLayout balanceBtn;// 余额查询
	private LinearLayout readBtn;// 补登充值
	private LinearLayout onLineBtn;// 在线充值
	private long exitTime;
	private RelativeLayout guanyu;
	private LinearLayout readBtnwangye;// 网页补登界面
	// private TelephonyManager tm;
	private com.nohttp.util.SnackBar mSnackBar;
	private com.nohttp.util.SnackBar.Builder SnackBar;
	private int REQUEST_CONTACTS;
	private CProgressDialog dialog;
	private UpdateManager mUpdateManager;

	private String phone;

	// private String[] PERMISSIONS_CONTACT = {
	// Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_PHONE_STATE};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		updateStatus();
		// checkPermission();
		readBtn = (LinearLayout) findViewById(R.id.readBtn);
		balanceBtn = (LinearLayout) findViewById(R.id.balanceBtn);
		onLineBtn = (LinearLayout) findViewById(R.id.onLineBtn);
		readBtnwangye = (LinearLayout) findViewById(R.id.readBtnwangye);
		guanyu = (RelativeLayout) findViewById(R.id.guanyu);
		readBtn.setOnClickListener(this);
		balanceBtn.setOnClickListener(this);
		onLineBtn.setOnClickListener(this);
		readBtnwangye.setOnClickListener(this);
		SharedPreferences sp = MainActivity.this.getSharedPreferences(
				"Session", Activity.MODE_PRIVATE);
		phone = sp.getString("username", "");
		Editor editor = sp.edit();
		editor.commit();

		tem();

		exitTime = 0;
		guanyu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog(MainActivity.this).builder()
						.setTitle("版本  v1.2")
						.setMsg("目前仅支持交通部CPU卡手机充值" + "\n" + "山东恒宇电子有限公司")
						.setPositiveButton("确认", new OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}).show();
			}
		});

	}

	private void tem() {

		if ((phone.equals(""))) {
			Intent in = new Intent(MainActivity.this, PhonenuberActivity.class);
			in.putExtra("IMEI", getNativePhoneNumber());
			startActivity(in);

			SIMCardInfo siminfo = new SIMCardInfo(MainActivity.this);

			String phone = getNativePhoneNumber().toString();

			if (siminfo != null) {

				new AlertDialog(MainActivity.this)
						.builder()
						.setTitle("提示")
						.setMsg("当前检测到的手机IMEI" + phone + "\n" + "\n"
								+ "为查询充值记录请确保手机号正确性")
						.setPositiveButton("完善资料", new OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent in = new Intent(MainActivity.this,
										PhonenuberActivity.class);
								in.putExtra("IMEI", getNativePhoneNumber());
								startActivity(in);
							}
						})

						.setCancelable(false).show();

			} else {
				// Toast.makeText(MainActivity.this, "数据获取失败！",
				// Toast.LENGTH_LONG).show();
			}

		} else {
			SharedPreferences sp3 = MainActivity.this.getSharedPreferences(
					"Session", Activity.MODE_PRIVATE);
			// 获取Editor对象
			Editor editor3 = sp3.edit();

			editor3.putString("username", phone);
			editor3.putString("IMEI", getNativePhoneNumber());
			editor3.commit();

		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		exitTime = 0;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.balanceBtn:
			NfcManager manager = (NfcManager) MainActivity.this
					.getSystemService(Context.NFC_SERVICE);
			NfcAdapter adapter = manager.getDefaultAdapter();
			if (adapter != null && adapter.isEnabled()) {
				// adapter存在，能启用
			} else {
				Toast.makeText(MainActivity.this, "请在系统设置中打开nfc",
						Toast.LENGTH_LONG).show();
			}
			intent.putExtra("status", "balance");
			break;
		case R.id.readBtn:// 补登充值
			NfcManager managereor = (NfcManager) MainActivity.this
					.getSystemService(Context.NFC_SERVICE);
			NfcAdapter adaptereor = managereor.getDefaultAdapter();
			if (adaptereor != null && adaptereor.isEnabled()) {
				// adapter存在，能启用
			} else {
				Toast.makeText(MainActivity.this, "请在系统设置中打开nfc",
						Toast.LENGTH_LONG).show();
			}
			intent.putExtra("status", "ordelist");
			break;
		case R.id.onLineBtn:
			NfcManager managere = (NfcManager) MainActivity.this
					.getSystemService(Context.NFC_SERVICE);
			NfcAdapter adaptere = managere.getDefaultAdapter();
			if (adaptere != null && adaptere.isEnabled()) {
				// adapter存在，能启用
			} else {
				Toast.makeText(MainActivity.this, "请在系统设置中打开nfc",
						Toast.LENGTH_LONG).show();
			}
			intent.putExtra("status", "read");
			break;
		case R.id.readBtnwangye:
			NfcManager managerw = (NfcManager) MainActivity.this
					.getSystemService(Context.NFC_SERVICE);
			NfcAdapter adapterw = managerw.getDefaultAdapter();
			if (adapterw != null && adapterw.isEnabled()) {
				// adapter存在，能启用
			} else {
				Toast.makeText(MainActivity.this, "请在系统设置中打开nfc",
						Toast.LENGTH_LONG).show();
			}
			intent.putExtra("status", "onLine");

		}
		intent.setClass(MainActivity.this, ReadActivity.class);
		startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次返回键退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				// 点击在两秒以内

				removeALLActivity();// 执行移除所以Activity方法
				System.exit(0);
				android.os.Process.killProcess(android.os.Process.myPid());
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onSucceed(int what, Response<String> response) {

	}

	@Override
	public void onFailed(int what, Response<String> response) {

	}

	private void updateStatus() {

		dialog = new CProgressDialog(MainActivity.this);
		if (dialog != null) {
			dialog.loadDialog();
			Thread td = new Thread() {

				public void run() {
					Map<String, String> map = new HashMap<String, String>();
					map.put("code", "22");

					String jsonStr = DBHandler.getRecord(DBHandler.UPDATE_code,
							map);

					Log.e(null, "666666666666666666666666" + jsonStr);

					try {
						JSONObject json = new JSONObject(jsonStr);

						if (json != null && json.getString("success") != null) {
							String success = json.getString("success");
							if (success.equals("true")) {
								Message message = handler.obtainMessage();
								handler.sendEmptyMessage(0);
								String msg = json.getString("version");
								message.what = Constant.WHAT_GET_PAYMENT_SUCCESS;
								message.obj = msg;
								message.sendToTarget();
							} else {
								Message message = handler.obtainMessage();
								handler.sendEmptyMessage(1);
								message.sendToTarget();
							}
						}
					} catch (JSONException e) {
						/*
						 * message.what = Constant.WHAT_CONN_DB_FAILURE;
						 * message.obj = "数据库状态更新失败："+e.getMessage();
						 */
						Log.e("数据库状态更新失败", e.getMessage());
					}
					// message.sendToTarget();
				}

			};
			td.start();
		}

	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.WHAT_GET_PAYMENT_SUCCESS:
				dialog.removeDialog();
				msg.obj.toString();

				PackageManager packageManager = MainActivity.this
						.getPackageManager();
				PackageInfo packageInfo;
				String versionCode = "";
				try {
					packageInfo = packageManager.getPackageInfo(
							MainActivity.this.getPackageName(), 0);
					versionCode = packageInfo.versionCode + "";
					Log.e(null, "666666666666666666666666777777777"
							+ versionCode);

					if (Integer.parseInt(msg.obj.toString()) > Integer
							.parseInt(versionCode)) {
						mUpdateManager = new UpdateManager(MainActivity.this);
						mUpdateManager.checkUpdateInfo();
					}

				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}

				// 这里来检测版本是否需要更新
				/*
				 * mUpdateManager = new UpdateManager(LoginActivity.this);
				 * mUpdateManager.checkUpdateInfo(); Log.e(null,
				 * "666666666666666666666666777777777"+msg.obj.toString());
				 */
				break;
			case 1:
				dialog.removeDialog();
				Toast.makeText(MainActivity.this, "获取版本信息失败", Toast.LENGTH_LONG)
						.show();

				break;
			default:
				break;
			}
		}
	};

	public String getNativePhoneNumber() {
		/*
		 * String NativePhoneNumber = null; NativePhoneNumber =
		 * telephonyManager.getLine1Number(); return
		 * telephonyManager.getLine1Number();
		 */

		TelephonyManager tm = (TelephonyManager) MainActivity.this
				.getSystemService(MainActivity.this.TELEPHONY_SERVICE);
		StringBuilder sb = new StringBuilder();

		sb.append(tm.getDeviceId());

		return sb.toString();

	}
}
