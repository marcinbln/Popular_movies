package com.example.popularmovies.adapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.popularmovies.R;
import com.example.popularmovies.repository.db.Result;

import java.util.Objects;

public class RecyclerViewAdapter extends PagedListAdapter<Result, RecyclerViewAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Result> RESULT_COMPARATOR = new DiffUtil.ItemCallback<Result>() {
        @Override
        public boolean areItemsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Result oldItem, @NonNull Result newItem) {
            return oldItem == newItem;
        }
    };
    final private RecyclerViewReadyCallback mRecyclerViewReadyCallback;
    final private ItemClickListener mItemClickListener;

    public RecyclerViewAdapter(ItemClickListener listener, RecyclerViewReadyCallback mRecyclerViewReadyCallback) {
        super(RESULT_COMPARATOR);
        mItemClickListener = listener;
        this.mRecyclerViewReadyCallback = mRecyclerViewReadyCallback;
    }

    @Override
    public int getItemCount() {
        int count = super.getItemCount();

        mRecyclerViewReadyCallback.onLayoutReady(count);


        return super.getItemCount();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_recyclerview_item, parent, false);

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.5);
        view.setLayoutParams(layoutParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public interface RecyclerViewReadyCallback {
        void onLayoutReady(int count);
    }


    public interface ItemClickListener {
        void onItemClickListener(int itemID);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView tv;
        private final ImageView userPic;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userPic = itemView.findViewById(R.id.poster_offline);
            tv = itemView.findViewById(R.id.no_poster_title);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("CheckResult")
        void bind(Result result) {

            if (result != null) {

                String relativePosterPath = result.getPosterPath();

                if (relativePosterPath != null) {

                    tv.setVisibility(View.INVISIBLE);
                    String fullPath = "https://image.tmdb.org/t/p/w342/" + relativePosterPath;

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.error(R.drawable.baseline_cloud_off_24);

                    Glide.with(itemView.getContext())
                            .load(fullPath)
                            .apply(requestOptions)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    userPic.requestLayout();
                                    userPic.setImageResource(0);
                                    userPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    userPic.requestLayout();
                                    userPic.setImageResource(0);
                                    userPic.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    return false;
                                }
                            })
                            .into(userPic);
                } else {
                    userPic.setImageResource(R.drawable.outline_image_24);
                    userPic.setScaleType(ImageView.ScaleType.CENTER);
                    tv.setText(result.getTitle());
                    tv.setVisibility(View.VISIBLE);
                }
            }

        }

        @Override
        public void onClick(View view) {
            int itemID = Objects.requireNonNull(getItem(getAdapterPosition())).getId();
            mItemClickListener.onItemClickListener(itemID);
        }
    }


}