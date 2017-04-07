package com.hitech.JokKade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.WindowManager;
import com.hitech.JokKade.R;

public class SplashScreen extends Activity {

	// Splash screen timer
	private static int SPLASH_TIME_OUT = 3000;

	protected PowerManager.WakeLock mWakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			super.onCreate(savedInstanceState);
			// App.setDefaultFont(this, "Droid", "DroidNaskh.ttf");
			final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
					"My Tag");
			this.mWakeLock.acquire();

			// getActionBar().hide();
			setContentView(R.layout.activity_splash);
			new Handler().postDelayed(new Runnable() {

				/*
				 * Showing splash screen with a timer. This will be useful when
				 * you want to show case your app logo / company
				 */

				@Override
				public void run() {
					// This method will be executed once the timer is over
					// Start your app main activity
					try {
						Intent i = new Intent(SplashScreen.this,
								Dashboard.class);
						startActivity(i);
					} catch (Exception e) {
						e.printStackTrace();
						Intent i = new Intent(SplashScreen.this,
								Dashboard.class);
						startActivity(i);
					}
					// close this activity
					finish();
				}
			}, SPLASH_TIME_OUT);
		} catch (Exception e) {
			Intent i = new Intent(SplashScreen.this,Dashboard.class);
			startActivity(i);
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		this.mWakeLock.release();
		super.onDestroy();
	}

}
