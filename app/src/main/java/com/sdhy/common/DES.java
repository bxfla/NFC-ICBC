package com.sdhy.common;

public class DES {
	private static final int arrBitCP[] = { // 逆初始置IP-1
		39,  7, 47, 15, 55, 23, 63, 31,
		38,  6, 46, 14, 54, 22, 62, 30,
		37,  5, 45, 13, 53, 21, 61, 29,
		36,  4, 44, 12, 52, 20, 60, 28,
		35,  3, 43, 11, 51, 19, 59, 27,
		34,  2, 42, 10, 50, 18, 58, 26,
		33,  1, 41,  9, 49, 17, 57, 25,
		32,  0, 40,  8, 48, 16, 56, 24 };
	
	private static final int arrBitPM[] = { // 置换运算P
		15, 6, 19, 20, 28, 11, 27, 16, 0, 14, 22, 25, 4, 17, 30, 9,
		1, 7, 23, 13, 31, 26, 2, 8, 18, 12, 29, 5, 21, 10, 3, 24 };
	
	private static final int arrBitExp[] = { // 位选择函数E
		31, 0, 1, 2, 3, 4, 3, 4, 5, 6, 7, 8, 7, 8, 9, 10,
		11, 12, 11, 12, 13, 14, 15, 16, 15, 16, 17, 18, 19, 20, 19, 20,
		21, 22, 23, 24, 23, 24, 25, 26, 27, 28, 27, 28, 29, 30, 31, 0};
	
	private static final int arrBitPMC1[] = { //选择置换PC-1
		  56, 48, 40, 32, 24, 16,  8,
	       0, 57, 49, 41, 33, 25, 17,
	       9,  1, 58, 50, 42, 34, 26,
	      18, 10,  2, 59, 51, 43, 35,
	      62, 54, 46, 38, 30, 22, 14,
	       6, 61, 53, 45, 37, 29, 21,
	      13,  5, 60, 52, 44, 36, 28,
	      20, 12,  4, 27, 19, 11,  3};

	private static final int arrBitPMC2[] = { // 选择置换PC-2
		  13, 16, 10, 23,  0,  4,
	       2, 27, 14,  5, 20,  9,
	      22, 18, 11,  3, 25,  7,
	      15,  6, 26, 19, 12,  1,
	      40, 51, 30, 36, 46, 54,
	      29, 39, 50, 44, 32, 47,
	      43, 48, 38, 55, 33, 52,
	      45, 41, 49, 35, 28, 31};

	private static final int arrBitIP[] = { // 初始值置IP
		 57, 49, 41, 33, 25, 17,  9,  1,
	     59, 51, 43, 35, 27, 19, 11,  3,
	     61, 53, 45, 37, 29, 21, 13,  5,
	     63, 55, 47, 39, 31, 23, 15,  7,
	     56, 48, 40, 32, 24, 16,  8,  0,
	     58, 50, 42, 34, 26, 18, 10,  2,
	     60, 52, 44, 36, 28, 20, 12,  4,
	     62, 54, 46, 38, 30, 22, 14,  6 };

	private static final int arrsBox[][] = { // 8个S盒
			{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7, 0, 15, 7,
					4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8, 4, 1, 14, 8,
					13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0, 15, 12, 8, 2, 4,
					9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 },
			{ 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10, 3, 13, 4,
					7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5, 0, 14, 7, 11,
					10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15, 13, 8, 10, 1, 3,
					15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 },
			{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8, 13, 7, 0,
					9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1, 13, 6, 4, 9, 8,
					15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7, 1, 10, 13, 0, 6, 9,
					8, 7, 4, 15, 14, 3, 11, 5, 2, 12 },
			{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15, 13, 8, 11,
					5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9, 10, 6, 9, 0, 12,
					11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4, 3, 15, 0, 6, 10, 1,
					13, 8, 9, 4, 5, 11, 12, 7, 2, 14 },
			{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9, 14, 11, 2,
					12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6, 4, 2, 1, 11, 10,
					13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14, 11, 8, 12, 7, 1, 14,
					2, 13, 6, 15, 0, 9, 10, 4, 5, 3 },
			{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11, 10, 15, 4,
					2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8, 9, 14, 15, 5, 2,
					8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6, 4, 3, 2, 12, 9, 5, 15,
					10, 11, 14, 1, 7, 6, 0, 8, 13 },
			{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1, 13, 0, 11,
					7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6, 1, 4, 11, 13,
					12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2, 6, 11, 13, 8, 1, 4,
					10, 7, 9, 5, 0, 15, 14, 2, 3, 12 },
			{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7, 1, 15, 13,
					8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2, 7, 11, 4, 1, 9,
					12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8, 2, 1, 14, 7, 4, 10,
					8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
	
	private static int si(int s, int inByte) {
		int c = (inByte & 0x20) | ((inByte & 0x1e) >> 1) | ((inByte & 0x01) << 4);
		return (arrsBox[s][c] & 0x0f);
	}
	private static void permutation(int[] inData) {
		int[] newData = new int[4];
		for (int i = 0; i <= 31; i++) {
			if ((inData[arrBitPM[i]>>3] & (1 << (7 - (arrBitPM[i] & 0x07)))) != 0) {
				newData[i>>3] = ((newData[i>>3]) | (1 << (7 - (i & 0x07))));
			}
		}
		for (int i = 0; i <= 3; i++) {
			inData[i] = newData[i];
		}
	}
	private static void expand(int[] inData, int[] outData) {
		// FillChar(outData, 6, 0);
		for (int i = 0; i < outData.length; i++) {
			outData[i] = 0;
		}
		for (int i = 0; i <= 47; i++) {
			if ((inData[arrBitExp[i]>>3] & (1 << (7 - (arrBitExp[i] & 0x07)))) != 0) {
				outData[i>>3] = ((outData[i>>3]) | (1 << (7 - (i & 0x07))));
			}
		}
	}
	private static void initPermutation(int[] inData) {
		int[] newData = new int[8];
		for (int i = 0; i <= 63; i++) {
			if ((inData[arrBitIP[i]>>3] & (1 << (7 - (arrBitIP[i] & 0x07)))) != 0) {
				newData[i>>3] = ((newData[i>>3]) | (1 << (7 - (i & 0x07))));
			}
		}
		for (int i = 0; i <= 7; i++) {
			inData[i] = newData[i];
		}
	}
	private static void conversePermutation(int[] inData) {
		int[] newData = new int[8];
		for (int i = 0; i <= 63; i++) {
			if ((inData[arrBitCP[i]>>3] & (1 << (7 - (arrBitCP[i] & 0x07)))) != 0) {
				newData[i>>3] = ((newData[i>>3]) | (1 << (7 - (i & 0x07))));
			}
		}
		for (int i = 0; i <= 7; i++) {
			inData[i] = newData[i];
		}
	}
	private static void permutationChoose1(int[] inData, int[] outData) {
		// FillChar(outData, 7, 0)
		for (int i = 0; i < outData.length; i++) {
			outData[i] = 0;
		}
		for (int i = 0; i <= 55; i++) {
			if ((inData[arrBitPMC1[i]>>3] & (1 << (7 - (arrBitPMC1[i] & 0x07)))) != 0) {
				outData[i>>3] = (outData[i>>3] | (1 << (7 - (i & 0x07))));
			}
		}
	}
//	TODO 返回值
	private static void permutationChoose2(int[] inData, int[] outData) {
		// FillChar(outData, 6, 0)
		for (int i = 0; i <= 47; i++) {
			if ((inData[arrBitPMC2[i]>>3] & (1 << (7 - (arrBitPMC2[i] & 0x07)))) != 0) {
				outData[i>>3] = (outData[i>>3] | (1 << (7 - (i & 0x07))));
			}
		}
	}
	private static void cycleMove(int[] inData, int bitMove) {
		for (int i = 0; i <= bitMove-1; i++) {
			inData[0] = ((inData[0] << 1) | (inData[1] >> 7));
			inData[1] = ((inData[1] << 1) | (inData[2] >> 7));
			inData[2] = ((inData[2] << 1) | (inData[3] >> 7));
			inData[3] = ((inData[3] << 1) | ((inData[0] & 0x10) >> 4));
			inData[0] = (inData[0] & 0x0f);
		}
	}
	public static void EncryHex(int[] PData, int[] KeyData, int[] EData) {
		int[][] subKey = new int[16][6];
		int[] StrByte = new int[8];
		int[] OutByte = new int[8];
		makeKey(KeyData, subKey);
		for (int i = 0; i <= (PData.length/8 - 1); i++) {
			int m = 0;
			System.arraycopy(PData, i*8, StrByte, 0, 8);
			desData(0, StrByte, OutByte, subKey); //0加密1解密
			for (int j = i * 8; j < (i+1)*8; j++) {
				EData[j] = OutByte[m];
				m++;
			}
		}
	}
	public static void DecryHex(int[] PData, int[] KeyData, int[] EData) {
		int[][] subKey = new int[16][6];
		int[] StrByte = new int[8];
		int[] OutByte = new int[8];
		makeKey(KeyData, subKey);
		int m = 0;
		for (int i = 0; i <= (PData.length/8 - 1); i++) {
			m = 0;
			System.arraycopy(PData, i*8, StrByte, 0, 8);
			desData(1, StrByte, OutByte, subKey); //0加密1解密
			for (int j = i * 8; j < (i+1)*8; j++) {
				EData[j] = OutByte[m];
				m++;
			}
		}

	}
//	TODO 返回值
	private static void makeKey(int[] inKey, int[][] outKey) {
		int[] bitDisplace = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
		int[] outData56 = new int[7];
		int[] key28l = new int[4];
		int[] key28r = new int[4];
		int[] key56o = new int[7];
		permutationChoose1(inKey, outData56);
		key28l[0] = (outData56[0] >> 4);
		key28l[1] = ((outData56[0] << 4) | (outData56[1] >> 4));
		key28l[2] = ((outData56[1] << 4) | (outData56[2] >> 4));
		key28l[3] = ((outData56[2] << 4) | (outData56[3] >> 4));
		key28r[0] = (outData56[3] & 0x0f);
		key28r[1] = outData56[4];
		key28r[2] = outData56[5];
		key28r[3] = outData56[6];
		for (int i = 0; i <= 15; i++) {
			cycleMove(key28l, bitDisplace[i]);
			cycleMove(key28r, bitDisplace[i]);
			key56o[0] = ((key28l[0] << 4) | (key28l[1] >> 4));
			key56o[1] = ((key28l[1] << 4) | (key28l[2] >> 4));
			key56o[2] = ((key28l[2] << 4) | (key28l[3] >> 4));
			key56o[3] = ((key28l[3] << 4) | key28r[0]);
			key56o[4] = key28r[1];
			key56o[5] = key28r[2];
			key56o[6] = key28r[3];
			permutationChoose2(key56o, outKey[i]);
		}
	}
	private static void encry(int[] inData, int[] subKey, int[] outData) {
		int[] buf = new int[8];
		int[] outBuf = new int[6];
		expand(inData, outBuf);
		for (int i = 0; i <= 5; i++) {
			outBuf[i] = (outBuf[i] ^ subKey[i]);
		}
		buf[0] = (outBuf[0] >> 2);
		buf[1] = (((outBuf[0] & 0x03) << 4) | (outBuf[1] >> 4));
		buf[2] = (((outBuf[1] & 0x0f) << 2) | (outBuf[2] >> 6));
		buf[3] = (outBuf[2] & 0x3f);
		buf[4] = (outBuf[3] >> 2);
		buf[5] = (((outBuf[3] & 0x03) << 4) | (outBuf[4] >> 4));
		buf[6] = (((outBuf[4] & 0x0f) << 2) | (outBuf[5] >> 6));
		buf[7] = (outBuf[5] & 0x3f);
		for (int i = 0; i <= 7; i++) {
			buf[i] = si(i, buf[i]);
		}
		for (int i = 0; i <= 3; i++) {
			outBuf[i] = ((buf[i*2] << 4) | buf[i*2+1]);
		}
		permutation(outBuf);
		for (int i = 0; i <= 3; i++) {
			outData[i] = outBuf[i];
		}
	}
	private static void desData(int desMode, int[] inData, int[] outData, int[][] subKey) {
		// inData, outData 都为8Bytes，否则出错
		int[] buf = new int[4];
		int[] temp = new int[4];
		for (int i = 0; i <= 7; i++) {
			outData[i] = inData[i];
		}
		initPermutation(outData);
		if (desMode == 0) {
			for (int i = 0; i <= 15; i++) {
				for (int j = 0; j <= 3; j++) {
					temp[j] = outData[j];
				}
				for (int j = 0; j <= 3; j++) {
					outData[j] = outData[j+4];
				}
				encry(outData, subKey[i], buf);
				for (int j = 0; j <= 3; j++) {
					outData[j+4] = (byte) (temp[j] ^ buf[j]);
				}
			}
			for (int j = 0; j <= 3; j++) {
				temp[j] = outData[j+4];
			}
			for (int j = 0; j <= 3; j++) {
				outData[j+4] = outData[j];
			}
			for (int j = 0; j <= 3; j++) {
				outData[j] = temp[j];
			}
		} else {
			for (int i = 15; i >= 0; i--) {
				for (int j = 0; j <= 3; j++) {
					temp[j] = outData[j];
				}
				for (int j = 0; j <= 3; j++) {
					outData[j] = outData[j+4];
				}
				encry(outData, subKey[i], buf);
				for (int j = 0; j <= 3; j++) {
					outData[j+4] = (byte) (temp[j] ^ buf[j]);
				}
			}
			for (int j = 0; j <= 3; j++) {
				temp[j] = outData[j+4];
			}
			for (int j = 0; j <= 3; j++) {
				outData[j+4] = outData[j];
			}
			for (int j = 0; j <= 3; j++) {
				outData[j] = temp[j];
			}
		}
		conversePermutation(outData);
	}
}
