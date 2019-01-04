/*
 * Copyright (c) 2018 hglf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.hotstu.labo.listscreen;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import github.hotstu.naiue.util.MODisplayHelper;
import github.hotstu.naiue.util.MOViewHelper;
import github.hotstu.naiue.widget.pullRefresh.MOPullRefreshLayout;
import github.hotstu.naiue.widget.recycler.MOCommonViewHolder;
import github.hotstu.naiue.widget.recycler.MOTypedRecyclerAdapter;
import github.hotstu.naiue.widget.recycler.PredictiveLinearLayoutManager;


/**
 * 支持列表懒加载的基类，用于 viewpager，也可以单独使用
 * 懒加载需要在view可见时手动dispatchHit
 */
public abstract class BaseListScreen extends FrameLayout {
    public static final int LOADING_TYPE_IDLE = 0;
    public static final int LOADING_TYPE_INITSIAL = 1;
    public static final int LOADING_TYPE_LOADMORE = 2;
    public static final int LOADING_TYPE_REFRESH = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOADING_TYPE_IDLE, LOADING_TYPE_INITSIAL, LOADING_TYPE_LOADMORE, LOADING_TYPE_REFRESH})
    public @interface LoadingType {
    }

    private static final String TAG = "BaseListScreen";
    private final LoadMoreItem loadMoreItem = new LoadMoreItem();
    protected RecyclerView mRecyclerView;
    protected MOPullRefreshLayout pull_to_refresh;
    protected EmptyView emptyView;

    private int mDiffRecyclerViewSaveStateId = MOViewHelper.generateViewId();
    private boolean isViewInitiated = false;
    private boolean isVisibleToUser = false;
    private boolean isDataInitiated = false;
    @LoadingType
    private int loading = 0;
    SimpleEmptyViewAdapter simpleEmptyViewAdapter;

    public BaseListScreen(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.listscreen_layout, this, true);
        mRecyclerView = findViewById(R.id.recyclerView);
        pull_to_refresh = findViewById(R.id.pull_to_refresh);
        emptyView = findViewById(R.id.emptyView);
        pull_to_refresh.setEnabled(false);
        emptyView.setAdapter(getEmptyVidewAdapter());
        emptyView.hide();
        emptyView.setClickable(true);
    }

    protected EmptyView.EmptyViewAdapter getEmptyVidewAdapter() {
        if (simpleEmptyViewAdapter == null) {
            simpleEmptyViewAdapter = new SimpleEmptyViewAdapter(getContext());
        }
        return simpleEmptyViewAdapter;
    }

    protected LoadingFooterView.FooterViewAdapter getFooterVidewAdapter() {
        if (simpleEmptyViewAdapter == null) {
            simpleEmptyViewAdapter = new SimpleEmptyViewAdapter(getContext());
        }
        return simpleEmptyViewAdapter;
    }

    protected abstract String getTitle();


    protected RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 是否要启用lazy load 功能，该功能需要手动dispatchDisplayHint
     *
     * @return
     */
    protected boolean lazyLoadEnabled() {
        return false;
    }


    protected void initRecyclerView() {
        LinearLayoutManager layoutManager = getLayoutManager();
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(new ReachBootomListener(layoutManager));
        MOTypedRecyclerAdapter adapter = getAdapter();
        if (adapter != null) {
            getAdapter().addDelegate(new LoadMoreDelegate());
            mRecyclerView.setAdapter(getAdapter());
        } else {
            Log.e(getTitle(), "adapter== null");
        }
    }

    protected LinearLayoutManager getLayoutManager() {
        return new PredictiveLinearLayoutManager(getContext());
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initRecyclerView();
        Log.d(TAG, "onAttachedToWindow");
        isViewInitiated = true;
        if (lazyLoadEnabled()) {
            prepareFetchData();
        } else {
            isVisibleToUser = true;
            prepareFetchData();
        }
    }

    @Override
    protected void onDisplayHint(int hint) {
        super.onDisplayHint(hint);
        Log.d(TAG, getTitle() + "onDisplayHint" + hint);
        isVisibleToUser = hint == VISIBLE;
        if (hint == VISIBLE && lazyLoadEnabled()) {
            prepareFetchData();
        }
    }

    public void prepareFetchData() {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated)) {
            fetchData();
            isDataInitiated = true;
        }
    }

    /**
     *
     */
    protected abstract void fetchData();

    /**
     * 当滑动到底部并且{@link #isLoading() }为false， {@link #hasMoreData()}为true时调用
     */
    protected abstract void loadMoreData();

    /**
     * 如果为false则不触发loadMoreData()方法
     *
     * @return
     */
    protected abstract boolean hasMoreData();

    protected abstract MOTypedRecyclerAdapter getAdapter();


    protected boolean isLoading() {
        return loading != LOADING_TYPE_IDLE;
    }

    /**
     * @param loading
     */
    protected void setLoading(@LoadingType int loading) {
        int oldstate = this.loading;
        this.loading = loading;
        if (loading == LOADING_TYPE_IDLE) {
            if (emptyView.isVisible()) {
                emptyView.hide();
            }
            pull_to_refresh.finishRefresh();
            if (oldstate == LOADING_TYPE_LOADMORE) {
                hideLoadMore();
            }
        } else if (loading == LOADING_TYPE_INITSIAL) {
            emptyView.asLoadingView("加载中...");
        } else if (loading == LOADING_TYPE_REFRESH) {
            //NOOP
        } else if (loading == LOADING_TYPE_LOADMORE) {
            showLoadMore();
        }
    }

    protected void finishPullRefresh() {
        pull_to_refresh.finishRefresh();
    }

    protected void setOnPullListener(MOPullRefreshLayout.OnPullListener listener) {
        pull_to_refresh.setOnPullListener(listener);
        pull_to_refresh.setEnabled(listener != null);
    }


    /**
     * 提示数据为空 或者加载出错
     *
     * @param msg
     * @param reason
     * @param callback
     */
    protected void showErrorMessage(String msg, String reason, OnClickListener callback) {
        emptyView.asErrorView(msg, reason, "点击重试", callback);
    }


    private void showLoadMore() {
        if (getAdapter().findItem(loadMoreItem) == RecyclerView.NO_POSITION) {
            getAdapter().addItem(loadMoreItem);
        }

    }

    private void hideLoadMore() {
        getAdapter().removeItem(loadMoreItem);
    }


    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchSaveInstanceState(container);
        mRecyclerView.setId(id);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        int id = mRecyclerView.getId();
        mRecyclerView.setId(mDiffRecyclerViewSaveStateId);
        super.dispatchRestoreInstanceState(container);
        mRecyclerView.setId(id);
    }

    public class ReachBootomListener extends RecyclerView.OnScrollListener {
        private LinearLayoutManager mLayoutManager;

        public ReachBootomListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            Log.d(TAG, "onScrolled" + isLoading());
            if (isLoading()) {
                return;
            }
            if (!hasMoreData()) {
                post(() -> {
                    loadMoreItem.hasMore = false;
                    showLoadMore();
                });
                return;
            }
            loadMoreItem.hasMore = true;
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();
            if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                post(BaseListScreen.this::loadMoreData);
            }
        }

    }

    private final class LoadMoreItem {
        boolean hasMore = true;

    }

    class LoadMoreDelegate implements MOTypedRecyclerAdapter.AdapterDelegate {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(MOTypedRecyclerAdapter adapter, ViewGroup parent) {
            LoadingFooterView loadingEmptyView = new LoadingFooterView(parent.getContext());
            loadingEmptyView.setAdapter(getFooterVidewAdapter());
            loadingEmptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    MODisplayHelper.dp2px(parent.getContext(), 52)));
            return new MOCommonViewHolder(loadingEmptyView);
        }

        @Override
        public void onBindViewHolder(MOTypedRecyclerAdapter adapter, RecyclerView.ViewHolder holder, Object data) {
            if (((LoadMoreItem) data).hasMore) {
                ((LoadingFooterView) holder.itemView).asLoading("正在加载更多...");
            } else {
                ((LoadingFooterView) holder.itemView).asNomore("没有更多了");
            }
        }

        @Override
        public boolean isDelegateOf(Class<?> clazz, Object item, int position) {
            return LoadMoreItem.class.isAssignableFrom(clazz);
        }
    }

    public static abstract class SimpleOnPullListener implements MOPullRefreshLayout.OnPullListener {
        @Override
        public void onMoveTarget(int offset) {
            //NOOP
        }

        @Override
        public void onMoveRefreshView(int offset) {
            //NOOP
        }
    }


}
