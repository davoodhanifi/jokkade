package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.kobjects.util.Util;

import com.hitech.JokKade.Login.AsyncCallWS;
import com.hitech.JokKade.database.DataBaseBridge;
import com.hitech.JokKade.utility.App;
import com.hitech.JokKade.utility.JsonParser;
import com.hitech.JokKade.utility.Pair;
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
import android.telephony.TelephonyManager;
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

public class Support extends ActionBarActivity implements
		android.view.View.OnClickListener {

	private Button btnSend;
	private EditText txtMessage;
	private String message;
	private UserSessionManager session;
	private User user;
	private Context mContext;
	private Boolean isConnectionExist = false;
	private View noInternet;
	private WIFIInternetConnectionDetector cd;
	private ProgressDialog progressDialog;
	private String result;
	private Pair pair;
	private boolean errorFlag = false;

	private SharedPreferences appPrefs;
	private Boolean isPremium = false;
	private String nickName = "";
	private EditText txtEmail;
	private TextView lblEmail;

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
			setContentView(R.layout.activity_support);
			btnSend = (Button) findViewById(R.id.btnSend);
			txtMessage = (EditText) findViewById(R.id.txtMessage);
			txtEmail = (EditText) findViewById(R.id.txtEmail);
			lblEmail = (TextView) findViewById(R.id.lblEmail);
			mContext = this;
			btnSend.setOnClickListener(this);

			setTitle("پشتیبانـــی");
			getSupportActionBar().setIcon(R.drawable.ic_contact);

			// creating connection detector class instance
			noInternet = (View) findViewById(R.id.mostLogin);
			noInternet.setOnClickListener(this);
			cd = new WIFIInternetConnectionDetector(mContext);
			isConnectionExist = cd.checkMobileInternetConn();
			// check for Internet status
			if (!isConnectionExist) {
				// Internet Connection doesn't exists
				noInternet.setVisibility(View.VISIBLE);
				btnSend.setEnabled(false);

			} else {
				// Internet connection exist
				noInternet.setVisibility(View.GONE);
			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			btnSend.setTypeface(App.SetTypeFace(mContext));
			session = new UserSessionManager(mContext);
			HashMap<String, String> hash = session.getUserDetails();
			nickName = (String) hash.get("name");
			if (!nickName.equals("")) {
				txtEmail.setVisibility(View.GONE);
				lblEmail.setVisibility(View.GONE);
			}

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
	public void onClick(View view) {
		try {
			InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			String email = "";
			if (nickName != null && !nickName.equals(""))
				email = nickName;
			else
				email = txtEmail.getText().toString();

			if (view == btnSend) {
				message = txtMessage.getText().toString();

				if (!message.equals("") && !email.equals("")) {

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
						App.ShowMessageBox(this, "خطا در ارسال، لطفا دوباره ارسال کن!");
					}

					// pd = ProgressDialog.show(this, "بـــاس صـــب کنی...",
					// "آدم بـــاس صبـــــور همیشه باشه...", true, false);
				} else {
					String msg = "فیلدها رو وارد کن!";
					App.ShowMessageBox(mContext, msg);
				}
			}

			else if (view == noInternet) {
				isConnectionExist = cd.checkMobileInternetConn();
				// check for Internet status
				if (!isConnectionExist) {
					// Internet Connection doesn't exists
					noInternet.setVisibility(View.VISIBLE);
					btnSend.setEnabled(false);

				} else {
					// Internet connection exist
					noInternet.setVisibility(View.GONE);
					btnSend.setEnabled(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			App.ShowMessageBox(mContext, "خطا در ارسال، لطفا دوباره ارسال کن!");
		}

	}

	public class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String phoneModel = "";
			String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;
			if (model.startsWith(manufacturer)) {
				phoneModel = model;
			} else {
				phoneModel = manufacturer + " " + model;
			}

			appPrefs = mContext.getSharedPreferences("mIsPremium",
					mContext.MODE_WORLD_READABLE);
			isPremium = appPrefs.getBoolean("mIsPremium", isPremium);

			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			int netType = cm.getActiveNetworkInfo().getType();

			List<Parameter> wsParams = new ArrayList<Parameter>();
			Parameter paramFeedback = new Parameter();
			Parameter paramPhoneModel = new Parameter();
			Parameter paramNetType = new Parameter();
			Parameter paramIsPremium = new Parameter();
			Parameter paramMPhoneSerialNo = new Parameter();
			Parameter paramUsername = new Parameter();

			paramFeedback.Name = "feedback";
			paramFeedback.Value = message;
			wsParams.add(paramFeedback);

			paramPhoneModel.Name = "phoneModel";
			paramPhoneModel.Value = phoneModel;
			wsParams.add(paramPhoneModel);

			paramNetType.Name = "netType";
			paramNetType.Value = "-";
			wsParams.add(paramNetType);

			paramIsPremium.Name = "isPremium";
			paramIsPremium.Value = Boolean.toString(isPremium);
			wsParams.add(paramIsPremium);

			TelephonyManager tMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneSerialNo = tMgr.getDeviceId();

			paramMPhoneSerialNo.Name = "mPhoneSerialNo";
			paramMPhoneSerialNo.Value = mPhoneSerialNo;
			wsParams.add(paramMPhoneSerialNo);

			String email = "";
			if (nickName != null && !nickName.equals(""))
				email = nickName;
			else
				email = txtEmail.getText().toString();

			paramUsername.Name = "nickName";
			paramUsername.Value = email;
			wsParams.add(paramUsername);

			try {
				WebService webService = new WebService();
				result = webService.ExecWebServiceRequset("AddNewFeedbacks",
						wsParams);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (result != null) {
				pair = new Pair();
				JsonParser parser = new JsonParser();
				pair = parser.PairParser(result);
			} else
				errorFlag = true;
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			String msg = "";
			String res = pair.Value;
			if (res.equals("OK")) {
				msg = "تشکر، متن ثبت شد.";
				App.ShowMessageBox(mContext, msg);
				try {
					finish();
					Intent intent = new Intent(mContext, Dashboard.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// startActivity(getIntent());
			} else if (res.equals("Try")) {
				msg = "متاسفانه، متن ذخیره نشد، دوباره تلاش کن.";
				App.ShowMessageBox(mContext, msg);
				txtMessage.setText("");
			} else if (errorFlag) {
				msg = "مشکل در ارتباط با سرور!";
				App.ShowMessageBox(mContext, msg);
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
