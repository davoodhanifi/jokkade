package com.hitech.JokKade.utility;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager.BadTokenException;
import android.widget.TextView;
import android.widget.Toast;
import com.hitech.JokKade.R;
import com.hitech.JokKade.R.layout;

public class App extends Application {
	private static Context context;
	private static Resources resource;
	private static Boolean boolVal = null;
	private static Typeface face;
	private static int e = -1;
	private static ProgressDialog pDialog;

	public static String GetDb() {
		File file = new File((new StringBuilder())
				.append(Environment.getExternalStorageDirectory())
				.append("/zanbass").toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		String s = file.getPath();
		Log.e("db", (new StringBuilder()).append(s).append("/zanbass.db")
				.toString());
		return (new StringBuilder()).append(s).append("/zanbass.db").toString();
	}

	public static void TextViewOperation(TextView textview, Context cont) {
		textview.setText(GetString((String) textview.getText()));
	   // textview.setTypeface(SetTypeFace(cont));
	}

	public static String GetString(String text) {
		if (text != null) {
			if (boolVal == null) {
				Paint paint = new Paint();
				paint.setARGB(255, 255, 0, 0);
				int i = (int) paint.measureText("\u0628");
				Bitmap bitmap = Bitmap.createBitmap(i, 30,
						android.graphics.Bitmap.Config.ALPHA_8);
				Bitmap bitmap1 = Bitmap.createBitmap(i, 30,
						android.graphics.Bitmap.Config.ALPHA_8);
				(new Canvas(bitmap)).drawText("\u0628\u0647", 0.0F, 30F, paint);
				(new Canvas(bitmap1)).drawText("\u0628", 0.0F, 30F, paint);
				ByteBuffer bytebuffer = ByteBuffer.allocate(bitmap.getHeight()
						* bitmap.getRowBytes());
				bitmap.copyPixelsToBuffer(bytebuffer);
				ByteBuffer bytebuffer1 = ByteBuffer.allocate(bitmap1
						.getHeight() * bitmap1.getRowBytes());
				bitmap1.copyPixelsToBuffer(bytebuffer1);
				boolVal = Boolean.valueOf(Arrays.equals(bytebuffer.array(),
						bytebuffer1.array()));
			}
			if (boolVal.booleanValue()) {
				// text = context.
			}
		}
		return text;
	}

	public static Typeface SetTypeFace(Context cont) {
		if (face == null) {
			face = Typeface.createFromAsset(cont.getAssets(), "DroidNaskh.ttf");
		}
		return face;
	}

	public static void d() {
		// TODO Auto-generated method stub

	}

	public String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest
					.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void ShowMessageBox(Context cont, String msg) {
		Toast toast = Toast.makeText(cont, msg, Toast.LENGTH_LONG);
		// toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
		toast.show();
	}

	public static ProgressDialog createProgressDialog(Context mContext) {
		ProgressDialog dialog = new ProgressDialog(mContext);
		try {
			dialog.show();
		} catch (BadTokenException e) {
			e.printStackTrace();
		}
		dialog.setCancelable(false);
		dialog.setContentView(R.layout.progressdialog);
		// dialog.setMessage(Message);
		return dialog;
	}

	public static void setDefaultFont(Context context,
			String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(),
				fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	protected static void replaceFont(String staticTypefaceFieldName,
			final Typeface newTypeface) {
		try {
			final Field StaticField = Typeface.class
					.getDeclaredField(staticTypefaceFieldName);
			StaticField.setAccessible(true);
			StaticField.set(null, newTypeface);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
