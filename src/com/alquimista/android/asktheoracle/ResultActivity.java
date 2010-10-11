package com.alquimista.android.asktheoracle;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {
	private final static String TAG ="AskTheOracle.ResultActivity";
	private final static boolean DEBUG = true;

	private ResultItemView mGoogleTranslate;
	private ResultItemView mWikipedia;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);




		ArrayList<String> al = new ArrayList<String>();

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		sharedPref.getString(SettingsPreference.INPUT_LANGUAGE,
				SettingsPreference.DEFAULT_VALUE_INPUT_LANGUAGE);

		// check wiktionary setting
		if( sharedPref.getBoolean(SettingsPreference.GT_ENABLE, SettingsPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
//			al.add( MediaWikiHelper.TYPE );
			if ( DEBUG ) Log.d(TAG, "wiktionary enabled");
		}

		// check wiktionary setting
		if( sharedPref.getBoolean(SettingsPreference.WIKTIONARY_ENABLE, SettingsPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
			al.add( MediaWikiHelper.TYPE_WIKTIONARY );
			if ( DEBUG ) Log.d(TAG, "wiktionary enabled");
		}

		// check wikipedia setting
		if( sharedPref.getBoolean(SettingsPreference.WIKIPEDIA_ENABLE, SettingsPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
			al.add( MediaWikiHelper.TYPE_WIKIPEDIA );
			if ( DEBUG ) Log.d(TAG, "wikipedia enabled");
		}


        mGoogleTranslate = (ResultItemView) findViewById(R.id.google_translate_result);
        mWikipedia = (ResultItemView) findViewById(R.id.wikipedia_result);

        Intent intent = getIntent();

        String query = intent.getStringExtra("query");

        if ( DEBUG ) Log.d(TAG, "onCreate, query:"+query );

    	TextView tv = new TextView(this);
    	if ( DEBUG ) Log.d(TAG, "A");
    	tv.setText(query);
    	if ( DEBUG ) Log.d(TAG, "B");

    	mGoogleTranslate.setContent(tv);

    	if ( DEBUG ) Log.d(TAG, "C");
    	mGoogleTranslate.showLoading(true);
    	if ( DEBUG ) Log.d(TAG, "D");
    	mGoogleTranslate.setVisibility(View.VISIBLE);
    	if ( DEBUG ) Log.d(TAG, "E");

    	mWikipedia.showLoading(true);
    	if ( DEBUG ) Log.d(TAG, "F");
    	mWikipedia.setVisibility(View.VISIBLE);
    	if ( DEBUG ) Log.d(TAG, "G");
	}
}
