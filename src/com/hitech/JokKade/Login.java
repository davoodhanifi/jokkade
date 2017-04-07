package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.List;

import org.kobjects.util.Util;

import com.hitech.JokKade.Dashboard.AsyncCallWS;
import com.hitech.JokKade.database.DataBaseBridge;
import com.hitech.JokKade.utility.App;
import com.hitech.JokKade.utility.JsonParser;
import com.hitech.JokKade.utility.Parameter;
import com.hitech.JokKade.utility.TextViewPlus;
import com.hitech.JokKade.utility.User;
import com.hitech.JokKade.utility.UserSessionManager;
import com.hitech.JokKade.utility.WIFIInternetConnectionDetector;
import com.hitech.JokKade.utility.WebService;
import com.hitech.JokKade.R;

import android.R.string;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.sax.RootElement;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Login extends ActionBarActivity implements
		android.view.View.OnClickListener {

	private Button btnLogin;
	private Button btnRegsiter;
	private TextView txtLoginErr;
	private TextView txtForgetPassword;
	private EditText txtEmail;
	private EditText txtPassword;
	private UserSessionManager session;
	private String email = "";
	private String password = "";
	private User user;
	private Context mContext;
	private Boolean isConnectionExist = false;
	private View noInternet;
	private WIFIInternetConnectionDetector cd;
	private ProgressDialog progressDialog;
	private Intent sentenceActivity;
	private String redirect;
	private boolean errorFlag = false;
	private SharedPreferences emailPrefs;

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
			setContentView(R.layout.login);
			btnLogin = (Button) findViewById(R.id.btnLogin);
			btnRegsiter = (Button) findViewById(R.id.btnRegister);
			txtLoginErr = (TextView) findViewById(R.id.txtLoginErr);
			// txtForgetPassword = (TextView) findViewById(R.id.btnForgotPass);
			txtEmail = (EditText) findViewById(R.id.txtEmail);
			txtPassword = (EditText) findViewById(R.id.txtPassword);

			mContext = this;
			btnLogin.setOnClickListener(this);
			btnRegsiter.setOnClickListener(this);
			// txtForgetPassword.setOnClickListener(this);

			setTitle("ورود");
			getSupportActionBar().setIcon(R.drawable.login);

			// creating connection detector class instance
			noInternet = (View) findViewById(R.id.mostLogin);
			noInternet.setOnClickListener(this);
			cd = new WIFIInternetConnectionDetector(mContext);
			isConnectionExist = cd.checkMobileInternetConn();

			emailPrefs = this.getSharedPreferences("remember",
					MODE_WORLD_READABLE);

			String prfEmail = emailPrefs.getString("email", "");
			String prfPass = emailPrefs.getString("password", "");
			if (!prfEmail.equals("") && !prfPass.equals("")) {
				txtEmail.setText(prfEmail);
				txtPassword.setText(prfPass);
			}

			// check for Internet status
			if (!isConnectionExist) {
				// Internet Connection doesn't exists
				noInternet.setVisibility(View.VISIBLE);
				btnLogin.setEnabled(false);

			} else {
				// Internet connection exist
				noInternet.setVisibility(View.GONE);
			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			redirect = getIntent().getStringExtra("redirect");
			sentenceActivity = new Intent(this, AddSentence.class);

			btnLogin.setTypeface(App.SetTypeFace(mContext));
			btnRegsiter.setTypeface(App.SetTypeFace(mContext));
		} catch (Exception e) {
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
				this.finish();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View view) {
		try {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			if (view == btnLogin) {
				email = txtEmail.getText().toString();
				password = txtPassword.getText().toString();

				if (!email.equals("") && !password.equals("")) {
					App assistant = new App();
					password = assistant.md5(password);
					try {
						AsyncCallWS task = new AsyncCallWS();
						task.execute();
						if (progressDialog == null) {
							progressDialog = App.createProgressDialog(mContext);
							progressDialog.show();
						} else {
							progressDialog.show();
						}
					} catch (Exception e) {
						e.printStackTrace();
						App.ShowMessageBox(this, "مشکل در ورود!");
					}

					// pd = ProgressDialog.show(this, "بـــاس صـــب کنی...",
					// "آدم بـــاس صبـــــور همیشه باشه...", true, false);

				} else {
					String msg = "فیلدها رو وارد کن!";
					App.ShowMessageBox(this, msg);
				}
			} else if (view == btnRegsiter) {
				try {
					Intent intent = new Intent(this, ResgisterUser.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (view == txtForgetPassword) {

			}

			else if (view == noInternet) {
				isConnectionExist = cd.checkMobileInternetConn();
				// check for Internet status
				if (!isConnectionExist) {
					// Internet Connection doesn't exists
					noInternet.setVisibility(View.VISIBLE);
					btnLogin.setEnabled(false);
				} else {
					// Internet connection exist
					noInternet.setVisibility(View.GONE);
					btnLogin.setEnabled(true);
				}
			}
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

	public class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			List<Parameter> wsParams = new ArrayList<Parameter>();
			Parameter paramEmail = new Parameter();
			Parameter paramPass = new Parameter();
			paramEmail.Name = "email";
			paramEmail.Value = email;

			wsParams.add(paramEmail);

			paramPass.Name = "password";
			paramPass.Value = password;
			wsParams.add(paramPass);

			WebService webService = new WebService();
			user = new User();
			String result = webService.ExecWebServiceRequset("Authenticate",
					wsParams);
			user = new User();
			if (result != null) {
				JsonParser parser = new JsonParser();
				user = parser.UserParser(result);

			} else
				errorFlag = true;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			try {
				progressDialog.dismiss();
				if (user != null && user.nickName != null) {
					String name = user.nickName;
					String userEmail = user.email;
					int userId = user.id;
					session = new UserSessionManager(mContext);
					session.createUserLoginSession(name, userEmail, userId);
					emailPrefs = mContext.getSharedPreferences("remember", 0);
					SharedPreferences.Editor prefsEditor = emailPrefs.edit();
					prefsEditor.putString("email", email);
					prefsEditor.putString("password", txtPassword.getText()
							.toString());
					prefsEditor.commit();
					String msg = "سلام " + name + "، خوش اومدی.";
					App.ShowMessageBox(mContext, msg);
					if (redirect != null && redirect.equals("addSentence")) {
						try {
							getIntent().removeExtra("redirect");
							startActivity(sentenceActivity);
						} catch (Exception e) {
							e.printStackTrace();
							getIntent().removeExtra("redirect");
							startActivity(sentenceActivity);
						}
					}

					else
						finish();

				} else if (errorFlag) {
					String msg = "مشکل در ارتباط با سرور اصلی !";
					App.ShowMessageBox(mContext, msg);
				} else {
					String msg = "ایمیل یا رمز عبور اشتباهه!";
					App.ShowMessageBox(mContext, msg);
					txtEmail.setText("");
					txtPassword.setText("");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

	}

}
