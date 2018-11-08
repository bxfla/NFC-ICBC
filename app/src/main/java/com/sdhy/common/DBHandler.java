package com.sdhy.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


/**
 * 与服务器交互查询数据库
 * */
public class DBHandler {
    //private static String IP = "120.192.74.58";
    //private static String PORT = "8008";//webservice对应的tomcat端口
 // private static String URL = "http://123.232.38.10:4002/ICRecharge/pay!";
  //private static String URLup = "http://123.232.38.10:4002/";
  private static String URLup = "http://192.168.1.122:8080/";
  // private static String URL = "http://192.168.1.122:8080/hywxpay/charge!";
  
	
	private static String URL = "http://weixin.hezebus.com:8008/ICRecharge/pay!";
    private static String TAG = "访问数据库错误";
	public static final String ACTION_CHARGEMONEY = "getChargeMoney.action";
	public static final String ACTION_UPDATE_MOBILE_STATUS_ORDER = "updMobileStatusByOrder.action";
	//public static final String ACTION_UPDATE_MOBILE_STATUS_ORDER2 ="updateMobileOrderStatus.action";
	public static final String ACTION_UPDATE_CHARGE_STATUS = "updChargeStatus.action";
	public static final String ACTION_LOGIN = "login.action";
	public static final String ACTION_GENORDER = "genOrder.action";
	//public static String ACTION_GENORDERlist = "listBySerialNo.action";
	public static String ACTION_GENORDERlist ="listByCardNo.action";
	public static final String ACTION_GET_KEY = "getKeyBySerialNo.action";//读取m1卡密钥
	public static final String UPDATE_code = "getAppVersion.action";//版本升级
	public static String listBankSuccData = "listBankSuccData.action";//未补登接口
	
	
	/**
	 * 根据卡号查询充值记录 
	 * */
	@SuppressWarnings("deprecation")
	public static String getRecord(String action, Map<String, String> map){
	  	String result="";
		HttpClient httpClient = new DefaultHttpClient();
		// 发送参数组装
		List<NameValuePair> nvs = new ArrayList<NameValuePair>();
		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			nvs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		//卡号加密
		HttpPost httpRequst = new HttpPost(URL+action);
		try {
			// 将参数添加的POST请求中
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvs, "utf-8");
			httpRequst.setEntity(uefEntity);
			Log.d("查询", URL);
			Log.e(null, "###########################################"+URL+action);
			// 发送请求
			HttpResponse res = httpClient.execute(httpRequst);
			Log.e(null, "3333999999###########################################"+res.getStatusLine().getStatusCode());
			// 从状态行中获取状态码，判断响应码是否符合要求   如果请求响应码是200，则表示成功
			if (res.getStatusLine().getStatusCode() == 200) {  
				HttpEntity entity = res.getEntity();
				// 读取返回值
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(),"utf-8"));
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				reader.close();
				result=sb.toString();
				Log.e(null, "11113333999999###########################################"+result);
			}   
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "无法连接服务器");
		}  catch (Exception e) {
			 Log.e(TAG, Log.getStackTraceString(e));
		}
		return result;
	}
         
	
	/**
	 * 
	 * 程序版本检查
	 * **/
	public static int getVersion(String url){
	    HttpPost httpRequst = new HttpPost(url);
		HttpClient httpClient = new DefaultHttpClient();
		//组参数
		List<NameValuePair> nvs = new ArrayList<NameValuePair>();
		nvs.add(new BasicNameValuePair("version", "10"));
		StringBuffer sb = new StringBuffer();
		int version =0;
		try {
			// 将参数添加的POST请求中
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvs,"utf-8");
		     httpRequst.setEntity(uefEntity);
			// 发送请求
			HttpResponse res = httpClient.execute(httpRequst);
			HttpEntity entity = res.getEntity();
			// 读取返回值
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			//解析返回的json
			JSONObject json =new JSONObject(sb.toString());
		     version=Integer.parseInt(json.get("versionNumber").toString());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}
	public static String FenToYuan(Object num){
		double nums = Double.parseDouble(num.toString())/100;
		DecimalFormat fnum = new DecimalFormat("0.00");
		return fnum.format(nums);
	}

	public static void main(String args[]) {
	//	boolean result = isReverse("10270000", "EFD8FFFF");
     //   String hexStr=           Integer.toHexString(10000);
//		byte [] bb =hexString2Bytes("047a76469181a6a4b6e94ae7b18b81147b4faabb194fa0916476cbaa8073aaf0c491a3f612b78e84b627a0e5949c42f1421574d09f92383e40b80691a968c6ba1de1ca524eb2f45d9dabea5b1efc2202513ee5cbe84f699ca434d11d2255fbaaa578b49ebb036d66ec4e538af7110c7df4c158159fd199630f30dcdcfa5451dba32b87c64649c66a303759b817742c9c4aee69c675a12d6d4fba68a6857876c705ea10677d0533b139915dece7f6e94d26019c31cf105942fccb0d23059ba178d6fbbe139ca1ec1c1a18ad740683fc34cc025cce05ef87177e183bf0dc3d3a01c5411116ff2e52153b4d49ee445f416fa512955a7c9a7db41f4501c11821d1ee");
//				
//
//        System.out.println(bb.length);
        System.out.println(FenToYuan("24"));
	}
}   
