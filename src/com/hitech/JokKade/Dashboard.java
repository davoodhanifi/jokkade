package com.hitech.JokKade;

import com.hitech.JokKade.database.DataBaseBridge;
import com.hitech.JokKade.utility.App;
import com.hitech.JokKade.utility.JsonParser;
import com.hitech.JokKade.utility.NavDrawerListAdapter;
import com.hitech.JokKade.utility.Parameter;
import com.hitech.JokKade.utility.Sentence;
import com.hitech.JokKade.utility.TabsPagerAdapter;
import com.hitech.JokKade.utility.TextViewPlus;
import com.hitech.JokKade.utility.Typefaces;
import com.hitech.JokKade.utility.UserSessionManager;
import com.hitech.JokKade.utility.WIFIInternetConnectionDetector;
import com.hitech.JokKade.utility.WebService;
import com.hitech.JokKade.R;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;
import java.util.List;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.style.TypefaceSpan;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Dashboard extends ActionBarActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;

	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<RelativeLayout> navDrawerItems;
	private NavDrawerListAdapter adapter;
	private long lastBackkeyPressTime = 0;

	private UserSessionManager session;
	private Context mContext;
	private int count;

	private ViewPager viewPager;
	private FragmentPagerAdapter mAdapter;
	private android.support.v7.app.ActionBar actionBar;
	private Boolean isConnectionExist = false;
	private WIFIInternetConnectionDetector cd;

	private ProgressDialog progressDialog;
	private boolean errorFlag = false;
	private boolean quotaError = false;
	protected PowerManager.WakeLock mWakeLock;
	private TabPageIndicator indicator;
	private LinearLayout drawer;
	private Boolean doubleBackToExitPressedOnce = false;
	private static TypefaceSpan face;

	private SharedPreferences appPrefs;
	private Boolean isPremium = false;

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
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
					"My Tag");
			this.mWakeLock.acquire();

			mContext = this;
			actionBar = getSupportActionBar();

			setContentView(R.layout.dashboard);

			getSupportActionBar().setIcon(R.drawable.ic_action_unread);
			appPrefs = mContext.getSharedPreferences("mIsPremium",
					MODE_WORLD_READABLE);

			isPremium = appPrefs.getBoolean("mIsPremium", isPremium);
		  // App.ShowMessageBox(mContext, "user is "+ isPremium);

			session = new UserSessionManager(mContext);

			mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
			viewPager = (ViewPager) findViewById(R.id.pager);
			viewPager.setAdapter(mAdapter);

			indicator = (TabPageIndicator) findViewById(R.id.indicator);
			Typeface tf = Typefaces.get(mContext, "DroidNaskh.ttf");
			indicator.setTypeFace(tf);
			indicator.setViewPager(viewPager);

			indicator
					.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
						@Override
						public void onPageSelected(int position) {
							try {
								viewPager.setCurrentItem(position);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void onPageScrolled(int position,
								float positionOffset, int positionOffsetPixels) {
						}

						@Override
						public void onPageScrollStateChanged(int state) {
						}
					});

			drawer = (LinearLayout) findViewById(R.id.right_drawer);
			mTitle = mDrawerTitle = getTitle();

			// load slide menu items
			navMenuTitles = getResources().getStringArray(
					R.array.nav_drawer_items);

			mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);

			mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
					R.drawable.ic_drawer, // nav menu toggle icon
					R.string.app_name, // nav drawer open - description for
										// accessibility
					R.string.app_name // nav drawer close - description for
										// accessibility
			) {
				public void onDrawerClosed(View view) {
					// calling onPrepareOptionsMenu() to show action bar icons
					supportInvalidateOptionsMenu();

				}

				public void onDrawerOpened(View drawerView) {
					// calling onPrepareOptionsMenu() to hide action bar icons
					supportInvalidateOptionsMenu();

				}
			};
			mDrawerLayout.setDrawerListener(mDrawerToggle);

			if (savedInstanceState == null) {
				// on first time display view for first nav item
				displayView(0);
			}

			RelativeLayout unreadHolder = (RelativeLayout) findViewById(R.id.home_holder);
			unreadHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(0);
				}
			});

			RelativeLayout addSentenceHolder = (RelativeLayout) findViewById(R.id.add_sentence_holder);
			addSentenceHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(1);
				}
			});
			RelativeLayout supportHolder = (RelativeLayout) findViewById(R.id.support);
			supportHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(2);
				}
			});
			RelativeLayout rulesHolder = (RelativeLayout) findViewById(R.id.terms);
			rulesHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(3);
				}
			});

			RelativeLayout aboutHolder = (RelativeLayout) findViewById(R.id.about);
			aboutHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(4);
				}
			});

			
			RelativeLayout commentHolder = (RelativeLayout) findViewById(R.id.comment);
			commentHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(6);
				}
			});
			
			RelativeLayout zanbaasHolder = (RelativeLayout) findViewById(R.id.zanbaas);
			zanbaasHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(7);
				}
			});
			
			RelativeLayout makanYabHolder = (RelativeLayout) findViewById(R.id.makanYab);
			makanYabHolder.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					displayView(5);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		try {
			this.mWakeLock.release();
			super.onDestroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		// menu.removeItem(R.id.action_back);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_addSentence:
			if (!session.isUserLoggedIn()) {
				try {
					Intent sentenceActivity = new Intent(this, Login.class);
					sentenceActivity.putExtra("redirect", "addSentence");
					startActivity(sentenceActivity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					Intent sentenceActivity = new Intent(this,
							AddSentence.class);
					startActivity(sentenceActivity);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.action_logout:
			if (session.isUserLoggedIn()) {
				try {
					session.logoutUser();
					finish();
					startActivity(getIntent());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.action_update:
			/* if (isPremium) { */
			cd = new WIFIInternetConnectionDetector(mContext);
			isConnectionExist = cd.checkMobileInternetConn();
			// check for Internet status
			if (isConnectionExist) {
				// Internet Connection exists
				try {
					AsyncCallWS task = new AsyncCallWS();
					task.execute();
					if (progressDialog == null) {
						progressDialog = App.createProgressDialog(mContext);
						progressDialog.show();
					} else {
						progressDialog.show();
					}
					viewPager.notifyAll();
					mAdapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
					// App.ShowMessageBox(this, "مشکل در بروزرسانی!");
				}
			} else {
				// Internet connection doesn't exist
				App.ShowMessageBox(this,
						"ّبرای بروزرسانی جملات باید به اینترنت متصل باشید.");
			}
			/*
			 * } else { String msg = "میخـــوای متن های بیشتری داشته باشی؟؟؟\n"
			 * + "بـــاس نرم افزار رو به نسخه طلایی ارتقـــا بدی";
			 * App.ShowMessageBox(this, msg); displayView(8); }
			 */
			break;
		default:
			return super.onOptionsItemSelected(item);

		}
		return true;
	}

	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			super.onBackPressed();
			return;
		}

		this.doubleBackToExitPressedOnce = true;
		App.ShowMessageBox(this, " برای خروج کلید برگشت رو دوباره فشار بده.");
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				doubleBackToExitPressedOnce = false;
			}
		}, 2000);
	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (!mDrawerLayout.isDrawerOpen(drawer)) {
				mDrawerLayout.openDrawer(drawer);
			} else
				mDrawerLayout.closeDrawer(drawer);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	/* *
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		try {
			boolean drawerOpen = mDrawerLayout.isDrawerOpen(drawer);
			menu.findItem(R.id.action_addSentence).setVisible(!drawerOpen);
			menu.findItem(R.id.action_update).setVisible(!drawerOpen);

			if (!session.isUserLoggedIn()) {
				menu.removeItem(R.id.action_logout);
			} else
				menu.findItem(R.id.action_logout).setVisible(!drawerOpen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {
		// update the main content by replacing fragments
		try {
			Fragment fragment = null;
			Intent intent = getIntent();

			Intent sentenceActivity = null;
			Intent publicAcitivity = null;
			mDrawerLayout.closeDrawer(drawer);
			switch (position) {
			case 0:
				setTitle("خانه");
				break;
			case 1:
				if (!session.isUserLoggedIn()) {
					try {
						sentenceActivity = new Intent(mContext, Login.class);
						sentenceActivity.putExtra("redirect", "addSentence");
						startActivity(sentenceActivity);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					try {
						sentenceActivity = new Intent(mContext,
								AddSentence.class);
						startActivity(sentenceActivity);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// break;
			case 2:
				try {
					publicAcitivity = new Intent(mContext, Support.class);
					startActivity(publicAcitivity);
					return;
				} catch (Exception e) {
					e.printStackTrace();
				}

			case 3:
				try {
					publicAcitivity = new Intent(mContext, About.class);
					publicAcitivity.putExtra("queryString", "rules");
					startActivity(publicAcitivity);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			case 4:
				try {
					  publicAcitivity = new Intent(mContext, About.class);
					  publicAcitivity.putExtra("queryString", "about");
					  startActivity(publicAcitivity);
					 
					/*
					Intent viewIntent = new Intent(
							"android.intent.action.EDIT",
							Uri.parse("bazaar://details?id=com.zanbaas.activity"));
					startActivity(viewIntent);*/
/*
					Intent viewIntent = new Intent(
							"android.intent.action.VIEW",
							Uri.parse("bazaar://details?id=com.zanbaas.activity"));
					startActivity(viewIntent);*/
					return;
				} catch (Exception e) {
					//App.ShowMessageBox(mContext, " برای نظر دادن، نیاز به نصب نرم افزار کافه بازار است. ");
					e.printStackTrace();
					return;
				}
			case 5:
				try {
					Intent viewIntent = new Intent(
							"android.intent.action.VIEW",
							Uri.parse("bazaar://details?id=com.android.locationplacedetailsv2"));
					startActivity(viewIntent);
					return;
				} catch (Exception e) {
					App.ShowMessageBox(mContext, " برای دریافت نرم افزار مکان یاب، نیاز به نصب نرم افزار کافه بازار است. ");
					e.printStackTrace();
					return;
				}
			case 6:
				try {
					 
					Intent viewIntent = new Intent(
							"android.intent.action.EDIT",
							Uri.parse("bazaar://details?id=com.hitech.JokKade"));
					startActivity(viewIntent);
					return;
				} catch (Exception e) {
					App.ShowMessageBox(mContext, " برای نظر دادن، نیاز به نصب نرم افزار کافه بازار است. ");
					e.printStackTrace();
					return;
				}
				
			case 7:
				try {
					Intent viewIntent = new Intent(
							"android.intent.action.VIEW",
							Uri.parse("bazaar://details?id=com.zanbaas.activity"));
					startActivity(viewIntent);
					return;
				} catch (Exception e) {
					App.ShowMessageBox(mContext, " برای دریافت نرم افزار زن باس، نیاز به نصب نرم افزار کافه بازار است. ");
					e.printStackTrace();
					return;
				}

			default:
				break;
			}
			try {
				mAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		try {
			mTitle = title;
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setDisplayShowTitleEnabled(false);

			LayoutInflater inflator = LayoutInflater.from(mContext);
			View v = inflator.inflate(R.layout.titleview, null);

			((TextViewPlus) v.findViewById(R.id.title)).setText(mTitle);
			getSupportActionBar().setCustomView(v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
				|| newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			try {
				mAdapter.notifyDataSetChanged();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public class AsyncCallWS extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			String result = null;
			List<Parameter> wsParams = new ArrayList<Parameter>();

			Parameter paramMaxId = new Parameter();
			Parameter paramIsPremium = new Parameter();
			Parameter paramMPhoneSerialNo = new Parameter();
			Parameter paramUsername = new Parameter();
			Parameter paramCount = new Parameter();
			DataBaseBridge sql = new DataBaseBridge(mContext);
			int maxId = 0;
			try {
				maxId = sql.GetMaxId();
			} catch (Exception e) {
				e.printStackTrace();
			}

			paramMaxId.Name = "maxId";
			paramMaxId.Value = Integer.toString(maxId);
			wsParams.add(paramMaxId);

			paramIsPremium.Name = "isPremium";
			paramIsPremium.Value = Boolean.toString(isPremium);
			wsParams.add(paramIsPremium);

			TelephonyManager tMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mPhoneSerialNo = tMgr.getDeviceId();

			paramMPhoneSerialNo.Name = "mPhoneSerialNo";
			paramMPhoneSerialNo.Value = mPhoneSerialNo;
			wsParams.add(paramMPhoneSerialNo);

			SharedPreferences emailPrefs = mContext.getSharedPreferences(
					"remember", MODE_WORLD_READABLE);
			String prfEmail = emailPrefs.getString("email", "");
			if (!prfEmail.equals("")) {
				paramUsername.Name = "username";
				paramUsername.Value = prfEmail;
				wsParams.add(paramUsername);
			} else {
				paramUsername.Name = "username";
				paramUsername.Value = "";
				wsParams.add(paramUsername);
			}
			paramCount.Name = "count";
			paramCount.Value = Integer.toString(10);
			wsParams.add(paramCount);

			try {
				WebService webService = new WebService();
				result = webService.ExecWebServiceRequset(
						"GetNewKhastasSentences", wsParams);
			} catch (Exception e) {
				e.printStackTrace();
			}
			count = 0;
			quotaError = false;
			errorFlag = false;
			if (result != null) {
				JsonParser parser = new JsonParser();
				List<Sentence> sentences = parser.SentencesParser(result);

				count = sentences.size();
				if (!isPremium) {
					for (Sentence sentence : sentences) {
						if (sentence.id != -1) {
							try {
								sql.InsertSentence(sentence);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							quotaError = true;
						}
					}
				} else {
					for (Sentence sentence : sentences) {
						try {
							sql.InsertSentence(sentence);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				errorFlag = true;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			String msg = "";

			if (count > 0 && !quotaError) {
				msg = count + " " + "آیتم دریافت و ذخیره شد.";
				App.ShowMessageBox(mContext, msg);
			} else if (errorFlag) {
				msg = "مشکل در ارتباط با سرور اصلی!";
				App.ShowMessageBox(mContext, msg);
			} else if (quotaError) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(
						mContext);

				// Setting Dialog Title
				alertDialog.setTitle("ارتقا نرم افزار");

				// Setting Dialog Message
				alertDialog
						.setMessage("با نسخه جاری روزانه تنها به تعداد محدودی از جملات دسترسی داری. \n با ارتقا نرم افزار به نسخه طلایی هر روز میتونی جملات بیشتری رو داشته باشی. میخوای ارتقا بدی؟");

				// Setting Icon to Dialog
				// alertDialog.setIcon(R.drawable.delete);

				// Setting Positive "Yes" Button
				alertDialog.setPositiveButton("بلی",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								// Write your code here to invoke YES event
							
							}
						});

				// Setting Negative "NO" Button
				alertDialog.setNegativeButton("فعلا نه !",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to invoke NO event
								// Toast.makeText(mContext, "You clicked on NO",
								// Toast.LENGTH_SHORT).show();
								dialog.cancel();
							}
						});
				alertDialog.show();
			} else {
				// msg =
				// "متاسفانه، آیتمی دریافت نشد، سرور مشغوله چند دقیقه بعد دوباره تلاش کن.";
				msg = "آیتم جدیدی موجود نیست، دقایقی دیگر دوباره تلاش کن.";
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
