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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @author hglf
 * @since 2018/9/26
 */
public class EmptyView extends FrameLayout {
    public interface EmptyViewAdapter {
        View getEmptyView(View prev, String msg);

        View getLoadingView(View prev, String msg);

        View getErrorView(View prev, String msg, String reason, String buttonText, View.OnClickListener callback);
    }

    EmptyViewAdapter mAdapter;
    View emptyView;
    View loadingView;
    View errorView;

    public EmptyView(@NonNull Context context) {
        super(context);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAdapter(@NonNull EmptyViewAdapter adapter) {
        this.mAdapter = adapter;
        this.removeAllViews();
        emptyView = null;
        loadingView = null;
        errorView = null;
    }

    boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    void hide() {
        setVisibility(View.GONE);
    }

    void asEmptyView(String msg) {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (emptyView == null) {
            emptyView = mAdapter.getEmptyView(emptyView, msg);
            LayoutParams params = generateDefaultLayoutParams();
            params.gravity = Gravity.CENTER;
            emptyView.setLayoutParams(params);
            this.addView(emptyView);
        } else {
            //TODO set data
            mAdapter.getEmptyView(emptyView, msg);
        }
        emptyView.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
    }

    void asLoadingView(String msg) {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (loadingView == null) {
            loadingView = mAdapter.getLoadingView(loadingView,msg);
            LayoutParams params = generateDefaultLayoutParams();
            params.gravity = Gravity.CENTER;
            loadingView.setLayoutParams(params);
            this.addView(loadingView);
        } else {
            loadingView = mAdapter.getLoadingView(loadingView,msg);
        }
        loadingView.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
    }

    void asErrorView(String msg, String reason, String buttonText, View.OnClickListener callback) {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (errorView == null) {
            errorView = mAdapter.getErrorView(errorView, msg, reason, buttonText, callback);
            LayoutParams params = generateDefaultLayoutParams();
            params.gravity = Gravity.CENTER;
            errorView.setLayoutParams(params);
            this.addView(errorView);
        } else {
            errorView = mAdapter.getErrorView(errorView, msg, reason, buttonText, callback);
        }
        errorView.setVisibility(View.VISIBLE);
        this.setVisibility(View.VISIBLE);
    }

}
