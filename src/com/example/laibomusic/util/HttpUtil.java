package com.example.laibomusic.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;
import android.widget.Toast;

/**
 * @author 王康
 *from happy mucic project
 */
public class HttpUtil {


	private static final int REQUEST_TIME = 20 * 1000;
	private static DefaultHttpClient httpclent  = new DefaultHttpClient();

	private static final int SO_TIMEOUT = 20 * 1000;

	private static final String geciurl = "http://geci.me/api/lyric/";



	public static String getArtistAlbumn(String artist){
		if(artist == null || artist.equals("")){
			Log.d("TAG","getArtistAlbumn 里面artist参数为null ");
			return null;
		}

		if (artist.contains("<") || artist.contains(">")) {
			artist = artist.replaceAll("<", "");
			artist = artist.replaceAll(">", "");
		}
		String url = "http://image.haosou.com/j?q=" + artist
				+ "&src=srp&query_tag=壁纸&sn=30&pn=30";
//          url = url.replaceAll("&", "%26");
		url = url.replaceAll(" ", "%20");
		//这两句话我加的，处理字符串中有问题的url的问题
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		String temp = getResponseText(url, params);
		return temp;
	}

	private static String getResponseText(String url , List<NameValuePair> params){

		int flag = -1;

		String result = "{";

		HttpPost httppost  = null;

		try {

			httppost = new HttpPost(url);
			httppost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));


			httpclent.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIME);
			httpclent.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			HttpResponse httpresponse = httpclent.execute(httppost);
			flag = httpresponse.getStatusLine().getStatusCode();
			if(flag == 200){
				HttpEntity entity = httpresponse.getEntity();
				result = result + "\"comment\":"
						+ EntityUtils.toString(entity, HTTP.UTF_8) + ",";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e ){
			Log.d("TAG",e.getMessage()+" 报错的");
		}finally{
			if(httppost != null) httppost .abort();
		}
		return result + "\"flag\":" + flag + "}";
	}

	public static String getGECIjson(String songname , List<NameValuePair> params){
		int flag = -1;
		String result = "";
		if(songname == null || songname.length() <= 0)
			return null;
		if(params == null) params = new ArrayList<NameValuePair>();
		HttpPost httpost = null;
		try{
			String url = geciurl + songname.trim();
			httpost = new HttpPost(url);
			httpost.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			httpclent.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, REQUEST_TIME);
			httpclent.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
			HttpResponse httpresponse = httpclent.execute(httpost);
			flag = httpresponse.getStatusLine().getStatusCode();
			if(flag == 200){
				HttpEntity entity = httpresponse.getEntity();
				result = EntityUtils.toString(entity, HTTP.UTF_8);
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 从指定的URL中获取数组
	 *
	 * @param urlPath
	 * @return
	 * @throws Exception
	 */
	public static String readParse(String urlPath) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		URL url = new URL(urlPath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		InputStream inStream = conn.getInputStream();
		while ((len = inStream.read(data)) != -1) {
			outStream.write(data, 0, len);
		}
		inStream.close();
		return new String(outStream.toByteArray());// 通过out.Stream.toByteArray获取到写的数据
	}

}
