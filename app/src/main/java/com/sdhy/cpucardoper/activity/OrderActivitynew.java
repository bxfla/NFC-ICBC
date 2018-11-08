package com.sdhy.cpucardoper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.alpha.live.R;
import com.alpha.live.R.id;
import com.alpha.live.R.layout;
import com.alpha.live.R.menu;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.nohttp.dialog.CProgressDialog;
import com.sdhy.common.Constant;
import com.sdhy.common.DBHandler;
import com.sdhy.common.HYENCRY;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class OrderActivitynew extends Activity {
	private ListView new_alllist;// 下拉刷新，上拉加载的列表
	Thread OrderThread;
	private String phone;
	private String IMEI;
	private ArrayList<Map<String, Object>> li = new ArrayList<Map<String, Object>>();
	private Orderadapter Adapter;// 自定义的适配器
	private RelativeLayout order_black3;
	private CProgressDialog dialog;
	private String cardNo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_activitynew);
		new_alllist=(ListView) findViewById(R.id.cuslist);
		cardNo=getIntent().getStringExtra("cardNo");
		order_black3=(RelativeLayout) findViewById(R.id.order_black3);
		order_black3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OrderActivitynew.this.finish();
			}
		});
		sp();
		new_alllist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				Log.e(null, li.get(position).get("status")+"");
				if((li.get(position).get("status")+"").equals("支付成功，等待补登")){
					Intent intent = new Intent();
					intent.putExtra("action", "readorder");
					intent.putExtra("orderId", li.get(position).get("orderId")+"");
					intent.putExtra("cardNo", li.get(position).get("cardNo")+"");
					intent.putExtra("balance", li.get(position).get("payment")+"");
					if((li.get(position).get("cardNo")+"").length()>10){
						intent.putExtra("type", "cpu");	
					}else{
						intent.putExtra("type", "m1");	
					}
				
					intent.setClass(OrderActivitynew.this, ChargeActivity.class);
					startActivity(intent);
				}
				
			}
		});
		thread();
		OrderThread.start();
	}
	private void sp(){
		SharedPreferences sp = OrderActivitynew.this.getSharedPreferences("Session", Activity.MODE_PRIVATE);
		phone = sp.getString("username", "");
		IMEI =sp.getString("IMEI", "");
		Editor editor = sp.edit();
		editor.commit();

	}
	public void thread() {
		dialog = new CProgressDialog(OrderActivitynew.this);
		dialog.loadDialog();
		OrderThread = new Thread() {
			@Override
			public void run() {
				
				Map<String, String> map = new HashMap<String, String>();
				
				
			/*	map.put("SerialNo", IMEI);
				String strMD5 = MD5.encode("sdhy" + IMEI + "order");
				map.put("Make", strMD5);*/
				
				map.put("CardNo", cardNo);
				String strMD5 = MD5.encode("sdhy" + cardNo + "order");
				map.put("Make", strMD5);
				map.put("type", "1");
				map.put("userName", phone);
				map.put("terminalType", "0");
				
				String jsonStr = DBHandler.getRecord(DBHandler.ACTION_GENORDERlist, map);
				Log.e(null, "2222222222222======="+strMD5);
				
				
				try {
					JSONObject json = new JSONObject(jsonStr);
				
					
					
					Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
					// 通过Gson 解析后台传过来的数据
					Map<String, Object> map1 = gson.fromJson(jsonStr, new TypeToken<Map<String, Object>>() {
					}.getType());
					
			/*		li = (ArrayList<Map<String, Object>>) map1.get("data");
					Log.e(null, "2222222222222"+li);*/
					/*Adapter = new Mineskilladapter(MineskillActivity.this,
							(ArrayList<Map<String, Object>>) map.get("data"));
					new_alllist.setAdapter(Adapter);
					if (li != null && li.size() != 0) {
						li.clear();

						li = (ArrayList<Map<String, Object>>) map.get("data");
						Adapter.data = li;
						Adapter.notifyDataSetChanged();
					}*/
					
					
					
					
					
					
					
					if (json != null && json.getString("success") != null) {
						String success = json.getString("success");
						if (success.equals("true")) {
							li = (ArrayList<Map<String, Object>>) map1.get("data");
							handler.sendEmptyMessage(0);
							
							Log.e(null, "2222222222222"+li);
							
							
							
							
						} else {
							String msg = json.getString("msg");
							
						}
					} else {
						
					}
				} catch (JSONException e) {
					
				}
				
			}
		};
	}
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				dialog.removeDialog();
				if(li!=null){
					if(li.size()==0){
						Toast.makeText(OrderActivitynew.this, "当前无该用户的交易记录", 3).show();
					}else{
						Adapter = new Orderadapter(OrderActivitynew.this,
								li);
						new_alllist.setAdapter(Adapter);

					}
					
				}else{
					Toast.makeText(OrderActivitynew.this, "当前无该用户的交易记录", 3).show();

				}
				
				
				break;

			default:
				break;
			}
		}
	};
}
