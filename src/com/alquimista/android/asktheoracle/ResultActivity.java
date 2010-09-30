package com.alquimista.android.asktheoracle;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {
	private final static String TAG ="AskTheOracle.ResultActivity";

	private ResultItemView mGoogleTranslate;
	private ResultItemView mWikipedia;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);

        mGoogleTranslate = (ResultItemView) findViewById(R.id.google_translate_result);
        mWikipedia = (ResultItemView) findViewById(R.id.wikipedia_result);

    	TextView tv = new TextView(this);
    	tv.setText("this a text view created by onSearchRequested");

    	mGoogleTranslate.setContent(tv);
    	mGoogleTranslate.showLoading(true);
    	mGoogleTranslate.setVisibility(View.VISIBLE);

    	mWikipedia.showLoading(true);
    	mWikipedia.setVisibility(View.VISIBLE);
	}
}
