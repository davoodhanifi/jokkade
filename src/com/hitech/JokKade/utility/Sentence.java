package com.hitech.JokKade.utility;

public class Sentence {

	public int id;
	public int userId;
	public String tvName;
	public String tvZanbaasText;
	public String tvLike;
	//public String tag;
	public boolean isReaded;
	public boolean isLiked;
	public boolean isDeleted;
	public boolean isSynced;
	/*
	 * public int j; public int k;
	 */
	public int likeNumber;
	public int regDate;

	public void getLikeNumber() {
		likeNumber = -1;
	}

	public Sentence() {
	}

}
