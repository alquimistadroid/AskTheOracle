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

//		[
//		 	[
//		 	 	["posting","post",""]
//		 	],
//		 	[
//		 	 	["noun",
//		 	 	 	["pos","jabatan","tiang","tempat","surat","tonggak","kotak pos","pusat","majalah","tempat tugas","turus","tempat semula"]
//		 	 	],
//		 	 	["verb",
//		 	 	 	["memasang","mencatat","menempatkan","mengumumkan","membukukan","mencatatkan","memberitahukan","mengirim melalui pos"]
//		 	 	],
//		 	 	["adverb",
//		 	 	 	["tergesa-gesa"]
//		 	 	]
//		 	 ],
//		 	 "en"
//		 ]
		try {
//			JSONObject root = new JSONObject(result);
//			JSONObject responseData = root.getJSONObject("responseData");
//
////			JSONObject translatedText = responseData.getJSONObject();
//			result = responseData.getString("translatedText");

			JSONArray root = new JSONArray(result);
			JSONArray translatedWord = root.getJSONArray(0);
			result = "++++"+translatedWord.getJSONArray(0).getString(0)+"++++";

			JSONArray dictionary = root.getJSONArray(1);

			result += "-------------------------------------";

			for ( int i = 0 ; i < dictionary.length() ; i++ )
			{
				JSONArray ar = dictionary.getJSONArray(i);
				result += "|=ZZZZ" + ar.getString(0)+"=";
				JSONArray word = ar.getJSONArray(1);
				for ( int j = 0 ; j < word.length() ; j++ )
				{
					result += "|" + word.getString(j);
				}
				result += "-------------------------------------";
			}

		} catch (JSONException je) {
    		throw new RuntimeException(je);
		}

		return result;
	}

	public static String plainTextToWebViewContent(String input)
	{

		return input;
	}
}
