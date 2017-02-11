package com.example.laibomusic.util;

public class TimeUtil {
	
	
	public static String getTime(long time) {
		String str = "";
		time   = time / 1000;
		int s = (int) (time % 60);
		
		int m = (int) (time / 60 % 60);
		if (s / 10 == 0)
			str = m + ":0" + s;
		else
			str = m + ":" + s;
		return str;
	}
	
	
	
}
