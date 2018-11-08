package com.sdhy.common;

public class DesPBOC2 {
	
	/**
	 * 十进制转为2位16进制数，不足2位前补0
	 * @param v
	 * @return
	 */
	private static String intToHex(int v) {
		String val = Integer.toHexString(v);
		if (val.length() == 1) {
			val = "0" + val;
		} else if (val.length() > 2) {
			val = val.substring(val.length() - 2, val.length());
		}
		return val;
	}
	private static String InverseHEXStr(String strHex) {
		StringBuffer vStr = new StringBuffer();
		while(strHex.length() % 2 != 0) {
			strHex = strHex + "0";
		}
		for (int i = 0; i <= (strHex.length()/2 -1); i++) {
			vStr.append(intToHex(255-Integer.parseInt(strHex.substring(i*2, i*2+2), 16)));
		}
		return vStr.toString();
	}

	private static String GetDPKLeft(String Data, String Key) {
		StringBuffer sbResult = new StringBuffer();
		int[] K1Data = new int[8];
		int[] K2Data = new int[8];
		int[] OData = new int[8];
		while (Key.length() < 32) {
			Key = Key + "0";
		}
		for (int i = 0; i <= 7; i++) {
			K1Data[i] = Integer.parseInt(Key.substring(i*2, i*2+2), 16);
			K2Data[i] = Integer.parseInt(Key.substring(i*2+16, i*2+16+2), 16);
		}
		while (Data.length() < 16) {
			Data = Data + "0";
		}
		int DataLen = Data.length() / 2;
		int[] BData = new int[DataLen];
		for (int i = 0; i <= DataLen-1; i++) {
			BData[i] = Integer.parseInt(Data.substring(i*2, i*2+2), 16);
		}
		DES.EncryHex(BData, K1Data, OData);
		DES.DecryHex(OData, K2Data, BData);
		DES.EncryHex(BData, K1Data, OData);
		for (int i = 0; i < OData.length; i++) {
			sbResult.append(intToHex(OData[i]));
		}
		return sbResult.toString();
	}

	private static String GetDPKRight(String Data, String Key) {
		StringBuffer sbResult = new StringBuffer();
		int[] K1Data = new int[8];
		int[] K2Data = new int[8];
		int[] OData = new int[8];
		while (Key.length() < 32) {
			Key = Key + "0";
		}
		for (int i = 0; i <= 7; i++) {
			K1Data[i] = Integer.parseInt(Key.substring(i*2, i*2+2), 16);
			K2Data[i] = Integer.parseInt(Key.substring(i*2+16, i*2+16+2), 16);
		}
		while (Data.length() < 16) {
			Data = Data + "0";
		}
		String vStr = InverseHEXStr(Data);
		int DataLen = vStr.length() / 2;
		int[] BData = new int[DataLen];
		for (int i = 0; i <= DataLen-1; i++) {
			BData[i] = Integer.parseInt(vStr.substring(i*2, i*2+2), 16);
		}
		DES.EncryHex(BData, K1Data, OData);
		DES.DecryHex(OData, K2Data, BData);
		DES.EncryHex(BData, K1Data, OData);
		for (int i = 0; i < OData.length; i++) {
			sbResult.append(intToHex(OData[i]));
		}
		return sbResult.toString();
	}
	public static String Encry3DESStrHex(String Str, String Key) {
		StringBuffer sbResult = new StringBuffer();
		int[] K1Data = new int[8];
		int[] K2Data = new int[8];
		while (Key.length() < 32) {
			Key = Key + "0";
		}
		while (Str.length() % 16 != 0) {
			Str = Str + "00";
		}
		int[] PData = new int[Str.length() / 2];
		int[] EData = new int[Str.length() / 2];
		for (int i = 0; i < PData.length; i++) {
			PData[i] = Integer.parseInt(Str.substring(i*2, i*2+2), 16);
		}
		for (int i = 0; i < 8; i++) {
			K1Data[i] = Integer.parseInt(Key.substring(i*2, i*2+2), 16);
			K2Data[i] = Integer.parseInt(Key.substring(i*2+16, i*2+16+2), 16);
		}
		DES.EncryHex(PData, K1Data, EData);
		DES.DecryHex(EData, K2Data, PData);
		DES.EncryHex(PData, K1Data, EData);
		for (int i = 0; i < EData.length; i++) {
			sbResult.append(intToHex(EData[i]));
		}
		return sbResult.toString();
	}
	public static String Decry3DESStrHex(String Str, String Key) {
        StringBuffer sbResult = new StringBuffer();
        int[] K1Data = new int[8];
        int[] K2Data = new int[8];
        while (Key.length() < 32) {
            Key = Key + "0";
        }
        while (Str.length() % 16 != 0) {
            Str = Str + "00";
        }
        int[] PData = new int[Str.length() / 2];
        int[] EData = new int[Str.length() / 2];
        for (int i = 0; i < PData.length; i++) {
            PData[i] = Integer.parseInt(Str.substring(i*2, i*2+2), 16);
        }
        for (int i = 0; i < 8; i++) {
            K1Data[i] = Integer.parseInt(Key.substring(i*2, i*2+2), 16);
            K2Data[i] = Integer.parseInt(Key.substring(i*2+16, i*2+16+2), 16);
        }
        DES.DecryHex(PData, K1Data, EData);
        DES.EncryHex(EData, K2Data, PData);
        DES.DecryHex(PData, K1Data, EData);
        for (int i = 0; i < EData.length; i++) {
            sbResult.append(intToHex(EData[i]));
        }
        return sbResult.toString();
    }
	public static String CPUCardMac(String InitVector, String Data, String Key) {
		byte CacuType = 0;
		StringBuffer sbResult = new StringBuffer();
		int[] IV = new int[8];
		int[] K1Data = new int[8];
		int[] K2Data = new int[8];
		int[] XORData = new int[8];
		int[] OData = new int[8];
		while (InitVector.length() % 2 != 0) {
			InitVector = InitVector + "0";
		}
		for (int i = 0; i <= InitVector.length() / 2 - 1; i++) {
			if (i <= 7) {
				IV[i] = Integer.parseInt(InitVector.substring(i*2, i*2+2), 16);
			}
		}
		while (Key.length() < 16) {
			Key = Key + "0";
		}
		if (Key.length() > 16) {
			while (Key.length() < 32) {
				Key = Key + "0";
			}
		}
		int iden = Key.length() / 16;
//		8字节DES计算，16字节3DES计算
		if (iden == 1) {
			CacuType = 1;
		} else {
			CacuType = 2;
		}
		for (int i = 0; i <= 7; i++) {
			K1Data[i] = Integer.parseInt(Key.substring(i*2, i*2+2), 16);
			if (CacuType == 2) {
				K2Data[i] = Integer.parseInt(Key.substring(i*2+16, i*2+16+2), 16);
			}
		}
		while (Data.length() % 2 != 0) {
			Data = Data + "0";
		}
		int DataLen = Data.length() / 2;
		int[] BData = new int[DataLen];
		DataLen = DataLen % 8;
		if (DataLen == 0) {
			BData = new int[BData.length+8];
			for (int i = 0; i <= 6; i++) {
				BData[BData.length-i-1] = 0;
			}
			BData[BData.length-1-7] = 0x80;
		} else if (DataLen == 7) {
			BData = new int[BData.length+1];
			BData[BData.length-1] = 0x80;
		} else {
			BData = new int[BData.length+1];
			BData[BData.length-1] = 0x80;
			BData = new int[BData.length+7-DataLen];
			for (int i = 0; i <= 7-DataLen-1; i++) {
				BData[BData.length-1-i] = 0;
			}
		}
		for (int i = 0; i <= Data.length()/2-1; i++) {
			BData[i] = Integer.parseInt(Data.substring(i*2, i*2+2), 16);
		}
		DataLen = BData.length / 8;
		for (int j = 0; j <= 7; j++) {
			XORData[j] = (IV[j] ^ BData[j]);
		}
		DES.EncryHex(XORData, K1Data, OData);
		for (int i = 1; i <= DataLen-1; i++) {
			for (int j = 0; j <= 7; j++) {
				XORData[j] = (OData[j] ^ BData[i*8+j]);
			}
			DES.EncryHex(XORData, K1Data, OData);
		}
		if (CacuType == 2) {
			///右半部分解密
			DES.DecryHex(OData, K2Data, XORData);
			///左半部分加密
			DES.EncryHex(XORData, K1Data, OData);
		}
		for (int i = 0; i <= 3; i++) {
			sbResult.append(intToHex(OData[i]));
		}
		return sbResult.toString();
	}
	
	public static String SMacHexStr(String Data, String Key) {
		int[] lKeyData = new int[8];
		int[] lSrcData = new int[8];
		int[] lMacData = new int[8];
		int[] lBufData = new int[100];

		while (Key.length() < 16) {
			Key = Key +'0';
		}

		int lIndex = 0;
		while (lIndex <= 7) {
		    lKeyData[lIndex] = Integer.parseInt(Key.substring(lIndex*2, lIndex*2+2), 16);
		    lIndex++;
		}

		for (lIndex = 0; lIndex <= Data.length() / 2 - 1; lIndex++) {
			lBufData[lIndex] = Integer.parseInt(Data.substring(lIndex*2, lIndex*2+2), 16);
		}

		lBufData[lIndex] = 0x80;

		lIndex++;
		while (lIndex < Data.length() / 2 + 8) {
		    lBufData[lIndex] = 0;
		    lIndex++;
		}

		int lDataLen = (Data.length() / 2) / 8 + 1;

		for (lIndex = 0; lIndex <= lDataLen - 1; lIndex++) {
			for (int lIndex2 = 0; lIndex2 <= 7; lIndex2++) {
				lSrcData[lIndex2] = (lMacData[lIndex2] ^ lBufData[lIndex*8+lIndex2]);
			}
			DES.EncryHex(lSrcData, lKeyData, lMacData);
		}
		StringBuffer sbResult = new StringBuffer();
		for (lIndex = 0; lIndex < lMacData.length; lIndex++) {
			sbResult.append(intToHex(lMacData[lIndex]));
		}
		return sbResult.toString();
	}
	public static void main(String[] args) {
		String src="DF4F2AE70013800";
		String key="5BD32C73465DEC6E9D676815A4E3D96E";
		String des = Encry3DESStrHex(src, key);
		System.out.println(des);
	}
}
