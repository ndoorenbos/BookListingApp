package com.example.android.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by ndoor on 11/24/2016.
 * Helper methods related to requesting and receiving book data from Google Books
 */

public final class QueryUtils {
    // Tag for the log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Create a private constructor because no one should ever create a {@link QueryUtils} object.
    // This class is only meant to hold static variables and methods, which can be accessed
    // directly from the class name QueryUtils.
    private QueryUtils() {
    }

    /**
     * Query the Google Books API and return an ArrayList of {@link Book} objects meeting the
     * requested requirements of our query.
     * @param requestUrlString is the query url to Google Books
     * @return ArrayList<Book>
     */
    public static ArrayList<Book> fetchBookData(String requestUrlString) {
        // Create URL object from the given String
        URL requestUrl = createUrl(requestUrlString);

        // Perform HTTP request to the URL and receive a JSON response back. If there is a problem
        // with connecting to the server, an IOException exception object will be thrown. Catch the
        // exception so the app does not crash, and print the error message to the logs.
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(requestUrl);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        if (jsonResponse != null) {
            // Extract relevant fields from the JSON response and create an ArrayList of {@link Book}
            // objects
            ArrayList<Book> books = extractBooks(jsonResponse);

            // Return the {@link Event}
            return books;
        }

        return null;
    }

    /**
     * Return an ArrayList of {@link Book} objects that has been built up from parsing a JSON
     * response
     * @param jsonResponse is the JSON response from the server as a String
     * @return ArrayList<Book>
     */
    public static ArrayList<Book> extractBooks(String jsonResponse) {
        String titleString = String.valueOf(R.string.no_title);
        String descriptionString = String.valueOf(R.string.no_description);
        String authorsString = String.valueOf(R.string.no_authors);
        String bookURLString = null;
        String imageUrlString = null;
        Bitmap bookImageBitmap = null;

        // Create an empty ArrayList that we can start adding {@link Book} objects to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the JSON response String. If there is a problem with the way the JSON is
        // formatted, a JSONException exception object will be thrown. Catch the exception so the
        // app does not crash, and print the error message to the logs.
        try {
            JSONObject jsonRootObject = new JSONObject(jsonResponse);

            // Get the instance of the JSONArray that contains "items" JSONObjects
            JSONArray itemsJsonArray = jsonRootObject.optJSONArray("items");

            if (itemsJsonArray != null) {
                // Iterate the itemsJsonArray and print the info of JSONObjects
                for (int itemsIndex = 0; itemsIndex < itemsJsonArray.length(); itemsIndex++) {
                    try {
                        JSONObject itemsJsonObject = itemsJsonArray.getJSONObject(itemsIndex);

                        if (itemsJsonObject != null) {
                            // Get the "volumeInfo" JSONObject from the current itemsJsonObject
                            JSONObject volumeJsonObject = itemsJsonObject.optJSONObject("volumeInfo");
                            if (volumeJsonObject != null) {
                                // Extract the title and description Strings from the volumeJsonObject above
                                titleString = volumeJsonObject.optString("title").toString();
                                descriptionString = volumeJsonObject.optString("description").toString();

                                // Get the "authors" JSONArray from the current volumeJsonObject, iterate the
                                // JSONArray, and store the authors in a single String
                                JSONArray authorsJsonArray = volumeJsonObject.optJSONArray("authors");

                                if (authorsJsonArray != null) {
                                    authorsString = authorsJsonArray.getString(0).toString();

                                    for (int authorsIndex = 1; authorsIndex < authorsJsonArray.length(); authorsIndex++) {
                                        authorsString = authorsString + ", " +
                                                authorsJsonArray.getString(authorsIndex).toString();
                                    }
                                }

                                // Extract the URL String for the book from the current volumeJsonObject
                                bookURLString = volumeJsonObject.optString("infoLink").toString();

                                // Extract the URL String for the book image from the current volumeJsonObject
                                JSONObject imageJsonObject = volumeJsonObject.optJSONObject("imageLinks");
                                if (imageJsonObject != null) {
                                    imageUrlString = imageJsonObject.optString("thumbnail").toString();

                                    // Get the book image from Google Books and store as a Bitmap.
                                    try {
                                        bookImageBitmap = fetchImageBitmap(imageUrlString);
                                    } catch (IOException e) {
                                        Log.e(LOG_TAG, "Problem retrieving the book image from Google Books", e);
                                    }
                                }
                            }
                        }
                        // Add a new Book object to the books ArrayList and print to logs
                        books.add(new Book(titleString, authorsString, descriptionString, bookURLString,
                                bookImageBitmap));
                        Log.i(LOG_TAG, "Added " + titleString + ", " + authorsString + ", " +
                                descriptionString + ", " + bookURLString + ", " + imageUrlString);
                    } catch (JSONException e) {
                        Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
                    }
                }
            }
        } catch (JSONException e){
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }

    /**
     * Returns a new URL object from the given String url. If there is a problem with the way the
     * URL is formed, a MalformedURLException exception object will be thrown. Catch the exception
     * so the app does not crash, and print the error message to the logs.
     * @param stringUrl is the URL which we want in String form
     * @return URL
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response. If there is a
     * problem with retrieving the JSON information from the server, an IOException exception
     * object will be thrown. Catch the exception so the app does not crash, and print the error
     * message to the logs.
     * @param url in the query URL we wish to use when granted an HTTP URL Connection.
     * @return String of the JSON response from the query
     * @throws IOException
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream and
            // parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from
     * the server. If there is a problem with reading the InputStream, an IOException exception
     * object will be thrown. Catch the exception so the app does not crash, and print the error
     * message to the logs.
     * @param inputStream is the response from the server
     * @return String of the JSON response
     * @throws IOException
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            try {
                InputStreamReader inputStreamReader =
                        new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem with reading the InputStream", e);
            }
        }
        return output.toString();
    }

    /**
     * Get the Bitmap image of the book from Google Books.
     * @param imageUrlString is the URL of the image from Google Books given as a String
     * @return the image as a Bitmap
     * @throws IOException
     */
    private static Bitmap fetchImageBitmap(String imageUrlString) throws IOException {
        InputStream inputStream = null;
        Bitmap imageBitmap = null;

        // If the URL is null, then return early.
        if (imageUrlString == null) {
            return imageBitmap;
        }

        try {
            // Download image Bitmap from URL and decode it.
            InputStream imageInputStream = new java.net.URL(imageUrlString).openStream();
            imageBitmap = BitmapFactory.decodeStream(imageInputStream);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book image.", e);
        }

        return imageBitmap;
    }

    /**
     * Create the request URL String using the search words entered by the user
     * @param searchWordString is the String of search words
     * @return URL String
     */
    public static String getSearchUrlString(String searchWordString) {
        String searchURLString = "https://www.googleapis.com/books/v1/volumes?q=";
        String searchWords = searchWordString;

        if (searchWords != null) {
            // Get the string of search words and split them up as an array of individual words
            String[] words = searchWords.split(" ");

            // Add the first word of the array to the searchURLString
            searchURLString = searchURLString + words[0];

            // Continue to create the search URL using the array of words if there is more
            for (int wordIndex = 1; wordIndex < words.length; wordIndex++) {
                searchURLString = searchURLString + "+" + words[wordIndex];
            }
        }

        // Set the quantity of search items to 20 books
        searchURLString = searchURLString + "&maxResults=20";
        Log.v(LOG_TAG, searchURLString);

        return searchURLString;
    }
}