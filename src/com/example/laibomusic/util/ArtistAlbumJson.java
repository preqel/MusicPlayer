package com.example.laibomusic.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * @author 王康
 *  解析json格式的对象
 */
public class ArtistAlbumJson {

	protected static final int MAX_ARRAY = 4;


	public static List<String> parseMusicText(String artisname,String responseText	) throws JSONException, InterruptedException{
		List<String> result = null;
		if(responseText == null || responseText.equals("")) return null;
		JSONObject jsonobject = new JSONObject(responseText);
		if(jsonobject.has("flag")){
			int flag = jsonobject.getInt("flag");
			switch (flag){
				case 200:
					if(jsonobject.has("comment")){
						String json = jsonobject.getString("comment");
						JSONObject jsoncomment = new JSONObject(json);
						if(jsoncomment.has("list")){
							JSONArray jsonarray = jsoncomment.getJSONArray("list");
							int jlength = jsonarray.length();
							if(jlength <= 0) return null;
							///*
							if(jlength > MAX_ARRAY ){
								jlength  = MAX_ARRAY;
							}
							//*/
							String thumb_bak = "";
							result = new ArrayList<String>();
							for(int i = 0 ;i < jlength ;i++){
								JSONObject object = jsonarray.getJSONObject(i);
								if(object.has("thumb_bak")){
									thumb_bak = object.getString("thumb_bak");
									Log.d("TAG","parse successfully the jsondata -->thumb_bak:" + thumb_bak);
									result.add(thumb_bak);
									Thread.sleep(1000);
								}
							}
						}
					}
				case 404 :
					break;
				default:
					break;
			}
		}
		return result;
	}

	public static List<String> parseLRCText(String responseText) {

		List<String> result = new ArrayList<String>();
		if(responseText == null || responseText.equals(""))
			return null;
		try {
			JSONObject objectjson = new JSONObject(responseText);
			int count = objectjson.getInt("count");
			if(count <= 0 )  return result;
			JSONArray  resultjsons  = objectjson.getJSONArray("result");
			for(int i = 0;i<resultjsons.length(); i++){
				JSONObject  json = (JSONObject) resultjsons.get(i);
				String url  = json.getString("lrc");
				result.add(url);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;

	}
}
