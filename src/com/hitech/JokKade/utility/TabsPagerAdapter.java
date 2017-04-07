package com.hitech.JokKade.utility;

import com.hitech.JokKade.Sentences;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {
	private static final String[] CONTENT = new String[] { "ŒÊ«‰œÂ ‰‘œÂ Â«","‰‘«‰ ‘œÂ Â«" ,"ŒÊ«‰œÂ ‘œÂ Â«"};

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	 @Override
     public Fragment getItem(int position) {
         return new Sentences(CONTENT[position % CONTENT.length]);
     }

	 @Override
     public CharSequence getPageTitle(int position) {
         return CONTENT[position % CONTENT.length].toUpperCase();
     }
	 
     @Override
     public int getCount() {
       return CONTENT.length;
     }
     
     @Override
     public int getItemPosition(Object object) {
    	    return POSITION_NONE;
    	}
 }