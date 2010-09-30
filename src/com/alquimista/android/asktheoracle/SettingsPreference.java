package com.alquimista.android.asktheoracle;

import java.util.Locale;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsPreference extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // by default if user never select input language,
        // it will choose hh language
        ListPreference lp = (ListPreference) findPreference("input_language");
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
		return "en";
	}

}
