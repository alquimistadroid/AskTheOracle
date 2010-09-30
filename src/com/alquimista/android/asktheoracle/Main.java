package com.alquimista.android.asktheoracle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admob.android.ads.AdManager;


public class Main extends Activity {
	private final static String TAG ="AskTheOracle.Main";
	private final static boolean DEBUG = true;

	private ResultItemView mGoogleTranslate;
	private ResultItemView mWikipedia;

	private RelativeLayout mInstructionView;

	private static boolean mIsShowResult = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mGoogleTranslate = (ResultItemView) findViewById(R.id.google_translate_result);
        mWikipedia = (ResultItemView) findViewById(R.id.wikipedia_result);

        mInstructionView = (RelativeLayout) findViewById(R.id.instruction);

        AdManager.setTestDevices( new String[] {
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	} );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if ( ( KeyEvent.KEYCODE_BACK == keyCode ) && mIsShowResult )
    	{
    		preprareResultLayout(false);
    		return false;
    	}

    	return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
    	if ( DEBUG ) Log.d(TAG, "onSearchRequested");

    	// TODO Auto-generated method stub
    	super.onSearchRequested();

    	/*
    	mInstructionView.setVisibility(View.GONE);

    	TextView tv = new TextView(this);
    	tv.setText("this a text view created by onSearchRequested");

    	mGoogleTranslate.setContent(tv);
    	mGoogleTranslate.showLoading(true);
    	mGoogleTranslate.setVisibility(View.VISIBLE);

    	mWikipedia.showLoading(true);
    	mWikipedia.setVisibility(View.VISIBLE);
    	 */
    	preprareResultLayout( true );

    	TextView tv = new TextView(this);
    	tv.setText("this a text view created by onSearchRequested");

    	mGoogleTranslate.setContent(tv);

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

    	case R.id.search :
    		onSearchRequested();
    		return true;

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

    private void preprareResultLayout(boolean showResult)
    {

    	if ( showResult )
    	{
    		// show result search
    		mInstructionView.setVisibility(View.GONE);

    		//TODO: temporary
    		mGoogleTranslate.setVisibility(View.VISIBLE);
    		mWikipedia.setVisibility(View.VISIBLE);
    	}
    	else
    	{
    		// show instruction
    		mGoogleTranslate.setVisibility(View.GONE);
    		mWikipedia.setVisibility(View.GONE);
    		mInstructionView.setVisibility(View.VISIBLE);
    	}

    	mIsShowResult = showResult;
    }
}