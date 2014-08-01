package com.shawnaten.tools;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.shawnaten.networking.Network;
import com.shawnaten.networking.Places;

import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SearchProvider extends ContentProvider implements Callback<Places.AutocompleteResponse> {
	private final String[] pColumns = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1, 
			SearchManager.SUGGEST_COLUMN_INTENT_DATA};
	
	private Places.AutocompleteResponse autocomplete;

    private final Object sync = new Object();
	
	private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URIMatcher.addURI("com.shawnaten.weather.searchprovider", SearchManager.SUGGEST_URI_PATH_QUERY+"/*", 0);
	}

    @Override
	public Cursor query(Uri uri, String[] arg1, String arg2, String[] arg3, String arg4) {
		String query;
		query = uri.getLastPathSegment();
		switch (URIMatcher.match(uri)) {
		case 0:
            if (!query.equals("search_suggest_query")) {
                MatrixCursor cursor = new MatrixCursor(pColumns);

                Network.getInstance(getContext()).getAutocomplete(query, Locale.getDefault().getLanguage(), this);

                waitForData();

                if (autocomplete != null && autocomplete.getStatus().equals("OK")) {
                    for (int i = 0; i < autocomplete.getPredictions().length; i++) {
                        cursor.addRow(new Object[]{i, autocomplete.getPredictions()[i].getDescription(),
                                autocomplete.getPredictions()[i].getPlace_id()});
                    }
                }

                return cursor;
            }
		}		
		return null;
	}

    @Override
    public void success(Places.AutocompleteResponse autocompleteResponse, Response response) {
        synchronized (sync) {
            autocomplete = autocompleteResponse;
            sync.notify();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        synchronized (sync) {
            autocomplete = null;
            sync.notify();
        }
        Log.e("Retrofit", error.getMessage());
    }

    @Override
    public String getType(Uri uri) {
        String MIME;
        switch (URIMatcher.match(uri)) {
            case 0:
                MIME = String.format("%s%s%s", "vnd.android.cursor.dir/", "vnd.com.shawnaten.weather.searchprovider.",
                        SearchManager.SUGGEST_URI_PATH_QUERY);
                return MIME;
        }
        return null;
    }

    private void waitForData() {
        synchronized(sync) {
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
