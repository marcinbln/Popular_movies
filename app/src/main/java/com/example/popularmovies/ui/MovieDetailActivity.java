package com.example.popularmovies.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popularmovies.AppExecutors;
import com.example.popularmovies.Constants;
import com.example.popularmovies.R;
import com.example.popularmovies.adapters.MyReviewRecyclerViewAdapter;
import com.example.popularmovies.models.Review;
import com.example.popularmovies.models.Trailer;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.repository.db.AppDatabase;
import com.example.popularmovies.repository.db.Favourite;
import com.example.popularmovies.repository.network.RetrofitClient;
import com.example.popularmovies.repository.network.RetrofitServiceBuilder;
import com.example.popularmovies.viewModels.MovieDetailViewModel;
import com.example.popularmovies.viewModels.TrailersJson;
import com.google.android.youtube.player.YouTubeStandalonePlayer;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.popularmovies.Constants.YOUTUBE_API_KEY;

public class MovieDetailActivity extends AppCompatActivity {


    public static final String EXTRA_MOVIE_ID = "extraMovieId";
    public static int movieID;
    private final View.OnClickListener trailerClickListener = v -> trailerIconClick();

    RecyclerView recyclerView;
    boolean expandReviews;
    private TextView mTitle;
    private TextView mReleaseDate;
    private TextView mVoteAverage;
    private TextView mPlotSynopsis;
    private List<Review> reviewList;
    private ImageView mPoster;
    private ImageView mFavouritesIcon;
    private ImageView mExpandArrow;
    private AppDatabase mDbFavs;
    private final View.OnClickListener favouritesClickListener = v -> favouritesIconClick();
    private TextView mEmptyRV;
    private ProgressBar spinner;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        movieID = intent.getIntExtra(EXTRA_MOVIE_ID, 0);


        MovieDetailViewModel movieDetailViewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);

        movieDetailViewModel.setId(movieID);

        // MoviesRepository.getInstance(this).loadReviews(movieID);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mDbFavs = AppDatabase.getInstance(getApplicationContext());
        mTitle = findViewById(R.id.title);
        mReleaseDate = findViewById(R.id.releaseDate);
        mVoteAverage = findViewById(R.id.voteAverage);
        mPlotSynopsis = findViewById(R.id.plotSynopsis);
        mPoster = findViewById(R.id.poster);
        View mPlay = findViewById(R.id.playCL);
        View mFavourites = findViewById(R.id.favouritesCL);
        mFavouritesIcon = findViewById(R.id.favouriteIcon);
        ConstraintLayout mReviewsHeader = findViewById(R.id.reviews_header);
        recyclerView = findViewById(R.id.reviews_rv);
        mExpandArrow = findViewById(R.id.expand_arrow);
        mEmptyRV = findViewById(R.id.empty_rv);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
        reviewList = new ArrayList<>();
        expandReviews = false;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mReviewsHeader.setOnClickListener(v -> reviewsClick());

        mPlay.setOnClickListener(trailerClickListener);
        mFavourites.setOnClickListener(favouritesClickListener);

        movieDetailViewModel.getResult().observe(this, result -> {
            if (result != null) {
                String relativePosterPath = result.getPosterPath();
                if (relativePosterPath != null) {

                    String fullPath = "https://image.tmdb.org/t/p/w342/" + relativePosterPath;
                    Glide.with(getApplicationContext())
                            .load(fullPath)
                            .into(mPoster);
                } else {
                    mPoster.setImageDrawable(getResources().getDrawable(R.drawable.outline_image_24));
                    mPoster.setScaleType(ImageView.ScaleType.CENTER);
                }
                mTitle.setText(result.getTitle());
                        setTitle(result.getTitle());
                        mReleaseDate.setText(result.getReleaseDate());
                        mVoteAverage.setText(result.getVoteAverage().toString());
                        mPlotSynopsis.setText(result.getOverview());
                    }
                }
        );


        movieDetailViewModel.isFavourite().observe(this, isInFavourites -> {

                    if (isInFavourites != null) {
                        if (isInFavourites) {
                            mFavouritesIcon.setImageResource(R.drawable.icon_liked);
                            mFavouritesIcon.setTag(Constants.LIKED);
                        }
                    }
                }
        );

        movieDetailViewModel.getReviewList().observe(this, new Observer<List<Review>>() {
            @Override
            public void onChanged(List<Review> reviews) {
                spinner.setVisibility(View.GONE);
                reviewList = reviews;

                MyReviewRecyclerViewAdapter adapter = new MyReviewRecyclerViewAdapter(reviews);
                recyclerView.setAdapter(adapter);

                // if the list size is 0 and expand reviews clicked show an empty state for reviews
                if (expandReviews) {
                    handleEmptyStateReviews();
                }
            }
        });

    }

    private void reviewsClick() {

        // Update reviews expanded flag
        expandReviews = !expandReviews;

        // If list size is 0 show empty state recyclerview
        handleEmptyStateReviews();

        // Show reviews
        if (mExpandArrow.getTag().equals(Constants.REVIEWS_COLLAPSED)) {

            if (isInternetAvailable()) {
                spinner.setVisibility(View.VISIBLE);
                MoviesRepository.getInstance(this).loadReviews(MovieDetailActivity.movieID);
            } else if (!isInternetAvailable() && reviewList.size() == 0) {
                Toast.makeText(getApplicationContext(), R.string.offline_message, Toast.LENGTH_SHORT).show();
            }
            mExpandArrow.setImageResource(R.drawable.reviews_expanded);
            mExpandArrow.setTag(Constants.REVIEWS_EXPANDED);
        }
        // Hide reviews
        else {
            recyclerView.setVisibility(View.GONE);
            mEmptyRV.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            mExpandArrow.setImageResource(R.drawable.reviews_collapsed);
            mExpandArrow.setTag(Constants.REVIEWS_COLLAPSED);
        }


    }

    private void trailerIconClick() {

        RetrofitClient service = RetrofitServiceBuilder.buildService(RetrofitClient.class);
        Call<TrailersJson> mCall = service.getTrailers(movieID);
        mCall.enqueue(new Callback<TrailersJson>() {

            String youtubeKey;

            @Override
            public void onResponse(@NotNull Call<TrailersJson> call, @NotNull Response<TrailersJson> response) {

                TrailersJson trailersJson = response.body();
                List<Trailer> trailersList;
                if (trailersJson != null) {
                    trailersList = trailersJson.getResults();
                    if (trailersList.size() != 0) {

                        for (int i = 0; i < trailersList.size(); i++) {

                            if (trailersList.get(i).getType().equals(getResources().getString(R.string.trailer))) {
                                youtubeKey = trailersList.get(i).getKey();
                                break;
                            }

                        }

                        Log.d("key", "youtubeKey " + youtubeKey);
                        Intent trailerIntent = YouTubeStandalonePlayer.createVideoIntent(MovieDetailActivity.this, YOUTUBE_API_KEY, youtubeKey, 12, true, true);
                        startActivity(trailerIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Video not available", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(@NotNull Call<TrailersJson> call, @NotNull Throwable t) {

            }
        });

    }

    private void favouritesIconClick() {

        if (mFavouritesIcon.getTag().equals(Constants.NOT_LIKED)) {
            //add to favourites
            mFavouritesIcon.setImageResource(R.drawable.icon_liked);
            mFavouritesIcon.setTag(Constants.LIKED);
            Favourite favourite = new Favourite();
            favourite.setId(movieID);
            AppExecutors.getInstance().diskIO().execute(() -> mDbFavs.favouritesDao().addToFavourites(favourite));

        } else {
            //remove from favourites
            mFavouritesIcon.setImageResource(R.drawable.round_favorite_border_24);
            mFavouritesIcon.setTag(Constants.NOT_LIKED);
            Favourite favourite = new Favourite();
            favourite.setId(movieID);
            AppExecutors.getInstance().diskIO().execute(() -> mDbFavs.favouritesDao().deleteFromFavourites(favourite));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public boolean isInternetAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void handleEmptyStateReviews() {
        // if list size is 0 show empty state recyclerview
        if (reviewList.size() == 0) {
            mEmptyRV.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            // else show recyclerview
        } else {
            mEmptyRV.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

}
