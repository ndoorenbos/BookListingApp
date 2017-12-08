package com.example.android.booklistingapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ndoor on 11/24/2016.
 * {@link BookAdapter} is an {@link ArrayAdapter} that can provide the layout for the list of books,
 * which is a list of {@link Book} objects
 */

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Activity context, ArrayList<Book> books) {
        // Initialize the ArrayAdapter's internal storage for the context and the list. We have a
        // custom adapter, so we set the second argument to 0
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View bookListItemView = convertView;
        ViewHolder viewHolder;

        if (bookListItemView == null) {
            bookListItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item,
                    parent, false);

            // Create a ViewHolder and populate it with the views being updated by this adapter
            viewHolder = new ViewHolder();
            viewHolder.bookTitleTextView = (TextView)
                    bookListItemView.findViewById(R.id.book_title_text_view);
            viewHolder.bookAuthorTextView = (TextView)
                    bookListItemView.findViewById(R.id.book_author_text_view);
            viewHolder.bookDescriptionTextView = (TextView)
                    bookListItemView.findViewById(R.id.book_description_text_view);
            viewHolder.bookImageView = (ImageView)
                    bookListItemView.findViewById(R.id.book_image_view);

            // Store the holder with the view
            bookListItemView.setTag(viewHolder);
        } else {
            // Use the ViewHolder which is already there
            viewHolder = (ViewHolder) bookListItemView.getTag();
        }

        // Get the {@link Book} object located at this position on the list
        final Book currentBook = (Book) getItem(position);

        // If the current book object is not null, get the title, author, description, and image
        // from the {@link Book} object and update the corresponding Views.
        if (currentBook != null) {
            viewHolder.bookTitleTextView.setText(currentBook.getBookTitle());
            viewHolder.bookAuthorTextView.setText(currentBook.getBookAuthors());
            viewHolder.bookDescriptionTextView.setText(currentBook.getBookDescription());
            viewHolder.bookImageView.setImageBitmap(currentBook.getBookImage());
        }
        // Return the whole book list item layout so that it can be shown in the ListView
        return bookListItemView;
    }

    static class ViewHolder {
        ImageView bookImageView;
        TextView bookTitleTextView;
        TextView bookAuthorTextView;
        TextView bookDescriptionTextView;
    }
}