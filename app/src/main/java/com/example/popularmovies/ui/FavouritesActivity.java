package com.example.popularmovies.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.popularmovies.Constants;
import com.example.popularmovies.viewModels.FavouritesViewModel;
import com.example.popularmovies.repository.MoviesRepository;
import com.example.popularmovies.R;
import com.example.popularmovies.adapters.RecyclerViewAdapter;


public class FavouritesActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener, RecyclerViewAdapter.RecyclerViewReadyCallback {

    ConstraintLayout emptyTv;
    TextView emptyTvSubtitle;
    private RecyclerViewAdapter adapter;
    private FavouritesViewModel favouritesViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        setTitle(getResources().getString(R.string.favourites_activity_title));
        emptyTv = findViewById(R.id.empty_tv);

        emptyTvSubtitle = findViewById(R.id.empty_view_subtitle);
        emptyTvSubtitle.setText(R.string.favourites_empty_subtitle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        swipeRefreshLayout = findViewById(R.id.main_swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this::invalidateDataSource);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, Constants.NUM_COLUMNS);

        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(this, this);

        favouritesViewModel = new ViewModelProvider(this).get(FavouritesViewModel.class);

        favouritesViewModel.getDbFavIntegers().observe(this, integers -> {
            favouritesViewModel.getDbFavPaged(Constants.FAVOURITES, integers).observe(FavouritesActivity.this, results -> {

                if (results.size() != 0) {
                    emptyTv.setVisibility(View.GONE);
                }

                adapter.submitList(results);
            });
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClickListener(int itemID) {
        Intent intent = new Intent(FavouritesActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, itemID);
        startActivity(intent);
    }

    private void invalidateDataSource() {
        MoviesRepository.getInstance(this).mDb.dbFavDataSourceFactory.moviesPageKeyedDataSource.invalidate();
        swipeRefreshLayout.setRefreshing(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                openMostPopularActivity();
                break;

            case R.id.top_rated:
                openTopRatedActivity();
                break;

            case R.id.favourites:
                invalidateDataSource();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMostPopularActivity() {
        Intent intent = new Intent(FavouritesActivity.this, MostPopularActivity.class);
        startActivity(intent);
    }


    private void openTopRatedActivity() {
        Intent intent = new Intent(FavouritesActivity.this, TopRatedActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onLayoutReady(int count) {

    }
}
