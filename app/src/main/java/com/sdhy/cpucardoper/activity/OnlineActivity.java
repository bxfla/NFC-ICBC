package com.sdhy.cpucardoper.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alpha.live.R;
import com.nohttp.activity.BaseActivityse;
import com.nohttp.dialog.CProgressDialog;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.HYENCRY;
import com.sdhy.common.Utils;
import com.sdhy.cpucardoper.view.FlowRadioGroup;
import com.weixinpay.Constants;
import com.weixinpay.MD5;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OnlineActivity extends BaseActivityse implements OnClickListener {
	private EditText cardNoText;//卡号
	private ImageView iv_back;
	private FlowRadioGroup rg;
	private RelativeLayout alljob_black2;
	private RadioButton rb1, rb2, rb3, rb4, rb5, rb6,rb;
	private Button btn_next;
	String TAG="mifare";
	Thread moneyThread;
	String cardNo = "";
	private CProgressDialog dialog;
	private com.nohttp.util.AlertDialog aldialog;
	private Button select,btn_selectnew;//查询交易记录
	private String phone ;
	private String IMEI,card;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online);    
		cardNoText=(EditText)findViewById(R.id.textCardNo2);
		rg = (FlowRadioGroup) findViewById(R.id.rg2);
		rb1 = (RadioButton) findViewById(R.id.iv_charge_tv_money11);
		rb2 = (RadioButton) findViewById(R.id.iv_charge_tv_money22);
		rb3 = (RadioButton) findViewById(R.id.iv_charge_tv_money33);
		rb4 = (RadioButton) findViewById(R.id.iv_charge_tv_money44);
		rb5 = (RadioButton) findViewById(R.id.iv_charge_tv_money55);
		btn_next = (Button) findViewById(R.id.btn_login2);
		select= (Button) findViewById(R.id.btn_select);
		btn_selectnew= (Button) findViewById(R.id.btn_selectnew);
		SharedPreferences sp = OnlineActivity.this.getSharedPreferences("card",
				Activity.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sp.edit();
		card = sp.getString("card", "");
		editor.commit();
		cardNoText.setText(card);
		/*dbr.beginTransaction();
		Cursor curor = dbr.query("Recharge", null, "status=?",new String[] {"-1"}, null, null, null);
		if(curor.getCount()!=0){
			Toast.makeText(ActivityCardCharge.this, "您有已付款但未存值的订单", Toast.LENGTH_LONG).show();
		}else{
			
		}
		dbr.setTransactionSuccessful();
		dbr.endTransaction();*/
		btn_next.setOnClickListener(this);
		
		alljob_black2= (RelativeLayout) findViewById(R.id.alljob_black3);
		
		alljob_black2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in=new Intent( OnlineActivity.this,MainActivity.class);
		       	 startActivity(in);
		       	OnlineActivity.this.finish(); 
				
			}
		});
		
		

select.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if((cardNoText.getText().toString()).equals("")){
					Toast.makeText(OnlineActivity.this, "请输入要查询的卡号", Toast.LENGTH_SHORT).show();
					return;
				}else{
					
					if((cardNoText.getText().toString()).length()<=9){
						if((9-((cardNoText.getText().toString()).length()))==3){
							cardNo="000"+(cardNoText.getText().toString());	
						}else if((9-((cardNoText.getText().toString()).length()))==2){
							cardNo="00"+(cardNoText.getText().toString());
						}else if((9-((cardNoText.getText().toString()).length()))==1){
							cardNo="0"+(cardNoText.getText().toString());
						}else if((9-((cardNoText.getText().toString()).length()))==0){
							cardNo=cardNoText.getText().toString();
						}
			
					}else if((cardNoText.getText().toString()).length()==19|(cardNoText.getText().toString()).length()==20){
						 if((20-((cardNoText.getText().toString()).length()))==1){
								cardNo="0"+(cardNoText.getText().toString());
							}else if((20-((cardNoText.getText().toString()).length()))==0){
								cardNo=cardNoText.getText().toString();
							}
					}else{
					
						return;
					}
					
				}
				if(cardNo.length()<9||cardNo.length()>20){
					Toast.makeText(OnlineActivity.this, "请输入正确的卡号", Toast.LENGTH_SHORT).show();
				}else{
					Intent in=new Intent( OnlineActivity.this,OrderActivity.class);
					in.putExtra("card", cardNo);
			       	 startActivity(in);
			       	OnlineActivity.this.finish(); 
				}
				
			}
		});
		
		
btn_selectnew.setOnClickListener(new OnClickListener() {
	
	@Override
	public void onClick(View v) {
		
		Intent in=new Intent( OnlineActivity.this,OrderActivitynew.class);
		in.putExtra("card", cardNo);
       	 startActivity(in);
       //	OnlineActivity.this.finish(); 
		
	}
});
	
	
	}
	
	public void thread(){
		moneyThread =new Thread(){
			@Override
			public void run(){
				Map<String, String> map = new HashMap<String, String>();
				String cardNo = cardNoText.getText().toString();
				
				if(cardNo.length()<9){
					if((9-(cardNo.length()))==3){
						cardNo="000"+cardNo;	
					}else if((9-(cardNo.length()))==2){
						cardNo="00"+cardNo;
					}else if((9-(cardNo.length()))==1){
						cardNo="0"+cardNo;
					}
				
				}
				Log.e(null, "**********************"+cardNo);
				
				
				map.put("CardNo", cardNo);
				rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
				String strMoney = rb.getText().toString();
				// 去除末尾的“元”字获取金额
				strMoney = strMoney.substring(0, strMoney.length() - 1);
				map.put("PayMoney", strMoney);
				String strMD5 = MD5.encode("sdhy" + cardNo + strMoney + "order");
				map.put("Make", strMD5);
				
				SharedPreferences sp = OnlineActivity.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
				phone = sp.getString("username", "");
				IMEI=sp.getString("IMEI", "");
				Editor editor = sp.edit();
				
				editor.commit();
				map.put("SerialNo", IMEI);
				map.put("userName", phone);
				map.put("terminalType", "0");
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
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				/*2222222222
				map.put("CardNo", cardNo);
				rb = (RadioButton)findViewById(rg.getCheckedRadioButtonId());
				String strMoney = rb.getText().toString();
//				去除末尾的“元”字获取金额
				strMoney = strMoney.substring(0, strMoney.length() - 1);
				map.put("PayMoney", strMoney);
				String strMD5 = MD5.encode("sdhy" + cardNo + strMoney + "order");
				map.put("Make", strMD5);
				String jsonStr= DBHandler.getRecord(DBHandler.ACTION_GENORDER, map);
				Log.e("生成订单",jsonStr);
				Message message = handler.obtainMessage();
	        	try {
					JSONObject json = new JSONObject(jsonStr);
					if(json != null && json.getString("success") != null){
						String success = json.getString("success");
						if (success.equals("true")) {
							String name = json.getString("name");
							String idno = json.getString("idno");
							String orderId = json.getString("orderId");
//							ipAddr 必须是外网地址，否则跳转建行页面付款时会报客户端IP错误
							String ipAddr = json.getString("ip");
							String make = json.getString("make");
							String source = "sdhy" + name + idno + orderId + ipAddr + "order";
							String make2 = MD5.encode(source);
							if (make != null && make.equals(make2)) {
								String thirdAppInfo = "comccbpay" + HYENCRY.decode(Constants.MERCHANTID) + "HeZeBusPay";
								String tmp = "MERCHANTID=" + HYENCRY.decode(Constants.MERCHANTID) + "&POSID=" + HYENCRY.decode(Constants.POSID) + "&BRANCHID=" + HYENCRY.decode(Constants.BRANCHID)
											+ "&ORDERID=" + orderId + "&PAYMENT=" + strMoney + "&CURCODE=01&TXCODE=520100&REMARK1="+cardNo+"&REMARK2=";
								String pubKey = Constants.PubKey;
								pubKey = pubKey.substring(pubKey.length() - 30);
								String tempNew = tmp+"&TYPE=1&PUB="+pubKey+"&GATEWAY=&CLIENTIP="+ipAddr+"&REGINFO="+cardNo+"&PROINFO=&REFERER=&THIRDAPPINFO="+thirdAppInfo;
								String temp_New1 = tmp+"&TYPE=1&GATEWAY=&CLIENTIP="+ipAddr+"&REGINFO="+cardNo+"&PROINFO=&REFERER=&THIRDAPPINFO="+thirdAppInfo;
								String url = temp_New1+"&MAC="+MD5.encode(tempNew);
								Log.e(TAG, "url:" + url);
						        message.what = Constant.WHAT_GET_PAYMENT_SUCCESS;
								message.obj = url;
								
							} else {
								message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
								message.obj = "生成订单时密钥验证失败";
							}
						} else {
							String msg  = json.getString("msg");
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
					message.obj = "生成订单失败，信息："+e.getMessage();
				}
	        	message.sendToTarget();
			}
		};
	}*/
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {  
       	 Intent in=new Intent( OnlineActivity.this,MainActivity.class);
       	 startActivity(in);
       	OnlineActivity.this.finish();  
             return false;  
        }else {  
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
			}else if((cardNoText.getText().toString()).equals("")){
				Toast.makeText(this, "请输入卡号", Toast.LENGTH_SHORT).show();
				return;
			}
			
			
			

			aldialog=new com.nohttp.util.AlertDialog(OnlineActivity.this);
			rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
			aldialog.builder().setTitle("提示")
			.setMsg("充值金额为"+(rb.getText().toString())+",是否确认充值")
			.setPositiveButton("确认", new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog = new CProgressDialog(OnlineActivity.this);
					dialog.loadDialog();
				
						moneyget.start();
					
					
				}
			}).setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					aldialog.cacle();
					
				}
			}).show();
			
//			Intent intent = new Intent(this, ActivityCardChargeFill.class);
//			intent.putExtra("type", type);// 传递运营商参数
//			startActivity(intent);
		}
	}

	
	
	Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.WHAT_GET_PAYMENT_SUCCESS:
				dialog.removeDialog();
				Intent intent = new Intent(OnlineActivity.this, CCBWebActivity.class);
				intent.putExtra("URL", Constants.APPURL + "?" + msg.obj.toString());
				intent.putExtra("ssss", "2");
				Log.e(TAG, "*************************************"+"url:" + Constants.APPURL + "?" + msg.obj.toString());
		        startActivity(intent);
		        
		        
//		    	String url = Constants.APPURL + "?" + msg.obj.toString();
//		    	Log.d(TAG, "url:" + Constants.APPURL + "?" + msg.obj.toString());
//		    	Intent intent = new Intent(Intent.ACTION_VIEW);
//		    	intent.setData(Uri.parse(url));
//		    	startActivity(intent);
				break;
			case Constant.WHAT_GET_PAYMENT_ISZERO:
				dialog.removeDialog();
//				money = msg.obj.toString();
//				moneyText.setText(money + " 元");
//				if (Integer.parseInt(money) >= 0) {
//					submitBtn.setVisibility(View.VISIBLE);
//					toHomeText.setVisibility(View.GONE);
//					toHomeBtn.setVisibility(View.GONE);
//				} else {
//					submitBtn.setVisibility(View.GONE);
//					toHomeText.setVisibility(View.VISIBLE);zzz
//					toHomeBtn.setVisibility(View.VISIBLE);
//					toHomeText.setText("5 秒后返回首页");
//					mc.start();
//				}
				break;
			case Constant.WHAT_GET_PAYMENT_FAILURE:
				
				
				
				dialog.removeDialog();
				Toast.makeText(OnlineActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			case Constant.WHAT_GET_ISZERO:
				
				
				
				
				dialog.removeDialog();
				thread();
				moneyThread.start();
				//Toast.makeText(OnlineActivity.this, "您有一笔未充值的交易请完成后再进行交易", Toast.LENGTH_LONG).show();
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
				//Toast.makeText(OnlineActivity.this, "服务器连接异常", Toast.LENGTH_LONG).show();
				
				
				break;
			}
		}
	};
	
	Thread moneyget = new Thread() {
		@Override
		public void run() {
			
			if((cardNoText.getText().toString()).equals("")){
				Toast.makeText(OnlineActivity.this, "请输入要查询的卡号", Toast.LENGTH_SHORT).show();
				return;
			}else{
				
				if((cardNoText.getText().toString()).length()<=9){
					if((9-((cardNoText.getText().toString()).length()))==3){
						cardNo="000"+(cardNoText.getText().toString());	
					}else if((9-((cardNoText.getText().toString()).length()))==2){
						cardNo="00"+(cardNoText.getText().toString());
					}else if((9-((cardNoText.getText().toString()).length()))==1){
						cardNo="0"+(cardNoText.getText().toString());
					}else if((9-((cardNoText.getText().toString()).length()))==0){
						cardNo=cardNoText.getText().toString();
					}
		
				}else if((cardNoText.getText().toString()).length()==19|(cardNoText.getText().toString()).length()==20){
					 if((20-((cardNoText.getText().toString()).length()))==1){
							cardNo="0"+(cardNoText.getText().toString());
						}else if((20-((cardNoText.getText().toString()).length()))==0){
							cardNo=cardNoText.getText().toString();
						}
				}else{
				
					return;
				}
				
			}
			if(cardNo.length()<9||cardNo.length()>20){
				Toast.makeText(OnlineActivity.this, "请输入正确的卡号", Toast.LENGTH_SHORT).show();
			}else{
			
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("cardNo", HYENCRY.encode(cardNo));
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
				message.what = Constant.WHAT_GET_FAILURE ;
				message.obj = "获取充值金额失败，信息：" + e.getMessage();
				message.sendToTarget();
			}
			
		}
		
	};
}
