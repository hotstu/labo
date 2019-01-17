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

package github.hotstu.labodemo.listscreen;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import github.hotstu.labo.listscreen.BaseListScreen;
import github.hotstu.labo.rxfetch.Pagination;
import github.hotstu.naiue.widget.recycler.MOCommonViewHolder;
import github.hotstu.naiue.widget.recycler.MOTypedRecyclerAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author hglf
 * @since 2018/9/26
 */
public class DemoListScreen extends BaseListScreen{
    private final Pagination pager;
    private final MOTypedRecyclerAdapter mAdapter;
    private final MOTypedRecyclerAdapter.AdapterDelegate arrayAdapterDelegate = new MOTypedRecyclerAdapter.AdapterDelegate() {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(MOTypedRecyclerAdapter adapter, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            ((TextView) v).setTextColor(Color.BLACK);
            return new MOCommonViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MOTypedRecyclerAdapter adapter, RecyclerView.ViewHolder holder, Object data) {
            final String item = ((String) data);
            MOCommonViewHolder yhHolder = ((MOCommonViewHolder) holder);
            yhHolder.setText(android.R.id.text1, item);
            yhHolder.setClickListener(0, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("onClick", item);
                }
            });
        }

        @Override
        public boolean isDelegateOf(Class<?> clazz, Object item, int position) {
            return String.class.isAssignableFrom(clazz);
        }
    };

    private SimpleOnPullListener onPullListener = new SimpleOnPullListener() {
        @Override
        public void onRefresh() {
            fetchData(LOADING_TYPE_REFRESH);
        }
    };
    private final Random random;

    public DemoListScreen(Context context) {
        super(context);
        pager = new Pagination(30);
        mAdapter = new MOTypedRecyclerAdapter();
        mAdapter.addDelegate(arrayAdapterDelegate);
        random = new Random();
        setOnPullListener(onPullListener);

    }


    @Override
    protected String getTitle() {
        return "已处理";
    }

    @Override
    protected LinearLayoutManager getLayoutManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (getAdapter().getItem(position) instanceof String) {
                    return 1;
                } else {
                    return 2;
                }
            }
        });
        return gridLayoutManager;
    }

    @Override
    protected void fetchData() {
        fetchData(LOADING_TYPE_INITSIAL);
    }
    protected void fetchData(@LoadingType final int loadingType) {
        setLoading(loadingType);
        Log.d(getTitle(), "fetchData");
        pager.reset();
        Pagination.PaginationInfo paginationInfo = pager.getPagenationInfo();
        Log.d(getTitle(), paginationInfo + "");
        final List<String> data = new ArrayList<>(Arrays.asList("Helps", "Maintain", "Liver", "Health", "Function", "Supports", "Healthy", "Fat",
                "Metabolism", "Nuturally", "Bracket", "Refrigerator", "Bathtub", "Wardrobe", "Comb", "Apron", "Carpet", "Bolster", "Pillow", "Cushion"));
        Observable.just(data)
                .delay(1000, TimeUnit.MILLISECONDS)
                .map((Function<List<String>, List<String>>) strings -> {
                    float v = random.nextFloat();
                    if (v < .2f) {
                        throw new Exception("test");
                    }
                    if (v < .4f) {
                        return new ArrayList<>();
                    }
                    return strings;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(strings -> {
                    setLoading(LOADING_TYPE_IDLE);
                    if (strings.size() == 0) {
                        showErrorMessage("没有数据", null, v -> fetchData());
                    }
                    getAdapter().setDataSet(strings);
                    pager.commit();
                }, throwable -> {
                    throwable.printStackTrace();
                    setLoading(LOADING_TYPE_IDLE);
                    showErrorMessage("出错啦", "" + throwable.getMessage(), v -> fetchData());
                    pager.rollback();
                });
    }

    @Override
    protected void loadMoreData() {
        Log.d(getTitle(), "loadMoreData");
        setLoading(LOADING_TYPE_LOADMORE);
        pager.nextPage();
        Pagination.PaginationInfo paginationInfo = pager.getPagenationInfo();
        Log.d(getTitle(), paginationInfo + "");
        final List<String> data = new ArrayList<>(Arrays.asList("more" + paginationInfo.page + "-1" ,
                "more" + paginationInfo.page + "-2",
                "more" + paginationInfo.page + "-3",
                "more" + paginationInfo.page + "-4",
                "more" + paginationInfo.page + "-5"
        ));
        Observable.just(data)
                .delay(1000, TimeUnit.MILLISECONDS)
                .map((Function<List<String>, List<String>>) strings -> {
                    float v = random.nextFloat();
//                    if (v < .3f) {
//                        throw new Exception("test");
//                    }
//                    if (v < .2f) {
//                        return new ArrayList<>();
//                    }
                    return strings;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(strings -> {
                    getAdapter().addItems(strings);
                    pager.commit();
                    setLoading(LOADING_TYPE_IDLE);
                }, throwable -> {
                    throwable.printStackTrace();
                    pager.rollback();
                    setLoading(LOADING_TYPE_IDLE);
                });
    }

    @Override
    protected boolean hasMoreData() {
        return pager.getCurrentPage() <= 5;
    }


    @Override
    protected MOTypedRecyclerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    protected boolean lazyLoadEnabled() {
        return false;
    }

}
