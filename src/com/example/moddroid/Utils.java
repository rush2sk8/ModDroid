package com.example.moddroid;

public class Utils {

	//converts data
	public static float hexToFloat(String hex) {
		return Float.intBitsToFloat(new Long(Long.parseLong(hex,16)).intValue());
	}
	
	public static String floatToHex(float val) {
		return Float.toHexString(val);
	}
	
}
