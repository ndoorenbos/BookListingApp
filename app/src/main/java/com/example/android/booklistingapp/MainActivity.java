package com.example.android.booklistingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the search button TextView and set an OnClickListener on it
        TextView searchButton = (TextView) findViewById(R.id.main_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Find the EditText box and get the search words entered by the user
                EditText userSearchWords = (EditText) findViewById(R.id.main_search_box);
                String searchWordsString = userSearchWords.getText().toString();

                // Search button Intent, sends words to the SearchActivity
                Intent searchActivityIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchActivityIntent.putExtra("searchWords", searchWordsString);
                startActivity(searchActivityIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}