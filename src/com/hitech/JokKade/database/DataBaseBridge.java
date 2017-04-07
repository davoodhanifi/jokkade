package com.hitech.JokKade.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;
import com.hitech.JokKade.utility.App;
import com.hitech.JokKade.utility.Sentence;
import com.hitech.JokKade.utility.User;
import com.hitech.JokKade.R;

public class DataBaseBridge extends SQLiteAssetHelper {

	private static final String DATABASE_NAME = "poems.db";
	private static final int DATABASE_VERSION = 2;

	// public static SQLiteDatabase db = null;
	public Cursor cursor;
	public static String databaseName = "poems.db";
	public static String databasePath = "";

	// Context context;
	// public File databaseFile;

	public DataBaseBridge(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	/*
	 * private void copyDataBase(String s, String s1) throws IOException {
	 * InputStream inputstream = context.getAssets().open(s); FileOutputStream
	 * fileoutputstream = new FileOutputStream(s1); byte abyte0[] = new
	 * byte[1024]; do { int i = inputstream.read(abyte0); if (i <= 0) {
	 * fileoutputstream.flush(); fileoutputstream.close(); inputstream.close();
	 * return; } fileoutputstream.write(abyte0, 0, i); } while (true); }
	 */
	public Cursor GetOfflineMessages(String[] position) {
		SQLiteDatabase db = getReadableDatabase();
		cursor = db
				.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isRead = 0 ORDER BY isRead,id DESC limit 25 offset ?",
						position);
		return cursor;
	}

	public void CloseDatabase() {
		// db.close();
		cursor.close();
	}

	public Cursor GetReadMessages(String[] position) {
		SQLiteDatabase db = getReadableDatabase();
		//db.rawQuery("DELETE FROM sentences WHERE userId = 3 OR userId = 2 OR userId = 1 OR userId = 4", null);
		cursor = db
				.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isRead = 1 ORDER BY isRead,id DESC limit 25 offset ?",
						position);
		// CloseDatabase();
		return cursor;
	}

	public Cursor GetLikedMessages(String[] position) {
		SQLiteDatabase db = getReadableDatabase();
		cursor = db.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isLike = 1 ORDER BY isRead,id DESC limit 25 offset ?",
						position);

		// CloseDatabase();
		return cursor;
	}

	public final void UpdateSentence(Sentence sentence) {
		int number = 1;
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		contentvalues.put("userId", sentence.id);
		contentvalues.put("nickname", sentence.tvName);
		int isReaded;
		int isDeleted;
		int isLiked;
		if (sentence.isReaded) {
			isReaded = number;
		} else {
			isReaded = 0;
		}
		contentvalues.put("isRead", Integer.valueOf(isReaded));
		if (sentence.isDeleted) {
			isDeleted = number;
		} else {
			isDeleted = 0;
		}
		contentvalues.put("isDeleted", Integer.valueOf(isDeleted));
		if (sentence.isLiked) {
			isLiked = number;
		} else {
			isLiked = 0;
		}
		contentvalues.put("isLike", Integer.valueOf(isLiked));
		if (!sentence.isSynced) {
			number = 0;
		}
		contentvalues.put("isSynced", Integer.valueOf(number));
		db.update("sentences", contentvalues, (new StringBuilder("id = "))
				.append(sentence.id).toString(), null);
		db.close();
	}

	private SQLiteDatabase OpenDatabase() {
		return SQLiteDatabase.openDatabase(databasePath, null, 0);
	}

	public User GetUser(String email, String password) {
		User user = new User();

		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM users WHERE "
				+ new StringBuilder(" email = '").append(email)
						.append("'  AND password = '").append(password)
						.append("'  AND isObsolete = 0 ");
		cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			user.email = cursor.getString(cursor.getColumnIndex("email"));
			user.password = cursor.getString(cursor.getColumnIndex("password"));
			user.id = cursor.getInt(cursor.getColumnIndex("id"));
			user.nickName = cursor.getString(cursor.getColumnIndex("nickName"));
			if (cursor.getInt(cursor.getColumnIndex("isObsolete")) == 0)
				user.isObsolete = false;
			else
				user.isObsolete = true;
		}
		return user;
	}

	public void InsertSentence(Sentence sentence) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
//		String query = "SELECT max(id) as id FROM sentences";
//		cursor = db.rawQuery(query, null);
//		if (cursor.moveToFirst())
//			contentvalues.put("id",cursor.getInt(cursor.getColumnIndex("id")) + 1);
//		else
	    contentvalues.put("id",sentence.id);
		contentvalues.put("userId", sentence.userId);
		contentvalues.put("nickname", sentence.tvName);
		contentvalues.put("isRead", sentence.isReaded);
		contentvalues.put("isDeleted", sentence.isDeleted);
		contentvalues.put("isLike", sentence.isLiked);
		contentvalues.put("isSynced", sentence.isSynced);
		//contentvalues.put("tag", sentence.tag);
		contentvalues.put("savedOn", sentence.regDate);
		contentvalues.put("text", sentence.tvZanbaasText);

		db.insert("sentences", null, contentvalues);
		db.close();

	}

	public Sentence GetSentenceById() {
		Sentence s = new Sentence();
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT * FROM sentences WHERE id = 2";
		cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			s.id = cursor.getInt(cursor.getColumnIndex("id"));
			s.tvName = cursor.getString(cursor.getColumnIndex("nickname"));
			s.tvZanbaasText = cursor.getString(cursor.getColumnIndex("text"));
		}
		db.close();
		cursor.close();
		return s;
	}

	public void InsertUser(User newUser) {

		SQLiteDatabase db = getWritableDatabase();
		ContentValues contentvalues = new ContentValues();
		String query = "SELECT max(id) as id FROM users";
		try {
			cursor = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (cursor.moveToFirst())
		{
			int k = cursor.getInt(cursor.getColumnIndex("id"));
			contentvalues.put("id",++k);
		}else
			contentvalues.put("id",1);
		contentvalues.put("email", newUser.email);
		contentvalues.put("password", newUser.password);
		contentvalues.put("nickName", newUser.nickName);
		contentvalues.put("isObsolete", newUser.isObsolete);

		try {
			db.insert("users", null, contentvalues);
			db.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		

	}

	public Cursor GetStaredSentences(String[] position) {
		SQLiteDatabase db = getReadableDatabase();
		cursor = db.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isLike = 1 ORDER BY id DESC limit 25 offset ?",
						position);
		// CloseDatabase();
		return cursor;
	}
	
	public Cursor GetUnreadedSentences(String[] position,int count) {
		SQLiteDatabase db = getReadableDatabase();
		 cursor = db.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isRead = 0 ORDER BY id DESC limit "+ count +" offset ?",
						position);
		
		// CloseDatabase();
		return cursor;
	}
	
	public Cursor GetReadedSentences(String[] position) {
		SQLiteDatabase db = getReadableDatabase();
		cursor = db.rawQuery(
						"SELECT * FROM sentences WHERE isDeleted = 0 AND isRead = 1 ORDER BY id DESC limit 25 offset ?",
						position);
		// CloseDatabase();
		return cursor;
	}
	
	public int GetMaxId() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT max(id) as id  FROM sentences";
		try {
			cursor = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (cursor.moveToFirst())
		{
			int k = cursor.getInt(cursor.getColumnIndex("id"));
			return k;
		}
		return 0;
		
	}

	public String GetKey() {
		SQLiteDatabase db = getReadableDatabase();
		String query = "SELECT key as key FROM Application where id=1;";
		try {
			cursor = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if (cursor.moveToFirst())
		{
			String k = cursor.getString(cursor.getColumnIndex("key"));
			return k;
		}
		return "";
	}

	public void UpdateApplication(boolean mIsPremium,String token) {
		SQLiteDatabase db = getWritableDatabase();
		String query = "UPDATE Application SET isPermium='"+ mIsPremium +"',token='" + token +"' WHERE id=1;";
		try {
			cursor = db.rawQuery(query, null);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}