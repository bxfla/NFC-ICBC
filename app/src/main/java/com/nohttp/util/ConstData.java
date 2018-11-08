/*package com.nohttp.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.sdhy.patrolphone.bean.XGJH;

public class ConstData {

	public static int recDataThreadNum=0;
	public static int bagOrder=0;
	public static int ver=0;//本地黑名单版本号
	public static int bver=0;//服务器 查询到的黑名单版本号
	public static String Ip =""; //go服务器Ip
	public static String Port ="";//go服务器端口 ，socket
	public static String tomcatPort ="8081";//实时监控端口
	public static String Dwbh ="";
	public static String Dwmc ="";
	public static String Idbh ="";
	public static String Code="";
	public static String powers="";
	public static String JJCode ="";
	public static boolean conniden=false;
	public static boolean initNetFlag =false;//刷员工卡登录时的初始网络状态：false无网登录的  true有网登录的
	public static boolean connNetFlag =false;//无网登录的 是否执行过一次联网操作了.执行 true
	public static int downPlanCounter =0;//下载计划的次数
	public static boolean menuFlag =false;//
	public static String version="";//本地 程序版本号
	public static int remoteVersion=1;//数据库表中存的软件版本号
	public static int alertMinutes=10;//提前多少分钟提醒巡更截止，从数据库获取
	public static int planBeginTime=-1;//当前计划时间
	public static Map<String,Integer> patrolDataMap =new HashMap<String,Integer>();//卡号，当前计划时间Unix
	public static String netState ="";//手持机网络状态：wifi ,sim,无
	public static String netName ="";//巡更人员看不懂英文，改成 无  有两种显示。
	public static int staticLocs=-1;//当圈计划中已刷过的固定点数
	public static int staticBuses=-1;//当圈计划中已刷过的移动点数
	public static List<XGJH> staticPlanList=new ArrayList<XGJH>();
	public static List<Integer> blist=new ArrayList<Integer>();
	public static Map<String,byte[]>  warnBags =new HashMap<String,byte[]>();
	private static Map<String,byte[]>  dataBags=new HashMap<String,byte[]>();
	public static Map<Integer,Integer> msgMap =new HashMap<Integer,Integer>();//短信存储
	//----------本程序是否开机启动的标志------------
	public static boolean bootFlag =false;//非开机启动
	public static byte[] GetLoginBags(){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[33];
		//封装包头
		byte[] b1=getBagHeader(33,148,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
		//封装黑名单
		buffer[pos]=(byte)24;
		byte[] b=getBagNo(ver);		
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		buffer[pos+3]=(byte)26;
		buffer[pos+4]=(byte)1;
		buffer[32]=(byte)129;
		return buffer;
	}

	
	*//**
	 * 黑名单下载
	 *//*
	public static byte[] GetBlackBags(){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[33];
		//封装包头
		byte[] b1=getBagHeader(33,136,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
		//封装黑名单
		buffer[pos]=(byte)24;
		byte[] b=getBagNo(ver);		
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		buffer[pos+3]=(byte)26;
		buffer[pos+4]=(byte)1;
		buffer[32]=(byte)129;
		return buffer;
	}
	
	
	*//**
	 * 封装心跳包
	 *
	 *//*
	public static byte[] GetHeartBags(){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[28];
		//封装包头
		byte[] b1=getBagHeader(28,149,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		buffer[27]=(byte)129;
		return buffer;
	}
	
	
	*//**
	 * 获取报警数据
	 *//*
	public static byte[] GetWarnBags(byte b){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[30];
		//封装包头
		byte[] b1=getBagHeader(30,132,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		buffer[27]=(byte)16;
		buffer[28]=b;
		buffer[29]=(byte)129;
		warnBags.put(Integer.toString(bagOrder), buffer);
		return buffer;
	}
	
	
	
	
	
	*//**
	 * 上传交接数据
	 *//*
	
	public  static byte[] GetJJDataBags(int JJR,int XGR,int zs,int bs,int kh,int sn,int cardtype,int xh,int unix,int type){
		int pos=0;
		byte[] buffer=new byte[63];
		//封装包头
		byte[] b1=getBagHeader(63,130,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, XGR);
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装交接员卡号
		byte[] b5=getBagData(32, JJR);
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
		//封装时间
		byte[] b6=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b6, 0, buffer, pos, b6.length);
		pos=pos+b6.length;
		//封装总条数
		byte[] b=getBagNo(zs);
		buffer[pos]=(byte)23;
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		pos=pos+3;
		//封装当前包号
		b=getBagNo(bs);
		buffer[pos]=(byte)24;
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		pos=pos+3;
		//封装卡号
		byte[] b7=getBagData(7, kh);
		System.arraycopy(b7, 0, buffer, pos, b7.length);
		pos=pos+b7.length;
		//封装序号
		byte[] b8=getBagData(25, sn);
		System.arraycopy(b8, 0, buffer, pos, b8.length);
		pos=pos+b8.length;
		//封装类型
		buffer[pos]=(byte)8;
		buffer[pos+1]=(byte)cardtype;
		pos=pos+2;
		//封装编号
		byte[] b9=getBagData(21, xh);
		System.arraycopy(b9, 0, buffer, pos, b9.length);
		pos=pos+b9.length;
		//封装时间
		byte[] b10=getBagData(35, unix);
		System.arraycopy(b10, 0, buffer, pos, b10.length);
		pos=pos+b10.length;
		buffer[60]=(byte)33;
		buffer[61]=(byte)type;
		buffer[62]=(byte)129;
		return buffer;
	}
	
	*//**
	 * 上传巡更数据
	 * @return
	 *//*
	public static byte[] GetXGDataBags(int XGR,int zs,int bs,int kh,int sn,int cardtype,int xh,int unix,int type) {
		// TODO Auto-generated method stub
		int pos=0;
		byte[] buffer=new byte[58];
		//封装包头
		byte[] b1=getBagHeader(58,131,getBh());
		
		
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, XGR);
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
		//封装总条数(计划内)
		byte[] b=getBagNo(zs);
		//Log.e("ConstData","zs="+zs);
		buffer[pos]=(byte)23;
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		pos=pos+3;
		//Log.e("ConstData","封装总条数pos="+pos);
		//封装当前包号
		b=getBagNo(bs);
		//Log.e("ConstData","bs="+bs);
		buffer[pos]=(byte)24;
		buffer[pos+1]=b[0];
		buffer[pos+2]=b[1];
		pos=pos+3;
		//Log.e("ConstData","封装当前包号pos="+pos);
		//封装卡号
		byte[] b7=getBagData(7, kh);
		System.arraycopy(b7, 0, buffer, pos, b7.length);
		pos=pos+b7.length;
		//封装序号
		byte[] b8=getBagData(25, sn);
		System.arraycopy(b8, 0, buffer, pos, b8.length);
		pos=pos+b8.length;
		//封装巡更类型
		buffer[pos]=(byte)9;
		buffer[pos+1]=(byte)type;
		pos=pos+2;
		//封装类型
		buffer[pos]=(byte)8;
		buffer[pos+1]=(byte)cardtype;
		pos=pos+2;
		//封装编号
		byte[] b9=getBagData(21, xh);
		System.arraycopy(b9, 0, buffer, pos, b9.length);
		pos=pos+b9.length;
		//封装时间
		byte[] b10=getBagData(35, unix);
		System.arraycopy(b10, 0, buffer, pos, b10.length);
		pos=pos+b10.length;
		buffer[57]=(byte)129;
		 Log.e("组包巡更数据", buffer.toString());
		 dataBags.put(Integer.toString(bagOrder), buffer);
		return buffer;
	}
	
	
	*//**
	 * 获取巡更卡设置包
	 *//*
	public static byte[] GetSetCardBags(int xgbh,int xgkh,int type){
		int pos=0;
		byte[] buffer=new byte[40];
		//封装包头 0x86
		byte[] b1=getBagHeader(40,134,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
		byte[] b6=getBagData(7,xgkh);
		System.arraycopy(b6, 0, buffer, pos, b6.length);
		pos=pos+b6.length;
		buffer[pos]=(byte)8;
		buffer[pos+1]=(byte)type;
		pos=pos+2;
		byte[] b7=getBagData(21,xgbh);
		System.arraycopy(b7, 0, buffer, pos, b7.length);
		pos=pos+b7.length;
		buffer[39]=(byte)129;
		return buffer;
	}
	
	
	*//**
	 * 封装计划请求包
	 *//*
	public static byte[] getXGJH(int n1,int n2){
		int pos=0;
		byte[] buffer=new byte[34];
		//封装包头
		int num = getBh();
		byte[] b1=getBagHeader(34,135,num);
		
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//手持机时间 0x06 0x4C 0xDD 0x68 0x86 手持机时间 
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);////
		//0x18 0x00 0x01 当前数据序号 
		//0x17 0x00 0x00 数据总条数 
		buffer[27]=(byte)24;//0x18 
		buffer[28]=getBagNo(n1)[0];
		buffer[29]=getBagNo(n1)[1];
		buffer[30]=(byte)23;//0x17
		buffer[31]=getBagNo(n2)[0];
		buffer[32]=getBagNo(n2)[1];
		buffer[33]=(byte)129;//包尾 0x81
		return buffer;
	}
	
	
	*//**
	 * 补寻回应包
	 * @return
	 *//*
	public static byte[] GetXGBagsResp(){
		int pos=0;
		byte[] buffer=new byte[30];
		//封装包头
		byte[] b1=getBagHeader(30,170,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		buffer[27]=(byte)34;
		buffer[28]=(byte)1;
		buffer[29]=(byte)129;
		return buffer;
	}
	
	
	*//**
	 * 短信回应包
	 *//*
	public static byte[] getMsgResp(int bh,int unix){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[30];
		//封装包头
		byte[] b1=getBagHeader(30,146,bh);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装巡逻员卡号
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;
		//封装时间
		byte[] b5=getBagData(6, unix);
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		buffer[27]=(byte)26;
		buffer[28]=1;
		buffer[29]=(byte)129;
		return buffer;
	}
	*//**
	 * 封装 请求 软件版本号,判定是否要更新
	 * 一共23个字节
	 * 0x80
0x00 0x3d
0x01
0x00 0x6e
0x98 手持机请求 数据库中存的apk 版本号
0x01 四个字节  场站号
0x02 四个字节 手持机号
0x06 四个字节 手持机时间
0x81 
	 * *//*
	public static byte[] GetApkVersion(){
		int pos=0;
		byte[] buffer=new byte[28];
		//封装包头 0x98
		byte[] b1=getBagHeader(28,152,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装场站号
		byte[] b2=getBagData(1, Integer.parseInt(ConstData.Dwbh));
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装手持机号
		byte[] b3=getBagData(2, Integer.parseInt(ConstData.Idbh));
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//---------------------
		byte[] b4=getBagData(4, Integer.parseInt(ConstData.Code));
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos=pos+b4.length;		
		//封装时间
		byte[] b5=getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos=pos+b5.length;
	 
		buffer[27]=(byte)129;//包尾81
		return buffer;
	}
	
	*//**
	 * 获取包号
	 * @return
	 *//*
	public static int getBh(){
		bagOrder++;
		if(bagOrder>65535){
			bagOrder=1;
		}
		return bagOrder;
	}
	
	
	
	 
    *//**
     * 封装数据包的包头部分
     * @param length
     * @param type
     * @return
     *//*
    public static byte[] getBagHeader(int length,int type,int num){
    	byte[] b=getBagNo(num);
    	byte[] buffer=new byte[7];
    	buffer[0]=(byte) 128;
		buffer[1]=(byte) 0;
		buffer[2]=(byte) length;
		buffer[3]=(byte) 1;
		buffer[4]=(byte) b[0];
		buffer[5]=(byte) b[1];
		buffer[6]=(byte) type;
		return buffer;
    }
    
    
    *//**
     * 数据部分转4字节
     *//*
    
    public static byte[] getBagData(int value,int data){
    	byte bytes[]=new byte[5];
    	bytes[0]=(byte)value;
    	bytes[1]=(byte)(data>>24);
    	bytes[2]=(byte)((data>>16)&0xff);
    	bytes[3]=(byte)((data>>8)&0xff);
    	bytes[4]=(byte)(data&0xff);
    	return bytes;
    }

    *//**
     * 包号转换
     *//*
    public static byte[] getBagNo(int v){
    	byte bytes[]=new byte[2];
    	bytes[0]=(byte)((v>>8)&0xff);
    	bytes[1]=(byte)(v&0xff);
    	return bytes;
    }
    
    *//**
     * 包号解析
     *//*
    public static int getByteNo(byte[] b){
    	
    	int num =((b[0] & 0xff) << 8)+((b[1] & 0xff));
//    	byte bytes[]=new byte[2];
//    	bytes[0]=(byte)((v>>8)&0xff);
//    	bytes[1]=(byte)(v&0xff);
    	return num;
    }
    
    
    *//**判断巡更计划日期
     * 12点之前算作-1 天的
     * 12点之后算作当天
     * 日期格式：yyyyMMdd
     * 计算日期
     *//*
    
    public static String getPlanDate(){
    	String sj=new SimpleDateFormat("HHmm").format(new Date());
    	String rq=new SimpleDateFormat("yyyyMMdd").format(new Date());
    	if(Integer.parseInt(sj)<1200){
    		Calendar calendar = Calendar.getInstance(); //得到日历
    		calendar.setTime(new Date());				//把当前时间赋给日历
    		calendar.add(Calendar.DAY_OF_MONTH, -1);  	//设置为前一天
    		rq=new SimpleDateFormat("yyyyMMdd").format(calendar.getTime());
    	}
    	return rq;
    }
    
    
    
    
    
	*//**
	 * 字节转换
	 * @param src
	 * @return
	 *//*
    public static String bytesToHexString2(byte[] src) {  
    	StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        char[] buffer = new char[2];  
        for (int i = 0; i < src.length; i++) {  
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);  
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);  	 
            stringBuilder.append(" ");  
        }  
        return stringBuilder.toString();  
    }  
    
    public static boolean isCheck(int bh){
    	for(int i=0;i<blist.size();i++){
    		if(bh==blist.get(i)){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    
    *//**
     * 解析数据包
     * @param bags
     * @param val
     * @param Type
     * @return
     *//*
    public static int getvalue(byte[] bags, byte v){
    	if(bags.length>7){
    		byte[] b=new byte[bags.length-7];
    		for (int i=7; i<bags.length; i++){
    			b[i-7]=bags[i];
    		}
    		while(b.length>0){
    			byte h=b[0];
    			if (h == (byte)1 || h == (byte)2 || h == (byte)4 || h == (byte)6 || h == (byte)7 || h == (byte)18 || h == (byte)19 || h == (byte)21 || h == (byte)25 || h == (byte)32 || h == (byte)35) {
        			if(b.length>=5){
        				if(v==h){
        					int v0 = (b[1] & 0xff) << 24;
        					int v1 = (b[2] & 0xff) << 16; 
        					int v2 = (b[3] & 0xff) << 8; 
        					int v3 = (b[4] & 0xff) ; 
        					return v0+v1+v2+v3;
        				}
        				byte[] c=new byte[b.length-5];
        				for (int i=5;i<b.length;i++ ){
        	    			c[i-5]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "1解析数据异常");
        				return 0;
        			}
        		}else if(h==(byte)23||h==(byte)24){
        			if(b.length>=3){
        				if(v==h){
        					int v0 = (b[1] & 0xff) << 8; 
        					int v1 = (b[2] & 0xff) ; 
        					return v0+v1;
        				}
        				byte[] c=new byte[b.length-3];
        				for (int i=3;i<b.length;i++ ){
        	    			c[i-3]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "2解析数据异常");
        				return 0;
        			}
        		}else if(h == (byte)3 || h == (byte)8 || h == (byte)9 || h == (byte)16 || h == (byte)20 ||h==(byte)26|| h == (byte)33 || h == (byte)34){
        			if(b.length>=2){
        				if(v==h){
        					return (int)b[1];
        				}
        				byte[] c=new byte[b.length-2];
        				for (int i=2;i<b.length;i++ ){
        	    			c[i-2]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "3解析数据异常");
        				return 0;
        			}
        		}else{
        			return 0;
        		}
    		}
    	}
    	return 0;
    }
  *//**
   * 判定内外网 true 内网 false外网
   * *//*
	public static boolean isInnerIP(String ip){   
		  final long a1 = getIpNum("10.0.0.0");
		   final long a2 = getIpNum("10.255.255.255");
		   final long b1 = getIpNum("172.16.0.0");
		   final long b2 = getIpNum("172.31.255.255");
		   final long c1 = getIpNum("192.168.0.0");
		   final long c2 = getIpNum("192.168.255.255");
		   final long d1 = getIpNum("10.44.0.0");
		   final long d2 = getIpNum("10.69.0.255");

      long n = getIpNum(ip);
      return (n >= a1 && n <= a2) || (n >= b1 && n <= b2) || (n >= c1 && n <= c2) || (n >= d1 && n <= d2)||(ip.equals("127.0.0.1"));
}  

private static long getIpNum(String ipAddress) {   
   String [] ip = ipAddress.split("\\.");   
   long a = Integer.parseInt(ip[0]);   
   long b = Integer.parseInt(ip[1]);   
   long c = Integer.parseInt(ip[2]);   
   long d = Integer.parseInt(ip[3]);   
   return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;   
}

    
    
    
}
*/