package com.alquimista.android.asktheoracle;

import android.content.SearchRecentSuggestionsProvider;
import android.net.Uri;

public class SearchRecentProvider extends SearchRecentSuggestionsProvider {

	public final static String AUTHORITY = "com.alquimista.android.asktheoracle.SearchRecent";
	public final static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public final static int MODE = DATABASE_MODE_QUERIES;

	public SearchRecentProvider() {
        super();
        setupSuggestions(AUTHORITY, MODE);
    }
}
