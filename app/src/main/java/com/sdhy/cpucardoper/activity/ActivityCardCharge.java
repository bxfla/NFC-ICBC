package com.sdhy.cpucardoper.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.pay.demo.PayResult;
import com.alipay.sdk.pay.demo.util.OrderInfoUtil2_0;
import com.alpha.live.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nohttp.activity.BaseActivityse;
import com.nohttp.dialog.CProgressDialog;
import com.nohttp.util.MyX509TrustManager;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.HYENCRY;
import com.sdhy.common.Utils;
import com.sdhy.cpucardoper.view.FlowRadioGroup;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.weixinpay.Constants;
import com.weixinpay.MD5;

/**
 * @作者：GPX @时间：20170427
 * @描述：显示充值信息页面
 */
public class ActivityCardCharge extends BaseActivityse implements OnClickListener {
	private TextView cardNoText;// 卡号
	private TextView balanceText;// 余额
	private ImageView iv_back;
	private FlowRadioGroup rg;
	private RelativeLayout alljob_black2;
	private RadioButton rb1, rb2, rb3, rb4, rb5, rb6, rb;
	private Button btn_next;
	String TAG = "mifare";
	Thread moneyThread;
	private CProgressDialog dialog;
	Dialog dialogType;
	private PopupWindow mpopupWindow;// 自定义的弹出框
	private String strMoney="";
	private com.nohttp.util.AlertDialog aldialog;

	/** 支付宝支付业务：入参app_id */
	public static final String APPID = "2017071007703608";

	/** 支付宝账户登录授权业务：入参pid值 */
	public static final String PID = "2088221932370500";
	/** 支付宝账户登录授权业务：入参target_id值 */
	public static final String TARGET_ID = "66262626";// 此参数为随机数

	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/**
	 * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097
	 * &docType=1
	 */
	public static final String RSA2_PRIVATE = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCD8BPo30WJEtI8FWP5NgGVpuyqSen3YjPzR1kei85mZ3CSXM+HkcrNa+Zo52GdgcoQIxgKuwPFD/5+Zra77GyCq/OyeQ2DR/9hlP8ZR7jNHuF8yVfbWcW/brD7nDOX9NCzqwRxAH6a9PF2IR3qtYwjeoqjSRgm8NVU4KeM9hckc2ZaDj8e5SnmYG9Ghh47nWTTuAntSzQ73YvAO/S3WhymhxINzUSG7eUvE7ZENxU4L1SXIcNuxbhlQFw8Qaj8iE5PQGtxtSvC4MyIFuhXbWj79nFTiIRbVnxw6ZXsx/rVIYMjbtr6ofDEQQ9CbpS7vsVx2NqaC94Vwq9qGAm7eW/5AgMBAAECggEAb8iqtbTljzt5EBr3dyAAu7CP979kEYsMhORAZ5cAKtcdE4S6+BvX/a+bqenQVMiwUHfLWNwWV5IzhxizXgjz7sqNUPsrLb2EbEL2145izreJSFa2r0wiNQ0tuiAfvnFXmcIdozhODC/XnMR2OnPS5gRfDfVSFzwZIy6sBsxB00kTKLOf4qJQ9mWxoN9pPOk0wJfOboa7/4EqF2a5v8xYKW+DRidKk6IXQK/mlPSaGJDmkJCITU9JOcdx8GGVYOOMFtQ3pr1tHI5dNZfHSFs7aBB6reQVpyVycVLQmGu9mnmHXVZ4ZETFkAewD7MN7026LC4CbqpJDc8aLlh4ZTidOQKBgQC9+h85WOsan4HSeLDPWjeZ1ht92OEJuHKSaHnXu6+iJzhJq/ZpG51CRCbxR4ilnQR8yofYvuCyhu2qenbzH2s1TFHILLRcNpRlzn2tFSslvTogWgqKsqbN8zWAyok6PTPoELValkWoe+BHzRAYclG4/QLqgCNzfFfLuiqpSTsyfwKBgQCxylIlAzibYEBv3jIPimMaG2IcDa2WP9LB1JGUCn4ippceEtdXqqjW6SiK8luUzp2Luq4R1J+WSAdExqKQTgdSmCJdZfjfScf/1py43+WmJHv1zkeHI4pgLvztlr2RO+E9w0culwiAghOi+WOMqVfoVE2yOc8UImlsXwnY9KixhwKBgAjKAZfNnVLl/kOWgdeV/OyJE0yHNCIW/nX3j2uDq2R9HAWMhyHlvSjLrmGLRa8jzIlZdV95tTuNMoGomMg2s+cLWN1B9DYv8D2lh/rx98UPNBk7ETHJgh4VGmyS8jZXjoc1/q3qNd7NnFq2CgCXiWBdlDz1IK6/GfC69C5QzQRRAoGAT5SnVkft06au0SQdNlWUbIArVzFMLP/ef8J9c02LDkmRRPXcDDKopqHw8tju41nkrUHfXr3UvTj6f++FnqA+qNrOLw5Q6umkq0DOH/agXdDg57Lw0222J72Am3yo19R8yt+ZAiK6vIL89Ss6QyEd5zGtIYS8y19n3wlfaAyDYgsCgYA7SYofxspqEVH/5poMSx9Lc+1W40+1xxBvd7W9qoLrx5EUru74VlyypC85UcxLLvvywq1K5F/jGdjooNmnoyTJMQr7t5IeVqvspA+LycgK4BsE+ITjvdAXvGWjULa0YYWT2tlHPQEW2EAzGtEKbK6S8ot0+r3QyJCqbIwyrS7msg==";
	public static final String RSA_PRIVATE = "";

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;
	
	private String phone ;
	private String IMEI;
	
	private int version;
	String cardNo;
	private String result = "";
	private static String orderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_charge);
		cardNoText = (TextView) findViewById(R.id.textCardNo);
		balanceText = (TextView) findViewById(R.id.textBalance);
		
		rg = (FlowRadioGroup) findViewById(R.id.rg);
		
		rb1 = (RadioButton) findViewById(R.id.iv_charge_tv_money1);
		rb2 = (RadioButton) findViewById(R.id.iv_charge_tv_money2);
		rb3 = (RadioButton) findViewById(R.id.iv_charge_tv_money3);
		rb4 = (RadioButton) findViewById(R.id.iv_charge_tv_money4);
		rb5 = (RadioButton) findViewById(R.id.iv_charge_tv_money5);
		btn_next = (Button) findViewById(R.id.btn_login);
		/*
		 * dbr.beginTransaction(); Cursor curor = dbr.query("Recharge", null,
		 * "status=?",new String[] {"-1"}, null, null, null);
		 * if(curor.getCount()!=0){ Toast.makeText(ActivityCardCharge.this,
		 * "您有已付款但未存值的订单", Toast.LENGTH_LONG).show(); }else{
		 * 
		 * } dbr.setTransactionSuccessful(); dbr.endTransaction();
		 */
		btn_next.setOnClickListener(this);

		alljob_black2 = (RelativeLayout) findViewById(R.id.alljob_black2);

		alljob_black2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ActivityCardCharge.this.finish();

			}
		});

		Intent intent = getIntent();

		String ccbParam = intent.getStringExtra("CCBPARAM");
		if (ccbParam != null) {
			Log.d(TAG, "ccbParam:" + ccbParam);
		}
		// 获取卡号和余额
		cardNo = intent.getStringExtra("cardNo");
		int balance = intent.getIntExtra("balance", 0);
		cardNoText.setText(cardNo);
		balanceText.setText(Utils.FenToYuan(balance) + " 元");
	}

	//获取软件版本号 
	private int getVersionCode(){  
	    int versionCode = 0;  
	    try  {  
	    	//获取软件版本号，对应AndroidManifest.xml下android:versionCode  
	    	
	        versionCode = ActivityCardCharge.this.getPackageManager().getPackageInfo(ActivityCardCharge.this.getApplicationInfo().packageName, 0).versionCode;  
	        Log.e("updateversionmanager","versioncode="+String.valueOf(versionCode));
	    } catch (NameNotFoundException e){  
	    	e.printStackTrace();
	    }  
	    return versionCode;  
	}	
	
	public void thread() {
		moneyThread = new Thread() {
			@Override
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				String cardNo = cardNoText.getText().toString();
				
				System.out.println("++++++++===================="+cardNo);
				
				map.put("CardNo", cardNo);
				
				
				rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
				strMoney = rb.getText().toString();
				// 去除末尾的“元”字获取金额
				strMoney = strMoney.substring(0, strMoney.length() - 1);
				map.put("PayMoney", strMoney);
				String strMD5 = MD5.encode("sdhy" + cardNo + strMoney + "order");
				map.put("Make", strMD5);
				
				SharedPreferences sp = ActivityCardCharge.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
				phone = sp.getString("username", "");
				IMEI=sp.getString("IMEI", "");
				Editor editor = sp.edit();
				
				editor.commit();
				map.put("SerialNo", IMEI);
				map.put("MobileNo", phone);
				map.put("terminalType", "0");
				

				Log.e(null, "source************----------------*********"+IMEI);
				Log.e(null, "make************----------------*********"+phone);
				String jsonStr = DBHandler.getRecord(DBHandler.ACTION_GENORDER, map);
				Log.e("生成订单", jsonStr);
				Message message = handler.obtainMessage();
				try {
					JSONObject json = new JSONObject(jsonStr);
					if (json != null && json.getString("success") != null) {
						String success = json.getString("success");
						if (success.equals("true")) {
							String name = json.getString("name");
							String idno = json.getString("idno");
							String orderId = json.getString("orderId");
							// ipAddr 必须是外网地址，否则跳转建行页面付款时会报客户端IP错误
							String ipAddr = json.getString("ip");
							String make = json.getString("make");
							String carNo = json.getString("carNo");
							String source = "sdhy" + carNo + name + idno + orderId + ipAddr + "order";
							String make2 = MD5.encode(source);

							Log.e(null, "source************----------------*********"+source);
							Log.e(null, "make************----------------*********"+make);
							Log.e(null, "make2****************-----------**********"+make2);
							
							if (make != null && make.equals(make2)) {
								String thirdAppInfo = "comccbpay" + HYENCRY.decode(Constants.MERCHANTID) + "HeZeBusPay";
								String tmp = "MERCHANTID=" + HYENCRY.decode(Constants.MERCHANTID) + "&POSID="
										+ HYENCRY.decode(Constants.POSID) + "&BRANCHID="
										+ HYENCRY.decode(Constants.BRANCHID) + "&ORDERID=" + orderId + "&PAYMENT="
										+ strMoney + "&CURCODE=01&TXCODE=520100&REMARK1=" + cardNo + "&REMARK2=";
								String pubKey = Constants.PubKey;
								pubKey = pubKey.substring(pubKey.length() - 30);
								String tempNew = tmp + "&TYPE=1&PUB=" + pubKey + "&GATEWAY=&CLIENTIP=" + ipAddr
										+ "&REGINFO=" + cardNo + "&PROINFO=&REFERER=&THIRDAPPINFO=" + thirdAppInfo;
								String temp_New1 = tmp + "&TYPE=1&GATEWAY=&CLIENTIP=" + ipAddr + "&REGINFO=" + cardNo
										+ "&PROINFO=&REFERER=&THIRDAPPINFO=" + thirdAppInfo;
								String url = temp_New1 + "&MAC=" + MD5.encode(tempNew);
								Log.e(TAG, "url:" + url);
								message.what = Constant.WHAT_GET_PAYMENT_SUCCESS;
								message.obj = url;

							} else {
								message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
								message.obj = "生成订单时密钥验证失败";
							}
						} else {
							String msg = json.getString("msg");
							msg = msg == null || msg.trim().equals("") ? "生成订单失败，请返回首页重试" : msg;
							message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
							message.obj = msg;
						}
					} else {
						message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
						message.obj = "生成订单失败";
					}
				} catch (JSONException e) {
					message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
					message.obj = "生成订单失败，信息：" + e.getMessage();
				}
				message.sendToTarget();
			}
		};
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			Intent in = new Intent(ActivityCardCharge.this, MainActivity.class);
			startActivity(in);
			ActivityCardCharge.this.finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	// 点击事件
	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			finish();
		} else if (v == btn_next) {// 立即支付
			if (rg.getCheckedRadioButtonId() == -1) {// 判断是否选取金额
				Toast.makeText(this, "请选择充值金额", Toast.LENGTH_SHORT).show();
				return;
			}else{
				//show();
				showPopMenu();
			}
			
			
		//	showPopMenu();
			// Intent intent = new Intent(this, ActivityCardChargeFill.class);
			// intent.putExtra("type", type);// 传递运营商参数
			// startActivity(intent);
		}
		switch (v.getId()) {
			case R.id.btnJH:
				dialogType.dismiss();
				aldialog=new com.nohttp.util.AlertDialog(ActivityCardCharge.this);
				rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
				aldialog.builder().setTitle("提示")
				.setMsg("充值金额为"+(rb.getText().toString())+",是否确认充值")
				.setPositiveButton("确认", new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog = new CProgressDialog(ActivityCardCharge.this);
						dialog.loadDialog();
			// 在这里添加用户判断是否有上一笔交易记录			
					//	moneyget.start();
					//	new Thread(radiorun).start();
						Message message = handler.obtainMessage();
									message.what = Constant.WHAT_GET_SUCCESS;
									message.sendToTarget();	
						
					}
				}).setNegativeButton("取消", new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						aldialog.cacle();
						
					}
				}).show();
				break;
				
			case R.id.btnWX:
				if (!weixinAvilible(ActivityCardCharge.this)) {
					// Intent intent = new Intent(
					// MenuActivity.this,
					// RegisterActivity.class);
					// intent.putExtra("state", 1);
					// startActivity(intent);
					Toast.makeText(ActivityCardCharge.this,
							"请下载微信", Toast.LENGTH_SHORT)
							.show();
					dialogType.dismiss();
				} else {
					wxLogin();
					dialogType.dismiss();
				}
				break;

		default:
			break;
		}
	}
	
	
	public void show() {
		dialogType = new Dialog(this, R.style.ActionSheetDialogStyle);
        //填充对话框的布局
        View inflate = LayoutInflater.from(this).inflate(R.layout.dialog_layout, null);
        //初始化控件
        inflate.findViewById(R.id.btnJH).setOnClickListener(this);
        inflate.findViewById(R.id.btnWX).setOnClickListener(this);
        inflate.findViewById(R.id.btnZFB).setOnClickListener(this);
        //将布局设置给Dialog
        dialogType.setContentView(inflate);
        //获取当前Activity所在的窗体
        Window dialogWindow = dialogType.getWindow();
        if (dialogWindow == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogType.show();//显示对话框
    }
	
	// 判断手机是否有微信
	public static boolean weixinAvilible(Context context) {
			final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
			List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
			if (pinfo != null) {
				for (int i = 0; i < pinfo.size(); i++) {
					String pn = pinfo.get(i).packageName;
					if (pn.equals("com.tencent.mm")) {
						return true;
					}
				}
			}
			return false;
		}

	//調起微信確認
	public void wxLogin() {
//		final SendAuth.Req req = new SendAuth.Req();
//		req.scope = "snsapi_userinfo";
//		req.state = "wechat_sdk_demo";
//		SmartBusApp.wxApi.sendReq(req);
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
		msgApi.registerApp(com.nohttp.util.Constant.APP_ID);
		
		new Thread() {
			@Override
			public void run() {
				String postUrl = com.nohttp.util.Constant.BASE_URL+"ICRecharge/pay!getOrderId.action?CardNo="
						+ "15269179758"+"&version="+version;
				String json = "{" + "\"" + "cardNo" + "\"" + ":" + "\"" + cardNo + "\"" + "}";
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httpRequst = new HttpPost(postUrl);
				byte[] b;
				try {
					b = json.getBytes("UTF-8");
					// 创建SSLContext对象，并使用我们指定的信任管理器初始化
					TrustManager[] tm = { new MyX509TrustManager(ActivityCardCharge.this) };
					SSLContext sslContext = SSLContext.getInstance("SSL");
					sslContext.init(null, tm, new java.security.SecureRandom());
					// 从上述SSLContext对象中得到SSLSocketFactory对象
					SSLSocketFactory ssf = sslContext.getSocketFactory();
					URL url = new URL(postUrl);
					HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
					conn.setSSLSocketFactory(ssf);
					conn.setDoInput(true);
					conn.setDoOutput(true);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Accept", "application/JSON");
					conn.setRequestProperty("Content-type", "application/json;charset=UTF-8");
					conn.setRequestProperty("Content-length", json);
					conn.setHostnameVerifier(new HostnameVerifier() {
						@Override
						public boolean verify(String hostname, SSLSession session) {
							return true;
						}
					});
					conn.connect();
					OutputStream out = (OutputStream) conn.getOutputStream();
					out.write(b, 0, b.length);
					out.flush();
					out.close();

					// Get the response
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuffer sb = new StringBuffer();
					String line;
					while ((line = rd.readLine()) != null) {
						sb.append(line);
					}
					rd.close();
					result = sb.toString();
					Log.e(null, "-----------------------" + result);
					conn.disconnect();
					JSONObject json1 = new JSONObject(result);
					if (json1.getString("success").equals("true")) {
						orderId = json1.getString("orderId");
						System.out.println("orderId=" + orderId);
						handler.sendEmptyMessage(400);
					}
				} catch (IOException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					handler.sendEmptyMessage(300);
				} catch (Exception e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
			}
		}.start();
	}

	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {            
			switch (msg.what) {
			case Constant.WHAT_GET_PAYMENT_SUCCESS:
				dialog.removeDialog();
			
				Intent intent = new Intent(ActivityCardCharge.this, CCBWebActivity.class);
				intent.putExtra("URL", Constants.APPURL + "?" + msg.obj.toString());
				intent.putExtra("ssss", "1");
				Log.e(TAG,
						"*************************************" + "url:" + Constants.APPURL + "?" + msg.obj.toString());
				startActivity(intent);

				// String url = Constants.APPURL + "?" + msg.obj.toString();
				// Log.d(TAG, "url:" + Constants.APPURL + "?" +
				// msg.obj.toString());
				// Intent intent = new Intent(Intent.ACTION_VIEW);
				// intent.setData(Uri.parse(url));
				// startActivity(intent);
				break;
			case Constant.WHAT_GET_PAYMENT_ISZERO:
				dialog.removeDialog();
				// money = msg.obj.toString();
				// moneyText.setText(money + " 元");
				// if (Integer.parseInt(money) >= 0) {
				// submitBtn.setVisibility(View.VISIBLE);
				// toHomeText.setVisibility(View.GONE);
				// toHomeBtn.setVisibility(View.GONE);
				// } else {
				// submitBtn.setVisibility(View.GONE);
				// toHomeText.setVisibility(View.VISIBLE);zzz
				// toHomeBtn.setVisibility(View.VISIBLE);
				// toHomeText.setText("5 秒后返回首页");
				// mc.start();
				// }
				break;
			case Constant.WHAT_GET_ISZERO:
				dialog.removeDialog();
				thread();
				moneyThread.start();
			//	Toast.makeText(ActivityCardCharge.this, "您有一笔未充值的交易请完成后再进行交易", Toast.LENGTH_LONG).show();
				break;    
			case Constant.WHAT_GET_SUCCESS:
				dialog.removeDialog();
				thread();
				moneyThread.start();
				
				break;
			case Constant.WHAT_GET_FAILURE:
				dialog.removeDialog();
				thread();
				moneyThread.start();
			//	Toast.makeText(ActivityCardCharge.this, "服务器连接异常", Toast.LENGTH_LONG).show();
				
				
				break;
				
			case Constant.WHAT_GET_PAYMENT_FAILURE:
				dialog.removeDialog();
				Toast.makeText(ActivityCardCharge.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void showPopMenu() {
		View view = View.inflate(getApplicationContext(), R.layout.share_popup_menu, null);

		LinearLayout jianhang = (LinearLayout) view.findViewById(R.id.dialog_my_wallet);
		LinearLayout zhifubao = (LinearLayout) view.findViewById(R.id.zhifubao);
		LinearLayout weixin = (LinearLayout) view.findViewById(R.id.weixin);
		TextView quxiao = (TextView) view.findViewById(R.id.dialog_confirm_pay);

		jianhang.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
				dialog = new CProgressDialog(ActivityCardCharge.this);
				dialog.loadDialog();
				thread();
				moneyThread.start();

			}

		});
		zhifubao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();

				if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
					new AlertDialog.Builder(ActivityCardCharge.this).setTitle("警告")
							.setMessage("需要配置APPID | RSA_PRIVATE")
							.setPositiveButton("确定", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialoginterface, int i) {
									//
									finish();
								}
							}).show();
					return;

				}

				/**
				 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
				 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
				 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
				 * 
				 * orderInfo的获取必须来自服务端；
				 */
				boolean rsa2 = (RSA2_PRIVATE.length() > 0);
				Map<String, String> params = buildOrderParamMap(APPID, rsa2);

				Log.e(null, "******************************" + params);
				String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

				String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
				String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
				final String orderInfo = orderParam + "&" + sign;

				Runnable payRunnable = new Runnable() {

					@Override
					public void run() {
						PayTask alipay = new PayTask(ActivityCardCharge.this);
						Map<String, String> result = alipay.payV2(orderInfo, true);
						Log.i("msp", result.toString());

						Message msg = new Message();
						msg.what = SDK_PAY_FLAG;
						msg.obj = result;
						mHandler.sendMessage(msg);
					}
				};

				Thread payThread = new Thread(payRunnable);
				payThread.start();

			}
		});
		weixin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();
				/*
				 * Message msg = new Message(); msg.what =
				 * MenuActivity.SHOW_DATAPICK2;
				 * MenuActivity.this.dateandtimeHandler.sendMessage(msg);
				 */

			}
		});

		quxiao.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mpopupWindow.dismiss();

			}
		});

		view.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.poull);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_bottom_in));

		if (mpopupWindow == null) {
			mpopupWindow = new PopupWindow(this);
			mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
			mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);
			mpopupWindow.setBackgroundDrawable(new BitmapDrawable());

			mpopupWindow.setFocusable(true);
			mpopupWindow.setOutsideTouchable(true);
		}

		mpopupWindow.setContentView(view);
		mpopupWindow.showAtLocation(rb4, Gravity.BOTTOM, 0, 0);
		mpopupWindow.update();
	}

	/**
	 * 支付宝账户授权业务
	 * 
	 * @param v
	 */
	public void authV2(View v) {
		if (TextUtils.isEmpty(PID) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			new AlertDialog.Builder(this).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
						}
					}).show();
			return;
		}

		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 * 
		 * authInfo的获取必须来自服务端；
		 */
		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		Map<String, String> authInfoMap = OrderInfoUtil2_0.buildAuthInfoMap(PID, APPID, TARGET_ID, rsa2);
		String info = OrderInfoUtil2_0.buildOrderParam(authInfoMap);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = OrderInfoUtil2_0.getSign(authInfoMap, privateKey, rsa2);
		final String authInfo = info + "&" + sign;
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(ActivityCardCharge.this);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(authInfo, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(this);
		String version = payTask.getVersion();
		Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	/**
	 * 构造支付订单参数列表
	 * 
	 * @param pid
	 * @param app_id
	 * @param target_id
	 * @return
	 */
	public static Map<String, String> buildOrderParamMap(String app_id, boolean rsa2) {
		Map<String, String> keyValues = new HashMap<String, String>();

		keyValues.put("app_id", app_id);

		keyValues.put("biz_content",
				"{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"公交卡充值\",\"out_trade_no\":\""
						+ getOutTradeNo() + "\"}");

		keyValues.put("charset", "utf-8");

		keyValues.put("method", "alipay.trade.app.pay");

		keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

		keyValues.put("timestamp", "2016-07-29 16:55:53");

		keyValues.put("version", "1.0");

		return keyValues;
	}

	/**
	 * 要求外部订单号必须唯一。
	 * 
	 * @return
	 */
	private static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				
				
				
				
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为9000则代表支付成功
				if (TextUtils.equals(resultStatus, "9000")) {
					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
					Toast.makeText(ActivityCardCharge.this, "支付成功", Toast.LENGTH_SHORT).show();
					Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
					// 通过Gson 解析后台传过来的数据
					Map<String, Object> map = gson.fromJson(payResult.getResult(),
							new TypeToken<Map<String, Object>>() {
							}.getType());
					Map<String, Object> li =new HashMap<String, Object>();
					li=(Map<String, Object>)map.get("alipay_trade_app_pay_response");
					Log.e(null, "resultInfo+++++++++==========="+li.get("out_trade_no"));
					Log.e(null, "resultStatus+++++++++==========="+resultStatus);
					
					
				} else {
					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					Toast.makeText(ActivityCardCharge.this, "支付失败", Toast.LENGTH_SHORT).show();
				}
				break;
			}

			default:
				break;
			}
		};
	};

	
//	Thread moneyget = new Thread() {
//		
//		
//			
//			
//		};
		
		
		
	
	
	
	
	
	
		/*private Runnable radiorun = new Runnable() {
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
							Log.e("获取充值金额", "-666666666--------------------" + payment);

							if (payment == null || payment.trim().equals("0")) {
								message.what = Constant.WHAT_GET_SUCCESS;
								message.sendToTarget();
							} else {
								message.what = Constant.WHAT_GET_ISZERO;
								message.obj = payment;
								message.sendToTarget();
								
							}
						} else {
							
							message.what = Constant.WHAT_GET_SUCCESS;
							message.sendToTarget();
							String msg = json.getString("msg");
							msg = msg == null || msg.trim().equals("") ? "获取充值金额失败" : msg;
							message.what = Constant.WHAT_GET_FAILURE;
							message.obj = msg;
							message.sendToTarget();
						
						}
					} else {
						message.what = Constant.WHAT_GET_FAILURE;
						message.obj = "获取充值金额失败";
						message.sendToTarget();
					}
				} catch (JSONException e) {
					message.what = Constant.WHAT_GET_FAILURE;
					message.obj = "获取充值金额失败，信息：" + e.getMessage();
					message.sendToTarget();
				}
				
			}
		};*/
	
	
	
	
}
