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
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * @author hglf
 * @since 2018/9/26
 */
public class SimpleEmptyViewAdapter implements EmptyView.EmptyViewAdapter,LoadingFooterView.FooterViewAdapter {
    private final Context mContext;

    SimpleEmptyViewAdapter(Context context){
        this.mContext = context;
    }
    @Override
    public View getEmptyView(View prev, String msg) {
        if (prev != null && prev instanceof TextView) {
            ((TextView) prev).setText(msg);
            return prev;
        } else {
            TextView tv = new TextView(mContext);
            tv.setBackgroundColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setText(msg);
            return tv;
        }
    }

    @Override
    public View getLoadingView(View prev, String msg) {
        if (prev != null && prev instanceof TextView) {
            ((TextView) prev).setText(msg);
            return prev;
        } else {
            TextView tv = new TextView(mContext);
            tv.setGravity(Gravity.CENTER);
            tv.setText(msg);
            return tv;
        }
    }

    @Override
    public View getErrorView(View prev, String msg, String reason, String buttonText, View.OnClickListener callback) {
        if(prev == null) {
            FrameLayout errorView =  new FrameLayout(mContext);
            errorView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            errorView.setClickable(true);
            errorView.setBackgroundColor(Color.WHITE);
            TextView tv = new TextView(mContext);
            tv.setText(msg);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            errorView.addView(tv, params);
            errorView.setOnClickListener(callback);
            errorView.setTag(tv);
            return errorView;
        } else {
            ((TextView) prev.getTag()).setText(msg);
            prev.setOnClickListener(callback);
            return prev;
        }

    }

    @Override
    public View getNomoreView(View prev, String msg) {
        if (prev != null && prev instanceof TextView) {
            ((TextView) prev).setText(msg);
            return prev;
        } else {
            TextView tv = new TextView(mContext);
            tv.setGravity(Gravity.CENTER);
            tv.setText(msg);
            return tv;
        }
    }
}
