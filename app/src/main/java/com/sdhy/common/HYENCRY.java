package com.sdhy.common;


public class HYENCRY {

	public static String decode(String str) {//����
		  String vsTemp = "";
		  String vsPass = "";
		  String pspass ="";
		  int i = 0;
		  int viTemp = 0;
		  int viCz = 0;
		  int viNum = 0;
		  char vcBegin = 0;
		  char vcEnd = 0;
		  char vcFirst = 0;
		  char vcSecond = 0;
		  pspass = str;
		  if (str.length() < 1) {
			  return "";
		  }
		  //*��������ֵ*/
		  vcBegin = pspass.charAt(0);
		  vcEnd = pspass.charAt(pspass.length()-1);        //  {tgvvggvvggvvgvwxnv }
		  viCz =  vcEnd - vcBegin;
		  pspass = pspass.substring(1, pspass.length()-1);
		  i = pspass.length();
		  int viNUm = 0;
		  while (i >1) {
			  
			  viNum = viNum + 1;
		    if ( viNum % 2 == 1 ) {
		    	vcFirst = pspass.charAt(i-2);
		        vcSecond = pspass.charAt(i-1);
		    } else {
		    	vcFirst = pspass.charAt(i-1);
		        vcSecond = pspass.charAt(i-2);
		    }
		    viTemp = (vcFirst - vcBegin)*26 + (vcSecond - 97) - viCz;
		    vsTemp = String.valueOf((char)viTemp); 
		    vsPass = vsPass + vsTemp;
		    if (i > 1) {
		    	i = i - 2;
		    }
		  }
		return vsPass;
	}
	public static String encode(String str) {
		int i,viTemp,viAscSum,viSumMod,viDev,viMod,viMaxMod,viPos,viPos1;
		String vsModStr,vsDevStr,vsTemp,psPass;
		char vcChar,vcBegin,vcEnd;
		psPass = str.trim();
		if (psPass.length()<3) {
			return "";
		} else if (psPass.length()>30) {
			return "";
		}
		viTemp = psPass.charAt(0);
		if ((viTemp>'z') || ((viTemp>'Z') && (viTemp<'a')) || (viTemp<'0') || ((viTemp>'9') && (viTemp<'A'))) {
			return "";
		}
		viAscSum  = 0;
		for (int j = 0; j<psPass.length(); j++) {
			vcChar = psPass.charAt(j);
			if ((vcChar>'z') || ((vcChar>'Z') && (vcChar<'a')) || (vcChar<'0') || ((vcChar>'9') && (vcChar<'A'))) {
				return "";
			}
			viAscSum = viAscSum + vcChar;
		}
		viSumMod = viAscSum % 26;
		viMaxMod = viSumMod;
		vsModStr = "";
		vsDevStr = "";
		for (int j = 0; j<psPass.length(); j++) {
			vcChar = psPass.charAt(j);
			viTemp = vcChar + viSumMod;
			viDev = viTemp / 26;
			viMod = viTemp - viDev*26;
			if (viDev > viMaxMod) {
				viMaxMod = viDev;
			}
			vsModStr = vsModStr+','+String.valueOf((char)(viMod+97));
		    vsDevStr = vsDevStr+','+String.valueOf(viDev);
		}
		  vsModStr = vsModStr.substring(1, vsModStr.length());
		  vsDevStr = vsDevStr.substring(1, vsDevStr.length());

		//  /*������ASCII���ֵ�ܱ�ʾviMaxMod����������ĸ*/
		  viTemp = (int)(Math.random()*(26-viMaxMod));
		  vcBegin = (char)(viTemp+96);
		  vcEnd = (char)(viTemp+viSumMod+96);
		  vsTemp = "";
		  while (!vsDevStr.equals("")) {
			  vcChar = vsDevStr.charAt(0);
			  if (vsDevStr.length()<3) {
				  vsDevStr = vsDevStr.substring(0, vsDevStr.length());
				  viTemp = vcBegin + Integer.parseInt(String.valueOf(vcChar));
				  vsTemp = vsTemp + "," + String.valueOf((char)viTemp);
				  vsDevStr = "";
			  } else {
				  vsDevStr = vsDevStr.substring(2, vsDevStr.length());
				  viTemp = vcBegin + Integer.parseInt(String.valueOf(vcChar));
				  vsTemp = vsTemp + "," + String.valueOf((char)viTemp);
			  }
		  }
		  vsDevStr = vsTemp.substring(1, vsTemp.length());

		  ///*�γɼ��ܿ���*/
		  psPass = "";
		  i = 0;
		  while (!vsDevStr.equals("") && !vsModStr.equals("")) {
			  i = i + 1;
			  if (i % 2 == 1) {
				  viPos = vsDevStr.indexOf(",");
				  viPos1 = vsModStr.indexOf(",");
				  if (viPos == -1) {
					  psPass = vsDevStr + vsModStr + psPass;
					  break;
				  }
				  if (viPos1 == -1) {
					  viPos1 = vsModStr.length();
				  }
				  psPass = vsDevStr.substring(0, viPos) + vsModStr.substring(0, viPos1) + psPass;
				  vsDevStr = vsDevStr.substring(viPos + 1, vsDevStr.length() - viPos + 1);
				  vsModStr = vsModStr.substring(viPos1 + 1, vsModStr.length() - viPos1 + 1);
			  } else {
				  viPos = vsDevStr.indexOf(",");
				  viPos1 = vsModStr.indexOf(",");
				  if (viPos == -1) {
					  psPass = vsModStr + vsDevStr + psPass;
					  break;
				  }
				  if (viPos1 == -1) {
				  }
				  psPass = vsModStr.substring(0, viPos) + vsDevStr.substring(0, viPos1) + psPass;
				  vsDevStr = vsDevStr.substring(viPos + 1, vsDevStr.length() - viPos + 1);
				  vsModStr = vsModStr.substring(viPos1 + 1, vsModStr.length() - viPos1 + 1);
			  }
		  }
		  psPass = vcBegin + psPass + vcEnd;
		return psPass;
	}
	public static void main(String args[]) {
		System.out.println(HYENCRY.decode("ikchkkgfkkkdkkdhkkhn"));
		System.out.println(HYENCRY.encode("502024627"));
	}
}
