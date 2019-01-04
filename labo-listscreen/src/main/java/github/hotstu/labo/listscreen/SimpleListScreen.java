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

import github.hotstu.naiue.widget.recycler.MOTypedRecyclerAdapter;

/**
 * @author hglf
 * @since 2018/9/26
 */
public class SimpleListScreen extends BaseListScreen {
    public SimpleListScreen(Context context) {
        super(context);
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    protected void fetchData() {

    }

    @Override
    protected void loadMoreData() {

    }

    @Override
    protected boolean hasMoreData() {
        return false;
    }

    @Override
    protected MOTypedRecyclerAdapter getAdapter() {
        return null;
    }
}
