package com.koalatea.thehollidayinn.softwareengineeringdaily.podcast;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.koalatea.thehollidayinn.softwareengineeringdaily.R;
import com.koalatea.thehollidayinn.softwareengineeringdaily.data.models.Post;
import com.koalatea.thehollidayinn.softwareengineeringdaily.repositories.FilterRepository;
import com.koalatea.thehollidayinn.softwareengineeringdaily.util.ReactiveUtil;

import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by keithholliday on 2/4/18.
 */

public class TopRecListFragment extends SwitchableListGridView {
    private String title;
    private String tagId;
    private DisposableObserver<String> myDisposableObserver;
    private RecyclerViewSkeletonScreen skeletonScreen;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PodcastListViewModel podcastListViewModel;

    public static TopRecListFragment newInstance(String title, String tagId) {
        TopRecListFragment f = new TopRecListFragment();
        Bundle args = new Bundle();
        f.title = title;
        f.tagId = tagId;
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(
                R.layout.fragment_toprec_list,
                container,
                false);

        ButterKnife.bind(this, rootView);

        mRecyclerView = getRecycleView(rootView);
        setUpSkeletonLoading(mRecyclerView, rootView);
        setUpViewModel();
        this.setUpSubscription();

        return rootView;
    }

    private void setUpSkeletonLoading(RecyclerView recyclerView, View rootView) {
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(mPodcastAdapter)
                .load(R.layout.item_skeleton_news)
                .shimmer(true)
                .show();

        swipeRefreshLayout = rootView.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        podcastListViewModel.getPosts("", title, tagId);
                    }
                }
        );
    }

    private void setUpViewModel() {
        podcastListViewModel = ViewModelProviders
                .of(this)
                .get(PodcastListViewModel.class);
        podcastListViewModel.getPostList().observe(this, posts -> {
            if (posts != null) updatePosts(posts);
        });
    }

    public void setUpSubscription() {
        if (myDisposableObserver != null) {
            return;
        }

        myDisposableObserver = new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                podcastListViewModel.getPosts(s, title, tagId);
            }

            @Override
            public void onComplete() { }

            @Override
            public void onError(Throwable e) { }
        };
        FilterRepository filterRepository = FilterRepository.getInstance();
        filterRepository.getModelChanges().subscribe(myDisposableObserver);

        mPodcastAdapter.getPositionClicks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ReactiveUtil.toDetailObservable(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
        podcastListViewModel.getPosts("", title, tagId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myDisposableObserver != null) {
            myDisposableObserver.isDisposed();
        }
    }

    private void updatePosts(List<Post> postList) {
        mPodcastAdapter.setPosts(postList);
        swipeRefreshLayout.setRefreshing(false);
        skeletonScreen.hide();
    }
}
