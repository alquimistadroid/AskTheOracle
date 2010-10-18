package com.alquimista.android.asktheoracle.datasource;

import java.io.IOException;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.alquimista.android.asktheoracle.GoogleTranslateHelper;
import com.alquimista.android.asktheoracle.HttpProgressListener;
import com.alquimista.android.asktheoracle.MediaWikiHelper;
import com.alquimista.android.asktheoracle.preference.SettingPreference;

public class DataFactory {

	public final static boolean DEBUG = true;
	public final static String TAG = "AskTheOracle.DataFactory";

	private static Context mContext;

	public DataFactory( Context context )
	{
		mContext = context;
	}

	public void getDefinition(String query, HttpProgressListener listener )
		throws IOException
	{
		Context context = mContext;
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String language = sharedPref.getString(SettingPreference.INPUT_LANGUAGE, SettingPreference.DEFAULT_VALUE_INPUT_LANGUAGE);

		try
		{
			String s = "";
			if ( sharedPref.getBoolean( SettingPreference.GT_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
			{
				if ( DEBUG ) Log.d( TAG, "google translate enabled");
				s = GoogleTranslateHelper.translate(query, language, "id", listener);
				listener.onProgressLoading(HttpProgressListener.REQUEST_CODE_GT, s);
			}

			if ( sharedPref.getBoolean( SettingPreference.WIKTIONARY_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
			{
				if ( DEBUG ) Log.d( TAG, "wiktionary enabled");
				s = MediaWikiHelper.getDefinition(query, MediaWikiHelper.TYPE_WIKTIONARY, language);
				listener.onProgressLoading(HttpProgressListener.REQUEST_CODE_WIKTIONARY, s);
			}

			if ( sharedPref.getBoolean( SettingPreference.WIKIPEDIA_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
			{
				if ( DEBUG ) Log.d( TAG, "wikipedia enabled");
				s = MediaWikiHelper.getDefinition(query, MediaWikiHelper.TYPE_WIKIPEDIA, language);
				listener.onProgressLoading(HttpProgressListener.REQUEST_CODE_WIKIPEDIA, s);
			}
		}
		catch( RuntimeException re )
		{
			Log.d(TAG, re.getMessage() );
		}

	}

	public Collection<String> getSuggestion(String query, HttpProgressListener listener)
		throws IOException
	{

		Context context = mContext;
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String language = sharedPref.getString(SettingPreference.INPUT_LANGUAGE, SettingPreference.DEFAULT_VALUE_INPUT_LANGUAGE);
		Set<String> treeSet = new TreeSet<String>(sComparator);

		try
		{
			if ( sharedPref.getBoolean( SettingPreference.WIKTIONARY_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
			{
				if ( DEBUG ) Log.d( TAG, "wiktionary enabled");
				treeSet.addAll( MediaWikiHelper.getSuggestion(query, MediaWikiHelper.TYPE_WIKTIONARY, language, listener) );
			}

			if ( sharedPref.getBoolean( SettingPreference.WIKIPEDIA_ENABLE, SettingPreference.DEFAULT_VALUE_ENABLE_SOURCE) )
			{
				if ( DEBUG ) Log.d( TAG, "wikipedia enabled");
				treeSet.addAll( MediaWikiHelper.getSuggestion(query, MediaWikiHelper.TYPE_WIKIPEDIA, language, listener) );
			}
		}
		catch( RuntimeException re )
		{
			Log.d(TAG, re.getMessage() );
		}

		return treeSet;
	}


    private final static Comparator<String> sComparator = new Comparator<String>() {
        private final Collator collator = Collator.getInstance();

        public int compare(String str1, String str2) {
            return collator.compare( str1.toLowerCase(), str2.toLowerCase() );
        }
    };




}
