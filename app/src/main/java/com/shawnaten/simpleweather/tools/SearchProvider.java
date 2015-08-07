package com.shawnaten.simpleweather.tools;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SearchProvider extends ContentProvider {
	private final String[] pColumns = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, 
			SearchManager.SUGGEST_COLUMN_INTENT_DATA};
	
	private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URIMatcher.addURI("com.shawnaten.simpleweather.searchprovider", SearchManager.SUGGEST_URI_PATH_QUERY+"/*", 0);
	}

    @Override
	public Cursor query(Uri uri, String[] arg1, String arg2, String[] arg3, String arg4) {
		String query;
		query = uri.getLastPathSegment();
		switch (URIMatcher.match(uri)) {
		case 0:
            if (!query.equals("search_suggest_query")) {
                MatrixCursor cursor = new MatrixCursor(pColumns);

                /*
                Places.AutocompleteResponse autocomplete =
                        Network.getInstance().getAutocompleteSync(query);

                if (autocomplete != null && autocomplete.getStatus().equals(Network.PLACES_STATUS_OK)) {
                    for (int i = 0; i < autocomplete.getPredictions().length; i++) {
                        cursor.addRow(new Object[]{i, autocomplete.getPredictions()[i].getDescription(),
                                autocomplete.getPredictions()[i].getPlace_id()});
                    }
                    return cursor;
                }
                */
            }
		}		
		return null;
	}

    @Override
    public String getType(Uri uri) {
        String MIME;
        switch (URIMatcher.match(uri)) {
            case 0:
                MIME = String.format("%s%s%s", "vnd.android.cursor.dir/", "vnd.com.shawnaten.simpleweather.searchprovider.",
                        SearchManager.SUGGEST_URI_PATH_QUERY);
                return MIME;
        }
        return null;
    }

    // unused methods

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
        return 0;
    }

    @Override
    public Uri insert(Uri arg0, ContentValues arg1) {
        return null;
    }

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

}
