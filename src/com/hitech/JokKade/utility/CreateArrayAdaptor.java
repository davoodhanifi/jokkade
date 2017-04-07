package com.hitech.JokKade.utility;

import java.util.List;

import com.hitech.JokKade.database.DataBaseBridge;
import com.hitech.JokKade.R;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CreateArrayAdaptor extends ArrayAdapter implements
		android.view.View.OnClickListener,android.view.View.OnLongClickListener{

	private Context thisContext;
	private newInterface b;

	public CreateArrayAdaptor(Context context, List<Sentence> list,
			newInterface inteface) {
		super(context, R.layout.zanbaas_row, list);
		thisContext = context;
		b = inteface;
	}

	public final void SetInterface(newInterface inteface) {
		b = inteface;
	}

	@Override
	public final View getView(int i, View view, ViewGroup viewgroup) {
		OfflineActivityElement offlineElement;
		Sentence sentence;
		if (view == null) {
			view = ((LayoutInflater) thisContext
					.getSystemService("layout_inflater")).inflate(
					R.layout.zanbaas_row, viewgroup, false);
			offlineElement = new OfflineActivityElement();
			offlineElement.tvZanbaas = (TextView) view
					.findViewById(R.id.tvJomlak);
			offlineElement.tvName = (TextView) view
					.findViewById(R.id.tvNickname);
			offlineElement.btnSms = (ImageButton) view.findViewById(R.id.btnSms);
			offlineElement.btnlike = (ImageButton) view
					.findViewById(R.id.btnLike);
			offlineElement.btnShare = (ImageButton) view
					.findViewById(R.id.btnShare);
			offlineElement.btnRemove = (ImageButton) view
					.findViewById(R.id.btnRemove);
			view.setTag(offlineElement);
		} else {
			offlineElement = (OfflineActivityElement) view.getTag();
		}
		sentence = (Sentence) getItem(i);
		if (sentence != null) {
			offlineElement.tvZanbaas.setTag(Integer.valueOf(i));
			offlineElement.tvZanbaas.setText(sentence.tvZanbaasText.trim());

			offlineElement.tvName.setText(sentence.tvName.trim());
			offlineElement.btnlike.setTag(sentence);
			offlineElement.btnRemove.setTag(sentence);
			offlineElement.btnShare.setTag(sentence);
			offlineElement.btnSms.setTag(sentence);
			
			offlineElement.tvZanbaas.setOnClickListener(this);
		    offlineElement.tvZanbaas.setOnLongClickListener(this);
			offlineElement.btnlike.setOnClickListener(this);
			offlineElement.btnRemove.setOnClickListener(this);
			offlineElement.btnShare.setOnClickListener(this);
			offlineElement.btnSms.setOnClickListener(this);
			if (sentence.isLiked) {
				offlineElement.btnlike
						.setImageResource(R.drawable.ic_star_fill);
			} else {
				offlineElement.btnlike
						.setImageResource(R.drawable.ic_star_empty);
			}
			if (sentence.isReaded) {
				view.setBackgroundResource(R.color.read_bg_color);
			} else {
				view.setBackgroundResource(R.color.bg_color);
			}
//			if (sentence.likeNumber != -1) {
//				offlineElement.tvLike.setText((new StringBuilder("("))
//						.append(sentence.likeNumber).append(")").toString());
			//}
			App.TextViewOperation(offlineElement.tvZanbaas,thisContext);
			App.TextViewOperation(offlineElement.tvName,thisContext);
		}
		return view;

	}

	@Override
	public void onClick(View view) {
		DataBaseBridge sql = new DataBaseBridge(thisContext);
		Sentence sentence;
		int i;
		if (view.getId() == R.id.btnLike) {
			sentence = (Sentence) view.getTag();
			Intent intent;
			boolean flag;
			if (!sentence.isLiked) {
				flag = true;
			} else {
				flag = false;
			}
			sentence.isLiked = flag;
			sentence.isSynced = false;
			sql.UpdateSentence(sentence);
			notifyDataSetChanged();
		}

		if (view.getId() == R.id.btnShare) {
			sentence = (Sentence) view.getTag();
			Intent intent = new Intent("android.intent.action.SEND");
			intent.setType("text/plain");
			intent.putExtra("android.intent.extra.TEXT",
					sentence.tvZanbaasText.trim());
			thisContext.startActivity(Intent.createChooser(intent, "اشتراک گـــذاری..."));
		}
		
		if(view.getId() == R.id.btnSms){
			sentence = (Sentence) view.getTag();
			String smsBody = sentence.tvZanbaasText.trim();
			
		    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		    sendIntent.putExtra("sms_body", smsBody); 
		    sendIntent.setType("vnd.android-dir/mms-sms");
		    thisContext.startActivity(sendIntent);
		}
		if (view.getId() == R.id.btnRemove) {
			sentence = (Sentence) view.getTag();
			sentence.isDeleted = true;
			sql.UpdateSentence(sentence);
			remove(sentence);
			notifyDataSetChanged();
		}
		if (view.getId() == R.id.tvJomlak) {
			i = ((Integer) view.getTag()).intValue();
			sentence = (Sentence) getItem(i);
			sentence.isReaded = true;
			sql.UpdateSentence(sentence);
			notifyDataSetChanged();
			b.a(i);
		}
		
	}

	@Override
	public boolean onLongClick(View view) {
		TextView tv = (TextView) view;
    	String text = tv.getText().toString().trim();
		
		MyClipboardManager clipboardManager = new MyClipboardManager();
		clipboardManager.copyToClipboard(thisContext, text);
		App.ShowMessageBox(thisContext, "متن در حافظه کپی شد.");
		return false;
	}

}
