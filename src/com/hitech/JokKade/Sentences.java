package com.hitech.JokKade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hitech.JokKade.database.DataBaseBridge;
import com.hitech.JokKade.utility.App;
import com.hitech.JokKade.utility.Category;
import com.hitech.JokKade.utility.CreateArrayAdaptor;
import com.hitech.JokKade.utility.Sentence;
import com.hitech.JokKade.utility.UserSessionManager;
import com.hitech.JokKade.utility.newInterface;
import com.hitech.JokKade.R;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint({ "NewApi", "ValidFragment" })
public class Sentences extends Fragment implements
		android.view.View.OnClickListener,
		android.widget.AbsListView.OnScrollListener, newInterface {

	private boolean a;
	private List arrayList;
	private CreateArrayAdaptor sentences;
	private ListView lvSentence;
	private TextView tvEmpty;
	private String text;
	private String queryString;
	private UserSessionManager session;
	private String sentencesType;
	private android.support.v7.app.ActionBarActivity activity;
	private Context mcontext;

	private SharedPreferences appPrefs;
	private Boolean isPremium = false;

	public Sentences(String content) {
		// TODO Auto-generated constructor stub
		sentencesType = content;
	}

	private void SetListAdaptor() {
		a = true;
		// if (sentences == null) {
		sentences = new CreateArrayAdaptor(activity, new ArrayList(), this);
		lvSentence.setAdapter(sentences);
		// } else {
		// sentences.clear();
		// }
		CreateListAdaptor();
	}

	@SuppressLint("NewApi")
	private void CreateListAdaptor() {
		try {
			boolean flag = true;
			if (sentences != null) {
				String s = text;
				int cuCount = lvSentence.getCount();
				String query = queryString;
				ArrayList arraylist = new ArrayList();
				DataBaseBridge sql = new DataBaseBridge(activity);
				Cursor cursor = null;
				if (text == null) {
					cursor = null;
				}
				App.TextViewOperation(tvEmpty, mcontext);

				if (sentencesType.equals("خوانده نشده ها")) {
					String position[] = new String[1];
					// position[0] = String.valueOf(30);
					// int count =10;
					// if (isPremium) {
					position[0] = String.valueOf(cuCount);
					int count = 25;
					// }

					try {
						cursor = sql.GetUnreadedSentences(position, count);
					} catch (Exception e) {
						e.printStackTrace();
						App.ShowMessageBox(activity,
								"مشکل در ارتباط با پایگاه داده!");
					}

				} else if (sentencesType.equals("خوانده شده ها")) {
					String position[] = new String[1];
					position[0] = String.valueOf(cuCount);
					try {
						cursor = sql.GetReadedSentences(
								position);
					} catch (Exception e) {
						e.printStackTrace();
						App.ShowMessageBox(activity,
								"مشکل در ارتباط با پایگاه داده!");
					}

				} else if (sentencesType.equals("نشان شده ها")) {
					String position[] = new String[1];
					position[0] = String.valueOf(cuCount);

					try {
						cursor = sql.GetStaredSentences(
								position);
					} catch (Exception e) {
						e.printStackTrace();
						App.ShowMessageBox(activity,
								"مشکل در ارتباط با پایگاه داده!");
					}

				}
				if (cursor != null) {
					cursor.moveToFirst();
					while (!cursor.isAfterLast()) {
						Sentence offlineMessage = new Sentence();
						offlineMessage.id = cursor.getInt(0);
						// b1.b = Integer.valueOf(cursor.getInt(flag));
						offlineMessage.tvName = cursor.getString(2);
						offlineMessage.tvZanbaasText = cursor.getString(3);
						if (cursor.getInt(5) == 1)
							offlineMessage.isReaded = true;
						if (cursor.getInt(6) == 1)
							offlineMessage.isDeleted = true;
						if (cursor.getInt(7) == 1)
							offlineMessage.isLiked = true;

						arraylist.add(offlineMessage);
						cursor.moveToNext();
					}
					try {
						sql.CloseDatabase();
						cursor.close();
					} catch (Exception e) {
						e.printStackTrace();
					}

					Sentence sentence;
					for (Iterator iterator = arraylist.iterator(); iterator
							.hasNext(); sentences.add(sentence)) {
						sentence = (Sentence) iterator.next();
					}

					/*
					 * if (arraylist.size() != 25 && isPremium) { flag = false;
					 * }
					 * 
					 * a = flag;
					 */
					sentences.notifyDataSetChanged();

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		try {
			activity = (ActionBarActivity) getActivity();
			mcontext = activity.getApplicationContext();
			rootView = inflater.inflate(R.layout.stared, container, false);

			appPrefs = activity.getSharedPreferences("mIsPremium",
					mcontext.MODE_WORLD_READABLE);
			isPremium = appPrefs.getBoolean("mIsPremium", isPremium);
			// App.ShowMessageBox(activity, "user is "+ isPremium);

			session = new UserSessionManager(mcontext);
			queryString = activity.getIntent().getStringExtra("tag");
			lvSentence = (ListView) rootView.findViewById(R.id.lstStare);
			tvEmpty = (TextView) rootView.findViewById(R.id.tvEmptyStare);
			lvSentence.setEmptyView(tvEmpty);
			lvSentence.setDividerHeight(0);
			lvSentence.setOnScrollListener(this);

			if (text == null) {
			}
			App.TextViewOperation(tvEmpty, mcontext);
			// if (queryString.equals("unread"))
			SetListAdaptor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rootView;
	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void a(int i) {
		if (i == 20) {
			((CreateArrayAdaptor) lvSentence.getAdapter()).getItem(i);
			if (lvSentence.getAdapter().getCount() > i + 1) {
				i++;
			}
		}
		// = l;
		((CreateArrayAdaptor) lvSentence.getAdapter()).getItem(i);
		lvSentence.setSelection(i);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		try {
			if (++firstVisibleItem + visibleItemCount > totalItemCount)
				CreateListAdaptor();
			if (totalItemCount >= 100)
				view.setFastScrollEnabled(true);
			else
				view.setFastScrollEnabled(false);
		} catch (Exception e) {

		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {

	}

	@Override
	public void onResume() {
		super.onResume();
		// if (!queryString.equals("طنز"))
		// SetListAdaptor();
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		try {
			String s = (String) arrayList.get(item.getItemId());
			if (!s.equals(text)) {
				text = s;
				SetListAdaptor();
			}
		} catch (Exception e) {

		}
		return super.onContextItemSelected(item);
	}
}
