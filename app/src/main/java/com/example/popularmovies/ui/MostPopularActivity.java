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
import androidx.paging.PagedList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.popularmovies.AppExecutors;
import com.example.popularmovies.Constants;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.viewModels.MoviesViewModel;
import com.example.popularmovies.R;
import com.example.popularmovies.adapters.RecyclerViewAdapter;
import com.example.popularmovies.repository.db.Result;
import com.example.popularmovies.repository.db.AppDatabase;

public class MostPopularActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener, RecyclerViewAdapter.RecyclerViewReadyCallback {
    ConstraintLayout emptyView;
    private RecyclerViewAdapter adapter;
    private AppDatabase mDb;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emptyView = findViewById(R.id.empty_tv);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getResources().getString(R.string.most_popular_activity_title));

        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::invalidateDataSource);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);

        adapter = new RecyclerViewAdapter(this, this);

        GridLayoutManager layoutManager = new GridLayoutManager(this, Constants.NUM_COLUMNS);
        recyclerView.setLayoutManager(layoutManager);

        MoviesViewModel moviesViewModel = new ViewModelProvider(this).get(MoviesViewModel.class);

        //Observer for MutableLiveData<List<Result>> (saving into db each time list of 20 movies is downloaded)
        moviesViewModel.getMoviesMutable().observe(this, results -> {


            if (!(results == null)) {
                if (results.size() > 0) {
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        mDb.moviesDao().insertMoviesList(results);
                    });
                }
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        moviesViewModel.getMostPopularRepo(Constants.MOST_POPULAR).observe(this, (PagedList<Result> results) -> {
            adapter.submitList(results);
        });

        mDb = AppDatabase.getInstance(getApplicationContext());

        recyclerView.setAdapter(adapter);
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
                invalidateDataSource();
                break;

            case R.id.top_rated:
                swipeRefreshLayout.setRefreshing(true);
                openTopRatedActivity();
                break;

            case R.id.favourites:
                openFavouritesActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openFavouritesActivity() {
        Intent intent = new Intent(MostPopularActivity.this, FavouritesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void openTopRatedActivity() {
        Intent intent = new Intent(MostPopularActivity.this, TopRatedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClickListener(int itemID) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(MostPopularActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, itemID);
        startActivity(intent);
    }

    private void invalidateDataSource() {
        swipeRefreshLayout.setRefreshing(true);
        MoviesRepository.getInstance(this).dataSourceFactoryMostPopular.moviesPageKeyedDataSource.invalidate();

    }

    @Override
    public void onLayoutReady(int count) {

        if (count != 0) {
            emptyView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
        }

    }
}