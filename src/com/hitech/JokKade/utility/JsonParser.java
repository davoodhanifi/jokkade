package com.hitech.JokKade.utility;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class JsonParser {

	public User UserParser(String jsonEntry)
	{
		User user = new User();
		try {
			JSONObject obj = new JSONObject(jsonEntry);
			user.email = obj.getString("email");
			user.id = obj.getInt("id");
			user.password= obj.getString("password");
			user.nickName = obj.getString("nickName");
			return user;			
		} catch (JSONException e) {
			return null;
		}	
	}
	
	public Pair PairParser(String jsonEntry)
	{
		Pair pair = new Pair();
		try {
			JSONObject obj = new JSONObject(jsonEntry);
			pair.Title = obj.getString("Title");
			pair.Value = obj.getString("Value");
			return pair;			
		} catch (JSONException e) {
			return null;
		}	
	}
	public List<Sentence> SentencesParser(String jsonEntry)
	{
		try{
			List<Sentence> sentences = new ArrayList<Sentence>();
		  	JSONArray array = new JSONArray(jsonEntry);
			
			for(int i = 0;i< array.length();i++){
				 Sentence sentence = new Sentence();
				 JSONObject obj = array.getJSONObject(i);
				 
				 sentence.id = obj.getInt("id");
				 sentence.userId = obj.getInt("userId");
				 sentence.tvName = obj.getString("nickname");
				 sentence.tvZanbaasText = obj.getString("text");
				 //sentence.tag = obj.getString("category");
				 sentence.isDeleted = false;
				 sentence.isLiked = false;
				 sentence.isReaded = false;
				 sentence.isSynced = true;
				 sentence.regDate = 0;
				 
				 sentences.add(sentence);
			}
			return sentences;			
		} catch (JSONException e) {
			return null;
		}	
	}
}
