package com.walowtech.fblaapplication.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.walowtech.fblaapplication.Book;
import com.walowtech.fblaapplication.BookDetailsActivity;
import com.walowtech.fblaapplication.GridViewActivity;
import com.walowtech.fblaapplication.MainActivity;
import com.walowtech.fblaapplication.R;

import java.util.ArrayList;

/**
 * Created by mattw on 11/6/2017.
 */

//TODO doc

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder>{

    private ArrayList<Book> books;
    LayoutInflater inflater;
    Activity activity;
    Context context;

    public GridAdapter(Context context, Activity activity, ArrayList<Book> books){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.books = books;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.book_category_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Book currentBook = books.get(position);//Get current item

        holder.rating.setText((String.format("%.01f", currentBook.averageRating))); //Set rating
        holder.rating.setTypeface(MainActivity.handWriting);
        if(currentBook.coverSmall != null){ //If cover has been loaded
            holder.bookCover.setImageBitmap(currentBook.coverSmall);//Set cover
            holder.progressBar.setVisibility(View.GONE); //Hide loading indicator
        }

        final String GID = currentBook.GID;

        if(GID != null){
            holder.bookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView image = holder.bookCover;
                    android.support.v4.util.Pair<View, String> p1 = android.support.v4.util.Pair.create((View)image, context.getString(R.string.trans_iv_book_cover));
                    //Pair<View, String> p2 = Pair.create((View)text, getString(R.string.trans_tv_avg_rating));

                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1);
                    BitmapDrawable drawable = (BitmapDrawable) holder.bookCover.getDrawable();
                    Bitmap bitmap;
                    try {
                        bitmap = drawable.getBitmap();
                    }catch(NullPointerException NPE){
                        bitmap = null;
                        NPE.printStackTrace();
                    }

                    Intent i = new Intent(context, BookDetailsActivity.class);
                    Log.i("LoginActivity", "GID OF NEW: " + GID);
                    i.putExtra("GID", GID);
                    i.putExtra("BOOK_IMAGE", bitmap);
                    context.startActivity(i , options.toBundle());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView rating;
        ProgressBar progressBar;
        ImageView bookCover;

        ViewHolder(View itemView){
            super(itemView);
            rating = (TextView)itemView.findViewById(R.id.m_tv_rating);
            progressBar = (ProgressBar)itemView.findViewById(R.id.m_pb_book_progress);
            bookCover = (ImageView)itemView.findViewById(R.id.m_iv_book_cover);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            //TODO click listener
        }

        //gets data at click position
        Book getItem(int i){
            return books.get(i);
        }
    }
}
