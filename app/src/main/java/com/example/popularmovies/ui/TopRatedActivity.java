package com.example.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.popularmovies.AppExecutors;
import com.example.popularmovies.Constants;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.R;
import com.example.popularmovies.adapters.RecyclerViewAdapter;
import com.example.popularmovies.viewModels.TopRatedViewModel;
import com.example.popularmovies.repository.db.AppDatabase;

public class TopRatedActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener, RecyclerViewAdapter.RecyclerViewReadyCallback {

    ConstraintLayout emptyView;
    private RecyclerViewAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AppDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_rated);
        emptyView = findViewById(R.id.empty_tv);


        setTitle(getResources().getString(R.string.top_rated_activity_title));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::invalidateDataSource);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, Constants.NUM_COLUMNS);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(this, this);

        TopRatedViewModel topRatedViewmodel = new ViewModelProvider(this).get(TopRatedViewModel.class);

        topRatedViewmodel.getMoviesMutable().observe(this, results -> {

            if (!(results == null)) {
                if (results.size() > 0) {
                    AppExecutors.getInstance().diskIO().execute(() -> mDb.moviesDao().insertMoviesList(results));
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        topRatedViewmodel.getTopRatedMovies(Constants.TOP_RATED).observe(this, results -> {

            adapter.submitList(results);
        });

        mDb = AppDatabase.getInstance(getApplicationContext());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClickListener(int itemID) {
        Intent intent = new Intent(TopRatedActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, itemID);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                openMostPopularActivity();
                break;

            case R.id.top_rated:
                invalidateDataSource();
                break;

            case R.id.favourites:
                openFavouritesActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMostPopularActivity() {
        Intent intent = new Intent(TopRatedActivity.this, MostPopularActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openFavouritesActivity() {
        Intent intent = new Intent(TopRatedActivity.this, FavouritesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void invalidateDataSource() {
        swipeRefreshLayout.setRefreshing(true);
        MoviesRepository.getInstance(this).moviesDataSourceFactoryTopRated.moviesPageKeyedDataSource.invalidate();
    }

    @Override
    public void onLayoutReady(int count) {

        if (count != 0) {
            emptyView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}
