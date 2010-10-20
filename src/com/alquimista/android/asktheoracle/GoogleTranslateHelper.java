package com.alquimista.android.asktheoracle;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class GoogleTranslateHelper {
	public final static boolean DEBUG = true;
	public final static String TAG = "AskTheOracle.GoogleTranslateHelper";

	private static final String STYLE_SHEET = "<style>h1 {font-size:1.4em;font-weight:bold;margin-top:0.2em;margin-bottom:0.2em; } " +
			"h2 {font-size:0.8em;font-weight:bold;margin-top:0em;margin-bottom:0em;} " +
    		"ol {font-size:0.8em;margin-left:0.5em;margin-top:0em;margin-bottom:0em;} " +
    		"</style>";


//	private static final String GT_PAGE = "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&langpair=%s%%7C%s&q=%s";
	private static final String GT_PAGE = "http://translate.google.com/translate_a/t?client=t&sl=%s&tl=%s&pc=0&oc=1&text=%s";


	public static String translate ( String query, String from, String to , HttpProgressListener listener )
			throws IOException
	{
		if (DEBUG) Log.d( TAG, "translate("+query+")");

    	if ( TextUtils.isEmpty(query) )
    		throw new IllegalArgumentException("No translation for empty string");

    	InputStream inputStream = Utils.getUrlContent(String.format(GT_PAGE,
				from, to, Uri.encode(query)));

		String result = Utils.convertStreamToString(inputStream);

		try {
			JSONArray root = new JSONArray(result);
			JSONArray translatedWord = root.getJSONArray(0);
			result = "<h1>"+translatedWord.getJSONArray(0).getString(0)+"</h1>";

			JSONArray dictionary = root.getJSONArray(1);

			for ( int i = 0 ; i < dictionary.length() ; i++ )
			{
				JSONArray ar = dictionary.getJSONArray(i);
				result += "<h2>" + ar.getString(0)+"</h2>";
				JSONArray word = ar.getJSONArray(1);
				result += "<ol>";
				for ( int j = 0 ; j < word.length() ; j++ )
				{
					result += "<li>" + word.getString(j) + "</li>";
				}
				result += "</ol>";
			}

		} catch (JSONException je) {
    		if ( DEBUG ) Log.d( TAG , je.getMessage() );
		}

		return result;
	}

	public static String getDefaultStyle()
	{
		return STYLE_SHEET;
	}
}
