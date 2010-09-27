package com.alquimista.android.asktheoracle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.admob.android.ads.AdManager;


public class Main extends Activity {
	private final String TAG ="AskTheOracle.Main";

	private ResultItemView mGoogleTranslate;
	private ResultItemView mWikipedia;

	private TextView mTextView;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

        mGoogleTranslate = (ResultItemView) findViewById(R.id.google_translate_result);
        mWikipedia = (ResultItemView) findViewById(R.id.wikipedia_result);

        mTextView = (TextView) findViewById(R.id.textview);

        AdManager.setTestDevices( new String[] {
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	} );
    }

    @Override
    public boolean onSearchRequested() {
    	Log.d(TAG, "onSearchRequested");

    	// TODO Auto-generated method stub
    	super.onSearchRequested();

    	mTextView.setVisibility(View.GONE);

    	TextView tv = new TextView(this);
    	tv.setText("this a text view created by onSearchRequested");

    	mGoogleTranslate.setContent(tv);
    	mGoogleTranslate.showLoading(true);
    	mGoogleTranslate.setVisibility(View.VISIBLE);

    	mWikipedia.showLoading(true);
    	mWikipedia.setVisibility(View.VISIBLE);

    	return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

    	switch (item.getItemId()) {

    	case R.id.settings:
    		Intent intent = new Intent().setClass(this, Settings.class);
    		startActivity(intent);
    		return true;

    	case R.id.about:
			showAbout();
			return true;
		}

    	return false;
    }

    private void showAbout() {

    	// Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        //TODO : create a good credits and mention it source or its  license

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.icon);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.setPositiveButton(R.string.about_donate, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO: looking for donate through paypal :)
				//Toast.makeText(getApplicationContext(), "Donate", Toast.LENGTH_SHORT).show();

			}
		});

        builder.create();
        builder.show();
    }
}