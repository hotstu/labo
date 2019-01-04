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

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

/**
 * 适用于{@link BaseListScreen }的ViewPagerAdapter
 */
public abstract class PagerAdapter<T extends BaseListScreen> extends android.support.v4.view.PagerAdapter {
    BaseListScreen currentPrimaryItem = null;

    abstract public T getItem(@NonNull ViewGroup container,int position);

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        T ret = getItem(container, position);

        ViewGroup.LayoutParams param = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        container.addView(ret, param);
        if (ret != currentPrimaryItem) {
            ret.dispatchDisplayHint(View.INVISIBLE);
        }
        return ret;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        if (currentPrimaryItem == object) {
            currentPrimaryItem = null;
        }
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        if (object != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.dispatchDisplayHint(View.INVISIBLE);
            }
            if (object != null) {
                ((BaseListScreen) object).dispatchDisplayHint(View.VISIBLE);
            }
            currentPrimaryItem = (BaseListScreen) object;
        }
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
