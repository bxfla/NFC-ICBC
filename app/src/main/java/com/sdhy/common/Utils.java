package com.sdhy.common;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Utils {
	/**
	 * ����16�����ַ����� �Ƿ��� ÿ���ַ� 4���ֽ�
	 * */
	public static boolean isReverse(String hexStr1, String hexStr2) {
		// ת����2���ƣ�������
		String anotherBinary = hexString2binaryString(hexStr1);
		String thisBinary = hexString2binaryString(hexStr2);
		System.out.println(anotherBinary);
		System.out.println(thisBinary);

		if (thisBinary.length() != anotherBinary.length()) {
			return false;
		} else {
			String result = "";
			for (int i = 0; i < anotherBinary.length(); i++) {
				if (anotherBinary.charAt(i) == thisBinary.charAt(i)) {
					// false
					return false;
				} else {
					continue;
				}
			}

		}
		return true;
	}

	/**
	 * 16�����ַ� ת�������ַ�
	 * */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000"
					+ Integer.toBinaryString(Integer.parseInt(
							hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
	/**
	 * 16进制字符串转为字节数组
	 * @param hex 16进制字符串
	 * @return
	 */
	public static byte[] hexString2Bytes(String hex) {

		if ((hex == null) || (hex.equals(""))) {
			return null;
		} else if (hex.length() % 2 != 0) {
			return null;
		} else {
			hex = hex.toUpperCase();
			int len = hex.length() / 2;
			byte[] b = new byte[len];
			char[] hc = hex.toCharArray();
			for (int i = 0; i < len; i++) {
				int p = 2 * i;
				b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
			}
			return b;
		}

	}
    
    /**
	 * 字节数组转换为16进制字符串
	 * */
    public static String bytesToHexString(byte[] src) {  
    	StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        char[] buffer = new char[2];  
        for (int i = 0; i < src.length; i++) {  
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);  
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);  	 
       //     stringBuilder.append("");  
        }  
        return stringBuilder.toString();  
    } 
    /**
     * ��ȡĳ�ֽ������е�һ��
     * תΪ16�����ַ�
     * */
    public static String bytesToHexString(byte[] src,int start,int end) {  
    	StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        if(src.length<end){
        	return null;
        }
        byte[] newByte=new byte[end-start+1];
        int count=0;
        for(int i=start;i<=end;i++){
        	newByte[count++]=src[i];
        }
        char[] buffer = new char[2];  
        for (int i = 0; i < newByte.length; i++) {  
            buffer[0] = Character.forDigit((newByte[i] >>> 4) & 0x0F, 16);  
            buffer[1] = Character.forDigit(newByte[i] & 0x0F, 16);
            stringBuilder.append(buffer);  	 
       //     stringBuilder.append("");  
        }  
        return stringBuilder.toString();  
    } 
	/**
	 * 2����ת16����
	 * */ 
	  public static String binaryString2hexString(String bString)  
	      {  
	          if (bString == null || bString.equals("") || bString.length() % 8 != 0)  
	              return null;  
	          StringBuffer tmp = new StringBuffer();  
	          int iTmp = 0;  
	           for (int i = 0; i < bString.length(); i += 4)  
	          {  
	               iTmp = 0;  
	               for (int j = 0; j < 4; j++)  
	              {  
	                  iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);  
	              }  
	              tmp.append(Integer.toHexString(iTmp));  
	           }  
	           return tmp.toString();  
	      } 
	/**
	 * 16�����ַ���
	 * */ 
	 public static String getReverseHexString(String hexString){
		 String thisBinary=hexString2binaryString(hexString);
		 String resultBinary="";
		 for(int i=0;i<thisBinary.length();i++){
			 if(thisBinary.charAt(i)=='0'){
				 resultBinary+="1";
			 }else{
				 resultBinary+="0";
			 }
		 }
		 return  binaryString2hexString(resultBinary);
		 
	 }
	/**
	 * �ַ�ת��Ϊ�ֽ�
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	 
	 /**
		 * 16进制字符串，转成前低后高的16进制字符串
		 * */ 
		public static String hexStringReverseOrder(String hexStr,int totalLen){
			if(hexStr.length()%2!=0){
				hexStr="0"+hexStr;
			}
			 List<String> newList=new ArrayList<String>();
			 String reverseHexStr="";
			 for(int i=0;i<hexStr.length()/2;i++){
				 newList.add(hexStr.substring(i*2, i*2+2));
			 }
			 //逆转顺序
			 for(int i=newList.size()-1;i>=0;i--){
				 reverseHexStr+=newList.get(i);
			 }
			 //总长度 4个字节，不足的后面全补0
			 while(reverseHexStr.length()<totalLen){
				 reverseHexStr+="0";
			 }
			 return reverseHexStr;
		 }
	 /**
	  * byte[] תʮ����
	  * flag==0 ���� 
	  * flag==1 ǰ�ͺ�� Ҫ��תһ��
	  * */
	 public static int bytes2Int(byte[]b,int start,int end,int flag){
		 String str="";
		 if(flag==0){
		 for(int i=start;i<=end;i++){
				 str+=bytesToHexString(new byte[]{b[i]}).trim();
		 }
	     }else if(flag==1){
	    	 for(int i=end;i>=start;i--){
				 str+=bytesToHexString(new byte[]{b[i]}).trim();
			 
		 }
		}
		 int result=Integer.parseInt(str,16);
		 return result;
	 }
	 /**
	 * ��ת��ΪԪ
	 * @param num Ҫת�������
	 * @return
	 */
	public static String FenToYuan(Object num){
		double nums = Double.parseDouble(num.toString())/100;
		DecimalFormat fnum = new DecimalFormat("0.00");
		return fnum.format(nums);
	}
	
	/**
	 * 对 URL 中的 QueryString 进行解析的工具类
	 * @param queryString
	 * @return
	 */
	public static Map<String, String> queryStringParser(String queryString) {
	    Map<String, String> mapParam = new HashMap<String, String>();
	    StringTokenizer st = new StringTokenizer(queryString, "&");
        while (st.hasMoreTokens()) {
            String strPairs = st.nextToken();
            String strKey = strPairs.substring(0, strPairs.indexOf('='));
            String strValue = strPairs.substring(strPairs.indexOf('=') + 1);
            mapParam.put(strKey, strValue);
        }
	    return mapParam;
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
