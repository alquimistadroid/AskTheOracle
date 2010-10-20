package com.alquimista.android.asktheoracle.activity;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.admob.android.ads.AdManager;
import com.alquimista.android.asktheoracle.HttpProgressListener;
import com.alquimista.android.asktheoracle.R;
import com.alquimista.android.asktheoracle.datasource.DataFactory;
import com.alquimista.android.asktheoracle.datasource.SearchRecentProvider;
import com.alquimista.android.asktheoracle.preference.SettingPreference;

public class MainActivity extends Activity {
	public final static String TAG ="AskTheOracle.MainActivity";
	public final static boolean DEBUG = true;


	private RelativeLayout mInstructionView;
	private LinearLayout mSearchView;
	private ListView mSearchListView;

	private TextView mSearchKeyword;
	private ProgressBar mSearchListProgress;

	private static boolean mIsShowInstruction = true;

	public static SearchRecentSuggestions mRecentSuggestions;

	private static SuggestListWorker mWorker = null;

	private ArrayAdapter<String> mAdapter = null;

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String s = (String) parent.getAdapter().getItem(position);
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

        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mSearchListView.setAdapter(mAdapter);

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

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
	    		cancelWorker();
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
	    		Intent intent = new Intent().setClass(this, SettingsPreferenceActivity.class);
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
    	String query = "";
    	if ( Intent.ACTION_SEARCH.equals( intent.getAction() ) )
        {
    		query = intent.getStringExtra( SearchManager.QUERY );

    		//TODO: exception
    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

    		if ( sharedPref.getBoolean( SettingPreference.WIKTIONARY_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE)
    				|| sharedPref.getBoolean( SettingPreference.WIKIPEDIA_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
    		{
    			showInstructionView(false);
    			cancelWorker();
    			mWorker = new SuggestListWorker();
    			mWorker.execute(query);
    		}
    		else
    		{
    			showResultActivity(query);
    		}
        }
        else if( Intent.ACTION_VIEW.equals( intent.getAction() ) )
        {
        	query = intent.getDataString();
        	showResultActivity(query);
        }
    }

    private void cancelWorker()
    {
    	if ( mWorker != null )
		{
			mWorker.cancel(true);
			mWorker = null;
		}
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


    private void setListTitle(String title)
    {
    	mSearchKeyword.setText(title);
    }

    private void setResultProgress(boolean progress)
    {
    	mSearchListProgress.setVisibility(progress? View.VISIBLE : View.INVISIBLE);
    }

    private class SuggestListWorker extends AsyncTask<String, String, Integer>
    	implements HttpProgressListener
    {
    	String mKeyword;
    	@Override
    	protected Integer doInBackground(String... params) {
    		mKeyword = params[0];
    		try {
    			new DataFactory(MainActivity.this).getSuggestion(mKeyword, this);
			} catch (IOException ie) {
				return -1;
			}

			return 0;
    	}

    	@Override
    	protected void onPreExecute() {
    		MainActivity.this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					mAdapter.clear();
            		setResultProgress(true);
            		setListTitle(getResources().getString(R.string.loading_please_wait));
				}
			});
    	}

    	@Override
    	protected void onProgressUpdate(String... values) {
    		for (String string : values) {
				mAdapter.add(string);
			}
    	}

    	@Override
    	protected void onPostExecute(Integer errorCode) {
    		final int count = mAdapter.getCount();
    		final int error = errorCode.intValue();

    		MainActivity.this.runOnUiThread( new Runnable() {
				@Override
				public void run() {
					setResultProgress(false);

					if ( count == 0 )
					{
						if ( error == -1 )
						{
							setListTitle( getString(R.string.error_result) );
						}
						else
						{
							setListTitle( getString(R.string.empty_result) );
						}
					}
					else
					{
						setListTitle( getString(R.string.search_results, count, mKeyword ) );
					}
				}
			});
    	}

		@Override
		public void onProgressLoading(int requestCode, String result) {
			publishProgress(result);
		}
    }

}