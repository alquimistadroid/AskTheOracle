package com.alquimista.android.asktheoracle;

import java.util.Locale;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.provider.SearchRecentSuggestions;

public class SettingsPreference extends PreferenceActivity {

	public final static String GT_ENABLE = "gt_enable";
	public final static String WIKTIONARY_ENABLE = "wiktionary_enable";
	public final static String WIKIPEDIA_ENABLE = "wikipedia_enable";


	public final static String INPUT_LANGUAGE = "input_language";
	public final static String PRIVACY_CLEAR_HISTORY = "privacy_clear_history";

	public final static String DEFAULT_VALUE_INPUT_LANGUAGE = "en";
	public final static boolean DEFAULT_VALUE_ENABLE_SOURCE = true;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // by default if user never select input language,
        // it will choose hh language
        ListPreference lp = (ListPreference) findPreference(INPUT_LANGUAGE);
        if ( lp.getValue() == null )
        {
        	lp.setValue(getDefaultLanguage());
        }
	}

	/**
	 * get default language codes
	 * @return string codes for the supported language
	 */
	private String getDefaultLanguage()
	{
		String local = Locale.getDefault().getLanguage();

		for (String str : getResources().getStringArray(R.array.language_list_values)) {
			if ( local.equals(str) )
			{
				return str;
			}
		}
		return DEFAULT_VALUE_INPUT_LANGUAGE;
	}

}
