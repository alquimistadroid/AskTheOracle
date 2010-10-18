package com.alquimista.android.asktheoracle.activity;

import java.util.Locale;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

import com.alquimista.android.asktheoracle.R;
import com.alquimista.android.asktheoracle.preference.SettingPreference;

public class SettingsPreferenceActivity extends PreferenceActivity {

	public final static String TAG = "AskTheOracle.SettingsPreferenceActivity";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // by default if user never select input language,
        // it will choose hh language
        ListPreference lp = (ListPreference) findPreference(SettingPreference.GT_OUTPUT_LANGUAGE);
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
		return SettingPreference.DEFAULT_VALUE_GT_OUTPUT_LANGUAGE;
	}

}
