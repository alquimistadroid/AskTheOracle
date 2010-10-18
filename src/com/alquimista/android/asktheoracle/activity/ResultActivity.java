package com.alquimista.android.asktheoracle.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alquimista.android.asktheoracle.HttpProgressListener;
import com.alquimista.android.asktheoracle.R;
import com.alquimista.android.asktheoracle.datasource.DataFactory;
import com.alquimista.android.asktheoracle.preference.SettingPreference;
import com.alquimista.android.asktheoracle.widget.ResultItemView;

public class ResultActivity extends Activity {
	private final static String TAG ="AskTheOracle.ResultActivity";
	private final static boolean DEBUG = true;

	private ResultItemView mGoogleTranslate;
	private ResultItemView mWiktionary;
	private ResultItemView mWikipedia;

	private boolean returnImmediately = true;

	private static ResultWorker mWorker = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.result);

        mGoogleTranslate = (ResultItemView) findViewById(R.id.google_translate_result);
        mWiktionary = (ResultItemView) findViewById(R.id.wiktionary_result);
        mWikipedia = (ResultItemView) findViewById(R.id.wikipedia_result);

		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// check google translate setting
		if( sharedPref.getBoolean(SettingPreference.GT_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
			if ( DEBUG ) Log.d(TAG, "google_translate enabled");
			returnImmediately = false;
			mGoogleTranslate.setVisibility(View.VISIBLE);
			mGoogleTranslate.showLoading(true);

		}

		// check wiktionary setting
		if( sharedPref.getBoolean(SettingPreference.WIKTIONARY_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
			if ( DEBUG ) Log.d(TAG, "wiktionary enabled");
			returnImmediately = false;
			mWiktionary.setVisibility(View.VISIBLE);
			mWiktionary.showLoading(true);

		}

		// check wikipedia setting
		if( sharedPref.getBoolean(SettingPreference.WIKIPEDIA_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
		{
			if ( DEBUG ) Log.d(TAG, "wikipedia enabled");
			returnImmediately = false;
			mWikipedia.setVisibility(View.VISIBLE);
			mWikipedia.showLoading(true);

		}



        Intent intent = getIntent();

        String query = intent.getStringExtra("query");

        if ( returnImmediately )
        {
			Log.i( TAG, "no source has been selected or query is empty, return immediately");
        	finish();
        }
        else
        {
        	cancelWorker();
        	mWorker = new ResultWorker();
        	mWorker.execute(query);
        }
	}

	@Override
	public boolean onSearchRequested() {
		return false;
	}

    private void cancelWorker()
    {
    	if ( mWorker != null )
		{
			mWorker.cancel(true);
			mWorker = null;
		}
    }



	private void updateView(int requestCode, String result)
	{

		ResultItemView pointer = null;
		switch (requestCode) {
		case HttpProgressListener.REQUEST_CODE_GT:
			pointer = mGoogleTranslate;
			break;
		case HttpProgressListener.REQUEST_CODE_WIKTIONARY:
			pointer = mWiktionary;
			break;
		case HttpProgressListener.REQUEST_CODE_WIKIPEDIA:
			pointer = mWikipedia;
			break;
		}

		if ( pointer != null )
		{
			if ( ! TextUtils.isEmpty( result ) )
			{
				TextView tv = new TextView(this);
				tv.setText(result);
				pointer.setContent(tv);
			}
			pointer.showLoading(false);
		}
	}


	private class ResultItem{
		int mRequestCode;
		String mQuery;

		public ResultItem(int requestCode, String query)
		{
			mRequestCode = requestCode;
			mQuery = query;
		}
	}

	private class ResultWorker extends AsyncTask<String, ResultItem, Integer>
		implements HttpProgressListener
	{

		@Override
		protected Integer doInBackground(String... params) {
			try {
    			new DataFactory(ResultActivity.this).getDefinition(params[0], this);
			} catch (IOException ie) {
				return -1;
			}

			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			final int error = result.intValue();
			if ( error == -1 )
				Toast.makeText(ResultActivity.this, getString(R.string.error_result), Toast.LENGTH_SHORT);
		}

		@Override
		protected void onProgressUpdate(ResultItem... values) {

			for (final ResultItem resultItem : values) {
				ResultActivity.this.runOnUiThread( new Runnable() {

					@Override
					public void run() {
						updateView(resultItem.mRequestCode,resultItem.mQuery);
					}
				});

			}

		}

		@Override
		public void onProgressLoading(int requestCode, String result) {
			publishProgress(new ResultItem(requestCode, result));
		}
	}
}
