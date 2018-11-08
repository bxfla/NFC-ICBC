package com.sdhy.cpucardoper.activity;


import java.util.List;
import java.util.Map;

import com.alpha.live.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class Orderadapter extends BaseAdapter{
	
	
	public List<Map<String,Object>> data;
	private Context context ;
	

	public Orderadapter(Context context,List<Map<String,Object>> data ) {
		this.data = data;
		this.context = context;
	
		
	}
	
	
	
	@Override
	public int getCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "InflateParams", "NewApi" })
	@Override
	public View getView(int position, View view, ViewGroup group) {
		users u;
		if(view==null){
			u=new users();
			//得到一个LayoutInflater对象，因为我们需要这个对象来得到视图
			//将组件添加到view中
			view = LayoutInflater.from(context).inflate(R.layout.order, null);
			u.title=(TextView) view.findViewById(R.id.xudesc_titleww2);
			
			
			u.wages=(TextView) view.findViewById(R.id.xudesc_wages2);
			
			u.times=(TextView) view.findViewById(R.id.xudesc_time2);
			
			u.outtimes=(TextView) view.findViewById(R.id.xudesc_outtime2);
			u.Statu_time2=(TextView) view.findViewById(R.id.Statu_time2);
			u.orderblance=(TextView) view.findViewById(R.id.orderblance);
			
			view.setTag(u);
		}else{
			u = (users)view.getTag();
			
		}

		Log.e(null, "3333333333333"+data.get(position).get("orderDate"));
		u.title.setText(data.get(position).get("cardNo").toString());
		
		u.wages.setText(data.get(position).get("orderId").toString());
		u.times.setText(data.get(position).get("orderDate").toString());
		u.outtimes.setText(data.get(position).get("payment").toString()+"元");
		u.Statu_time2.setText(data.get(position).get("status").toString());
		if((data.get(position).get("status")).equals("支付成功，等待补登")){
			u.orderblance.setVisibility(View.VISIBLE);;
		}
		;
		
		return view;
	}
	
	
	
	
	
	class users{
		TextView title;//订单号
		TextView wages;//卡号
		TextView times;//订单时间
		TextView outtimes;//充值金额
		TextView Statu_time2;//充值状态
		TextView orderblance;//补登按钮
		
	}

}
