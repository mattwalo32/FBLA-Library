package com.walowtech.fblaapplication.Utils;

import android.app.SearchManager;
import android.app.Service;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.R;

/**
 * Adapts the book information into the suggestions bar.
 *
 * @author Matthew Walowski
 * @version 1.0
 * @since 1.0
 */
//Created 10/7/2017
public class SuggestionCursorAdapter extends SimpleCursorAdapter {

    private LayoutInflater cursorInflater;
    private Context mContext;
    private Context appContext;
    private int layout;
    private Cursor cr;

    public SuggestionCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.layout = layout;
        this.mContext = context;
        this.cursorInflater = LayoutInflater.from(context);
        this.cr = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return cursorInflater.inflate(layout, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);
        TextView suggestion = (TextView) view.findViewById(R.id.tv_suggestions);

        int TitleIndex = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);

        suggestion.setText(cursor.getString(TitleIndex));
        suggestion.setTypeface(MainActivity.handWriting);
    }
}
