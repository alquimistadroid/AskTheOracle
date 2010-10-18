package com.alquimista.android.asktheoracle.preference;


public class SettingPreference {


	public final static boolean DEBUG = true;
	public final static String TAG = "AskTheOracle.SettingPreference";

	public final static String GT_ENABLE = "gt_enable";
	public final static String WIKTIONARY_ENABLE = "wiktionary_enable";
	public final static String WIKIPEDIA_ENABLE = "wikipedia_enable";

	public final static String GT_OUTPUT_LANGUAGE = "gt_output_language";

	public final static String INPUT_LANGUAGE = "input_language";
	public final static String PRIVACY_CLEAR_HISTORY = "privacy_clear_history";

	public final static String DEFAULT_VALUE_GT_OUTPUT_LANGUAGE = "en";
	public final static String DEFAULT_VALUE_INPUT_LANGUAGE = "en";
	public final static boolean DEFAULT_VALUE_ENABLE_SOURCE = true;



//	private static String mLanguage;
//	private static boolean gtEnabled;
//	private static boolean wiktionaryEnabled;
//	private static boolean wikipediaEnabled;
//
//	private static SettingPreference sSingleton = null;
//
//
//
//	private SettingPreference()
//	{
//
//	}
//
//	public static SettingPreference getTheHandler(Context context)
//	{
//		if ( sSingleton == null )
//		{
//			sSingleton = new SettingPreference();
//		}
//
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//
//		refreshLanguageInput(sharedPref);
//
//		refreshSourceSettings(sharedPref);
//
//		return sSingleton;
//	}
//
//
//	public String getLanguageInput()
//	{
//		return mLanguage;
//	}
//
//	public boolean isGoogleTranslateEnable()
//	{
//		return gtEnabled;
//	}
//
//	public boolean isWikipediaEnable()
//	{
//		return wikipediaEnabled;
//	}
//
//	public boolean isWiktionary()
//	{
//		return wiktionaryEnabled;
//	}
//
//
//	private static void refreshLanguageInput(SharedPreferences sharedPref)
//	{
//		mLanguage = sharedPref.getString(INPUT_LANGUAGE, DEFAULT_VALUE_INPUT_LANGUAGE);
//	}
//
//	private static void refreshSourceSettings(SharedPreferences sharedPref)
//	{
//
//		// check google translate setting
//		gtEnabled = sharedPref.getBoolean(GT_ENABLE, DEFAULT_VALUE_ENABLE_SOURCE);
//
//		if ( gtEnabled )
//		{
//			if ( DEBUG ) Log.d(TAG, "google translate enabled");
//
//		}
//
//		// check wiktionary setting
//		wiktionaryEnabled = sharedPref.getBoolean(WIKTIONARY_ENABLE, DEFAULT_VALUE_ENABLE_SOURCE);
//		if ( wiktionaryEnabled )
//		{
//			if ( DEBUG ) Log.d(TAG, "wiktionary enabled");
//
//		}
//
//		// check wikipedia setting
//		wikipediaEnabled = sharedPref.getBoolean(WIKIPEDIA_ENABLE, DEFAULT_VALUE_ENABLE_SOURCE);
//		if ( wikipediaEnabled )
//		{
//			if ( DEBUG ) Log.d(TAG, "wikipedia enabled");
//		}
//
//
//	}



}
