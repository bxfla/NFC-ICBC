package com.sdhy.cpucardoper.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.live.R;
import com.nohttp.activity.BaseActivityse;
import com.nohttp.dialog.CProgressDialog;
import com.sdhy.common.ConstData;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.DBhelpersql;
import com.sdhy.common.DesPBOC2;
import com.sdhy.common.HYENCRY;
import com.sdhy.common.Utils;

/***
 * 确认充值
 */
public class ChargeActivity extends BaseActivityse implements OnClickListener {
	private TextView cardNoText;// 卡号
	private TextView balanceText;// 余额
	private TextView balanceLabel;
	private TextView moneyText;// 充值金额
	private TextView moneyLabel;
	private Button submitBtn;// 提交
	private String money;// 充值金额
	private String orderId;// 订单号

	private TextView toHomeText;
	private Button toHomeBtn;
	private MyCount mc;
	private String cardNo;

	private Socket socket;
	private InputStream in = null;
	private OutputStream out = null;
	private int receiveTime = 0;// 接收到服务器数据时间(int)(System.currentTimeMillis()/1000L)-t1>20)
	private RelativeLayout black;
	private String TAG = "mifare";
	private String action;
	boolean isSucc = false;
	boolean isSuccf = false;
	boolean isSuccfull = false;
	String strEntryKey = null;
	private int balance;
	private Intent intent;
	private TextView lblSuccInfo,succLabel,lblCardNo;
	private String order;//订单编号
	private String type="";
	private LinearLayout yuesy;
	private String phone ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge);
		database = new DBhelpersql(ChargeActivity.this);

		dbr = database.getReadableDatabase();
		SharedPreferences sp = ChargeActivity.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
		phone = sp.getString("username", "");
		cardNoText = (TextView) findViewById(R.id.textCardNo);
		balanceText = (TextView) findViewById(R.id.textBalance);
		balanceLabel = (TextView) findViewById(R.id.lblBalance);
		yuesy= (LinearLayout) findViewById(R.id.yuesy);
		moneyText = (TextView) findViewById(R.id.textMoney);
		submitBtn = (Button) findViewById(R.id.submitBtn);
		toHomeText = (TextView) findViewById(R.id.textToHome);
		toHomeBtn = (Button) findViewById(R.id.btnToHome);
		moneyLabel = (TextView) findViewById(R.id.lblMoney);
		lblSuccInfo= (TextView) findViewById(R.id.lblSuccInfo);
		lblCardNo= (TextView) findViewById(R.id.lblCardNo);
		black = (RelativeLayout) findViewById(R.id.home_black);
		submitBtn.setOnClickListener(this);
		toHomeBtn.setOnClickListener(this);
		black.setOnClickListener(this);
		submitBtn.setVisibility(View.GONE);
		toHomeText.setVisibility(View.GONE);
		toHomeBtn.setVisibility(View.GONE);
		mc = new MyCount(10000, 1000);

		 intent = getIntent();
		// 获取卡号和余额
		cardNo = intent.getStringExtra("cardNo");
		balance = intent.getIntExtra("balance", 0);

		action = intent.getStringExtra("action");
		order= intent.getStringExtra("orderId");
        type = intent.getStringExtra("type");
		String ccbParam = intent.getStringExtra("CCBPARAM");
		/*if(cardNo.equals("")){
			Toast.makeText(ChargeActivity.this, "支付失败", 3).show();
			lblSuccInfo.setText("支付失败");
			submitBtn.setVisibility(View.GONE);
			Intent intent3 = new Intent(ChargeActivity.this, ActivityCardCharge.class);
			startActivity(intent3);
		//	ChargeActivity.this.finish();
		}*/
		/*
		 * dbr.beginTransaction(); Cursor curor = dbr.query("Recharge", null,
		 * "status=?",new String[] {"-1"}, null, null, null);
		 * if(curor.getCount()!=0){ while(curor.moveToNext()){ money =
		 * curor.getString(curor.getColumnIndex("Cmoney"));
		 * 
		 * }
		 * 
		 * action = "write"; } dbr.setTransactionSuccessful();
		 * dbr.endTransaction();
		 */
		if (ccbParam != null) {
			Map<String, String> mapParam = Utils.queryStringParser(ccbParam);
			Log.i(TAG, "金额是 ==============" + mapParam.get("PAYMENT"));
			cardNo = mapParam.get("REMARK1") == null ? "" : mapParam.get("REMARK1");
			money = mapParam.get("PAYMENT") == null ? "" : mapParam.get("PAYMENT");
			action = "write";
		}

		// Log.i("卡号",cardNo);
		Log.e("卡内余额", balance + "");
		cardNoText.setText(cardNo);
		 succLabel = (TextView) findViewById(R.id.lblSuccInfo);
		/*
		 * 此处为外部链接跳转到该页面
		 */
	/*	if (action == null) {
			String aa=getIntent().getStringExtra("money");
			if((moneyText.getText().toString()).equals("")||aa==null||cardNo==null||cardNo.equals("")){
				Toast.makeText(ChargeActivity.this, "支付失败", 3).show();
				lblSuccInfo.setText("支付失败");
				submitBtn.setVisibility(View.GONE);
				Intent intent3 = new Intent(ChargeActivity.this, MainActivity.class);
				startActivity(intent3);
				ChargeActivity.this.finish();
			}else{
				Toast.makeText(ChargeActivity.this,moneyText.length(), Toast.LENGTH_SHORT).show();
				succLabel.setVisibility(View.VISIBLE);
				lblSuccInfo.setVisibility(View.VISIBLE);
				balanceLabel.setVisibility(View.GONE);
				balanceText.setVisibility(View.GONE);
				submitBtn.setVisibility(View.VISIBLE);
				submitBtn.setText("开始充值");
				toHomeBtn.setVisibility(View.GONE);
				toHomeText.setVisibility(View.GONE);
				moneyText.setText(getIntent().getStringExtra("money") + " 元");
				// moneyText.setText(money + " 元");
				Log.e(null, getIntent().getStringExtra("order"));
				cardNo = intent.getStringExtra("card");
				cardNoText.setText(cardNo);
				money = getIntent().getStringExtra("money");
				orderId = getIntent().getStringExtra("order");

				// 将订单号存值到本地数据中
				SharedPreferences sp = ChargeActivity.this.getSharedPreferences("recharge", Activity.MODE_PRIVATE);
				// 获取Editor对象
				Editor editor = sp.edit();
				editor.putString("orderid", orderId);
				editor.commit();
			}

			

		} else {*/
			if (action.equals("read")) {
				succLabel.setVisibility(View.GONE);
				balanceLabel.setVisibility(View.VISIBLE);
				balanceText.setVisibility(View.VISIBLE);
				balanceText.setText(Utils.FenToYuan(balance));

				submitBtn.setText("开始补登");

				moneyThread.start();
			} else if (action.equals("balance")) {
				succLabel.setVisibility(View.GONE);
				balanceLabel.setVisibility(View.VISIBLE);
				balanceText.setVisibility(View.VISIBLE);
				balanceText.setText(Utils.FenToYuan(balance));
				moneyLabel.setVisibility(View.GONE);
				moneyText.setVisibility(View.GONE);
				submitBtn.setText("开始补登");
				submitBtn.setVisibility(View.GONE);
				toHomeText.setVisibility(View.VISIBLE);
				toHomeBtn.setVisibility(View.VISIBLE);
				toHomeText.setText("5 秒后返回首页");

				mc.start();
			} else if (action.equals("write")) {// 建行返回支付成功通知
				
				if(cardNo.equals("")){
					succLabel.setText("您已取消了充值操作，请手动点击返回");
					succLabel.setVisibility(View.VISIBLE);
					yuesy.setVisibility(View.GONE);
					lblCardNo.setVisibility(View.GONE);
					moneyLabel.setVisibility(View.GONE);
					toHomeBtn.setVisibility(View.VISIBLE);
				}else{
					succLabel.setBackgroundColor(getResources().getColor(R.color.bg_green));
					succLabel.setVisibility(View.VISIBLE);
					balanceLabel.setVisibility(View.GONE);
					balanceText.setVisibility(View.GONE);
					/*balanceLabel.setVisibility(View.GONE);
					balanceText.setVisibility(View.GONE);
				
						submitBtn.setVisibility(View.VISIBLE);
						submitBtn.setText("开始充值");
					
					
					toHomeBtn.setVisibility(View.GONE);
					toHomeText.setVisibility(View.GONE);
					moneyText.setText(money + " 元");*/
					balanceLabel.setVisibility(View.VISIBLE);
					balanceText.setVisibility(View.VISIBLE);
					yuesy.setVisibility(View.GONE);
					balanceText.setText(Utils.FenToYuan(balance));
					
					submitBtn.setText("开始补登");

					moneyThread.start();
				}
				
			}else if (action.equals("readccb")) {
				succLabel.setVisibility(View.GONE);
				balanceLabel.setVisibility(View.VISIBLE);
				balanceText.setVisibility(View.VISIBLE);
				yuesy.setVisibility(View.GONE);
				balanceText.setText(Utils.FenToYuan(balance));
				
				submitBtn.setText("开始补登");

				moneyThread.start();
	        	
	        	
	        }else if (action.equals("readorder")){
	        	succLabel.setVisibility(View.GONE);
				balanceLabel.setVisibility(View.VISIBLE);
				balanceText.setVisibility(View.VISIBLE);
				yuesy.setVisibility(View.GONE);
				balanceText.setText(Utils.FenToYuan(balance));
				
				submitBtn.setText("开始补登");

				moneyThread.start();
	        }
		}

	//}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submitBtn:
			new Thread(loginRunnable).start();// 发送数据
			break;

		case R.id.btnToHome:
			mc.cancel();
			Intent intent = new Intent(ChargeActivity.this, MainActivity.class);
			startActivity(intent);
			ChargeActivity.this.finish();
			break;

		case R.id.home_black:
			mc.cancel();
			Intent intent2 = new Intent(ChargeActivity.this, MainActivity.class);
			startActivity(intent2);
			ChargeActivity.this.finish();
			break;

		}
	}

	Thread moneyThread = new Thread() {
		@Override
		public void run() {
			

			Map<String, String> map = new HashMap<String, String>();
			map.put("cardNo", HYENCRY.encode(cardNoText.getText().toString()));
			map.put("userName", phone);
			map.put("terminalType", "0");
			String jsonStr = DBHandler.getRecord(DBHandler.ACTION_CHARGEMONEY, map);
			Log.e("获取充值金额", cardNoText.getText().toString());
			Log.e("获取充值金额", "---------------------" + HYENCRY.encode(cardNoText.getText().toString()));
			Message message = handler.obtainMessage();
		
			try {
				
				JSONObject json = new JSONObject(jsonStr);
				if (json != null && json.getString("success") != null) {
					String success = json.getString("success");
					if (success.equals("true")) {
						String payment = json.getString("payment");
						
						if (payment == null || payment.trim().equals("")) {
							message.what = Constant.WHAT_GET_PAYMENT_SUCCESS;
							message.obj = "0.00";
						} else {
							message.what = Constant.WHAT_GET_PAYMENT_ISZERO;
							message.obj = json;
						
						}
					} else {
						String msg = json.getString("msg");
						msg = msg == null || msg.trim().equals("") ? "获取充值金额失败" : msg;
						message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
						message.obj = msg;
					
					}
				} else {
					message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
					message.obj = "获取充值金额失败";
				}
			} catch (JSONException e) {
				message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
				message.obj = "获取充值金额失败，信息：" + e.getMessage();
			}
			message.sendToTarget();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.WHAT_GET_PAYMENT_SUCCESS:
				moneyText.setText("0.00 元");
				submitBtn.setVisibility(View.GONE);
				toHomeText.setVisibility(View.VISIBLE);
				toHomeBtn.setVisibility(View.VISIBLE);
				toHomeText.setText("5 秒后返回首页");
				mc.start();
				break;
			case Constant.WHAT_GET_PAYMENT_ISZERO:
			//	money = msg.obj.toString();
				
				try {
					money = ((JSONObject) msg.obj).getString("payment");
					orderId= ((JSONObject) msg.obj).getString("orderId");
				} catch (JSONException e) {
				
					e.printStackTrace();
				}
				Log.e(null,"*************************"+msg.obj.toString());
			//	money="1";
				moneyText.setText(money + " 元");
				if (Integer.parseInt(money) > 0) {
					if(cardNo.length()==9){
						Toast.makeText(ChargeActivity.this, "不支持m1卡充值", 3).show();
						submitBtn.setVisibility(View.GONE);
						toHomeText.setVisibility(View.GONE);
						toHomeBtn.setVisibility(View.GONE);
					}else{
						submitBtn.setVisibility(View.VISIBLE);
						toHomeText.setVisibility(View.GONE);
						toHomeBtn.setVisibility(View.GONE);
					}
					
				} else {
					submitBtn.setVisibility(View.GONE);
					toHomeText.setVisibility(View.VISIBLE);
					toHomeBtn.setVisibility(View.VISIBLE);
					toHomeText.setText("5 秒后返回首页");
					mc.start();
				}
				break;
			case Constant.WHAT_GET_PAYMENT_FAILURE:
				Toast.makeText(ChargeActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case 7:
				
				
				String strKey = (String) msg.obj; 
				Intent intent = new Intent(ChargeActivity.this, ReadActivity.class);
				intent.putExtra("status", "charge");
				intent.putExtra("money", money);
				intent.putExtra("cardNo", cardNoText.getText().toString());
				intent.putExtra("orderId", orderId);
				
				Log.e(null, orderId+"777777777777777777777");
				intent.putExtra("key", strKey);
				startActivity(intent);
				ChargeActivity.this.finish();
				break;
			}
		}
	};

	class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			/*
			 * Intent intent = new Intent(ChargeActivity.this,
			 * MainActivity.class); startActivity(intent);
			 */
			ChargeActivity.this.finish();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			toHomeText.setText(millisUntilFinished / 1000 + " 秒后返回首页");
		}
	}

	@Override
	public void onBackPressed() {
		// 按返回键时返回到主页面
		/*
		 * Intent intent = new Intent(ChargeActivity.this, MainActivity.class);
		 * startActivity(intent);
		 */
		ChargeActivity.this.finish();
	}

	public boolean initConnect() {
		try {
			socket = new Socket();
			SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
			socket.connect(socAddress, 5000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			receiveTime = (int) (System.currentTimeMillis() / 1000L);
			ConstData.isConnect = true;
			ConstData.initNetFlag = true;

			Log.d(TAG, "222连接成功");
			return true;
		} catch (IOException e) {
			ConstData.isConnect = false;
			e.printStackTrace();
			Log.d(TAG, "连接失败");
			return false;
		}
	}

	public boolean reConnect() {
		SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
		try {
			socket = new Socket();
			socket.connect(socAddress, 3000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			ConstData.isConnect = true;
			sendData(ConstData.getLoginBags());
			new Thread(recRunnable).start();
		} catch (IOException e) {
			ConstData.isConnect = false;
		}
		return ConstData.isConnect;
	}

	public void stop() {
		try {
			ConstData.isConnect = false;
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	Runnable loginRunnable = new Runnable() {
		@Override
		public void run() {
			initConnect();
			// 登录包
			sendData(ConstData.getLoginBags());

			new Thread(recRunnable).start(); // 接收数据
		}
	};

	Runnable recRunnable = new Runnable() {
		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			int size = 0;
			/*
			 * boolean isSucc = false; boolean isSuccf = false; boolean
			 * isSuccfull = false; String strEntryKey = null;
			 */
			Log.e(null, "33311111111111111111111******************************");
			exit: while (true) {
				if (!ConstData.isConnect) {
					return;
				}
				try {
					int intRec = in.available();
					while (intRec == 0) {
						intRec = in.available();
						// 比较收包时间大于一分半,判定服务器socket连接已关闭
						if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
							break;
						}
					}
					// 比较收包时间大于一分半,判定服务器socket连接已关闭
					if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
						stop();
						receiveTime = (int) (System.currentTimeMillis() / 1000L);
						return;
					}
					receiveTime = (int) (System.currentTimeMillis() / 1000L);
					// Log.e("收包时间",Integer.toString(receiveTime));
					byte[] byeRec = new byte[intRec];
					int count = in.read(byeRec);
					size = count;
					while (true) {
						if (size < 7) {
							break;
						}
						// 80是手持机发送的包，A0是服务器发回的
						if (byeRec[0] == (byte) 160) {
							Log.d(TAG, "ResB:" + Utils.bytesToHexString(byeRec));
							// 包长度两个字节，一个字节8位
							int len = ((byeRec[1] & 0xff) << 8) + ((byeRec[2] & 0xff));
							if (len < 7) {
								break;
							} else if (byeRec.length == len) {
								if ((byeRec[0] == (byte) 160) && (byeRec[6]) == (byte) 0xAA) {// 登录包，有回应后发送加密机通讯包
									byte[] buffer = ConstData.getEncryptBags(cardNo);
									Log.d(TAG, "getEncryptBags:" + Utils.bytesToHexString(buffer).toUpperCase());
									sendData(buffer);
									Log.e(null, "111111111111111111111******************************"
											+ ((byeRec[0] == (byte) 160) && byeRec[6] == (byte) 0xBD));
									break;
								} else if ((byeRec[0] == (byte) 160) && byeRec[6] == (byte) 0xBD) {// 加密机回应包，解析出主密钥
									// 加密因子

									byte[] byeEncryRatio = ConstData.getValue(byeRec, (byte) 0x46);
									byte[] byeResData = ConstData.getValue(byeRec, (byte) 0x12);
									char[] chrRes = new char[byeResData.length];
									for (int i = 0; i < byeResData.length; i++) {
										chrRes[i] = (char) (byeResData[i] & 0xff);
									}
									String strCurDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
									String strKey = strCurDate + Utils.bytesToHexString(byeEncryRatio) + strCurDate
											+ strCurDate;
									Log.e(TAG, "strKey3333366666666666:" + strKey);
									Log.e(TAG, "strKey4444444466666666666:" + byeEncryRatio);
									String strResData = new String(chrRes);
									String strEntryKey1 = DesPBOC2.Decry3DESStrHex(strResData.substring(0, 16), strKey);
									String strEntryKey2 = DesPBOC2.Decry3DESStrHex(strResData.substring(16, 32),
											strKey);
									strEntryKey = strEntryKey1 + strEntryKey2;
									
									
									Log.e(TAG, "strKey5555555566666666666:" + strEntryKey1);
									Log.e(TAG, "strKey777777777766666666666:" + strEntryKey2);
									Log.e(TAG, "strEntryKey:" + strEntryKey);

									/*09-15 09:52:59.491: E/mifare(6426): strKey3333366666666666:20170915  59bb327b  2017091520170915
									09-15 09:52:59.491: E/mifare(6426): strKey4444444466666666666:[B@417f81b8
									09-15 09:52:59.497: E/mifare(6426): strKey5555555566666666666:3e68c101c219b5f2
									09-15 09:52:59.498: E/mifare(6426): strKey777777777766666666666:d3793215927f106b
									09-15 09:52:59.498: E/mifare(6426): strEntryKey:3e68c101c219b5f2d3793215927f106b*/

									
									
									
									
									isSuccfull = true;
									if (action == null) {
										isSuccf = true;
										// isSuccfull=true;
									} else {
										isSucc = true;

									}

									break exit;
								}
							}
						} else {
							break;
						}
					}
				} catch (Exception e) {
					Log.e(TAG, Log.getStackTraceString(e));
					if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
						stop();
						reConnect();
					}
				}
			}


			
			if (isSuccfull) {
				init();
				
			}
		}

	};

	public void init() {
		if (isSucc) {
			

			Message message = handler.obtainMessage();

			Log.e(null, "******************************");
			Map<String, String> map = new HashMap<String, String>();
			map.put("cardNo", HYENCRY.encode(cardNoText.getText().toString()));
			map.put("userName", phone);
			map.put("terminalType", "0");
			String action = null;
			/*if (orderId != null && !orderId.trim().equals("")) {
				map.put("orderId", HYENCRY.encode(orderId));
				action = DBHandler.ACTION_UPDATE_MOBILE_STATUS_ORDER;
			} else {*/
				action = DBHandler.ACTION_CHARGEMONEY;
			//}

			String jsonStr = DBHandler.getRecord(action, map);

			try {
				JSONObject json = new JSONObject(jsonStr);
				if (json != null && json.getString("success") != null) {
					String success = json.getString("success");
					if (success.equals("true")) {

						message.what = 7;
						message.obj = strEntryKey;
						
					
					} else {

						String msg = json.getString("msg");
						msg = msg == null || msg.trim().equals("") ? "更新订单状态失败" : msg;
						message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
						message.obj = msg;
					}
				} else {
					message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
					message.obj = "更新订单状态失败";
				}
			} catch (JSONException e) {
				message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
				message.obj = "更新订单状态失败，信息：" + e.getMessage();
			}
			message.sendToTarget();
		}
		if (isSuccf) {

			Message message = handler.obtainMessage();
			Map<String, String> map = new HashMap<String, String>();
			map.put("cardNo", cardNo);

			String action = null;
			if (orderId != null && !orderId.trim().equals("")) {
				map.put("orderId", orderId);
				map.put("userName", phone);
				map.put("terminalType", "0");
				action = DBHandler.ACTION_UPDATE_MOBILE_STATUS_ORDER;
			} else {
				map.put("userName", phone);
				map.put("terminalType", "0");
				action = DBHandler.ACTION_CHARGEMONEY;
			}
			String jsonStr = DBHandler.getRecord(action, map);
			Log.e("更新订单状态", "$$$$$$$$$$$$$$$$$$$$$$$$$$" + orderId);
			try {
				JSONObject json = new JSONObject(jsonStr);
				if (json != null && json.getString("success") != null) {
					String success = json.getString("success");
					if (success.equals("true")) {
						String orderc = "";
						SharedPreferences sp = ChargeActivity.this.getSharedPreferences("recharge",
								Activity.MODE_PRIVATE);
						orderc = sp.getString("orderid", "");
						Editor editor = sp.edit();
						// editor.clear();
						editor.commit();
						Log.e(null, "2222222******************************" + orderc);

						ContentValues values = new ContentValues();
						values.put("cardNo", cardNoText.getText().toString());
						values.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
						values.put("Cmoney", money);
						values.put("orderid", orderc);
						values.put("status", "1");
						values.put("YEcz", balance + ((Integer.parseInt(money)) * 100));

						dbr.beginTransaction();
						dbr.insert("Recharge", null, values);
						dbr.setTransactionSuccessful();
						dbr.endTransaction();

						message.what = 7;
						message.obj = strEntryKey;
					} else {
						
						String order2 = "";
						SharedPreferences sp = ChargeActivity.this.getSharedPreferences("recharge",
								Activity.MODE_PRIVATE);
						order2 = sp.getString("orderid", "");
						Log.e(null, "222222233333333******************************" + order2);
						Editor editor = sp.edit();
						editor.clear();
						editor.commit();
						dbr.beginTransaction();
						ContentValues values = new ContentValues();
						values.put("status", "-1");
						dbr.update("Recharge", values, "orderid=?", new String[] { order2 });
						dbr.setTransactionSuccessful();
						dbr.endTransaction();
						String msg = json.getString("msg");
						msg = msg == null || msg.trim().equals("") ? "更新订单状态失败" : msg;
						message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
						message.obj = msg;
					}
				} else {
					message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
					message.obj = "更新订单状态失败";
				}
			} catch (JSONException e) {
				message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
				message.obj = "更新订单状态失败，信息：" + e.getMessage();
			}
			message.sendToTarget();
		}
	}

	public void sendData(byte[] b) {
		try {
			out = getOut();
			Log.e(null, "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" + b);
			out.write(b);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "数据发送失败");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {

			Intent in = new Intent(ChargeActivity.this, MainActivity.class);
			startActivity(in);
			ChargeActivity.this.finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	public Socket getSocket() {
		return socket;
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}
	
	
	
}
