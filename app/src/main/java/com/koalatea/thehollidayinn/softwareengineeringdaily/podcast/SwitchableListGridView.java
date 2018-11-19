package com.koalatea.thehollidayinn.softwareengineeringdaily.podcast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.koalatea.thehollidayinn.softwareengineeringdaily.R;
import com.koalatea.thehollidayinn.softwareengineeringdaily.util.ReactiveUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SwitchableListGridView extends Fragment implements ToggleLayoutView {
    private boolean mIsGridViewOn = true;
    private Menu mMenu;
    public RecyclerView mRecyclerView;
    public PodcastAdapter mPodcastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public RecyclerView getRecycleView(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(mIsGridViewOn ? new GridLayoutManager(this.getContext(), 2):
                new LinearLayoutManager(this.getActivity()));

        mPodcastAdapter = new PodcastAdapter(this);
        recyclerView.setAdapter(mPodcastAdapter);


        return recyclerView;
    }

    @Override
    public boolean getGridViewState() {
        return mIsGridViewOn;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.mMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.list_grid_view:
                MenuItem listGridView = this.mMenu.findItem(R.id.list_grid_view);

                if(mIsGridViewOn) {
                    // change the layout to list view and icon to grid view
                    listGridView.setIcon(getResources().getDrawable(R.drawable.grid_view_icon));
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
                    mRecyclerView.setAdapter(mPodcastAdapter);
                    mIsGridViewOn = false;
                } else {
                    //change the layout to grid view and icon to list view
                    listGridView.setIcon(getResources().getDrawable(R.drawable.list_view_icon));
                    mRecyclerView.setLayoutManager(new GridLayoutManager(this.getContext(), 2));
                    mRecyclerView.setAdapter(mPodcastAdapter);
                    mIsGridViewOn = true;
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
