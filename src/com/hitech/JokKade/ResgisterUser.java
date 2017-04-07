package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.List;

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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
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

public class ResgisterUser extends ActionBarActivity implements
		android.view.View.OnClickListener {

	private Button btnRegsiter;
	private EditText txtEmail;
	private EditText txtPassword;
	private EditText txtNickName;
	private UserSessionManager session;
	private User newUser;
	private String result;
	private Pair pair;
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
			setContentView(R.layout.register);
			mContext = this;

			setTitle("ثبت نام");
			getSupportActionBar().setIcon(R.drawable.ic_adduser);

			txtEmail = (EditText) findViewById(R.id.txtEmail);
			txtPassword = (EditText) findViewById(R.id.txtPassword);
			txtNickName = (EditText) findViewById(R.id.txtNickname);
			btnRegsiter = (Button) findViewById(R.id.btnRegister);
			btnRegsiter.setOnClickListener(this);

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
				btnRegsiter.setEnabled(false);

			} else {
				// Internet connection exist
				noInternet.setVisibility(View.GONE);
				btnRegsiter.setEnabled(true);
			}
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			btnRegsiter.setTypeface(App.SetTypeFace(mContext));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		menu.removeItem(R.id.action_addSentence);
		menu.removeItem(R.id.action_update);
		// if(!session.isUserLoggedIn())
		menu.removeItem(R.id.action_logout);
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

	@SuppressLint("NewApi")
	@Override
	public void onClick(View view) {
		try {
			if (view == btnRegsiter) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				String email = txtEmail.getText().toString();
				String password = txtPassword.getText().toString();
				String nickName = txtNickName.getText().toString();

				App assistant = new App();

				if (!TextUtils.isEmpty(email)
						&& !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
								.matches() && !TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(nickName)) {
					String msg = "ساختار ایمیل صحیح نیست.";
					App.ShowMessageBox(this, msg);
					return;
				}
				if (!TextUtils.isEmpty(email)
						&& android.util.Patterns.EMAIL_ADDRESS.matcher(email)
								.matches() && !TextUtils.isEmpty(password)
						&& !TextUtils.isEmpty(nickName)) {
					newUser = new User();
					newUser.email = email;
					newUser.password = assistant.md5(password);
					newUser.nickName = nickName;
					newUser.isObsolete = false;
					try {
						DataBaseBridge sql = new DataBaseBridge(this);
						sql.InsertUser(newUser);
					} catch (Exception e) {
						e.printStackTrace();
					}

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
						App.ShowMessageBox(this, "مشکل در ثبت نام!");

					}

					// DataBaseBridge sql = new DataBaseBridge(this);
					// sql.InsertUser(newUser);

					// String msg = "تشکر، اطلاعاتت ثبت شد.";
					// App.ShowMessageBox(this, msg);
					// finish();
				} else {
					String msg = "فیلدها رو وارد کن!";
					App.ShowMessageBox(this, msg);

				}
			} else if (view == noInternet) {
				isConnectionExist = cd.checkMobileInternetConn();
				// check for Internet status
				if (!isConnectionExist) {
					// Internet Connection doesn't exists
					noInternet.setVisibility(View.VISIBLE);
					btnRegsiter.setEnabled(false);

				} else {
					// Internet connection exist
					noInternet.setVisibility(View.GONE);
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
			Parameter paramEmail = new Parameter();
			Parameter paramPass = new Parameter();
			Parameter paramUsername = new Parameter();
			Parameter paramPhoneModel = new Parameter();
			Parameter paramMPhoneSerialNo = new Parameter();

			paramEmail.Name = "email";
			paramEmail.Value = newUser.email;

			wsParams.add(paramEmail);

			paramPass.Name = "password";
			paramPass.Value = newUser.password;
			wsParams.add(paramPass);

			paramUsername.Name = "username";
			paramUsername.Value = newUser.nickName;
			wsParams.add(paramUsername);

			paramPhoneModel.Name = "phoneModel";
			paramPhoneModel.Value = phoneModel;
			wsParams.add(paramPhoneModel);

			TelephonyManager tMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneSerialNo = tMgr.getDeviceId();

			paramMPhoneSerialNo.Name = "mPhoneSerialNo";
			paramMPhoneSerialNo.Value = mPhoneSerialNo;
			wsParams.add(paramMPhoneSerialNo);

			try {
				WebService webService = new WebService();
				result = webService.ExecWebServiceRequset("RegisterNewUser",
						wsParams);
			} catch (Exception e) {
				e.printStackTrace();
			}

			pair = new Pair();
			if (result != null) {
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
			if (res != null) {
				if (res.equals("OK")) {
					msg = "تشکر، اطلاعاتت ثبت شد.";
					App.ShowMessageBox(mContext, msg);
					finish();
				} else if (res.equals("Try")) {
					msg = "متاسفانه، اطلاعاتت ذخیره نشد، دوباره تلاش کن.";
					App.ShowMessageBox(mContext, msg);
				} else if (res.equals("Exist")) {
					msg = "ایمیل یا نام نمایشی تکراریه، این فیلدها رو تغییر بده.";
					App.ShowMessageBox(mContext, msg);
				}
			} else if (errorFlag) {
				msg = "مشکل در ارتباط با سرور!";
				App.ShowMessageBox(mContext, msg);
			}
		}

		@Override
		protected void onPreExecute() {
			// Make ProgressBar invisible
			// pg.setVisibility(View.VISIBLE);
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}

	}

}
