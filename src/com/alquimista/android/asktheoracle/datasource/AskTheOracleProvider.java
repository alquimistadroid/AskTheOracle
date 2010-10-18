package com.alquimista.android.asktheoracle.datasource;

import java.io.IOException;
import java.util.Collection;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class AskTheOracleProvider extends ContentProvider {

	public final static String TAG ="AskTheOracle.SearchSuggestionProvider";
	public final static boolean DEBUG = true;

	public static final Uri CONTENT_URI =
        Uri.parse("content://com.alquimista.android.asktheoracle.AskTheOracleProvider");

	public final static String AUTHORITY = "com.alquimista.android.asktheoracle.AskTheOracleProvider";


	public final static String SUGGEST_URI_PATH_QUERY = SearchManager.SUGGEST_URI_PATH_QUERY;
//	public final static String GOOGLE_TRANSLATE_URI_PATH_QUERY = "search_google_query";
//	public final static String WIKIPEDIA_URI_PATH_QUERY = "search_wikipedia_query";
//	public final static String WIKTIONARY_URI_PATH_QUERY = "search_wiktionary_query";

//	public final static String GOOGLE_TRANSLATE_MIME_TYPE = "vnd.android.cursor.item/vnd.asktheoracle.googletranslate";
//	public final static String WIKIPEDIA_MIME_TYPE = "vnd.android.cursor.item/vnd.asktheoracle.wikipedia";
//	public final static String WIKTIONARY_MIME_TYPE = "vnd.android.cursor.item/vnd.asktheoracle.wiktionary";

	private static final int SEARCH_SUGGEST = 0;
//	private static final int SEARCH_GOOGLE_TRANSLATE = 1;
//	private static final int SEARCH_WIKIPEDIA = 2;
//	private static final int SEARCH_WIKTIONARY = 3;

//	private static final String COLUMNS_DEFINITION = "columns_definition";

    private static final UriMatcher sURIMatcher = buildUriMatcher();

//    public static final String[] SEARCH_COLUMNS = {
//        "_id",  // must include this column
//        COLUMNS_DEFINITION
//        };

    public static final String SUGGEST_COLUMNS_KEYWORD = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String SUGGEST_COLUMNS_INTENT_DATA = SearchManager.SUGGEST_COLUMN_INTENT_DATA;

	public static final String[] SEARCH_SUGGEST_COLUMNS = {
        "_id",  // must include this column
        SUGGEST_COLUMNS_KEYWORD,
        SUGGEST_COLUMNS_INTENT_DATA
        };


	/**
     * Sets up a uri matcher for search suggestion and shortcut refresh queries.
     */
    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SUGGEST_URI_PATH_QUERY + "/*/*", SEARCH_SUGGEST);
//        matcher.addURI(AUTHORITY, GOOGLE_TRANSLATE_URI_PATH_QUERY + "/*", SEARCH_GOOGLE_TRANSLATE);
//        matcher.addURI(AUTHORITY, WIKIPEDIA_URI_PATH_QUERY + "/*", SEARCH_WIKIPEDIA);
//        matcher.addURI(AUTHORITY, WIKTIONARY_URI_PATH_QUERY + "/*", SEARCH_WIKTIONARY);

        return matcher;
    }

	@Override
	public boolean onCreate() {
		return true;
	}



	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{

		if (DEBUG) Log.d( TAG, "query(uri:"+
				uri.toString()+", projection:"+ projection +", selection:"+selection+
				", selectionArgs:"+selectionArgs+", sortOrder:"+sortOrder+")");

        if (!TextUtils.isEmpty(sortOrder)) {
            throw new IllegalArgumentException("sortOrder not allowed for " + uri);
        }

		String query = null;

		switch (sURIMatcher.match(uri)) {
		case SEARCH_SUGGEST:
			if (uri.getPathSegments().size() > 1) {
				MatrixCursor c = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
				DataFactory df = new DataFactory( getContext() );
				query = uri.getLastPathSegment().toLowerCase();
				try {
					Collection<String> collection = df.getSuggestion(query, null);

					int i = 0;
					for (String str : collection) {
						c.addRow(new Object[] { i++, str, str });
					}
				}
				catch (IOException ie) {
					//throw new RuntimeException(TAG + " : no connection");
					Log.e(TAG, "no connection!" );
				}


				return c;
			}
			return null;
//		case SEARCH_GOOGLE_TRANSLATE:
//		case SEARCH_WIKIPEDIA:
//		case SEARCH_WIKTIONARY:
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

    /**
     * All queries for this provider are for the search suggestion and shortcut refresh mime type.
     */
	@Override
	public String getType(Uri uri) {
		if (DEBUG) Log.d( TAG, "getType(uri:"+uri.toString()+")");

		switch (sURIMatcher.match(uri)) {
	        case SEARCH_SUGGEST:
	            return SearchManager.SUGGEST_MIME_TYPE;
//			case SEARCH_GOOGLE_TRANSLATE:
//			case SEARCH_WIKIPEDIA:
//			case SEARCH_WIKTIONARY:
	        default:
	            throw new IllegalArgumentException("Unknown URL " + uri);
		}
	}

	/**
	 * unsupported operation
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * unsupported operation
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	/**
	 * unsupported operation
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}


}
