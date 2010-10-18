package com.alquimista.android.asktheoracle;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class MediaWikiHelper {
	private static final String TAG = "AskTheOracle.MediaWikiHelper";
	private static final boolean DEBUG = true;

	public final static String TYPE_WIKIPEDIA = "wikipedia";
	public final static String TYPE_WIKTIONARY = "wiktionary";


	public final static String LANGUAGE_INDONESIA = "id";
	public final static String LANGUAGE_ENGLISH = "en";
	public final static String LANGUAGE_JAPAN = "ja";
	public final static String LANGUAGE_GERMAN = "de";
	public final static String LANGUAGE_SPAIN = "es";
	public final static String LANGUAGE_FRANCE = "fr";
	public final static String LANGUAGE_RUSSIA = "ru";
	public final static String LANGUAGE_ITALY = "it";
	public final static String LANGUAGE_PORTUGAL = "pt";
	public final static String LANGUAGE_POLAND = "pl";
	public final static String LANGUAGE_NETHERLANDS = "nl";

	private final static String MEDIAWIKI_PAGE = "http://%s.%s.org/w/api.php?action=query&prop=revisions&titles=%s&" +
    		"rvprop=content&format=json";

	private final static String MEDIAWIKI_SUGGEST_PAGE = "http://%s.%s.org/w/api.php?action=opensearch&search=%s";

	private final static String MEDIAWIKI_SUGGEST_LIMIT = "&limit=3";

    private MediaWikiHelper() { };

    public static String getDefinition(String query, String type, String language)
    	throws IOException
    {
    	if (DEBUG) Log.d( TAG, "getDefinition("+query+")");

    	if ( TextUtils.isEmpty(query) )
    		throw new IllegalArgumentException("No suggestion for empty string");

		InputStream inputStream = Utils.getUrlContent(String.format(MEDIAWIKI_PAGE,
				language, type, Uri.encode(query)));

		String result = Utils.convertStreamToString(inputStream);

    	return result;
    }


    /**
     *
     * @param query
     * @param showAll
     * @return
     * @throws IOException
     */
	public static ArrayList<String> getSuggestion(String query, String type, String language, HttpProgressListener listener)
			throws IOException
	{
    	if (DEBUG) Log.d( TAG, "getSuggestions("+query+")");

    	if ( TextUtils.isEmpty(query) )
    		throw new IllegalArgumentException("No suggestion for empty string");

    	ArrayList<String> ar = new ArrayList<String>();

		InputStream inputStream = Utils.getUrlContent( String.format(
				MEDIAWIKI_SUGGEST_PAGE, language, type, Uri.encode(query))
				+ (listener == null ? MEDIAWIKI_SUGGEST_LIMIT : ""));

    	String result = Utils.convertStreamToString(inputStream);

    	try {
    		JSONArray root = new JSONArray(result);

    		JSONArray suggest = root.getJSONArray(1);

    		String s;

    		for ( int i = 0 ; i < suggest.length() ; i++ )
    		{
    			s = suggest.getString(i);
    			if ( listener == null )
    				ar.add( s );
    			else
    				listener.onProgressLoading(0, s);
    		}

    	}
    	catch (JSONException je) {
    		throw new RuntimeException(je);
		}

    	return ar;
    }

}
