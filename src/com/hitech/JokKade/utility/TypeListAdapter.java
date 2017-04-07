package com.hitech.JokKade.utility;

import java.util.ArrayList;

import com.hitech.JokKade.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TypeListAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TypeListItem> typeItems;

	public TypeListAdapter(Context context,
			ArrayList<TypeListItem> typeListItems) {
		this.context = context;
		this.typeItems = typeListItems;
	}

	@Override
	public int getCount() {
		return typeItems.size();
	}

	@Override
	public Object getItem(int position) {
		return typeItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.activity_choose_type, null);
		}

		//ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
		// TextView txtCount = (TextView)
		// convertView.findViewById(R.id.counter);

		txtTitle.setText(typeItems.get(position).getTitle());

		// displaying count
		// check whether it set visible or not

		return convertView;
	}

}
