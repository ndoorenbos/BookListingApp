package com.example.android.booklistingapp;

import android.content.Intent;
import android.content.Loader;
import android.app.LoaderManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    public String searchWords;
    public String searchURLString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Get the words string passed from the Intent and create the search URL String
        Intent searchIntent = new Intent(getIntent());
        searchWords = searchIntent.getExtras().getString("searchWords");
        searchURLString = QueryUtils.getSearchUrlString(searchWords);

        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Update the UI with the given book information
     * @param bookResults is the ArrayList of {@link Book} objects retrieved from the URL
     */
    private void updateUi(ArrayList<Book> bookResults) {
        // Create an ArrayList of {@link Book} objects and set the input ArrayList to it
        final ArrayList<Book> books = new ArrayList<Book>(bookResults);

        // Find the {@link ListView} with the ID book_list_view and make it visible
        ListView bookListView = (ListView) findViewById(R.id.book_list_view);
        bookListView.setVisibility(View.VISIBLE);

        // Create a new {@link BookAdapter} with {@link Book} objects
        BookAdapter bookAdapter = new BookAdapter(SearchActivity.this, books);

        // Set the adapter on the ListView so the list can be populated in the user interface
        bookListView.setAdapter(bookAdapter);

        // Create an OnItemClickListener for the book, links to the book's Google Books URL
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the URL from the current {@link Book} object
                Book currentBook = books.get(position);
                String bookUrlString = currentBook.getBookUrl();

                // Create a new Intent to open a web browser with the given URL String
                Intent bookWebIntent = new Intent(Intent.ACTION_VIEW);
                bookWebIntent.setData(Uri.parse(bookUrlString));
                startActivity(bookWebIntent);
            }
        });
    }

    // This method updates the UI in the case of no network connection
    private void noConnectionUI() {
        // Find the no connection TextView and make it visible
        TextView noConnectionTextView = (TextView) findViewById(R.id.no_connection_text_view);
        noConnectionTextView.setVisibility(View.VISIBLE);
    }

    // This method updates the UI in the case of no results
    private void tryAgainUI(){
        // Find the no results Linear Layout and make it visible
        LinearLayout noResultsLinearLayout = (LinearLayout)
                findViewById(R.id.no_results_layout);
        noResultsLinearLayout.setVisibility(View.VISIBLE);

        // Find the search button TextView and set an OnClickListener on it
        TextView searchButton = (TextView) findViewById(R.id.try_again_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the EditText box and get the search words entered by the user
                EditText userSearchWords = (EditText) findViewById(R.id.try_again_search_box);
                String searchWordsString = userSearchWords.getText().toString();

                // Search button Intent
                Intent searchActivityIntent = new Intent(SearchActivity.this, SearchActivity.class);
                searchActivityIntent.putExtra("searchWords", searchWordsString);
                startActivity(searchActivityIntent);
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        SearchLoader newSearchLoader =
                new SearchLoader(SearchActivity.this, searchURLString);
        return newSearchLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> bookLoader, List<Book> bookData) {
        ArrayList<Book> retrievedList = (ArrayList) bookData;

        // Find the {@link ProgressBar} with the ID progress_bar and set the visibility to GONE.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        if (retrievedList == null) {
            noConnectionUI();
        } else if (retrievedList.isEmpty()) {
            tryAgainUI();
        } else {
            updateUi(retrievedList);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> bookLoader) {
        ArrayList<Book> blankList = new ArrayList<Book>();
        updateUi(blankList);
    }
}