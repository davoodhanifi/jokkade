package com.hitech.JokKade;

import java.util.ArrayList;


import com.hitech.JokKade.utility.NavDrawerItem;
import com.hitech.JokKade.utility.NavDrawerListAdapter;
import com.hitech.JokKade.utility.TypeListAdapter;
import com.hitech.JokKade.utility.TypeListItem;
import com.hitech.JokKade.R;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ChooseType extends Activity {

	private String[] typesArray;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_type);
	    context = this;
		ArrayList<String> tasks = new ArrayList<String>();

		getActionBar().hide();
		ListView types = (ListView) findViewById(R.id.typeList);
		typesArray = getResources().getStringArray(R.array.types);
		ArrayList<TypeListItem> items = new ArrayList<TypeListItem>();
		for (int i = 0; i < typesArray.length; i++)
			items.add(new TypeListItem(typesArray[i]));

		types.setOnItemClickListener(new TypeClickListener());

		TypeListAdapter adapter = new TypeListAdapter(context, items);
		types.setAdapter(adapter);
	
	}

	private class TypeClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			getIntent().putExtra("type", typesArray[position]);
			finish();
		}
	}
}