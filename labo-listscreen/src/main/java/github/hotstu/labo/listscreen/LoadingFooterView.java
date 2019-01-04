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
public class LoadingFooterView extends FrameLayout {
    public interface FooterViewAdapter {
        View getNomoreView(View prev, String msg);

        View getLoadingView(View prev, String msg);
    }

    FooterViewAdapter mAdapter;
    View nomoreView;
    View loadingView;
    public LoadingFooterView(@NonNull Context context) {
        super(context);
    }

    public LoadingFooterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingFooterView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void setAdapter(FooterViewAdapter adapter) {
        this.mAdapter = adapter;
        nomoreView = null;
        loadingView = null;
        this.removeAllViews();
    }

    void asNomore(String msg) {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }

        if (nomoreView == null) {
            nomoreView = mAdapter.getNomoreView(nomoreView,msg);
            LayoutParams params = generateDefaultLayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            nomoreView.setLayoutParams(params);
            this.addView(nomoreView);
        } else {
            nomoreView = mAdapter.getNomoreView(nomoreView,msg);
        }
        nomoreView.setVisibility(View.VISIBLE);
    }

    void asLoading(String msg) {
        if (nomoreView != null) {
            nomoreView.setVisibility(View.GONE);
        }

        if (loadingView == null) {
            loadingView = mAdapter.getLoadingView(loadingView,msg);
            LayoutParams params = generateDefaultLayoutParams();
            params.width = LayoutParams.WRAP_CONTENT;
            params.height = LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.CENTER;
            loadingView.setLayoutParams(params);
            this.addView(loadingView);
        } else {
            loadingView = mAdapter.getLoadingView(loadingView,msg);
        }
        loadingView.setVisibility(View.VISIBLE);
    }

}
