package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.ConnectivityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndoor on 11/24/2016.
 */

public class SearchLoader extends AsyncTaskLoader<List<Book>> {
    private String mUrl;

    public SearchLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    // Check to see if the network is connected.
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (isNetworkAvailable(getContext())) {
            // Get the information from the server.
            ArrayList<Book> bookResults = QueryUtils.fetchBookData(mUrl);
            return bookResults;
        } else {
            return null;
        }
    }
}
