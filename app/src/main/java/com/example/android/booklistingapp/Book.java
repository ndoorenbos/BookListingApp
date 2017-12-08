package com.example.android.booklistingapp;

import android.graphics.Bitmap;

/**
 * Created by ndoor on 11/24/2016.
 * {@link Book} is a new class of Book information
 */

public class Book {
    private String mBookTitle;
    private String mBookAuthors;
    private String mBookDescription;
    private String mBookUrl;
    private Bitmap mBookImage;

    /**
     * Create a new Book object, taking in 3 String inputs
     * @param bookTitle is the title of the book, given as a String
     * @param bookAuthors are the authors of the book, given as a String
     * @param bookDescription is the description of the book, given as a String
     * @param bookUrl is the Google Books url for the book, given as a String
     * @param bookImage is the Google Books thumbnail image for the book, given as a Bitmap
     */
    public Book(String bookTitle, String bookAuthors, String bookDescription, String bookUrl,
                Bitmap bookImage) {
        mBookTitle = bookTitle;
        mBookAuthors = bookAuthors;
        mBookDescription = bookDescription;
        mBookUrl = bookUrl;
        mBookImage = bookImage;
    }

    // This method retrieves the title of the book from the Book object, returns a String
    public String getBookTitle() {
        return mBookTitle;
    }

    // This method retrieves the authors of the book from the Book object, returns a String
    public String getBookAuthors() {
        return mBookAuthors;
    }

    // This method retrieves the description of the book from the Book object, returns a String
    public String getBookDescription() {
        return mBookDescription;
    }

    // This method retrieves the description of the book from the Book object, returns a String
    public String getBookUrl() {
        return mBookUrl;
    }

    // This method retrieves the thumbnail image of the book from the Book object, returns a Bitmap
    public Bitmap getBookImage() {
        return mBookImage;
    }
}
