package com.sdhy.cpucardoper.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alpha.live.R;
import com.nohttp.activity.BaseActivityse;
import com.nohttp.util.SuperLoadingProgress;
import com.sdhy.common.ConstData;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.DBhelpersql;
import com.sdhy.common.DesPBOC2;
import com.sdhy.common.HYENCRY;
import com.sdhy.common.Utils;

@SuppressLint("SimpleDateFormat")
public class ReadActivity extends BaseActivityse implements OnClickListener {
	private ImageView cardImg, flagfalseImg;
	private View flagImg;

	private TextView readTitleText;

	private TextView readToastText;

	private TextView readMessText;// 数据库返回的微信充值记录

	private Button controlBtn;

	private NfcAdapter nfcAdapter;
	private String phone;

	String action = "";// 从哪个动作跳转过来的

	private int defaultSum = 0;// 默认每次充值多少钱，充值10元，实际写入的数值是1000分

	private int afterChargeMoney = 0;// 充值完成后卡内余额

	private String cardNo = "";// 充值卡卡号

	// 充值金额
	private boolean chargeFlag = false;// 充值写卡标志 ，false :尚未将金额写入卡中 ，true:已写

	// ---------------------------------------------------
	private boolean isnews = true;

	private PendingIntent pendingIntent;

	private IntentFilter[] mFilters;

	private String[][] mTechLists;

	private Tag tagFromIntent;

	private SimpleDateFormat sdfLong = new SimpleDateFormat("yyyyMMddHHmmss");

	private String TAG = "mifare";
	private RelativeLayout black;// 返回按钮
	private TextView text;
	
	private String orderId;//订单号
	private String serialNo;//序列号
	private TextView falsete1;
	private LinearLayout falsete2;
	
	private Socket socket;
	private InputStream in = null;
	private OutputStream out = null;
	private int receiveTime = 0;// 接收到服务器数据时间(int)(System.currentTimeMillis()/1000L)-t1>20)
	private Boolean send=true;
	private int money55555;
	private String action2 = null;
	private Map<String, String> map2;
	IsoDep isodep;
	String key;
	SuperLoadingProgress mSuperLoadingProgress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read);
		database = new DBhelpersql(ReadActivity.this);
		dbr = database.getReadableDatabase();
		flagImg = (View) findViewById(R.id.flagImg);
		cardImg = (ImageView) findViewById(R.id.cardImg);
		flagfalseImg = (ImageView) findViewById(R.id.flagfalseImg);
		readTitleText = (TextView) findViewById(R.id.readTitle);
		readToastText = (TextView) findViewById(R.id.readToast);
		readMessText = (TextView) findViewById(R.id.readMess);
		controlBtn = (Button) findViewById(R.id.toHomeBtnId);
		black = (RelativeLayout) findViewById(R.id.alljob_black);
		text = (TextView) findViewById(R.id.tttt);
		falsete1= (TextView) findViewById(R.id.falsete1);
		falsete2 = (LinearLayout) findViewById(R.id.falsete2);
		 mSuperLoadingProgress = (SuperLoadingProgress) findViewById(R.id.pro);
		readTitleText.setText("请刷卡");
		readToastText.setText("等待卡内容");
		controlBtn.setOnClickListener(this);
		controlBtn.setVisibility(View.GONE);
		flagImg.setVisibility(View.GONE);
		cardImg.setVisibility(View.VISIBLE);
		text.setVisibility(View.VISIBLE);
		SharedPreferences sp3 = ReadActivity.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
		phone = sp3.getString("username", "");
		black.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReadActivity.this, MainActivity.class);
				startActivity(intent);
				ReadActivity.this.finish();
			}
		});

		Intent intent = getIntent();
		action = intent.getStringExtra("status");
		
		if (action == null) {
			ReadActivity.this.finish();
		}
		String ccbParam = intent.getStringExtra("CCBPARAM");
		Log.e(null, "=======7777777777777=======================" + ccbParam);
		if (ccbParam != null) {
			Log.d("mifare", ccbParam);
		}
		PackageManager pm = getPackageManager();
		boolean nfc = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
		Log.e("是否支持", nfc ? "支持 NFC" : "不支持 NFC");
		initNFC();
	}

	public void initNFC() {
		// 获取默认的NFC控制器
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		if (nfcAdapter == null) {
			readTitleText.setText("设备不支持 NFC！");
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			readTitleText.setText("请在系统设置中先启用NFC功能！");
			finish();
			return;
		}
		pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		try {
			ndef.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		// ndef.addCategory("*/*");
		mFilters = new IntentFilter[] { ndef };// 过滤器
		mTechLists = new String[][] { new String[] { IsoDep.class.getName() },
				new String[] { MifareClassic.class.getName() } };// 允许扫描的标签类型
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		action = intent.getStringExtra("status");
		if (nfcAdapter == null) {
			readTitleText.setText("设备不支持 NFC！");
			return;
		}
		if (!nfcAdapter.isEnabled()) {
			readTitleText.setText("请在系统设置中先启用 NFC 功能！");
			finish();
			return;
		}
		// 得到是否检测到ACTION_TECH_DISCOVERED触发
		nfcAdapter.enableForegroundDispatch(this, pendingIntent, mFilters, mTechLists);
		if (isnews) {
			if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
				// 处理该intent
				processIntent(getIntent());
				isnews = false;
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
			processIntent(intent);
		}
	}

	private void processIntent(Intent intent) {
		// 授权密钥
		// 对于所有基于MifareClassic的卡来说，每个区最后一个块叫Trailer，16个byte，
		// 主要来存放读写该区的key，可以有A，B两个KEY，每个key长6byte
		// readBlock的索引从0开始到63，每个扇区有4个block
		// DE,87,75,7C,33,03
		// 3B,B3,7B,10,72,1D
		// byte[] NkeyA_1 = { (byte) 222, (byte) 135, (byte) 117, (byte) 124,
		// (byte) 51, (byte) 3 };
		// byte[] NkeyA_1 = { (byte) 63, (byte) 139, (byte) 127, (byte) 48,
		// (byte) 102, (byte) 214 };//19cz库的000011241的KeyA
		// byte[] NkeyB_1 = { (byte) 59, (byte) 179, (byte) 123, (byte) 16,
		// (byte) 114, (byte) 29 };
		// byte[] NkeyB_1 = { (byte) 103, (byte) 110, (byte) 36, (byte) 26,
		// (byte) 161, (byte) 96 };巡更卡KeyB
		
		// byte[] NkeyB_1 = { (byte) 25, (byte) 121, (byte) 37, (byte) 59,
		// (byte) 179, (byte) 86 };//19cz库的000011241的KeyB

		// 取出封装在intent中的TAG
		tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		Log.e("读取tag标签", tagFromIntent.toString());
		String reverseSerial = Utils.bytesToHexString(tagFromIntent.getId());
		serialNo = Utils.hexStringReverseOrder(reverseSerial, reverseSerial.length());

		Log.e("获取序列号", serialNo);
		Log.e("获取序列号", "序列号:" + Long.parseLong(serialNo, 16));

		// 你可以在检测到nfc标签后使用getTechList()方法来查看你所检测的tag到底支持哪些nfc标准。
		String tagStr = "";
		for (int i = 0; i < tagFromIntent.getTechList().length; i++) {
			tagStr += tagFromIntent.getTechList()[i] + " ";
		}
		if (tagStr.indexOf("IsoDep") > 0) {// cpu卡
			 Toast.makeText(ReadActivity.this, "读取到cpu卡", Toast.LENGTH_SHORT).show();
			handleCardCpu(tagFromIntent);
		} else if (tagStr.indexOf("MifareClassic") > 0) {// m1卡
			// m1卡
			getKeyB(serialNo);
			
			Toast.makeText(ReadActivity.this, "读取到M1卡", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(ReadActivity.this, "此卡不支持充值", Toast.LENGTH_SHORT).show();
			return;
		}
	}

	/**
	 * 处理cpu卡
	 */
	public void handleCardCpu(Tag tagFromIntent) {
		isodep = IsoDep.get(tagFromIntent);
		try {
			isodep.connect();
			if (action.equals("balance") || action.equals("read") || action.equals("onLine")) {
				Intent intent = new Intent();
				intent.putExtra("action", action);
				byte[] mf = { (byte) '2', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
						(byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1', };
				byte[] mfRsp = isodep.transceive(getSelectCommand(mf));
				Log.d(TAG, "mfRsp:" + HexToString(mfRsp));
				Log.e(null, "mfRsp:" + HexToString(mfRsp));
				// select Main Application
				// byte[] szt = { (byte) 'P', (byte) 'A', (byte) 'Y', (byte)
				// '.',
				// (byte) 'S', (byte) 'Z', (byte) 'T' };
				byte[] szt = { (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05 };
				byte[] sztRsp = isodep.transceive(getSelectCommand(szt));
				
				Log.e(null, "sztRsp--33---------------------:"+HexToString(sztRsp));
				// 读取公共应用基本文件(0x15)
				StringBuffer sbBaseInfo = new StringBuffer(Integer.toBinaryString(0x15));
				
				sbBaseInfo.reverse();
				if (sbBaseInfo.length() < 5) {
					sbBaseInfo.insert(0, "00000");
				}
				if (sbBaseInfo.length() > 5) {
					sbBaseInfo.delete(0, sbBaseInfo.length() - 5);
				}
				String strCmd = Integer.toHexString(Integer.parseInt(sbBaseInfo.insert(0, "100").toString(), 2));
				strCmd = strCmd.substring(strCmd.length() - 2, strCmd.length());
				
				Log.e(null, "strCmd-----------------------:"+strCmd);
				Log.d(TAG, "strCmd:" + strCmd);
				byte[] byeBaseCmd = { 0x00, (byte) 0xB0, (byte) Integer.parseInt(strCmd, 16), 0x00, 0x00 };

				Log.e(null, "byeBaseCmd-----------------------:"+ConstData.bytesToHexString2(byeBaseCmd));
				byte[] byeBaseInfo = isodep.transceive(byeBaseCmd);
				Log.d(TAG, "byeBaseInfo:" + HexToString(byeBaseInfo));
				Log.e(null, "szt--3344---------------------:"+ConstData.bytesToHexString2(byeBaseInfo));
				if (HexToString(byeBaseInfo).length() >= 20) {
					cardNo = HexToString(byeBaseInfo).substring(20, 40);
					Log.d(TAG, "cardNo:" + cardNo);
					intent.putExtra("cardNo", cardNo);
				} else {
					Toast.makeText(ReadActivity.this, "暂不支持此卡类型", Toast.LENGTH_LONG).show();
				}

				byte[] byeBalance = { (byte) 0x80, (byte) 0x5C, 0x00, 0x02, 0x04 };
				byte[] byeBalanceRsp = isodep.transceive(byeBalance);
				Log.d(TAG, "balanceRsp:" + HexToString(byeBalanceRsp));
				if (byeBalanceRsp != null && byeBalanceRsp.length > 4) {
					int intCash = byteToInt(byeBalanceRsp, 4);
					Log.d(TAG, "余额(分)：" + intCash);
					intent.putExtra("balance", intCash);
					if (action.equals("balance") || action.equals("read")) {
						intent.setClass(ReadActivity.this, ChargeActivity.class);
					} else if (action.equals("onLine")) {
						
						intent.setClass(ReadActivity.this, ActivityCardCharge.class);
					}
					startActivity(intent);
					ReadActivity.this.finish();
				}
			} else if (action.equals("charge") && !chargeFlag) {

			//	09-15 09:28:44.161: E/(5530): 获取的密钥为-------------**********--------------3e68c101c219b5f2d3793215927f106b
				cardNo = getIntent().getStringExtra("cardNo");
				map2 = new HashMap<String, String>();
				map2.put("cardNo", HYENCRY.encode(cardNo));

				defaultSum = Integer.parseInt(getIntent().getStringExtra("money")) * 100;// 充值金额，单位为分
			//	
				 key = getIntent().getStringExtra("key");
				
				Log.e(null, "获取的密钥为-------------**********--------------"+HYENCRY.encode(cardNo.toString()));
			//	09-15 09:19:32.985: E/mifare(5530): strKey3333366666666666:20170915  59bb2aa4   2017091520170915
			//	09-15 09:19:33.003: E/mifare(5530): strEntryKey:3e68c101c219b5f2d3793215927f106b

				


				orderId = getIntent().getStringExtra("orderId");

				
				if (orderId != null && !orderId.trim().equals("")) {
					map2.put("orderId", HYENCRY.encode(orderId));
					map2.put("userName", phone);
					map2.put("terminalType", "0");
					action2 = DBHandler.ACTION_UPDATE_MOBILE_STATUS_ORDER;
				} else {
					map2.put("userName", phone);
					map2.put("terminalType", "0");
					action2 = DBHandler.ACTION_CHARGEMONEY;
				}
				
				
				Thread td = new Thread() {
					
					
					
					public void run() {
						
						String jsonStr = DBHandler.getRecord(action2, map2);
						
						
						Log.e(null, orderId+"666666666666666666666666"+jsonStr);
						Message message = handler.obtainMessage();
						try {
							JSONObject json = new JSONObject(jsonStr);
							
							if(json != null && json.getString("success") != null){
								String success = json.getString("success");
								if (success.equals("true")) {
									
									handler.sendEmptyMessage(99);			
									
								} else {
									
									String msg = json.getString("msg");
									msg = msg == null || msg.trim().equals("") ? "数据库状态更新失败" : msg;
									message.what = Constant.WHAT_CONN_DB_FAILURE;
									message.obj = msg;
								}
							}
						} catch (Exception e) {
							message.what = Constant.WHAT_CONN_DB_FAILURE;
							message.obj = "数据库状态更新失败："+e.getMessage();
							Log.e("数据库状态更新失败",e.getMessage());
						}
						message.sendToTarget();
					}
					
				
					
				};
				td.start();	
				
	
				
			//	Log.e(null, "-22222----------------------------+++++++++++++++++"+ConstData.bytesToHexString2(ConstData.getRecharge(cardNo, orderId, defaultSum/100+"", afterChargeMoney/100+"", serialNo)));		
			//	ConstData.getRecharge(cardNo, orderId, defaultSum/100+"", afterChargeMoney/100+"", serialNo);
			

			}else if(action.equals("ordelist")){
				
				
				Intent intent = new Intent();
				intent.putExtra("action", action);
				byte[] mf = { (byte) '2', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
						(byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1', };
				byte[] mfRsp = isodep.transceive(getSelectCommand(mf));
				Log.d(TAG, "mfRsp:" + HexToString(mfRsp));
				Log.e(null, "mfRsp:" + HexToString(mfRsp));
				// select Main Application
				// byte[] szt = { (byte) 'P', (byte) 'A', (byte) 'Y', (byte)
				// '.',
				// (byte) 'S', (byte) 'Z', (byte) 'T' };
				byte[] szt = { (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05 };
				byte[] sztRsp = isodep.transceive(getSelectCommand(szt));
				Log.e(null, "szt--3344---------------------:"+HexToString(szt));
				Log.e(null, "sztRsp--33---------------------:"+HexToString(sztRsp));
				// 读取公共应用基本文件(0x15)
				StringBuffer sbBaseInfo = new StringBuffer(Integer.toBinaryString(0x15));
				sbBaseInfo.reverse();
				if (sbBaseInfo.length() < 5) {
					sbBaseInfo.insert(0, "00000");
				}
				if (sbBaseInfo.length() > 5) {
					sbBaseInfo.delete(0, sbBaseInfo.length() - 5);
				}
				String strCmd = Integer.toHexString(Integer.parseInt(sbBaseInfo.insert(0, "100").toString(), 2));
				strCmd = strCmd.substring(strCmd.length() - 2, strCmd.length());
				
				Log.e(null, "strCmd-----------------------:"+strCmd);
				Log.d(TAG, "strCmd:" + strCmd);
				byte[] byeBaseCmd = { 0x00, (byte) 0xB0, (byte) Integer.parseInt(strCmd, 16), 0x00, 0x00 };
				
				Log.e(null, "byeBaseCmd-----------------------:"+byeBaseCmd);
				byte[] byeBaseInfo = isodep.transceive(byeBaseCmd);
				Log.d(TAG, "byeBaseInfo:" + HexToString(byeBaseInfo));
				if (HexToString(byeBaseInfo).length() >= 20) {
					cardNo = HexToString(byeBaseInfo).substring(20, 40);
					Log.d(TAG, "cardNo:" + cardNo);
					intent.putExtra("cardNo", cardNo);
				} else {
					Toast.makeText(ReadActivity.this, "暂不支持此卡类型", Toast.LENGTH_LONG).show();
				}

				byte[] byeBalance = { (byte) 0x80, (byte) 0x5C, 0x00, 0x02, 0x04 };
				byte[] byeBalanceRsp = isodep.transceive(byeBalance);
				Log.d(TAG, "balanceRsp:" + HexToString(byeBalanceRsp));
				if (byeBalanceRsp != null && byeBalanceRsp.length > 4) {
					int intCash = byteToInt(byeBalanceRsp, 4);
					Log.d(TAG, "余额(分)：" + intCash);
					intent.putExtra("balance", intCash);
					
					
			       	intent.setClass(ReadActivity.this, OrderActivitynew.class);
					startActivity(intent);
					ReadActivity.this.finish();
				}
				
				
			}
			isodep.close();
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(ReadActivity.this, "读取卡片失败，请重新放置卡片", Toast.LENGTH_SHORT).show();
		} finally {
			try {
				if (isodep != null) {
					isodep.close();
				}
			} catch (IOException e) {
				Log.e(TAG, Log.getStackTraceString(e));
			}
		}
	}


	/**
	 * @see 读取m1卡密钥
	 */
	private void getKeyB(final String serialNo) {
		new Thread(sernaoxul).start();
	}
	Runnable sernaoxul = new Runnable() {
		@Override
		public void run() {
			
			if (initConnect()){
				//TODO
			//	initConnect();
				initConnect2();
				Log.e(null, "999999##############################"+ConstData.bytesToHexString2(ConstData.getxuliestatus(serialNo)));
				sendData(ConstData.getxuliestatus(serialNo));
				
				new Thread(recRunnable).start(); // 接收数据
			}else{
				Message message2 = handler.obtainMessage();
				
				message2.what =Constant.WHAT_CONN_DB_FAILURE;
				message2.obj="服务器连接异常，请检查网络";
					message2.sendToTarget();
				
			}
			
			// 登录包
			//sendData(ConstData.getLoginBags());
			
			
			
		}
	};
	
	
	
	

	private byte[] getSelectCommand(byte[] aid) {
		final ByteBuffer cmd_pse = ByteBuffer.allocate(aid.length + 6);
		cmd_pse.put((byte) 0x00) // CLA Class
				.put((byte) 0xA4) // INS Instruction
				.put((byte) 0x04) // P1 Parameter 1
				.put((byte) 0x00) // P2 Parameter 2
				.put((byte) aid.length) // Lc
				.put(aid).put((byte) 0x00); // Le
		return cmd_pse.array();
	}

	private String HexToString(byte[] data) {
		String temp = "";
		for (byte d : data) {
			temp += String.format("%02x", d);
		}
		return temp;
	}

	private int byteToInt(byte[] b, int n) {
		int ret = 0;
		for (int i = 0; i < n; i++) {
			ret = ret << 8;
			ret |= b[i] & 0x00FF;
		}
		if (ret > 100000 || ret < -100000)
			ret -= 0x80000000;
		return ret;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.WHAT_WRITE_CARD_SUCCESS:
				flagImg.setVisibility(View.VISIBLE);
				flagfalseImg.setVisibility(View.GONE);
				cardImg.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				readTitleText.setText("充值成功");
				readToastText.setText(Utils.FenToYuan(defaultSum) );
				readMessText.setText(Utils.FenToYuan(afterChargeMoney));
				controlBtn.setVisibility(View.VISIBLE);// 显示返回首页按钮

				orderId = getIntent().getStringExtra("orderId");

				/*chargeFlag = true; // 更新已经充值写卡标志之后，再处理连接数据库
				updateChargeStatus(cardNo, defaultSum, afterChargeMoney, orderId);*/

				String order2 = "";
				SharedPreferences sp = ReadActivity.this.getSharedPreferences("recharge", Activity.MODE_PRIVATE);
				order2 = sp.getString("orderid", "");
				Editor editor = sp.edit();
				// editor.clear();
				editor.commit();
				Log.e(null, "2222222******************************" + order2);
				dbr.beginTransaction();

				ContentValues values = new ContentValues();
				values.put("status", "1");
				dbr.update("Recharge", values, "orderid=?", new String[] { order2 });
				dbr.setTransactionSuccessful();
				dbr.endTransaction();
				/*new Thread(){
					public void run() {
						updateStatus();
					};
				}.start();*/
				break;
				
			case Constant.WHAT_WRITE_CARD_SUCCESS2:
				flagImg.setVisibility(View.VISIBLE);
				flagfalseImg.setVisibility(View.GONE);
				cardImg.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				readTitleText.setText("充值成功");
				readToastText.setText(Utils.FenToYuan(defaultSum) );
				readMessText.setText(Utils.FenToYuan(afterChargeMoney));
				controlBtn.setVisibility(View.VISIBLE);// 显示返回首页按钮

				orderId = getIntent().getStringExtra("orderId");
			
				break;
			case Constant.WHAT_CONN_DB_exception:
				mSuperLoadingProgress.setVisibility(View.VISIBLE);
				new Thread(){
		            @Override
		            public void run() {
		                try {
		                    mSuperLoadingProgress.setProgress(0);
		                    while(mSuperLoadingProgress.getProgress()<100) {
		                        Thread.sleep(10);
		                        mSuperLoadingProgress.setProgress(mSuperLoadingProgress.getProgress() + 1);
		                    }
		                    mSuperLoadingProgress.finishFail();
		                } catch (InterruptedException e) {
		                    e.printStackTrace();
		                }
		            }
		        }.start();
				flagImg.setVisibility(View.GONE);
				flagfalseImg.setVisibility(View.GONE);
				cardImg.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				readTitleText.setText("写卡成功,但与后台通讯中断");
				readToastText.setText(Utils.FenToYuan(defaultSum) );
				readMessText.setText(Utils.FenToYuan(afterChargeMoney));
				controlBtn.setVisibility(View.VISIBLE);// 显示返回首页按钮
				falsete1.setText("写卡成功,但与后台通讯中断");
				orderId = getIntent().getStringExtra("orderId");
			
				break;
			case Constant.WHAT_CONN_DB_FAILURE:
				flagImg.setVisibility(View.GONE);
				flagfalseImg.setVisibility(View.VISIBLE);
				cardImg.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				readTitleText.setText("网络连接异常！");
				Toast.makeText(ReadActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case Constant.WHAT_GET_KEYB_FAILURE:
				flagImg.setVisibility(View.GONE);
				flagfalseImg.setVisibility(View.VISIBLE);
				cardImg.setVisibility(View.GONE);
				text.setVisibility(View.GONE);
				falsete1.setVisibility(View.GONE);
				falsete2.setVisibility(View.GONE);
				readTitleText.setText("读卡失败！");
				Toast.makeText(ReadActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case Constant.WHAT_GET_KEYB_SUCCESS:
				byte[] NkeyB_1 = Utils.hexString2Bytes(HYENCRY.decode(msg.obj.toString()));
//				Tag tagFromIntent = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
				handleCardM1(NkeyB_1, tagFromIntent);
				break;
				
			case 99:
			
				try {
					isodep = IsoDep.get(tagFromIntent);
					isodep.connect();
					byte[] mf = { (byte) '2', (byte) 'P', (byte) 'A', (byte) 'Y', (byte) '.', (byte) 'S', (byte) 'Y',
							(byte) 'S', (byte) '.', (byte) 'D', (byte) 'D', (byte) 'F', (byte) '0', (byte) '1', };
					byte[] mfRsp;
					mfRsp = isodep.transceive(getSelectCommand(mf));
					Log.d(TAG, "mfRsp:" + HexToString(mfRsp));
					// select Main Application
					// byte[] szt = { (byte) 'P', (byte) 'A', (byte) 'Y', (byte)
					// '.',
					// (byte) 'S', (byte) 'Z', (byte) 'T' };
					byte[] szt = { (byte) 0xA0, 0x00, 0x00, 0x06, 0x32, 0x01, 0x01, 0x05 };
					byte[] sztRsp = isodep.transceive(getSelectCommand(szt));
					Log.d(TAG, "sztRsp:" + HexToString(sztRsp));

					// 读取余额
					byte[] byeBalance = { (byte) 0x80, (byte) 0x5C, 0x00, 0x02, 0x04 };
					byte[] byeBalanceRsp = isodep.transceive(byeBalance);
					Log.d(TAG, "balanceRsp:" + HexToString(byeBalanceRsp));

					/**************** 校验卡号是否一致 开始 *******************/
					// 读取公共应用基本文件(0x15)
					StringBuffer sbBaseInfo = new StringBuffer(Integer.toBinaryString(0x15));
					sbBaseInfo.reverse();
					if (sbBaseInfo.length() < 5) {
						sbBaseInfo.insert(0, "00000");
					}
					if (sbBaseInfo.length() > 5) {
						sbBaseInfo.delete(0, sbBaseInfo.length() - 5);
					}
					String strCmd = Integer.toHexString(Integer.parseInt(sbBaseInfo.insert(0, "100").toString(), 2));
					strCmd = strCmd.substring(strCmd.length() - 2, strCmd.length());
					Log.d(TAG, "strCmd:" + strCmd);
					byte[] byeBaseCmd = { 0x00, (byte) 0xB0, (byte) Integer.parseInt(strCmd, 16), 0x00, 0x00 };
					byte[] byeBaseInfo = isodep.transceive(byeBaseCmd);
					Log.d(TAG, "byeBaseInfo:" + HexToString(byeBaseInfo));
					String curCardNo = HexToString(byeBaseInfo).substring(20, 40);
					/*if (curCardNo == null || cardNo == null || !curCardNo.equals(cardNo)) {
						Toast.makeText(ReadActivity.this, "卡号两次读取不一致，请更换卡片后重试", Toast.LENGTH_SHORT).show();
						return;
					}*/
					/**************** 校验卡号是否一致 结束 *******************/

					StringBuffer sbCharge = new StringBuffer(Integer.toHexString(defaultSum));
					if (sbCharge.length() < 8) {
						sbCharge.insert(0, "00000000");
						sbCharge.delete(0, sbCharge.length() - 8);
					}
					sbCharge.append("000000100001");
					byte[] byeChargeCmd = { (byte) 0x80, 0x50, 0x00, 0x02, 0x0B, 0x01 };
					ByteBuffer chargeCmdBuffer = ByteBuffer.allocate(byeChargeCmd.length + sbCharge.length() / 2+1);
					chargeCmdBuffer.put(byeChargeCmd).put(Utils.hexString2Bytes(sbCharge.toString())).put((byte) 0x00);
					Log.d(TAG, "圈存指令:" + HexToString(chargeCmdBuffer.array()));
					byte[] byeInitQC = isodep.transceive(chargeCmdBuffer.array());
					chargeCmdBuffer.clear();
					// 第4-5字节为交易序号，第8-11字节为伪随机数
					Log.d(TAG, "初始化圈存:" + HexToString(byeInitQC));
					/****************** 校验 MAC 开始 ***********************/
					String strMAC1 = Utils.bytesToHexString(byeInitQC, 12, 15);
					Log.e(null, "strMAC1========" + strMAC1+"////////////////"+HexToString(byeInitQC));
					chargeCmdBuffer = ByteBuffer.allocate(8);
					chargeCmdBuffer.put(byeInitQC[8]).put(byeInitQC[9]).put(byeInitQC[10]).put(byeInitQC[11])
							.put(byeInitQC[4]).put(byeInitQC[5]).put((byte) 0x80);
					Log.d(TAG, "src:" + Utils.bytesToHexString(chargeCmdBuffer.array()));
					String str3Des = DesPBOC2.Encry3DESStrHex(Utils.bytesToHexString(chargeCmdBuffer.array()),
							key.toUpperCase());
					Log.d(TAG, "str3Des:" + str3Des);
					String strCurDate = sdfLong.format(new Date());
					sbCharge.insert(8, "02");
					String strData = Utils.bytesToHexString(byeBalanceRsp, 0, 3) + sbCharge.toString();
					String strMac2 = DesPBOC2.SMacHexStr(strData, str3Des);

					Log.e(null, "strMac1========" + strMAC1);
					Log.e(null, "strMac2========" + strMac2);
					

					if (!strMAC1.toUpperCase().equals(strMac2.substring(0, 8).toUpperCase())) {
						Toast.makeText(ReadActivity.this, "MAC校验失败，无法充值。", Toast.LENGTH_SHORT).show();
						return;
					}     
					/****************** 校验 MAC 结束 ***********************/

					sbCharge.append(strCurDate);
					String strMac = DesPBOC2.SMacHexStr(sbCharge.toString(), str3Des);
					Log.d(TAG, "strMac:" + strMac);
					sbCharge.delete(0, sbCharge.length());
					sbCharge.append("805200000B").append(strCurDate).append(strMac.substring(0, 8));
					Log.d(TAG, "sbCharge:" + sbCharge.toString());
					// 圈存交易
					byte[] byeRet = isodep.transceive(Utils.hexString2Bytes(sbCharge.toString()));
					Log.d(TAG, "圈存交易:" + HexToString(byeRet));
					// 获取充值后余额
					byte[] balance = { (byte) 0x80, (byte) 0x5C, 0x00, 0x02, 0x04 };
					byte[] balanceRsp = isodep.transceive(balance);
					Log.d(TAG, "balanceRsp:" + HexToString(balanceRsp));
					if (balanceRsp != null && balanceRsp.length > 4) {
						afterChargeMoney = byteToInt(balanceRsp, 4);
						
						Log.e(null, "-----------------------------+++++++++++++++++"+afterChargeMoney);
						
						Log.d(TAG, "余额(分)：" + afterChargeMoney);
					}
					
					
					

					// 更新数据库状态
					chargeFlag = true; // 更新已经充值写卡标志之后，再处理连接数据库
					/*new Thread(){
						public void run() {
							updateStatus();
						};
					}.start();*/
					SharedPreferences sp3 = ReadActivity.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
					phone = sp3.getString("username", "");
					Editor editor3 = sp3.edit();
					editor3.commit();
					updateChargeStatus(cardNo, defaultSum, afterChargeMoney, phone);
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
				
				
				
				
				
				
			}

		}
	};

private  void updateStatus(){
		
		new Thread(statu).start();
		
	}
	
	Runnable statu = new Runnable() {
		@Override
		public void run() {
			
			if (initConnect()){
				initConnect();
				
			//	Log.e(null, "##############################"+ConstData.bytesToHexString2(ConstData.getLoginstatus(order)));
				
				Log.e(null, "###rrrrrrrrrrr###########################"+"0000000000000000000");
				
				sendData(ConstData.getLoginstatus("000000000000000000000000000000"));
				
				new Thread(recRunnable).start(); // 接收数据
			}else{
				Message message2 = handler.obtainMessage();
				
				message2.what =Constant.WHAT_CONN_DB_FAILURE;
				message2.obj="服务器连接异常，请检查网络";
					message2.sendToTarget();
				
			}
			
			// 登录包
			//sendData(ConstData.getLoginBags());
			
			
			
		}
	};
	public boolean initConnect() {
		try {
			socket = new Socket();
			SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
			socket.connect(socAddress, 5000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
		//	receiveTime = (int) (System.currentTimeMillis() / 1000L);
			ConstData.isConnect = true;
			ConstData.initNetFlag = true;
Log.e(null, "连接成功连接成功连接成功连接成功连接成功连接成功连接成功连接成功连接成功");
			Log.d(TAG, "连接成功");
			return true;
		} catch (IOException e) {
			ConstData.isConnect = false;
			e.printStackTrace();
			Log.d(TAG, "连接失败");
			return false;
		}
	}
	
	public boolean initConnect2() {
		try {
			socket = new Socket();
		
			SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
			socket.connect(socAddress, 5000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
		//	receiveTime = (int) (System.currentTimeMillis() / 1000L);
			ConstData.isConnect = true;
			ConstData.initNetFlag = true;
Log.e(null, "连接成功连接成功连接成功连接成功连接成功连接成功连接成功连接成功连接成功");
			Log.d(TAG, "连接成功");
			return true;
		} catch (IOException e) {
			ConstData.isConnect = false;
			e.printStackTrace();
			Log.d(TAG, "连接失败");
			return false;
		}
	}
	public void sendData(byte[] b) {
		try {
			out = getOut();
			out.write(b);
			out.flush();

		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, "数据发送失败");
		}
	}
	
	Runnable recRunnable = new Runnable() {
		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			int size = 0;
			byte[] buffer=new byte[26];
			/*
			 * boolean isSucc = false; boolean isSuccf = false; boolean
			 * isSuccfull = false; String strEntryKey = null;
			 */
			exit: while (true) {
				if (!ConstData.isConnect) {
					return;
				}
				try {
					int intRec = in.available();
					while (intRec == 0) {
						intRec = in.available();
						// 比较收包时间大于一分半,判定服务器socket连接已关闭
						/*if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
							break;
						}*/
					}
					// 比较收包时间大于一分半,判定服务器socket连接已关闭
					/*if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
						stop();
						receiveTime = (int) (System.currentTimeMillis() / 1000L);
						return;
					}
					receiveTime = (int) (System.currentTimeMillis() / 1000L);*/
					
					
					
					byte[] byeRec = new byte[intRec];
					int count = in.read(byeRec);
					size = count;
					while (true) {
						if (size < 7) {
							break;
						}
						
						Log.e(null, "---------------jieshou--------------------------------"+HexToString(byeRec));
						// 80是手持机发送的包，A0是服务器发回的
						if ((byeRec[0] == (byte) 160) && (byeRec[6]) == (byte) 0x85) {
							Message message2 = handler.obtainMessage();
							if(byeRec[28]==(byte)0x01){
								message2.what = Constant.WHAT_WRITE_CARD_SUCCESS2;
								stop();
								message2.sendToTarget();
								Log.e(null, "---------------------shoushou--------------------------");
								return;
							}else{
								String msg = "数据库状态更新失败";
								message2.what = Constant.WHAT_CONN_DB_FAILURE;
								message2.obj = msg;
								stop();
								
							}
							message2.sendToTarget();
						
							
							Log.e(null, "---------------------shoushou--------------------------"+(byeRec[28]==(byte)0x01));
							
							return;
							
						} else if ((byeRec[0] == (byte) 160) && (byeRec[6]) == (byte) 0xaa){
							
							if((byeRec[30]==(byte)0x04)){
								
								if((byeRec[32]==(byte)0x1a)){
									
									if(send){
										send=false;
										stop();
										Message message4 = handler.obtainMessage();
										message4.what = Constant.WHAT_GET_KEYB_FAILURE;
										message4.obj = "序列号获取异常"+ConstData.bytesToHexString2(byeRec);
										message4.sendToTarget();
									}
									
								}else{
									
									Message message5 = handler.obtainMessage();
									buffer[0]=byeRec[32];
									buffer[1]=byeRec[33];
									buffer[2]=byeRec[34];
									buffer[3]=byeRec[35];
									buffer[4]=byeRec[36];
									buffer[5]=byeRec[37];
									buffer[6]=byeRec[38];
									buffer[7]=byeRec[39];
									buffer[8]=byeRec[40];
									buffer[9]=byeRec[41];
									buffer[10]=byeRec[42];
									buffer[11]=byeRec[43];
									buffer[12]=byeRec[44];
									buffer[13]=byeRec[45];
									buffer[14]=byeRec[46];
									buffer[15]=byeRec[47];
									buffer[16]=byeRec[48];
									buffer[17]=byeRec[49];
									buffer[18]=byeRec[50];
									buffer[19]=byeRec[51];
									buffer[20]=byeRec[52];
									buffer[21]=byeRec[53];
									buffer[22]=byeRec[54];
									buffer[23]=byeRec[55];
									buffer[24]=byeRec[56];
									buffer[25]=byeRec[57];
									String key = new String(buffer);
									message5.what = Constant.WHAT_GET_KEYB_SUCCESS;
									message5.obj = key;
									message5.sendToTarget();
									Log.e(null, "----44444---"+new String(buffer));
									break;

								}
							}else{
								if(send){
									send=false;
									stop();
									
									if(cardNo.length()==20){
										Message message3 = handler.obtainMessage();
										message3.what = Constant.WHAT_WRITE_CARD_SUCCESS2;
										message3.sendToTarget();
									}else{
										Message message3 = handler.obtainMessage();
										message3.what = Constant.WHAT_GET_KEYB_FAILURE;
										message3.obj = "序列号获取异常";
										message3.sendToTarget();
									}
								
								}
								
							}
							
							//	Log.e(null, "---------------------222222222shoushou--------------------------"+(byeRec[28]==(byte)0x01));
						}
						
					}
				} catch (Exception e) {
					Log.e(TAG, Log.getStackTraceString(e));
					/*if ((int) (System.currentTimeMillis() / 1000L) - receiveTime > 60) {
						stop();
						reConnect();
					}*/
				}
			}


			
			
		}

	};
	
	/***
	 * 更新数据库充值状态
	 */
	private void updateChargeStatus(final String cardNo, final int chargeMoney, final int afterChargeMoney,
			final String userName) {
		Thread td = new Thread() {
			
			
			
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("cardNo", HYENCRY.encode(cardNo));
				map.put("chargeMoney", Integer.toString(chargeMoney));
				map.put("afterChargeMoney", Integer.toString(afterChargeMoney));
			//	map.put("userName", HYENCRY.encode("100001"));
				map.put("userName", HYENCRY.encode(phone));
				map.put("terminalType", "0");
				map.put("orderId", HYENCRY.encode(orderId));
				String jsonStr = DBHandler.getRecord(DBHandler.ACTION_UPDATE_CHARGE_STATUS, map);
				
				Log.e(null, orderId+"666666666666666666666666777"+HYENCRY.encode(cardNo));
				Log.e(null, orderId+"66666666666666666666666677788"+Integer.toString(chargeMoney));
				Log.e(null, orderId+"66666666666666666666666677799"+Integer.toString(afterChargeMoney));
				Log.e(null, orderId+"666666666666666666666666777112"+HYENCRY.encode(userName));
				
				Log.e(null, orderId+"666666666666666666666666"+jsonStr);
				Message message = handler.obtainMessage();
				try {
					JSONObject json = new JSONObject(jsonStr);
					Log.e(null, "666666666666666666666666777777777"+json);
					if(json != null && json.getString("success") != null){
						String success = json.getString("success");
						if (success.equals("true")) {
							
							Message message2 = handler.obtainMessage();
							message2.what = Constant.WHAT_WRITE_CARD_SUCCESS;
							message2.sendToTarget();
							/*Thread tdup = new Thread() {
								public void run() {
									Map<String, String> map = new HashMap<String, String>();
									map.put("cardNo", HYENCRY.encode(cardNo));
									 map.put("orderId", orderId);
									String action = null;
									Message message = handler.obtainMessage();
									action = DBHandler.ACTION_UPDATE_MOBILE_STATUS_ORDER2;
									String jsonStr = DBHandler.getRecord(action, map);
									Log.e("更新订单状态", "-------------------------------" + jsonStr);
									try {
										JSONObject json = new JSONObject(jsonStr);
										if (json != null && json.getString("success") != null) {
											String success = json.getString("success");
											if (success.equals("true")) {
												Log.e("更新订单状态", "-------------------------------" + "8888888");
												
												Message message2 = handler.obtainMessage();
												message2.what = Constant.WHAT_WRITE_CARD_SUCCESS;
												message2.sendToTarget();

											} else {
												Log.e("更新订单状态", "-------------------------------" + "9999");
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


		
							};
							tdup.start();*/
						} else {
							
							
							String msg = json.getString("msg");
							msg = msg == null || msg.trim().equals("") ? "数据库状态更新失败" : msg;
							message.what = Constant.WHAT_CONN_DB_exception;
							message.obj = msg;
						}
					}
				} catch (JSONException e) {
					message.what = Constant.WHAT_CONN_DB_exception;
					message.obj = "数据库状态更新失败："+e.getMessage();
					Log.e("数据库状态更新失败",e.getMessage());
				}
				message.sendToTarget();
			}
		};
		td.start();
	}
	
	public boolean reConnect() {
		SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
		try {
			socket = new Socket();
			socket.connect(socAddress, 3000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
			ConstData.isConnect = true;
		//	sendData(ConstData.getLoginBags());
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
	
	public Socket getSocket() {
		return socket;
	}

	public InputStream getIn() {
		return in;
	}

	public OutputStream getOut() {
		return out;
	}
	

	

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.toHomeBtnId:// 返回首页
			Intent intent = new Intent(ReadActivity.this, MainActivity.class);
			startActivity(intent);
			ReadActivity.this.finish();
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// 按返回键时返回到主页面
		/*
		 * Intent intent = new Intent(ReadActivity.this, MainActivity.class);
		 * startActivity(intent);
		 */
		ReadActivity.this.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Log.e(null, "******************************" + action);
			if (action.equals("onLine") || action.equals("charge")) {
				String order2 = "";
				SharedPreferences sp = ReadActivity.this.getSharedPreferences("recharge", Activity.MODE_PRIVATE);
				order2 = sp.getString("orderid", "");
				Editor editor = sp.edit();
				// editor.clear();
				editor.commit();
				Log.e(null, "2222222******************************" + order2);
				dbr.beginTransaction();

				ContentValues values = new ContentValues();
				values.put("status", "-1");
				dbr.update("Recharge", values, "orderid=?", new String[] { order2 });
				dbr.setTransactionSuccessful();
				dbr.endTransaction();
			}
			Intent intent = new Intent(ReadActivity.this, MainActivity.class);
			startActivity(intent);
			ReadActivity.this.finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}
	
	
	/**
	 * 处理M1卡
	 * */
	public void handleCardM1(byte[] NkeyB_1, Tag tagFromIntent) {
		
		MifareClassic mics = MifareClassic.get(tagFromIntent);
		// 充值的单位是 分
		try {
			mics.connect();
			if (action.equals("balance") || action.equals("read")) {
				// 这里只验证KeyB通过即可，不用管KEYA
				// boolean flag1=mics.authenticateSectorWithKeyA(1, NkeyA_1);
				Intent intent = new Intent(ReadActivity.this, ChargeActivity.class);
				intent.putExtra("action", action);
				boolean keyBFlag = mics.authenticateSectorWithKeyB(1, NkeyB_1);
				if (keyBFlag) {
					byte[] cardBlock = mics.readBlock(4); // 卡号,1扇区4块，4--7字节
					
					
					byte[] cardNoBytes = new byte[4];
					cardNoBytes[0] = cardBlock[4];
					cardNoBytes[1] = cardBlock[5];
					cardNoBytes[2] = cardBlock[6];
					cardNoBytes[3] = cardBlock[7];
					cardNo = Utils.bytesToHexString(cardBlock, 4, 7);
					//卡号不足9位时前补0，补足9位
					if (cardNo.length() == 8) {
						cardNo = "0" + cardNo;
					}
					
					Log.e(null, "卡号是===================="+cardNo);
					intent.putExtra("cardNo", cardNo);
//					byte[] blockData = mics.readBlock(6);
//					// 充值时间
//					String year1 = Utils.bytesToHexString(new byte[] { blockData[0] }).trim();
//					String year2 = Utils.bytesToHexString(new byte[] { blockData[1] }).trim();
//					String month = Utils.bytesToHexString(new byte[] { blockData[2] }).trim();
//					String day = Utils.bytesToHexString(new byte[] { blockData[3] }).trim();
//					//充值前余额（单位：分）Hex低前高后
//					String m1 = Utils.bytesToHexString(new byte[] { blockData[4] }).trim();
//					String m2 = Utils.bytesToHexString(new byte[] { blockData[5] }).trim();
//					String m3 = Utils.bytesToHexString(new byte[] { blockData[6] }).trim();
//					String m4 = Utils.bytesToHexString(new byte[] { blockData[7] }).trim();
//					int money = Integer.parseInt(m4 + m3 + m2 + m1, 16);
//					String chargeTime = "卡号:" + cardNo + ";充值时间：" + year1 + year2
//							+ month + day + ";充值前余额："+ money;
//					Log.i("info", chargeTime);
//					timeView.setText(chargeTime);
				} else {
					Toast.makeText(ReadActivity.this, "读取卡号时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				//充值后余额.前低后高 16进制。1027 拆解为2710=十进制10000
				keyBFlag = mics.authenticateSectorWithKeyB(2, NkeyB_1);
				if (keyBFlag) {
					byte[] blockBytes = mics.readBlock(8);
					Log.e(null, "*******************"+Utils.bytesToHexString(blockBytes));
					//累计充值金额(分)Hex低前高后
					String m1 = Utils.bytesToHexString(new byte[] { blockBytes[0] }).trim();
					Log.e(null, "*111******************"+m1);
					String m2 = Utils.bytesToHexString(new byte[] { blockBytes[1] }).trim();
					Log.e(null, "*222******************"+m2);
					String m3 = Utils.bytesToHexString(new byte[] { blockBytes[2] }).trim();
					Log.e(null, "*222******************"+m3);
					String m4 = Utils.bytesToHexString(new byte[] { blockBytes[3] }).trim();
					Log.e(null, "*222******************"+m4);
					String sum = "累计充值金额16进制：" + m4 + m3 + m2 + m1;
					int sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					//充值后余额(分)Hex低前高后
					m1 = Utils.bytesToHexString(new byte[] { blockBytes[4] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes[5] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes[6] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes[7] }).trim();
					sum += "\n充值后余额16进制：" + m4 + m3 + m2 + m1;
					sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					// 钱包
					byte[] blockBytes1 = mics.readBlock(9);
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[0] }).trim();
					Log.e(null, "*///-------111******************"+m1);
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[1] }).trim();
					Log.e(null, "*///-------111******************"+m2);
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[2] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[3] }).trim();
					sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					intent.putExtra("balance", sum1);
					sum += "\n 钱包16进制：" + m4 + m3 + m2 + m1 + "\n  十进制" + sum1;
					// 钱包反
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[4] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[5] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[6] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[7] }).trim();
					Log.i("info", m4 + m3 + m2 + m1);
					sum += "\n 钱包反16进制：" + m4 + m3 + m2 + m1;
					// 钱包备份
					blockBytes1 = mics.readBlock(10);
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[0] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[1] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[2] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[3] }).trim();
					sum += "\n 钱包备份16进制：" + m4 + m3 + m2 + m1;
//					readToastText.setText(sum);
					Log.i("info", sum);
					
				    startActivity(intent);
//				    if (action.equals("balance")) {
				    	ReadActivity.this.finish();
//				    }
				} else {
					Toast.makeText(ReadActivity.this, "读取卡片余额时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
			} else if (action.equals("charge") && !chargeFlag) {// 已经充值过一次的不允许再充
				
				Toast.makeText(ReadActivity.this, "不支持M1卡的充值", Toast.LENGTH_SHORT).show();
				/*// 充值
				String money = getIntent().getStringExtra("money");
				String cardNo = getIntent().getStringExtra("cardNo");
				defaultSum = Integer.parseInt(money) * 100;//金额 单位为分
				//验证卡内卡号与所传卡号是否一致
				boolean keyBFlag = mics.authenticateSectorWithKeyB(1, NkeyB_1);
				if (keyBFlag) {
					byte[] cardBlock = mics.readBlock(4); // 卡号,1扇区4块，4--7字节
					String cardNoIn = Utils.bytesToHexString(cardBlock, 4, 7);
					if (Long.parseLong(cardNo)!= Long.parseLong(cardNoIn)) {
						Toast.makeText(ReadActivity.this, "获取的卡号与充值卡号不一致，充值失败", Toast.LENGTH_SHORT).show();
						return;
					}
				} else {
					Toast.makeText(ReadActivity.this, "读取卡号时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				byte[] preChargeByte = new byte[4];//充值前余额，将2扇区的充值后余额赋给此变量，并更新到1扇区中
				*//**
				 * 当对卡片进行充值/扣款操作时需要进行如下步骤： 1.读取4,5,8,9中的钱包值。
				 * 2.对钱包值进行判断，若钱包值不正确，则对钱包值进行恢复。 3.恢复原则
				 * 3.1若4,5,8,9中的值均一致，则认为钱包正确。
				 * 3.2若4,5一致，8,9一致，但4,5，与8,9钱包值不一致，则以8,9中的值为准，对4,5进行恢复
				 * 3.3若4,5一致, 8,9不一致，则用4,5中的值恢复8,9 3.4若8,9一致,
				 * 4,5不一致，则用8,9中的值恢复4.5 4．消费/充值 4.1
				 * 首先对4进行消费/充值操作，操作完后进行读取操作，若结果正确，则对5进行同样操作 4.2
				 * 若上面2步出现错误，则下次再对该卡进行操作时，首先进行恢复处理 4.3
				 * 对8进行消费/充值操作，操作完后进行读取操作,若该步骤出现错误
				 * ，则缓存该卡信息，下次对该卡片进行操作，先查询缓存查到该卡片，则使用4,5中的值对8进行恢复。 4.4
				 * 对9进行消费/充值操作，操作完后进行读取操作。若出现错误，不进行恢复操作。
				 * *//*
				// 1.把所有的数值读出来。从0开始2扇区，块索引8，9,10
				// 8：累计充值额，充值后余额，充值设备编号
				// 9:钱包正4字节，钱包反4字节，钱包正4字节
				// 10:钱包备份4字节，钱包备份反4字节，钱包备份4字节
				keyBFlag = mics.authenticateSectorWithKeyB(2, NkeyB_1);
				if (keyBFlag) {
					// ------------2扇区9块---------------------
					byte[] blockBytes = mics.readBlock(9);
					// 钱包
					String m1 = Utils.bytesToHexString(new byte[] { blockBytes[0] }).trim();
					String m2 = Utils.bytesToHexString(new byte[] { blockBytes[1] }).trim();
					String m3 = Utils.bytesToHexString(new byte[] { blockBytes[2] }).trim();
					String m4 = Utils.bytesToHexString(new byte[] { blockBytes[3] }).trim();
					int sum = Utils.bytes2Int(blockBytes, 0, 3, 1);//高低转换
					// 钱包反
					String m5 = Utils.bytesToHexString(new byte[] { blockBytes[4] }).trim();
					String m6 = Utils.bytesToHexString(new byte[] { blockBytes[5] }).trim();
					String m7 = Utils.bytesToHexString(new byte[] { blockBytes[6] }).trim();
					String m8 = Utils.bytesToHexString(new byte[] { blockBytes[7] }).trim();
					// 测试一下钱包和反值是否对应
					boolean testBool = Utils.isReverse(m1 + m2 + m3 + m4, m5 + m6 + m7 + m8);
					//TODO:校验testBool，若为false需恢复
					afterChargeMoney = sum + defaultSum;//
					
					
					String reverseStr = Utils.hexStringReverseOrder(Integer.toHexString(afterChargeMoney), 4 * 2);
					byte[] reverseBytes = Utils.hexString2Bytes(reverseStr);
					// 写钱包1
					blockBytes[0] = reverseBytes[0];
					blockBytes[1] = reverseBytes[1];
					blockBytes[2] = reverseBytes[2];
					blockBytes[3] = reverseBytes[3];
					// 写钱包反
					String newReverseStr = Utils.getReverseHexString(reverseStr);
					byte[] newReverseBytes = Utils.hexString2Bytes(newReverseStr);
					
					blockBytes[4] = newReverseBytes[0];
					blockBytes[5] = newReverseBytes[1];
					blockBytes[6] = newReverseBytes[2];
					blockBytes[7] = newReverseBytes[3];
					// 写钱包 2
					blockBytes[8] = reverseBytes[0];
					blockBytes[9] = reverseBytes[1];
					blockBytes[10] = reverseBytes[2];
					blockBytes[11] = reverseBytes[3];
					mics.writeBlock(9, blockBytes);
//					readToastText.setText("钱包原值：" + sum + "分 新值：" + afterChargeMoney
//							+ "分 \n钱包反原值：\n" + m5 + m6 + m7 + m8 + " 新反值："
//							+ reverseStr + "  " + testBool);
					// ----------2扇区8块--------------累计金额---------------
					byte[] totalChargeByte = mics.readBlock(8);
					int totalSum = Utils.bytes2Int(totalChargeByte, 0, 3, 1);
					int newTotalSum = totalSum + defaultSum;// 新累计值
					// int newTotalSum=10000;
					String newTotalStr = Integer.toHexString(newTotalSum);
					reverseStr = Utils.hexStringReverseOrder(newTotalStr, 4 * 2);
					reverseBytes = Utils.hexString2Bytes(reverseStr);
					// 赋值新累计金额
					totalChargeByte[0] = reverseBytes[0];
					totalChargeByte[1] = reverseBytes[1];
					totalChargeByte[2] = reverseBytes[2];
					totalChargeByte[3] = reverseBytes[3];
					
					// -------充值后余额  单位是 分
					preChargeByte[0] = totalChargeByte[4];
					preChargeByte[1] = totalChargeByte[5];
					preChargeByte[2] = totalChargeByte[6];
					preChargeByte[3] = totalChargeByte[7];
					int totalBalance = Utils.bytes2Int(totalChargeByte, 4, 7, 1);
					int newTotalBalance = totalBalance + defaultSum;//
					// int newTotalBalance=100;
					reverseStr = Utils.hexStringReverseOrder(
							Integer.toHexString(newTotalBalance), 4 * 2);
					reverseBytes = Utils.hexString2Bytes(reverseStr);
					// 赋值新 充值余额
					totalChargeByte[4] = reverseBytes[0];
					totalChargeByte[5] = reverseBytes[1];
					totalChargeByte[6] = reverseBytes[2];
					totalChargeByte[7] = reverseBytes[3];

//					String str = "充值前累计充值额：" + totalSum + "分后：" + newTotalSum
//							+ "分 \n充值前余额：" + totalBalance + "元\n后："
//							+ newTotalBalance + "元";
//					readTitleText.setText(str);
					mics.writeBlock(8, totalChargeByte);

					// Log.e("line 267", "第二次写扇区失败") ;

					// --2扇区 10块----写钱包备份------------------------------
					byte[] walletBytes = mics.readBlock(10);
					String t1 = Utils.bytesToHexString(new byte[] { walletBytes[0] }).trim();// 钱包备份
					String t2 = Utils.bytesToHexString(new byte[] { walletBytes[1] }).trim();
					String t3 = Utils.bytesToHexString(new byte[] { walletBytes[2] }).trim();// 钱包备份
					String t4 = Utils.bytesToHexString(new byte[] { walletBytes[3] }).trim();
					int walletBakSum = Utils.bytes2Int(walletBytes, 0, 3, 1);
					// 钱包备份反
					String t5 = Utils.bytesToHexString(new byte[] { walletBytes[4] }).trim();// 钱包备份反
					String t6 = Utils.bytesToHexString(new byte[] { walletBytes[5] }).trim();//
					String t7 = Utils.bytesToHexString(new byte[] { walletBytes[6] }).trim();//
					String t8 = Utils.bytesToHexString(new byte[] { walletBytes[7] }).trim();//
					// 测试一下钱包和反值是否对应
					boolean testWalletBool = Utils.isReverse(t1 + t2 + t3 + t4,
							t5 + t6 + t7 + t8);
					//TODO:校验testWalletBool，若为false需恢复
					int newWalletBakSum = walletBakSum + defaultSum;//
					String reverseWalletStr = Utils.hexStringReverseOrder(
							Integer.toHexString(newWalletBakSum), 4 * 2);
					byte[] reverseWalletBytes = Utils.hexString2Bytes(reverseWalletStr);
					
					// 写钱包备份1
					walletBytes[0] = reverseWalletBytes[0];
					walletBytes[1] = reverseWalletBytes[1];
					walletBytes[2] = reverseWalletBytes[2];
					walletBytes[3] = reverseWalletBytes[3];
					// 写钱包备份反
					String newReverseWalletStr = Utils.getReverseHexString(reverseWalletStr);
					byte[] newReverseWalletBytes = Utils.hexString2Bytes(newReverseWalletStr);
					
					walletBytes[4] = newReverseWalletBytes[0];
					walletBytes[5] = newReverseWalletBytes[1];
					walletBytes[6] = newReverseWalletBytes[2];
					walletBytes[7] = newReverseWalletBytes[3];
					// 写钱包备份 2
					walletBytes[8] = reverseWalletBytes[0];
					walletBytes[9] = reverseWalletBytes[1];
					walletBytes[10] = reverseWalletBytes[2];
					walletBytes[11] = reverseWalletBytes[3];
					mics.writeBlock(10, walletBytes);
//					String walletBakResult = "钱包备份原值：" + walletBakSum + "分 新："
//							+ newWalletBakSum + "分 \n钱包备份反原值：\n" + t5 + t6 + t7
//							+ t8 + " 新：" + newReverseWalletStr;
//					// ---------------
//					readToastText.setText(readToastText.getText().toString() + "\n"
//							+ walletBakResult);
				} else {
					Toast.makeText(ReadActivity.this, "密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				
				keyBFlag = mics.authenticateSectorWithKeyB(1, NkeyB_1);
				if (keyBFlag) {
					byte[] blockBytes = mics.readBlock(6);
					String currDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
					byte[] currDateBytes = Utils.hexString2Bytes(currDate);
					blockBytes[0] = currDateBytes[0];
					blockBytes[1] = currDateBytes[1];
					blockBytes[2] = currDateBytes[2];
					blockBytes[3] = currDateBytes[3];
					
					blockBytes[4] = preChargeByte[0];
					blockBytes[5] = preChargeByte[1];
					blockBytes[6] = preChargeByte[2];
					blockBytes[7] = preChargeByte[3];
					
					int chargeMoney = defaultSum / 100;//8,9填充充值金额，单位为元
					String pay = "0000" + chargeMoney;
					if (pay.length() > 4) {
						pay = pay.substring(pay.length() - 4, pay.length());
					}
					byte[] payBytes = Utils.hexString2Bytes(pay);
					blockBytes[8] = payBytes[0];
					blockBytes[9] = payBytes[1];
					mics.writeBlock(6, blockBytes);
					
					chargeFlag = true; // 更新已经充值写卡标志之后，再处理连接数据库
					//TODO
			//		updateChargeStatus(cardNo, defaultSum, afterChargeMoney, userName);
				} else {
					Toast.makeText(ReadActivity.this, "密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				
				updateChargeStatus(cardNo, defaultSum, afterChargeMoney, orderId);*/
				/*new Thread(){
					public void run() {
						updateStatus();
					};
				}.start();*/
			}else if (action.equals("onLine")|| action.equals("charge")) {
				boolean keyBFlag = mics.authenticateSectorWithKeyB(1, NkeyB_1);
				if (keyBFlag) {
					byte[] cardBlock = mics.readBlock(4); // 卡号,1扇区4块，4--7字节
					
					
					byte[] cardNoBytes = new byte[4];
					cardNoBytes[0] = cardBlock[4];
					cardNoBytes[1] = cardBlock[5];
					cardNoBytes[2] = cardBlock[6];
					cardNoBytes[3] = cardBlock[7];
					cardNo = Utils.bytesToHexString(cardBlock, 4, 7);
					//卡号不足9位时前补0，补足9位
					if (cardNo.length() == 8) {
						cardNo = "0" + cardNo;
					}
					
					Log.e(null, "卡号是===================="+cardNo);
			
					
				} else {
					Toast.makeText(ReadActivity.this, "读取卡号时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				//充值后余额.前低后高 16进制。1027 拆解为2710=十进制10000
				keyBFlag = mics.authenticateSectorWithKeyB(2, NkeyB_1);
				if (keyBFlag) {
					byte[] blockBytes = mics.readBlock(8);
					Log.e(null, "*******************"+Utils.bytesToHexString(blockBytes));
					//累计充值金额(分)Hex低前高后
					String m1 = Utils.bytesToHexString(new byte[] { blockBytes[0] }).trim();
					Log.e(null, "*111******************"+m1);
					String m2 = Utils.bytesToHexString(new byte[] { blockBytes[1] }).trim();
					Log.e(null, "*222******************"+m2);
					String m3 = Utils.bytesToHexString(new byte[] { blockBytes[2] }).trim();
					Log.e(null, "*222******************"+m3);
					String m4 = Utils.bytesToHexString(new byte[] { blockBytes[3] }).trim();
					Log.e(null, "*222******************"+m4);
					String sum = "累计充值金额16进制：" + m4 + m3 + m2 + m1;
					int sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					//充值后余额(分)Hex低前高后
					m1 = Utils.bytesToHexString(new byte[] { blockBytes[4] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes[5] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes[6] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes[7] }).trim();
					sum += "\n充值后余额16进制：" + m4 + m3 + m2 + m1;
					sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					// 钱包
					byte[] blockBytes1 = mics.readBlock(9);
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[0] }).trim();
					Log.e(null, "*///-------111******************"+m1);
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[1] }).trim();
					Log.e(null, "*///-------111******************"+m2);
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[2] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[3] }).trim();
					money55555 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
				}
				Intent intent=new Intent();
				/*intent.putExtra("cardNo", cardNo);
				intent.putExtra("balance", money55555);*/
				intent.setClass(ReadActivity.this, MainActivity.class);
				startActivity(intent);
				Toast.makeText(ReadActivity.this, "不支持M1卡充值", 3).show();
				ReadActivity.this.finish();
			}else if(action.equals("ordelist")){
				Intent intent = new Intent(ReadActivity.this, OrderActivitynew.class);
				intent.putExtra("action", action);
				boolean keyBFlag = mics.authenticateSectorWithKeyB(1, NkeyB_1);
				if (keyBFlag) {
					byte[] cardBlock = mics.readBlock(4); // 卡号,1扇区4块，4--7字节
					
					
					byte[] cardNoBytes = new byte[4];
					cardNoBytes[0] = cardBlock[4];
					cardNoBytes[1] = cardBlock[5];
					cardNoBytes[2] = cardBlock[6];
					cardNoBytes[3] = cardBlock[7];
					cardNo = Utils.bytesToHexString(cardBlock, 4, 7);
					//卡号不足9位时前补0，补足9位
					if (cardNo.length() == 8) {
						cardNo = "0" + cardNo;
					}
					
					Log.e(null, "卡号是===================="+cardNo);
					intent.putExtra("cardNo", cardNo);
//					byte[] blockData = mics.readBlock(6);
//					// 充值时间
//					String year1 = Utils.bytesToHexString(new byte[] { blockData[0] }).trim();
//					String year2 = Utils.bytesToHexString(new byte[] { blockData[1] }).trim();
//					String month = Utils.bytesToHexString(new byte[] { blockData[2] }).trim();
//					String day = Utils.bytesToHexString(new byte[] { blockData[3] }).trim();
//					//充值前余额（单位：分）Hex低前高后
//					String m1 = Utils.bytesToHexString(new byte[] { blockData[4] }).trim();
//					String m2 = Utils.bytesToHexString(new byte[] { blockData[5] }).trim();
//					String m3 = Utils.bytesToHexString(new byte[] { blockData[6] }).trim();
//					String m4 = Utils.bytesToHexString(new byte[] { blockData[7] }).trim();
//					int money = Integer.parseInt(m4 + m3 + m2 + m1, 16);
//					String chargeTime = "卡号:" + cardNo + ";充值时间：" + year1 + year2
//							+ month + day + ";充值前余额："+ money;
//					Log.i("info", chargeTime);
//					timeView.setText(chargeTime);
				} else {
					Toast.makeText(ReadActivity.this, "读取卡号时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
				//充值后余额.前低后高 16进制。1027 拆解为2710=十进制10000
				keyBFlag = mics.authenticateSectorWithKeyB(2, NkeyB_1);
				if (keyBFlag) {
					byte[] blockBytes = mics.readBlock(8);
					Log.e(null, "*******************"+Utils.bytesToHexString(blockBytes));
					//累计充值金额(分)Hex低前高后
					String m1 = Utils.bytesToHexString(new byte[] { blockBytes[0] }).trim();
					Log.e(null, "*111******************"+m1);
					String m2 = Utils.bytesToHexString(new byte[] { blockBytes[1] }).trim();
					Log.e(null, "*222******************"+m2);
					String m3 = Utils.bytesToHexString(new byte[] { blockBytes[2] }).trim();
					Log.e(null, "*222******************"+m3);
					String m4 = Utils.bytesToHexString(new byte[] { blockBytes[3] }).trim();
					Log.e(null, "*222******************"+m4);
					String sum = "累计充值金额16进制：" + m4 + m3 + m2 + m1;
					int sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					//充值后余额(分)Hex低前高后
					m1 = Utils.bytesToHexString(new byte[] { blockBytes[4] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes[5] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes[6] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes[7] }).trim();
					sum += "\n充值后余额16进制：" + m4 + m3 + m2 + m1;
					sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					sum += "\n  十进制" + sum1 + " \n 二进制" + Integer.toBinaryString(sum1);
					
					// 钱包
					byte[] blockBytes1 = mics.readBlock(9);
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[0] }).trim();
					Log.e(null, "*///-------111******************"+m1);
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[1] }).trim();
					Log.e(null, "*///-------111******************"+m2);
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[2] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[3] }).trim();
					sum1 = Integer.parseInt(m4 + m3 + m2 + m1, 16);
					intent.putExtra("balance", sum1);
					sum += "\n 钱包16进制：" + m4 + m3 + m2 + m1 + "\n  十进制" + sum1;
					// 钱包反
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[4] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[5] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[6] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[7] }).trim();
					Log.i("info", m4 + m3 + m2 + m1);
					sum += "\n 钱包反16进制：" + m4 + m3 + m2 + m1;
					// 钱包备份
					blockBytes1 = mics.readBlock(10);
					m1 = Utils.bytesToHexString(new byte[] { blockBytes1[0] }).trim();
					m2 = Utils.bytesToHexString(new byte[] { blockBytes1[1] }).trim();
					m3 = Utils.bytesToHexString(new byte[] { blockBytes1[2] }).trim();
					m4 = Utils.bytesToHexString(new byte[] { blockBytes1[3] }).trim();
					sum += "\n 钱包备份16进制：" + m4 + m3 + m2 + m1;
//					readToastText.setText(sum);
					Log.i("info", sum);
					
				    startActivity(intent);
//				    if (action.equals("balance")) {
				    	ReadActivity.this.finish();
//				    }
				} else {
					Toast.makeText(ReadActivity.this, "读取卡片余额时密钥验证失败", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		} catch (IOException e) {
			Log.e("处理卡失败", Log.getStackTraceString(e));
			e.printStackTrace();
		} finally {
			try {
				if (mics != null) {
					mics.close();
				}
			} catch (IOException e) {
				Log.e("mics.close失败", Log.getStackTraceString(e));
			}
		}
	}
	
	

}
