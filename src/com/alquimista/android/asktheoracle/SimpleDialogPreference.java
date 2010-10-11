package com.alquimista.android.asktheoracle;

import android.content.Context;
import android.preference.DialogPreference;
import android.provider.SearchRecentSuggestions;
import android.util.AttributeSet;
import android.util.Log;

public class SimpleDialogPreference extends DialogPreference {
	private final static String TAG = "SimpleDialogPreference";
	private final static boolean DEBUG = true;

	public SimpleDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.yesNoPreferenceStyle);
	}

	public SimpleDialogPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			if( DEBUG ) Log.d(TAG, "getkey : " + getKey() + ", positive result");

			if( SettingsPreference.PRIVACY_CLEAR_HISTORY.equals( getKey() ) )
			{
//				SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
//				        SearchRecentProvider.AUTHORITY, SearchRecentProvider.MODE);
//				suggestions.clearHistory();
				MainActivity.mRecentSuggestions.clearHistory();

				setEnabled(false);
			}
		}
	}
}
