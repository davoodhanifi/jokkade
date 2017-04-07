package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hitech.JokKade.Login.AsyncCallWS;
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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddSentence extends ActionBarActivity implements
		android.view.View.OnClickListener {

	private EditText txtSentence;
	private Button btnAddSentence;
	private Button btnChooseType;
	private TextView lblError;
	private UserSessionManager session;
	private String result;
	private Pair pair;
	private String sentence;
	private Context mContext;
	private Boolean isConnectionExist = false;
	private View noInternet;
	private WIFIInternetConnectionDetector cd;
	private ProgressDialog progressDialog;
	private boolean errorFlag = false;

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
			setContentView(R.layout.activity_addsentence);
			mContext = this;

			setTitle("ارسال متن");
			getSupportActionBar().setIcon(R.drawable.ic_addsentence);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);

			txtSentence = (EditText) findViewById(R.id.txtSentence);
			btnAddSentence = (Button) findViewById(R.id.btnAddSentence);
			// btnChooseType = (Button) findViewById(R.id.btnChooseType);
			lblError = (TextView) findViewById(R.id.txtLoginErr);

			btnAddSentence.setOnClickListener(this);
			// btnChooseType.setOnClickListener(this);
			// session = new UserSessionManager(getApplicationContext());

			// creating connection detector class instance
			noInternet = (View) findViewById(R.id.mostLogin);
			noInternet.setOnClickListener(this);
			cd = new WIFIInternetConnectionDetector(mContext);
			isConnectionExist = cd.checkMobileInternetConn();
			// check for Internet status
			if (!isConnectionExist) {
				// Internet Connection doesn't exists
				noInternet.setVisibility(View.VISIBLE);
				btnAddSentence.setEnabled(false);

			} else {
				// Internet connection exist
				noInternet.setVisibility(View.GONE);
			}
			btnAddSentence.setTypeface(App.SetTypeFace(mContext));
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

			if (view == btnAddSentence) {
				sentence = txtSentence.getText().toString();
				if (!TextUtils.isEmpty(sentence)) {
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
						App.ShowMessageBox(this, "مشکل در ارسال متن!");
						e.printStackTrace();
					}

				} else {
					String msg = "متن رو وارد کن! ";
					App.ShowMessageBox(this, msg);
				}

			} else if (view == btnChooseType) {
				try {
					Intent intent = new Intent(mContext, ChooseType.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (view == noInternet) {
				isConnectionExist = cd.checkMobileInternetConn();
				// check for Internet status
				if (!isConnectionExist) {
					// Internet Connection doesn't exists
					noInternet.setVisibility(View.VISIBLE);
					btnAddSentence.setEnabled(false);

				} else {
					// Internet connection exist
					noInternet.setVisibility(View.GONE);
					btnAddSentence.setEnabled(true);
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
			String phoneModel = "";
			String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;

			if (model.startsWith(manufacturer)) {
				phoneModel = model;
			} else {
				phoneModel = manufacturer + " " + model;
			}

			List<Parameter> wsParams = new ArrayList<Parameter>();
			Parameter paramSentence = new Parameter();
			Parameter paramNickname = new Parameter();
			Parameter paramUserId = new Parameter();
			Parameter paramPhoneModel = new Parameter();
			Parameter paramMPhoneSerialNo = new Parameter();

			paramPhoneModel.Name = "phoneModel";
			paramPhoneModel.Value = phoneModel;
			wsParams.add(paramPhoneModel);

			TelephonyManager tMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneSerialNo = tMgr.getDeviceId();

			paramMPhoneSerialNo.Name = "mPhoneSerialNo";
			paramMPhoneSerialNo.Value = mPhoneSerialNo;
			wsParams.add(paramMPhoneSerialNo);

			session = new UserSessionManager(mContext);
			HashMap<String, String> hash = session.getUserDetails();
			String nickName = (String) hash.get("name");
			String userId = hash.get("id");

			// create param's
			paramSentence.Name = "sentence";
			paramSentence.Value = txtSentence.getText().toString();
			wsParams.add(paramSentence);

			paramUserId.Name = "userId";
			paramUserId.Value = userId;
			wsParams.add(paramUserId);

			paramNickname.Name = "nickname";
			paramNickname.Value = nickName;
			wsParams.add(paramNickname);

			try {
				WebService webService = new WebService();
				result = webService.ExecWebServiceRequset("AddNewKhastasSentence",
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
				finish();
				try {
					Intent intent = new Intent(mContext, Dashboard.class);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// startActivity(getIntent());
			} else if (res.equals("Try")) {
				msg = "متاسفانه، متن ذخیره نشد، دوباره تلاش کن.";
				App.ShowMessageBox(mContext, msg);
				txtSentence.setText("");
			} else if (errorFlag) {
				msg = "مشکل در ارتباط با سرور اصلی!";
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
