package com.alquimista.android.asktheoracle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.admob.android.ads.AdManager;


public class MainActivity extends Activity {
	public final static String TAG ="AskTheOracle.MainActivity";
	public final static boolean DEBUG = true;

	private final static int INTERNAL_REQUEST_CODE = 0;
	private final static int GLOBAL_REQUEST_CODE = 1;

	private RelativeLayout mInstructionView;
	private ListView mSearchListView;

	private static boolean mIsShowInstruction = true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setContentView(R.layout.main);

        mInstructionView = (RelativeLayout) findViewById(R.id.instruction);
        mSearchListView = (ListView) findViewById(R.id.search_list);


        AdManager.setTestDevices( new String[] {
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	} );

        handleIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

    	if ( DEBUG ) Log.d(TAG, "keycode = "+ keyCode +", mIsShowInstruction:"+mIsShowInstruction );

    	if ( ( KeyEvent.KEYCODE_BACK == keyCode ) && !mIsShowInstruction )
    	{
    		showInstructionView(true);
    		return false;
    	}
    	else if ( KeyEvent.KEYCODE_DPAD_CENTER == keyCode )
    	{
    		showInstructionView(false);
    		//showResultActivity();
    		//startActivityForResult(intent, 1);


    		//preprareResultLayout(false);
    		return false;
    	}

    	return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
    	if ( DEBUG ) Log.d(TAG, "onSearchRequested");


    	return super.onSearchRequested();
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
	    		Intent intent = new Intent().setClass(this, SettingsPreference.class);
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

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
    	if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) )
        {
        	String query = intent.getStringExtra( SearchManager.QUERY );

        	if ( DEBUG ) Log.d(TAG, "Intent.ACTION_SEARCH, query:" + query );

        	showInstructionView(false);

        	//TODO: do(query)
        }
        else if( Intent.ACTION_VIEW.equals( intent.getAction() ) )
        {
        	if ( DEBUG ) Log.d(TAG, "Intent.ACTION_VIEW" );
        }
    }

    private void showResultActivity()
    {
		Intent intent = new Intent(this, ResultActivity.class);
		startActivity(intent);
    }

    private void showInstructionView( boolean showInstruction )
    {
    	if ( showInstruction != mIsShowInstruction )
    	{
    		if ( DEBUG ) Log.d(TAG, showInstruction ? "SHOW" : "GONE" );
    		mInstructionView.setVisibility( showInstruction ? View.VISIBLE : View.GONE );
    		mSearchListView.setVisibility( showInstruction ? View.GONE : View.VISIBLE );
    		mIsShowInstruction = showInstruction;
    	}
    }

}