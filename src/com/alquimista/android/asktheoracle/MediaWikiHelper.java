package com.alquimista.android.asktheoracle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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

	private static final int HTTP_STATUS_OK = 200;

	/**
     * User-agent string to use when making requests. Should be filled using
     * {@link #prepareUserAgent(Context)} before making any other calls.
     */
	private static String sUserAgent = null;
	private static String mLanguage;
	private static String mType;

	private static MediaWikiHelper sSingleton = null;

    private MediaWikiHelper() { };

    /**
     *
     * @param context
     * @param type
     * @param language
     * @return
     */
    public static MediaWikiHelper getTheHandler(Context context, String type, String language)
    {
    	if(sSingleton == null)
    	{
    		sSingleton = new MediaWikiHelper();
    		sSingleton.prepareUserAgent(context);
    	}

    	sSingleton.setLanguage(language);
    	sSingleton.setType(type);
    	return sSingleton;
    }

    /**
     *
     * @param language
     */
    public void setLanguage( String language )
    {
    	mLanguage = language;
    }

    /**
     *
     * @param type
     */
    public void setType( String type )
    {
    	mType = type;
    }

    /**
     *
     * @param query
     * @param showAll
     * @return
     * @throws IOException
     */
	public ArrayList<String> getSuggestion(String query, boolean showAll)
			throws IOException
	{
    	if (DEBUG) Log.d( TAG, "getSuggestions("+query+")");

    	if ( TextUtils.isEmpty(query) )
    		throw new IllegalArgumentException("No suggestion for empty string");

    	ArrayList<String> ar = new ArrayList<String>();

		InputStream inputStream = getUrlContent( String.format(
				MEDIAWIKI_SUGGEST_PAGE, mLanguage, mType,Uri.encode(query))
				+ (showAll ? "" : MEDIAWIKI_SUGGEST_LIMIT));

    	String result = convertStreamToString(inputStream);

    	try {
    		JSONArray root = new JSONArray(result);

    		JSONArray suggest = root.getJSONArray(1);

    		for ( int i = 0 ; i < suggest.length() ; i++ )
    		{
    			ar.add( suggest.getString(i) );
    		}

    	}
    	catch (JSONException je) {
    		throw new RuntimeException(je);
		}

    	return ar;
    }


    /**
     * Prepare the internal User-Agent string for use. This requires a
     * {@link Context} to pull the package name and version number for this
     * application.
     */
    private void prepareUserAgent(Context context) {
        try {
            // Read package name and version number from manifest
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            sUserAgent = String.format(context.getString(R.string.template_user_agent),
                    info.packageName, info.versionName);

        } catch(NameNotFoundException e) {
            Log.e(TAG, "Couldn't find package information in PackageManager", e);
        }
    }

    /**
     *
     * @param url
     * @return
     * @throws IOException
     */
    private InputStream getUrlContent(String url)
    	throws IOException
    {

    	if (DEBUG) Log.d( TAG, "getUrlContent("+url+")");

		// Create client and set our specific user-agent string
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", sUserAgent);

		HttpResponse response = client.execute(request);

		// Check if server response is valid
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != HTTP_STATUS_OK) {
			throw new RuntimeException( new HttpException("Invalid response from server: " + status.toString()));
		}

		// Pull content stream from response
		HttpEntity entity = response.getEntity();
		return entity.getContent();
	}

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


}
