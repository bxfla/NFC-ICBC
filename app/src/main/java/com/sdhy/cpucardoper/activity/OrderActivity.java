package com.sdhy.cpucardoper.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alpha.live.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nohttp.dialog.CProgressDialog;
import com.sdhy.common.ConstData;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.HYENCRY;
import com.weixinpay.MD5;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class OrderActivity extends Activity {
	private RelativeLayout black;
	private Socket socket;
	private InputStream in = null;
	private OutputStream out = null;
	private String money;
	private String order;
	private CProgressDialog dialog;
	private String card;
	private ArrayList<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	private NOOrderadapter Adapter;// 自定义的适配器
	private ListView new_alllist;// 下拉刷新，上拉加载的列表
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		
		card=getIntent().getStringExtra("card");
		
		
		SharedPreferences sp = OrderActivity.this.getSharedPreferences("card",
				Activity.MODE_PRIVATE);
		// 获取Editor对象
		Editor editor = sp.edit();
		editor.putString("card", card);
		editor.commit();
		init();
		
	}
	private void init(){
		new_alllist=(ListView) findViewById(R.id.cuslist2);
		black=(RelativeLayout) findViewById(R.id.alljob_blackorder);
		
		 
		if(card.length()>15){
			dialog = new CProgressDialog(OrderActivity.this);
			if(dialog!=null){
				dialog.loadDialog();
			}
			
			moneyThread.start();//充值
			
		}else{
			dialog = new CProgressDialog(OrderActivity.this);
			dialog.loadDialog();
			moneyThread.start();//充值
		//	new Thread(loginRunnable).start();// 发送数据
		}
		
		black.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent in=new Intent( OrderActivity.this,OnlineActivity.class);
		       	 startActivity(in);
		       	OrderActivity.this.finish();
				
			}
		});
	}
	
	Runnable loginRunnable = new Runnable() {
		@Override
		public void run() {
			
			if (initConnect()){
				initConnect();
				
				Log.e(null, "##############################"+ConstData.bytesToHexString2(ConstData.getLoginbudeng(card.toString())));
				sendData(ConstData.getLoginbudeng(card));
				
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
	
	public void sendData(byte[] b) {
		try {
			out = getOut();
			out.write(b);
			out.flush();
			Log.d(null, "数据发送成功");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(null, "数据发送失败");
		}
	}
	
	Runnable recRunnable = new Runnable() {
		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			int size = 0;
			
			  boolean isSucc = false; boolean isSuccf = false; boolean
			  isSuccfull = false; String strEntryKey = null;
			 
			exit: while (true) {
				if (!ConstData.isConnect) {
					return;
				}
				try {
					int intRec = in.available();
					while (intRec == 0) {
						intRec = in.available();
					 
					}
					
					byte[] byeRec = new byte[intRec];
					int count = in.read(byeRec);
					size = count;
					while (true) {
						if (size < 7) {
							break;
						}
	

						Log.e(null, "---------------jies33333hou--------------------------------"+ConstData.bytesToHexString2(byeRec));
						// 80是手持机发送的包，A0是服务器发回的
						if ((byeRec[0] == (byte) 160) && (byeRec[6]) == (byte) 0xaa) {
							Message message2 = handler.obtainMessage();
							byte[] buffer=new byte[32];
							byte[] buffer2=new byte[33];
							byte[] buffer3=new byte[34];
							if(byeRec.length<40){
								Log.e(null, "---------------------ppppppppppp--------------------------");
								money="0";
								message2.what = Constant.WHAT_GET_PAYMENT_ISZERO;
								message2.sendToTarget();
							}else{
								if((byeRec[64]==(byte)0x1a)){
									
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
									buffer[26]=byeRec[58];
									buffer[27]=byeRec[59];
									buffer[28]=byeRec[60];
									buffer[29]=byeRec[61];
									buffer[30]=byeRec[62];
									buffer[31]=byeRec[63];
									
									//按 ‘;’分割成字符串数组
									String[] strs = (new String(buffer)).split(";");
									//第一个分号之前的字符串自然就是数组里的第一个
									money=strs[1]; //打印出 x1
									order=strs[0];
									message2.what = Constant.WHAT_GET_PAYMENT_ISZERO;
									message2.sendToTarget();
									Log.e(null, "---------------------sho77777ushou--------------------------"+order);
								}else if((byeRec[65]==(byte)0x1a)){ 
									buffer2[0]=byeRec[32];
									buffer2[1]=byeRec[33];
									buffer2[2]=byeRec[34];
									buffer2[3]=byeRec[35];
									buffer2[4]=byeRec[36];
									buffer2[5]=byeRec[37];
									buffer2[6]=byeRec[38];
									buffer2[7]=byeRec[39];
									buffer2[8]=byeRec[40];
									buffer2[9]=byeRec[41];
									buffer2[10]=byeRec[42];
									buffer2[11]=byeRec[43];
									buffer2[12]=byeRec[44];
									buffer2[13]=byeRec[45];
									buffer2[14]=byeRec[46];
									buffer2[15]=byeRec[47];
									buffer2[16]=byeRec[48];
									buffer2[17]=byeRec[49];
									buffer2[18]=byeRec[50];
									buffer2[19]=byeRec[51];
									buffer2[20]=byeRec[52];
									buffer2[21]=byeRec[53];
									buffer2[22]=byeRec[54];
									buffer2[23]=byeRec[55];
									buffer2[24]=byeRec[56];
									buffer2[25]=byeRec[57];
									buffer2[26]=byeRec[58];
									buffer2[27]=byeRec[59];
									buffer2[28]=byeRec[60];
									buffer2[29]=byeRec[61];
									buffer2[30]=byeRec[62];
									buffer2[31]=byeRec[63];
									buffer2[32]=byeRec[64];
									//按 ‘;’分割成字符串数组
									String[] strs = (new String(buffer2)).split(";");
									//第一个分号之前的字符串自然就是数组里的第一个
									money=strs[1]; //打印出 x1
									order=strs[0];
									Log.e(null, "---------------------sho88888ushou--------------------------"+order);
									message2.what = Constant.WHAT_GET_PAYMENT_ISZERO;
									message2.sendToTarget();
									
								}else if((byeRec[66]==(byte)0x1a)){
									buffer3[0]=byeRec[32];
									buffer3[1]=byeRec[33];
									buffer3[2]=byeRec[34];
									buffer3[3]=byeRec[35];
									buffer3[4]=byeRec[36];
									buffer3[5]=byeRec[37];
									buffer3[6]=byeRec[38];
									buffer3[7]=byeRec[39];
									buffer3[8]=byeRec[40];
									buffer3[9]=byeRec[41];
									buffer3[10]=byeRec[42];
									buffer3[11]=byeRec[43];
									buffer3[12]=byeRec[44];
									buffer3[13]=byeRec[45];
									buffer3[14]=byeRec[46];
									buffer3[15]=byeRec[47];
									buffer3[16]=byeRec[48];
									buffer3[17]=byeRec[49];
									buffer3[18]=byeRec[50];
									buffer3[19]=byeRec[51];
									buffer3[20]=byeRec[52];
									buffer3[21]=byeRec[53];
									buffer3[22]=byeRec[54];
									buffer3[23]=byeRec[55];
									buffer3[24]=byeRec[56];
									buffer3[25]=byeRec[57];
									buffer3[26]=byeRec[58];
									buffer3[27]=byeRec[59];
									buffer3[28]=byeRec[60];
									buffer3[29]=byeRec[61];
									buffer3[30]=byeRec[62];
									buffer3[31]=byeRec[63];
									buffer3[32]=byeRec[64];
									buffer3[33]=byeRec[65];
									//按 ‘;’分割成字符串数组
									String[] strs = (new String(buffer3)).split(";");
									//第一个分号之前的字符串自然就是数组里的第一个
									money=strs[1]; //打印出 x1
									order=strs[0];
									Log.e(null, "---------------------shou99999shou--------------------------"+order);
									Log.e(null, "---------------------shou99999shouXXXXXXXXXX--------------------------"+money);
									message2.what = Constant.WHAT_GET_PAYMENT_ISZERO;
									message2.sendToTarget();
								}else{
									
									Log.e(null, "---------------------p3333pppppppppp--------------------------");
									money="0";
									message2.what = Constant.WHAT_GET_PAYMENT_FAILURE;
									message2.sendToTarget();
								}
							}
							
							

						
							
							
							return;
							
						} else {
							break;
						}
					}
				} catch (Exception e) {
				}
			}


			
			
		}

	};
	
	
	public boolean initConnect() {
		try {
			socket = new Socket();
			SocketAddress socAddress = new InetSocketAddress(ConstData.Ip, Integer.parseInt(ConstData.Port));
			socket.connect(socAddress, 10000);
			out = socket.getOutputStream();
			in = socket.getInputStream();
		//	receiveTime = (int) (System.currentTimeMillis() / 1000L);
			ConstData.isConnect = true;
			ConstData.initNetFlag = true;

			return true;
		} catch (IOException e) {
			ConstData.isConnect = false;
Message message2 = handler.obtainMessage();
			
			message2.what =Constant.WHAT_CONN_DB_FAILURE;
			message2.obj="服务器连接异常，请检查网络";
				message2.sendToTarget();
			e.printStackTrace();
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
	
	
	/**
	 * @see 获取cpu充值金额
	 */

	Thread moneyThread = new Thread() {
		@Override
		public void run() {

			Map<String, String> map = new HashMap<String, String>();
			map.put("CardNo", card);
			String strMD5 = MD5.encode("sdhy" + card + "order");
			map.put("Make", strMD5);
			
			String jsonStr = DBHandler.getRecord(DBHandler.listBankSuccData, map);
			Log.e("获取充值金额", jsonStr);
			
			Message message = handler.obtainMessage();
			try {
				JSONObject json = new JSONObject(jsonStr);
				
				Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
				// 通过Gson 解析后台传过来的数据
				Map<String, Object> map1 = gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>() {
				}.getType());
				if (json != null && json.getString("success") != null) {
					String success = json.getString("success");
					if (success.equals("true")) {
						li = (ArrayList<Map<String, Object>>) map1.get("data");
						handler.sendEmptyMessage(0);
						
						
					} else {
						String msg = json.getString("msg");
						msg = msg == null || msg.trim().equals("") ? "无该卡号未补登订单" : msg;
						message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
						message.obj = msg;
					}
				} else {
					message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
					message.obj = "获取充值金额失败";
				}
			} catch (JSONException e) {
				message.what = Constant.WHAT_GET_PAYMENT_FAILURE;
				message.obj = "获取订单失败，信息：" + e.getMessage();
			}
			message.sendToTarget();
		}
	};
	
	
	

	Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				dialog.removeDialog();
				if(li!=null){
					if(li.size()==0){
						Toast.makeText(OrderActivity.this, "当前无该用户的交易记录", 3).show();
					}else{
						Adapter = new NOOrderadapter(OrderActivity.this,
								li);
						new_alllist.setAdapter(Adapter);

					}
					
				}else{
					Toast.makeText(OrderActivity.this, "当前无该用户的交易记录", 3).show();

				}
				
				break;
			case Constant.WHAT_CONN_DB_FAILURE:
				dialog.removeDialog();
				
				Toast.makeText(OrderActivity.this, "服务器连接异常", Toast.LENGTH_SHORT).show();
//				
				break;
			case Constant.WHAT_GET_PAYMENT_FAILURE:
				dialog.removeDialog();
				Toast.makeText(OrderActivity.this, "无该卡号记录", Toast.LENGTH_SHORT).show();
				break;
			case Constant.WHAT_GET_PAYMENT_ISZERO:
				dialog.removeDialog();
				
			break;
			}
		}
	};
	
	
	
	
	@Override  
    public boolean onKeyDown(int keyCode, KeyEvent event) {  
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {  
        	Intent in=new Intent( OrderActivity.this,OnlineActivity.class);
	       	 startActivity(in);
	       	OrderActivity.this.finish();
             return false;  
        }else {  
            return super.onKeyDown(keyCode, event);  
        }  
          
    } 
	
	
	
	
}
