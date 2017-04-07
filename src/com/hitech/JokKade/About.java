package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hitech.JokKade.Login.AsyncCallWS;
import com.hitech.JokKade.utility.JsonParser;
import com.hitech.JokKade.utility.Pair;
import com.hitech.JokKade.utility.Parameter;
import com.hitech.JokKade.utility.TextViewPlus;
import com.hitech.JokKade.utility.User;
import com.hitech.JokKade.utility.UserSessionManager;
import com.hitech.JokKade.utility.WIFIInternetConnectionDetector;
import com.hitech.JokKade.utility.WebService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class About extends ActionBarActivity {

	private WebView webViewAbout;
	private Context mContext;
	private Boolean isConnectionExist = false;
	private View noInternet;
	private WIFIInternetConnectionDetector cd;
	private ProgressDialog progressDialog;
	private static Typeface face;

	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void forceRTLIfSupported() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			getWindow().getDecorView().setLayoutDirection(
					View.LAYOUT_DIRECTION_LTR);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			forceRTLIfSupported();
			setContentView(R.layout.activity_about);
			mContext = this;
			webViewAbout = (WebView) findViewById(R.id.aboutContent);
			// webViewAbout.getSettings().setJavaScriptEnabled(true);
			String tag = getIntent().getStringExtra("queryString");
			if (tag.equals("about")) {
				setTitle("دربــاره مـا");
				getSupportActionBar().setIcon(R.drawable.ic_info);
				try {
					webViewAbout.loadUrl("file:///android_asset/about.htm");
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (tag.equals("rules")) {
				setTitle("قـــوانین");
				getSupportActionBar().setIcon(R.drawable.law);
				try {
					webViewAbout.loadUrl("file:///android_asset/rules.htm");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void setTitle(CharSequence title) {
		try {
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayShowTitleEnabled(false);

			LayoutInflater inflator = LayoutInflater.from(mContext);
			View v = inflator.inflate(R.layout.titleview, null);

			((TextViewPlus) v.findViewById(R.id.title)).setText(title);
			getSupportActionBar().setCustomView(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		try {
			getMenuInflater().inflate(R.menu.activity_main, menu);
			menu.removeItem(R.id.action_addSentence);
			menu.removeItem(R.id.action_update);
			// if(!session.isUserLoggedIn())
			menu.removeItem(R.id.action_logout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar actions click
		switch (item.getItemId()) {
		case android.R.id.home:
			try {
				this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
