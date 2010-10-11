package com.alquimista.android.asktheoracle;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.admob.android.ads.AdManager;

public class MainActivity extends Activity {
	public final static String TAG ="AskTheOracle.MainActivity";
	public final static boolean DEBUG = true;


	private RelativeLayout mInstructionView;
	private LinearLayout mSearchView;
	private ListView mSearchListView;

	private TextView mSearchKeyword;
	private ProgressBar mSearchListProgress;

	private static boolean mIsShowInstruction = true;
	private String mKeyword;
	private String mLanguage;

	static SearchRecentSuggestions mRecentSuggestions;

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Cursor c = (Cursor) parent.getAdapter().getItem(position);
			String s = c.getString(c.getColumnIndex(AskTheOracleProvider.SUGGEST_COLUMNS_INTENT_DATA));
			showResultActivity(s);
		}

	};

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        if ( DEBUG ) Log.d(TAG, "onCreate("+ savedInstanceState +")" );

        mInstructionView = (RelativeLayout) findViewById(R.id.instruction);
        mSearchView = (LinearLayout) findViewById(R.id.search_view);
        mSearchListView = (ListView) findViewById(R.id.search_list);
        mSearchListView.setOnItemClickListener( itemClickListener );

        mSearchKeyword = (TextView) findViewById(R.id.search_keyword);
        mSearchListProgress = (ProgressBar) findViewById(R.id.search_progress);

        mRecentSuggestions = new SearchRecentSuggestions(this,
                SearchRecentProvider.AUTHORITY, SearchRecentProvider.MODE);

        AdManager.setTestDevices( new String[] {
        	     AdManager.TEST_EMULATOR,             // Android emulator
        	} );

        handleIntent(getIntent());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

//    	if ( DEBUG ) Log.d(TAG, "keycode = "+ keyCode + ", event : " + event.toString() );

    	switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
	    	if (!mIsShowInstruction )
	    	{
	    		showInstructionView(true);
	    		return false;
	    	}
		default:
			break;
		}


    	return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onSearchRequested() {
    	if ( DEBUG ) Log.d(TAG, "onSearchRequested");

    	startSearch(null, false, null, false);

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
        	setKeyword(intent.getStringExtra( SearchManager.QUERY ) );

    		ArrayList<String> al = new ArrayList<String>();

    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    		setLanguage(sharedPref.getString(SettingsPreference.INPUT_LANGUAGE,
					SettingsPreference.DEFAULT_VALUE_INPUT_LANGUAGE) );

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

    		int size = al.size ();

    		if ( size > 0 )
    		{
	        	showInstructionView(false);
	        	refreshSearchList(null);

	    		String params [] = new String [size];
	    		al.toArray (params);
	    		if ( DEBUG ) Log.d(TAG, params.toString());

	        	new ResultListWorker().execute(params);
    		}


        }
        else if( Intent.ACTION_VIEW.equals( intent.getAction() ) )
        {
        	String query = intent.getDataString();

        	if ( DEBUG ) Log.d(TAG, "Intent.ACTION_VIEW, query:"+query );

        	showResultActivity(query);
        }
    }

    private void refreshSearchList(Cursor c)
    {
    	if ( c != null )
    	{
	    	startManagingCursor(c);
	    	ListAdapter adapter = new SimpleCursorAdapter(this,
	                // Use a template that displays a text view
	                android.R.layout.simple_list_item_1,
	                // Give the cursor to the list adatper
	                c,
	                // Map the NAME column in the people database to...
	                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 } ,
	                // The "text1" view defined in the XML template
	                new int[] {android.R.id.text1});
	        mSearchListView.setAdapter(adapter);
    	}
    	else
    		mSearchListView.setAdapter(null);
    }

    private void showResultActivity(String query)
    {
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra("query", query);

		// Record the query string in the recent queries suggestions provider.
      	mRecentSuggestions.saveRecentQuery(query, null);

		startActivity(intent);
    }

    private void showInstructionView( boolean showInstruction )
    {
    	if ( showInstruction != mIsShowInstruction )
    	{
    		mInstructionView.setVisibility( showInstruction ? View.VISIBLE : View.GONE );
    		mSearchView.setVisibility( showInstruction ? View.GONE : View.VISIBLE );
    		mIsShowInstruction = showInstruction;
    	}
    }

    private String getKeyword()
    {
    	return mKeyword;
    }

    private void setKeyword( String keyword )
    {
    	mKeyword = keyword;
    }

    private String getLanguage()
    {
    	return mLanguage;
    }

    private void setLanguage( String language )
    {
    	mLanguage = language;
    }

    private void setListTitle(String title)
    {
    	mSearchKeyword.setText(title);
    }

    private void setResultProgress(boolean progress)
    {
    	mSearchListProgress.setVisibility(progress? View.VISIBLE : View.INVISIBLE);
    }

    private class ResultListWorker extends AsyncTask<String, Void, Cursor>
    {
    	@Override
    	protected Cursor doInBackground(String... params) {
    		Uri uri;

    		String language = getLanguage();

    		uri = Uri.withAppendedPath(
					AskTheOracleProvider.CONTENT_URI,
					SearchManager.SUGGEST_URI_PATH_QUERY);
			uri = Uri.withAppendedPath(uri, language);
			uri = Uri.withAppendedPath(uri, getKeyword());

    		return getContentResolver().query(uri, null, null, params, null);
    	}

    	@Override
    	protected void onPreExecute() {

    		MainActivity.this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
            		setResultProgress(true);
            		setListTitle(getResources().getString(R.string.loading_please_wait));
				}
			});
    	}

    	@Override
    	protected void onPostExecute(Cursor result) {
    		final Cursor c = result;

    		MainActivity.this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					setResultProgress(false);
        			if ( c == null )
            		{
            			setListTitle( getString(R.string.error_result) );
            		}
            		else if ( c.getCount() == 0 )
            		{
            			setListTitle( getString(R.string.empty_result) );
            		}
            		else
            		{
        				refreshSearchList(c);
            			setListTitle( getString(R.string.search_results, c.getCount(), getKeyword() ) );
            		}

				}
			});
    	}
    }

}