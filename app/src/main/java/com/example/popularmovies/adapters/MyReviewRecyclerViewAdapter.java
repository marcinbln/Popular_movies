package com.example.popularmovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.popularmovies.R;
import com.example.popularmovies.models.Review;

import java.util.List;

public class MyReviewRecyclerViewAdapter extends RecyclerView.Adapter<MyReviewRecyclerViewAdapter.ViewHolder> {

    private final List<Review> mValues;

    public MyReviewRecyclerViewAdapter(List<Review> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mAuthorLabel.setText(R.string.author);
        holder.mAuthor.setText(mValues.get(position).getAuthor());
        holder.mContentView.setText(mValues.get(position).getContent());


    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthorLabel;
        public final TextView mAuthor;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mAuthorLabel = (TextView) view.findViewById(R.id.author_label);
            mAuthor = (TextView) view.findViewById(R.id.author);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }


}