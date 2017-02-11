package com.example.laibomusic.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import android.util.Log;

public class LRCUtils {

	static  boolean isLRCExist = false;
	public static Vector<Timelrc>  lrclist  ;
     
	public static File putLRC(String result , String path) {
		byte[] bytes = result.getBytes();
		BufferedOutputStream  stream = null;
		File file = null;
		file = new File(path); 
        try {
			FileOutputStream fileoutputsteam  = new FileOutputStream(file);
			stream = new BufferedOutputStream(fileoutputsteam);
			stream.write(bytes);
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
        finally{
        	try {
        		if(stream != null)
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
       return file;		
	}

	/**
	 *  if(	Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
	 * 去从本地读取解析lrc文件
	 * @param filePath
	 */
	public static boolean getLRC(String filePath) {
	
		clearLrcList();
		File file = new File(filePath);
        if(!file.exists()){
        	return false;
        }
        StringBuffer sb = new StringBuffer();
        char[] buffer = new char[128];
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			while (reader.read(buffer) != -1) {
			    sb.append(new String(buffer));
			    buffer = new char[1024];
			    Log.d("TAG","buffer char[1024]  又读取了一篇");
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		Log.d("TAG","解析过来的歌词：" +  sb.toString());
		
 		if (sb == null || sb.length() <= 0)
 			return false;
 		else {
			AnalyzeLRC(sb.toString());
			return true;
 		}
	}

	public static void clearLrcList() {
		if(lrclist != null){
			lrclist.clear();
			lrclist = new Vector<Timelrc>();
		}
	}

	/**
	 * 解析lrc歌词字符串 
	 * @param lrc
	 * @return
	 */
	public static  String AnalyzeLRC(String lrc){
		
		lrclist = new Vector<Timelrc>();
		int left = lrc.indexOf("[");
		int right = lrc.indexOf("]");
		
//		Long time[] = new Long[GetTotalTags(lrc)];
		long time ;
		int i = 0;
	    while(left!= -1 || right != -1){
	    	time = TimeToLong(lrc.substring(left + 1,right));
	    	
			if (time == -1) {
				lrc = lrc.substring(right + 1);
				left = lrc.indexOf("[");
				right = lrc.indexOf("]");
				continue;
			}
	    	lrc = lrc.substring(right + 1);
 	    	    	
			final long tp = time;
			String lrcstr =  lrc.substring( 0,lrc.indexOf("\n"));
			if(lrcstr.contains("[")&& lrcstr.contains("]")){    //如果一行报文中有两个时间
				
				int left2 = lrcstr.indexOf("[");
				int right2 = lrcstr.indexOf("]");
				String newtime = lrcstr.substring(left2 + 1 , right2 );
	  			long timepoint  = TimeToLong(newtime);
				lrcstr = lrcstr.substring(right2 + 1);
				Timelrc t2 = new Timelrc();
				t2.timePoint = timepoint;
				t2.lrcstr = lrcstr;
				lrclist.add(t2);
				Timelrc t1 = new Timelrc();
				t1.timePoint = tp;
				t1.lrcstr = lrcstr;
				lrclist.add(t1);
				
			}else{                                               //如果只有一个时间
				Timelrc t1 = new Timelrc()	;
				t1.timePoint = tp;
				t1.lrcstr = lrcstr;
				lrclist.add(t1);
			}
            lrc = lrc.substring(lrc.indexOf("\n"));
			left= lrc.indexOf("[");
			right = lrc.indexOf("]");
	    	i++;
	    }
	    
	    //重新排列
	    Collections.sort(lrclist, new Comparator() {

            public int compare(Object left, Object right) {
            	Timelrc l = (Timelrc)left;
            	Timelrc r = (Timelrc)right;
              if(  l.getTimePoint() > r.getTimePoint()) 
            	  return 1;
              else return -1;
                
            }
        });
	    
	    lrclist.clone();
	    return "";
	}
	
	 public static  long TimeToLong(String Time)  
     {  
         try  
         {  
             String[] s1 = Time.split(":");  
             int min = Integer.parseInt(s1[0]);  
             String[] s2 = s1[1].split("\\.");  
             int sec = Integer.parseInt(s2[0]);  
             int mill = 0;  
             if (s2.length > 1)   
                 mill = Integer.parseInt(s2[1]);  
             return min * 60 * 1000 + sec * 1000 + mill * 10;  
         }  
         catch (Exception e)  
         {  
             return -1;  
         }  
     }  
 
	/**
	 * 根据时间得到那句歌词
	 * @param progress
	 */
	public static String getLRCStr(int progress) {
		if(lrclist.size() <= 0 ) return null;
		for(int i = 0;i < lrclist.size() - 1;i ++){
			if(progress > lrclist.get(i).timePoint && progress< lrclist.get(i+1).timePoint && !lrclist.get(i).isShow()   ){
				lrclist.get(i).isShow = true;
				Log.d("TAG",lrclist.get(i).isShow()+" is the is shown");
				return lrclist.get(i).lrcstr;
			}
		}
		return null;
	}

	/**
	 * 根据时间得到那句歌词
	 * TO-DO  解决
	 * @param progress
	 */
	public static int getLRCIndex(int progress) {
		if(lrclist.size() <= 0 ) return 0;
		for(int i = 0;i < lrclist.size() - 1;i ++){
			if(progress > lrclist.get(i).timePoint && progress< lrclist.get(i+1).timePoint && !lrclist.get(i).isShow()   ){
				lrclist.get(i).isShow = true;
				Log.d("TAG",lrclist.get(i).isShow()+" is the is shown");
				return i;
			}
		}
		return 0;
	}
	
	 
//	private static int GetTotalTags(String lrcstr) {
//		//TO-DO
//		String copy = new String(lrcstr);
//		String srcCount1[] = lrcstr.split("[");
//		String srcCount2[] = copy.split("[");
//		if (srcCount1.length <= 0 && srcCount2.length <= 0)
//			return 1;
//		else
//			return (srcCount1.length > srcCount2.length) ? srcCount1.length
//					: srcCount2.length;
//	}
	
	private static int GetTotalTags(String lrcstr) {
		String srcCount1[] = lrcstr.trim().split("\\[");
		return srcCount1.length;
	}
	  /**
	   * 从本地去读文件
	 * @param f
	 */
	public void ReadLRC(File f) {
		try {
			if (!f.exists()) {
				Log.d("TAG", "not exit the lrc file");
				isLRCExist = false;
				// strLRC =
				// main.getResources().getString(R.string.lrcservice_no_lyric_found);
			} else {
				lrclist = new Vector<Timelrc>();
				isLRCExist = true;
				InputStream is = new BufferedInputStream(new FileInputStream(f));
				BufferedReader br = new BufferedReader(new InputStreamReader(
						is, GetCharset(f)));
				String strTemp = "";
				while ((strTemp = br.readLine()) != null) {
					// Log.d(TAG,"strTemp = "+strTemp);
					strTemp = AnalyzeLRC(strTemp); //
				}
				br.close();
				is.close();
				//Collections.sort(lrclist, new Sort());

				for (int i = 0; i < lrclist.size(); i++) {
					Log.d("TAG", "time = " + lrclist.get(i).getTimePoint()
							+ "   string = " + lrclist.get(i).toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	
	
	
	private class Sort implements Comparator<Timelrc> {

		@Override
		public int compare(Timelrc lhs, Timelrc rhs) {
			return 0;
		}
		
	}
	
	   /**
	 * @param file
	 * 判断编码格式
	 * @return
	 */
	public String GetCharset(File file){  
	         String charset = "GBK";  
	         byte[] first3Bytes = new byte[3];  
	         try  
	         {  
	             boolean checked = false;  
	             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));  
	             bis.mark(0);  
	             int read = bis.read(first3Bytes, 0, 3);  
	             if (read == -1)  
	                 return charset;  
	             if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE)  
	             {  
	                 charset = "UTF-16LE";  
	                 checked = true;  
	             }  
	             else if (first3Bytes[0] == (byte) 0xFE && first3Bytes[1] == (byte) 0xFF)  
	             {  
	                 charset = "UTF-16BE";  
	                 checked = true;  
	             }  
	             else if (first3Bytes[0] == (byte) 0xEF && first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF)  
	             {  
	                 charset = "UTF-8";  
	                 checked = true;  
	             }  
	             bis.reset();  
	             if (!checked)  
	             {  
	                 int loc = 0;  
	                 while ((read = bis.read()) != -1)  
	                 {  
	                     loc++;  
	                     if (read >= 0xF0)  
	                         break;  
	                     if (0x80 <= read && read <= 0xBF) // 
	                         break;  
	                     if (0xC0 <= read && read <= 0xDF)  
	                     {  
	                         read = bis.read();  
	                         if (0x80 <= read && read <= 0xBF) //  
	                             continue;  
	                         else  
	                             break;  
	                     }  
	                     else if (0xE0 <= read && read <= 0xEF)  
	                     { 
	                         read = bis.read();  
	                         if (0x80 <= read && read <= 0xBF)  
	                         {  
	                             read = bis.read();  
	                             if (0x80 <= read && read <= 0xBF)  
	                             {  
	                                 charset = "UTF-8";  
	                                 break;  
	                             }  
	                             else  
	                                 break;  
	                         }  
	                         else  
	                             break;  
	                     }  
	                 }  
	             }  
	             bis.close();  
	         }  
	         catch (Exception e)  
	         {  
	             e.printStackTrace();  
	         }  
	         return charset;  
	     }  
	
    public static void main(String[] args)
    {   lrclist = new Vector<Timelrc>();
    	String result = AnalyzeLRC("[01:40.00][00:16.00]今天我寒夜里看雪飘过 \n[01:48.00][00:24.00]怀著冷却了的心窝飘远方 \n[01:53.00][00:29.00]风雨里追赶 \n[01:57.00][00:33.00]雾里分不清影踪 \n[02:00.00][00:36.00]天空海阔你与我 \n[02:03.00][00:39.00]可会变(谁没在变)111\n");
    	System.out.println(result);
    	for(Timelrc t : lrclist)System.out.println(t.toString() + "\n");
    }	
	
	
	public static class Timelrc {
		
		private String lrcstr ;
		private long timePoint;
		private boolean isShow = false;
		
		public Timelrc(){
			lrcstr = null;
			timePoint = 0;
			isShow = false;
		}

		public String getLrcstr() {
			return lrcstr;
		}

		public void setLrcstr(String lrcstr) {
			this.lrcstr = lrcstr;
		}

		public boolean isShow() {
			return isShow;
		}

		public void setShow(boolean isShow) {
			this.isShow = isShow;
		}

		public long getTimePoint() {
			return timePoint;
		}

		public void setTimePoint(int timePoint) {
			this.timePoint = timePoint;
		}

		@Override
		public String toString() {
			return  "timePoint:" + timePoint + "lrcstr:"+ lrcstr;
		}
			
	
	}
	
	
}
