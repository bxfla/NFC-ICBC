package com.sdhy.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class ConstData {

	public static int recDataThreadNum=0;
	public static int bagOrder=0;
	public static int ver=0;//本地黑名单版本号
	public static int bver=0;//服务器 查询到的黑名单版本号
	public static String Ip ="120.192.74.58"; //go服务器Ip
	public static String Port ="50005";//go服务器端口 ，socket
	
	/*public static String Ip ="123.232.38.10"; //go服务器Ip
	public static String Port ="50010";//go服务器端口 ，socket
	public static String Port2 ="50020";//go服务器端口 ，socket	
*/	
	/*public static String Ip ="123.232.38.10"; //go服务器Ip
	public static String Port ="12345";//go服务器端口 ，65004
*/	//public static String Port1 ="8018";

	public static boolean isConnect=false;
	public static boolean initNetFlag =false;//刷员工卡登录时的初始网络状态：false无网登录的  true有网登录的
	public static boolean connNetFlag =false;//无网登录的 是否执行过一次联网操作了.执行 true
	public static int downPlanCounter =0;//下载计划的次数
	public static boolean menuFlag =false;//
	public static String version="";//本地 程序版本号
	public static int planBeginTime=-1;//当前计划时间
	public static Map<String,Integer> patrolDataMap = new HashMap<String,Integer>();//卡号，当前计划时间Unix
	public static String netState = "";//手持机网络状态：wifi ,sim,无
	public static String netName = "";//巡更人员看不懂英文，改成 无  有两种显示。
	public static Map<String,byte[]> warnBags = new HashMap<String,byte[]>();
	private static Map<String,byte[]> dataBags= new HashMap<String,byte[]>();
	//----------本程序是否开机启动的标志------------
	public static boolean bootFlag =false;//非开机启动
	
	public static byte[] getLoginBags(){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[34];
		//封装包头
		byte[] b1=getBagHeader(0x80,34,0x82,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//封装线路号 0x01
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//封装车号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		//封装调度员编号
        byte[] b4=getBagData(4, 0);
        System.arraycopy(b4, 0, buffer, pos, b4.length);
        pos += b4.length;
        //封装时间
        byte[] b5 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
        System.arraycopy(b5, 0, buffer, pos, b5.length);
        pos += b5.length;
        buffer[pos++] = (byte) 0x1A;
        buffer[pos++] = (byte) 0x01;
		//校验和
		int intPlus = 0;
        for (int i = 0; i < pos; i++) {
            intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
        buffer[pos++] = (byte)(intPlus>>24);
        buffer[pos++] = (byte)((intPlus>>16)&0xff);
        buffer[pos++] = (byte)((intPlus>>8)&0xff);
        buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos]=(byte)0x81;//包尾
		return buffer;
	}

	/**
	 * 获取加密密钥
	 * @param cardNo 16进制卡号
	 * @return 加密数据
	 */
	public static byte[] getEncryptBags(String cardNo){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[74];
		//封装包头
		byte[] b1=getBagHeader(0x80,buffer.length, 0xBE,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos += b1.length;
		//封装线路
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos += b2.length;
		//封装车号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos += b3.length;
		//封装调度员编号
		byte[] b4=getBagData(4, 0);
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos += b4.length;
		//封装时间
		byte[] b5 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos += b5.length;
//		0x44 加密机获取密钥
		buffer[pos++] = 0x44;
        buffer[pos++] = 0x01;
//      0x45 密钥类型
        buffer[pos++] = 0x45;
        buffer[pos++] = 0x01;
//      0x46 分散因子，取当前时间(0x06)
        buffer[pos++] = 0x46;
        System.arraycopy(b5, 1, buffer, pos, b5.length - 1);
        pos += b5.length - 1;
//      0x12 加密密钥
//      先取卡号，不足32位前补0
		buffer[pos++]=0x12;
		String strCardNo32 = "00000000000000000000000000000000" + cardNo;
		strCardNo32 = strCardNo32.substring(strCardNo32.length() - 32, strCardNo32.length());
		String strCurDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String strKey = strCurDate + Utils.bytesToHexString(b5, 1, b5.length - 1) + strCurDate + strCurDate;
		String strFirstRet = DesPBOC2.Encry3DESStrHex(strCardNo32.substring(0, 16), strKey);
		String strLastRet = DesPBOC2.Encry3DESStrHex(strCardNo32.substring(16, 32), strKey);
		for (char c : (strFirstRet + strLastRet).toCharArray()) {
		    buffer[pos++] = (byte) c;
		}
		int intPlus = 0;
		for (int i = 0; i < pos; i++) {
		    intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
		buffer[pos++] = (byte)(intPlus>>24);
		buffer[pos++] = (byte)((intPlus>>16)&0xff);
		buffer[pos++] = (byte)((intPlus>>8)&0xff);
		buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos++] = (byte)0x81;
		return buffer;
	}
	
	
	
	
	public static byte[] getxuliestatus(String xulie){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[43];
		//封装包头
		byte[] b1=getBagHeader(0xA0,43,0xD6,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//预留字段
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//设备编号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		
		
		//类型
        byte[] b4=xulieleibu();
        System.arraycopy(b4, 0, buffer, pos, b4.length);
        pos += b4.length;
      //预留字段
  		byte[] b5=getBagData(4, 0);
  		System.arraycopy(b5, 0, buffer, pos, b5.length);
  		pos= pos+b5.length;
        
        //封装时间
        byte[] b6 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
        System.arraycopy(b6, 0, buffer, pos, b6.length); 
        pos += b6.length;
        buffer[pos++] = (byte) 0x12;
       
      //预留字段
  		byte[] b7=xulie(xulie);
  		System.arraycopy(b7, 0, buffer, pos, b7.length);
  		pos= pos+b7.length;
        
		//校验和
		int intPlus = 0;
        for (int i = 0; i < pos; i++) {
            intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
        buffer[pos++] = (byte)(intPlus>>24);
        buffer[pos++] = (byte)((intPlus>>16)&0xff);
        buffer[pos++] = (byte)((intPlus>>8)&0xff);
        buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos]=(byte)0xA1;//包尾
		return buffer;
	}
	 public static byte[] xulie(String xulie){
		  	
			byte b[] = xulie.getBytes();//String转换为byte[]
			System.out.println(b);
		  	return b;
		  } 
	
public static byte[] getLoginstatus(String order){
		
		Log.e(null, "---------------------------------------"+order);
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[65];
		//封装包头
		byte[] b1=getBagHeader(0xA0,65,0xD6,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//预留字段
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//设备编号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		
		
		//类型
        byte[] b4=loginleista();
        System.arraycopy(b4, 0, buffer, pos, b4.length);
        pos += b4.length;
      //预留字段
  		byte[] b5=getBagData(4, 0);
  		System.arraycopy(b5, 0, buffer, pos, b5.length);
  		pos= pos+b5.length;
        
        //封装时间
        byte[] b6 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
        System.arraycopy(b6, 0, buffer, pos, b6.length);
        pos += b6.length;
        buffer[pos++] = (byte) 0x12;
       
      //预留字段
  		byte[] b7=order(order);
  		System.arraycopy(b7, 0, buffer, pos, b7.length);
  		pos= pos+b7.length;
        
		//校验和
		int intPlus = 0;
        for (int i = 0; i < pos; i++) {
            intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
        buffer[pos++] = (byte)(intPlus>>24);
        buffer[pos++] = (byte)((intPlus>>16)&0xff);
        buffer[pos++] = (byte)((intPlus>>8)&0xff);
        buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos]=(byte)0xA1;//包尾
		return buffer;
	}
	

	
	/**
	 * 充值金额
	 * @return 加密数据
	 */
	public static byte[] getRecharge(String cardNo,String order,String Recharge ,String yue,String xulie){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[167];
		//封装包头
		byte[] b1=getBagHeader(0x80,buffer.length, 0x85,getBh());
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos += b1.length;
		//封装线路
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos += b2.length;
		//封装车号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos += b3.length;
		//封装司机卡号
		byte[] b4=getBagData(4, 0);
		System.arraycopy(b4, 0, buffer, pos, b4.length);
		pos += b4.length;
		//封装时间
		byte[] b5 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
		System.arraycopy(b5, 0, buffer, pos, b5.length);
		pos += b5.length;

		//封装包数
		byte[] b6 = getBagData(27, 0);
		System.arraycopy(b5, 0, buffer, pos, b6.length);
		pos += b6.length;

		//封装消费数据
		byte[] b7 = getxiaofei(cardNo, order, Recharge , yue, xulie);
		System.arraycopy(b7, 0, buffer, pos, b7.length);
        pos += b7.length;
      
		//校验和
		int intPlus = 0;
        for (int i = 0; i < pos; i++) {
            intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
        buffer[pos++] = (byte)(intPlus>>24);
        buffer[pos++] = (byte)((intPlus>>16)&0xff);
        buffer[pos++] = (byte)((intPlus>>8)&0xff);
        buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos]=(byte)0x81;//包尾
		
		return buffer;
	}
	
	  /**
     * 登陆类型
     */
    public static byte[] loginlei(){
    	byte bytes[]=new byte[2];
    	
    	bytes[0]=(byte)0x03;
    	bytes[1]=(byte)0x01;
    	return bytes;
    }
   
    public static byte[] loginleista(){
    	byte bytes[]=new byte[2];
    	
    	bytes[0]=(byte)0x03;
    	bytes[1]=(byte)0x03;
    	return bytes;
    }
    public static byte[] xulieleibu(){
    	byte bytes[]=new byte[2];
    	
    	bytes[0]=(byte)0x03;
    	bytes[1]=(byte)0x04;
    	return bytes;
    }
    public static byte[] versta(){
    	byte bytes[]=new byte[2];
    	
    	bytes[0]=(byte)0x03;
    	bytes[1]=(byte)0x05;
    	return bytes;
    }
public static byte[] order(String order){
	  	
		byte b[] = order.getBytes();//String转换为byte[]
		System.out.println(b);
	  	return b;
	  }
	public static byte[] getLoginbudeng(String card){
		int pos=0;
		//封装登录包
		byte[] buffer=new byte[44];
		//封装包头
		byte[] b1=getBagHeader(0xA0,44,0xD6,0);
		System.arraycopy(b1, 0, buffer, pos, b1.length);	
		pos= b1.length;
		//预留字段
		byte[] b2=getBagData(1, 0);
		System.arraycopy(b2, 0, buffer, pos, b2.length);
		pos= pos+b2.length;
		//设备编号
		byte[] b3=getBagData(2, 0);
		System.arraycopy(b3, 0, buffer, pos, b3.length);
		pos= pos+b3.length;
		
		
		//类型
        byte[] b4=loginleibu();
        System.arraycopy(b4, 0, buffer, pos, b4.length);
        pos += b4.length;
      //预留字段
  		byte[] b5=getBagData(4, 0);
  		System.arraycopy(b5, 0, buffer, pos, b5.length);
  		pos= pos+b5.length;
        
        //封装时间
        byte[] b6 = getBagData(6, (int)(System.currentTimeMillis()/1000L));
        System.arraycopy(b6, 0, buffer, pos, b6.length);
        pos += b6.length;
        buffer[pos++] = (byte) 0x12;
       
      //预留字段
  		byte[] b7=car(card);
  		System.arraycopy(b7, 0, buffer, pos, b7.length);
  		pos= pos+b7.length;
        
		//校验和
		int intPlus = 0;
        for (int i = 0; i < pos; i++) {
            intPlus += buffer[i] & 0xFF;//为负数时清除高位的1 
        }
        buffer[pos++] = (byte)(intPlus>>24);
        buffer[pos++] = (byte)((intPlus>>16)&0xff);
        buffer[pos++] = (byte)((intPlus>>8)&0xff);
        buffer[pos++] = (byte)(intPlus&0xff);
		buffer[pos]=(byte)0xA1;//包尾
		return buffer;
	}
	
	
	  public static byte[] loginleibu(){
	    	byte bytes[]=new byte[2];
	    	
	    	bytes[0]=(byte)0x03;
	    	bytes[1]=(byte)0x02;
	    	return bytes;
	    }
	  public static byte[] car(String car){
		  	
			byte b[] = car.getBytes();//String转换为byte[]
			System.out.println(b);
		  	return b;
		  }
	
	/**
	 * 获取包号
	 * @return
	 */
	public static int getBh(){
		bagOrder++;
		if(bagOrder>65535){
			bagOrder=1;
		}
		return bagOrder;
	}
	 
    /**
     * 封装数据包的包头部分
     * @param length
     * @param type
     * @return
     */
    public static byte[] getBagHeader(int pacHeader,int length,int type,int num){
    	byte[] b=getBagNo(num);
    	byte[] buffer=new byte[7];
    	buffer[0]=(byte) pacHeader;
		buffer[1]=(byte) 0;
		buffer[2]=(byte) length;
		buffer[3]=(byte) 2;
		buffer[4]=(byte) b[0];
		buffer[5]=(byte) b[1];
		buffer[6]=(byte) type;
		return buffer;
    }
    
    /**
     * 数据部分转4字节
     */
    public static byte[] getBagData(int value,int data){
    	byte bytes[]=new byte[5];
    	bytes[0]=(byte)value;
    	bytes[1]=(byte)(data>>24);
    	bytes[2]=(byte)((data>>16)&0xff);
    	bytes[3]=(byte)((data>>8)&0xff);
    	bytes[4]=(byte)(data&0xff);
    	return bytes;
    }
    
    /**
     * 数据部分转4字节
     */
    public static byte[] getxiaofei(String cardNo,String order,String Recharge ,String yue,String xulie){
    	byte bytes[]=new byte[130];
    	bytes[0]=(byte)0;
    	bytes[1]=(byte)0;
    	bytes[2]=(byte)0;
    	bytes[3]=(byte)0;
    	
    	//此处为卡号
    	bytes[4]=(byte)00;
    	bytes[5]=Byte.parseByte(String.valueOf(cardNo.charAt(0))+String.valueOf(cardNo.charAt(1)));
    	bytes[6]=Byte.parseByte(String.valueOf(cardNo.charAt(2))+String.valueOf(cardNo.charAt(3)));
    	bytes[7]=Byte.parseByte(String.valueOf(cardNo.charAt(4))+String.valueOf(cardNo.charAt(5)));
    	bytes[8]=Byte.parseByte(String.valueOf(cardNo.charAt(6))+String.valueOf(cardNo.charAt(7)));
    	bytes[9]=Byte.parseByte(String.valueOf(cardNo.charAt(8))+String.valueOf(cardNo.charAt(9)));
    	bytes[10]=Byte.parseByte(String.valueOf(cardNo.charAt(10))+String.valueOf(cardNo.charAt(11)));
    	bytes[11]=Byte.parseByte(String.valueOf(cardNo.charAt(12))+String.valueOf(cardNo.charAt(13)));
    	bytes[12]=Byte.parseByte(String.valueOf(cardNo.charAt(14))+String.valueOf(cardNo.charAt(15)));
    	bytes[13]=Byte.parseByte(String.valueOf(cardNo.charAt(16))+String.valueOf(cardNo.charAt(17)));
    	bytes[14]=Byte.parseByte(String.valueOf(cardNo.charAt(18))+String.valueOf(cardNo.charAt(19)));
    	
    	
    	//此处为充值金额
    	if(Recharge.length()==1){
    		Recharge="00"+Recharge;
    	}else if(Recharge.length()==2){
    		Recharge="0"+Recharge;
    	}
    	
    	bytes[15]=Byte.parseByte(String.valueOf(Recharge.charAt(0)));
    	bytes[16]=Byte.parseByte(String.valueOf(Recharge.charAt(1)));
    	bytes[17]=Byte.parseByte(String.valueOf(Recharge.charAt(2)));
    	
    	
    	// 此处为余额
    	if(yue.length()==1){
    		yue="000"+yue;
    	}else if(yue.length()==2){
    		yue="00"+yue;
    	}else if (yue.length()==3){
    		yue="0"+yue;
    	}
    	bytes[18]=Byte.parseByte(String.valueOf(yue.charAt(0)));
    	bytes[19]=Byte.parseByte(String.valueOf(yue.charAt(1)));
    	bytes[20]=Byte.parseByte(String.valueOf(yue.charAt(2)));
    	bytes[21]=Byte.parseByte(String.valueOf(yue.charAt(3)));
    	//交易类型
    	bytes[22]=(byte)0;
    	bytes[23]=(byte)0;
    	//卡类型
    	bytes[24]=(byte)0;
    	bytes[25]=(byte)0;
    	//钱包累计交易次数
    	bytes[26]=(byte)0;
    	bytes[27]=(byte)0;
    	//设备交易流水号
    	bytes[28]=(byte)0;
    	bytes[29]=(byte)0;
    	bytes[30]=(byte)0;
    	bytes[31]=(byte)0;
    	//设备终端号
    	bytes[32]=(byte)0;
    	bytes[33]=(byte)0;
    	bytes[34]=(byte)0;
    	bytes[35]=(byte)0;
    	//线路编号
    	bytes[36]=(byte)0;
    	bytes[37]=(byte)0;
    	//车载机编号
    	bytes[38]=(byte)0;
    	bytes[39]=(byte)0;
    	//司机卡卡号
    	bytes[40]=(byte)0;
    	bytes[41]=(byte)0;
    	bytes[42]=(byte)0;
    	bytes[43]=(byte)0;
    	//交易日期
    	long now_1=System.currentTimeMillis()/1000 ;
    	bytes[44] = (byte)((now_1 & 0xFF000000) >>> 24);
    	bytes[45] = (byte)((now_1 & 0xFF0000) >>> 16);
    	bytes[46] = (byte)((now_1 & 0xFF00) >>> 8);
    	bytes[47] = (byte)(now_1 & 0xFF);
    	//交易时间
    	bytes[48]=(byte)0;
    	bytes[49]=(byte)0;
    	bytes[50]=(byte)0;
    	//车载机设备地址
    	bytes[51]=(byte)0;
    	//站点号/上下行
    	bytes[52]=(byte)0;
    	//PSAM卡卡号
    	bytes[53]=(byte)0;
    	bytes[54]=(byte)0;
    	bytes[55]=(byte)0;
    	bytes[56]=(byte)0;
    	bytes[57]=(byte)0;
    	bytes[58]=(byte)0;
    	//PSAM卡序号
     	bytes[59]=(byte)0;
    	bytes[60]=(byte)0;
    	bytes[61]=(byte)0;
    	bytes[62]=(byte)0;
    	bytes[63]=(byte)0;
    	bytes[64]=(byte)0;
     	bytes[65]=(byte)0;
    	bytes[66]=(byte)0;
    	bytes[67]=(byte)0;
    	bytes[68]=(byte)0;
    	bytes[69]=(byte)0;
    	bytes[70]=(byte)0;
    	bytes[71]=(byte)0;
    	//TAC
    	bytes[72]=(byte)0;
    	bytes[73]=(byte)0;
    	bytes[74]=(byte)0;
    	bytes[75]=(byte)0;
    	//发卡机构代码
      	bytes[76]=(byte)0;
    	bytes[77]=(byte)0;
    	bytes[78]=(byte)0;
    	bytes[79]=(byte)0;
      	bytes[80]=(byte)0;
    	bytes[81]=(byte)0;
    	bytes[82]=(byte)0;
    	bytes[83]=(byte)0;
    	//伪随机数
    	bytes[84]=(byte)0;
    	bytes[85]=(byte)0;
    	bytes[86]=(byte)0;
    	bytes[87]=(byte)0;
    	//有效期
    	bytes[88]=(byte)0;
    	bytes[89]=(byte)0;
    	bytes[90]=(byte)0;
    	bytes[91]=(byte)0;
    	//交通部城市代码
    	bytes[92]=(byte)0;
    	bytes[93]=(byte)0;
    	//卡片应用类型标识
    	bytes[94]=(byte)0;
    	bytes[95]=(byte)0;
    	bytes[96]=(byte)0;
    	bytes[97]=(byte)0;
    	bytes[98]=(byte)0;
    	bytes[99]=(byte)0;
    	//订单号
    	
    	bytes[100]=Byte.parseByte(String.valueOf(order.charAt(0)));
    	bytes[101]=Byte.parseByte(String.valueOf(order.charAt(1)));
    	bytes[102]=Byte.parseByte(String.valueOf(order.charAt(2)));
    	bytes[103]=Byte.parseByte(String.valueOf(order.charAt(3)));
    	bytes[104]=Byte.parseByte(String.valueOf(order.charAt(4)));
    	bytes[105]=Byte.parseByte(String.valueOf(order.charAt(5)));
    	bytes[106]=Byte.parseByte(String.valueOf(order.charAt(6)));
    	bytes[107]=Byte.parseByte(String.valueOf(order.charAt(7)));
    	bytes[108]=Byte.parseByte(String.valueOf(order.charAt(8)));
    	bytes[109]=Byte.parseByte(String.valueOf(order.charAt(9)));
    	bytes[110]=Byte.parseByte(String.valueOf(order.charAt(10)));
    	bytes[111]=Byte.parseByte(String.valueOf(order.charAt(11)));
    	bytes[112]=Byte.parseByte(String.valueOf(order.charAt(12)));
    	bytes[113]=Byte.parseByte(String.valueOf(order.charAt(13)));
    	bytes[114]=Byte.parseByte(String.valueOf(order.charAt(14)));
    	bytes[115]=Byte.parseByte(String.valueOf(order.charAt(15)));
    	bytes[116]=Byte.parseByte(String.valueOf(order.charAt(16)));
    	bytes[117]=Byte.parseByte(String.valueOf(order.charAt(17)));
    	bytes[118]=Byte.parseByte(String.valueOf(order.charAt(18)));
    	bytes[119]=Byte.parseByte(String.valueOf(order.charAt(19)));
    	bytes[120]=Byte.parseByte(String.valueOf(order.charAt(20)));
    	bytes[121]=Byte.parseByte(String.valueOf(order.charAt(21)));
    	bytes[122]=Byte.parseByte(String.valueOf(order.charAt(22)));
    	bytes[123]=Byte.parseByte(String.valueOf(order.charAt(23)));
    	bytes[124]=Byte.parseByte(String.valueOf(order.charAt(24)));
    	bytes[125]=Byte.parseByte(String.valueOf(order.charAt(25)));
    	bytes[126]=Byte.parseByte(String.valueOf(order.charAt(26)));
    	bytes[127]=Byte.parseByte(String.valueOf(order.charAt(27)));
    	bytes[128]=Byte.parseByte(String.valueOf(order.charAt(28)));
    	bytes[129]=Byte.parseByte(String.valueOf(order.charAt(29)));
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	return bytes;
    }
    
    

    /**
     * 包号转换
     */
    public static byte[] getBagNo(int v){
    	byte bytes[]=new byte[2];
    	bytes[0]=(byte)((v>>8)&0xff);
    	bytes[1]=(byte)(v&0xff);
    	return bytes;
    }
    
    /**
     * 包号解析
     */
    public static int getByteNo(byte[] b){
    	int num =((b[0] & 0xff) << 8)+((b[1] & 0xff));
//    	byte bytes[]=new byte[2];
//    	bytes[0]=(byte)((v>>8)&0xff);
//    	bytes[1]=(byte)(v&0xff);
    	return num;
    }
    
	/**
	 * 字节转换
	 * @param src
	 * @return
	 */
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
    
    /**
     * 解析数据包
     * @param bags
     * @param val
     * @param Type
     * @return
     */
    public static byte[] getValue(byte[] bags, byte v){
    	if(bags.length>7){
    		byte[] b=new byte[bags.length-7];
    		for (int i=7; i<bags.length; i++){
    			b[i-7]=bags[i];
    		}
    		while(b.length>0){
    			byte h = b[0];
    			if (h == (byte)1 || h == (byte)2 || h == (byte)4 || h == (byte)6 || h == (byte)7 || h == (byte)19 
    			        || h == (byte)21 || h == (byte)25 || h == (byte)32 || h == (byte)35 || h == (byte)0x46) {
        			if(b.length>=5){
        				if(v==h){
        					byte[] byeRes = new byte[4];
        					System.arraycopy(b, 1, byeRes, 0, 4);
        					return byeRes;
        				}
        				byte[] c=new byte[b.length-5];
        				for (int i=5;i<b.length;i++ ){
        	    			c[i-5]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "1解析数据异常");
        				return new byte[]{0};
        			}
        		}else if(h==(byte)23||h==(byte)24){
        			if(b.length>=3){
        				if(v==h){
//        					int v0 = (b[1] & 0xff) << 8; 
//        					int v1 = (b[2] & 0xff) ; 
//        					return v0+v1;
        					byte[] byeRes = new byte[2];
                            System.arraycopy(b, 1, byeRes, 0, 2);
                            return byeRes;
        				}
        				byte[] c=new byte[b.length-3];
        				for (int i=3;i<b.length;i++ ){
        	    			c[i-3]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "2解析数据异常");
        				return new byte[]{0};
        			}
        		}else if(h == (byte)3 || h == (byte)8 || h == (byte)9 || h == (byte)16 || h == (byte)20 ||h==(byte)26
        		        || h == (byte)33 || h == (byte)34 || h == (byte)0x44 || h == (byte)0x45){
        			if(b.length>=2){
        				if(v==h){
        				    byte[] byeRes = new byte[1];
        				    byeRes[0] = b[1];
        					return byeRes;
        				}
        				byte[] c=new byte[b.length-2];
        				for (int i=2;i<b.length;i++ ){
        	    			c[i-2]=b[i];
        	    		}
        				b=c;
        			}else{
        				Log.e("Log", "3解析数据异常");
        				return new byte[]{0};
        			}
        		} else if (h == (byte)0x12) {//12项为变长字段
        		    byte[] byeRes = new byte[b.length - 6];//去除前面的0x12，后面的4字节校验和及包尾
        		    System.arraycopy(b, 1, byeRes, 0, b.length - 6);
                    return byeRes;
        		}
        		else{
        			return new byte[]{0};
        		}
    		}
    	}
    	return new byte[]{0};
    }
  /**
   * 判定内外网 true 内网 false外网
   * */
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