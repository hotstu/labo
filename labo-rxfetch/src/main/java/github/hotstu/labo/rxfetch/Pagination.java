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

package github.hotstu.labo.rxfetch;

import android.util.Log;

/**
 *  分页辅助工具类
 */
public class Pagination {
    private static final int STATE_PENDDING = 0;
    private static final int STATE_IDLE = 1;
    private static final int DEFAULT_PAGESIZE = 20;
    private static final int DEFAULT_FIRSTPAGE = 1;
    private static final String TAG = "Pagenation";

    private int state;
    private int currentPage;
    private int penddingPage;
    private final int pageSize;
    private final int firstPage;

    public Pagination() {
        this(DEFAULT_FIRSTPAGE, DEFAULT_PAGESIZE);
    }

    public Pagination(int pageSize) {
        this(DEFAULT_FIRSTPAGE, pageSize);
    }

    public Pagination(int firstPage, int pageSize) {
        this.firstPage = firstPage;
        this.state = STATE_IDLE;
        this.pageSize = pageSize;
        this.currentPage = firstPage;
        this.penddingPage = -1;
    }

    public static final class PaginationInfo {
        public final int page;
        public final int pageSize;

        PaginationInfo(int page, int pageSize) {
            this.page = page;
            this.pageSize = pageSize;
        }

        @Override
        public String toString() {
            return hashCode() + "@{page="+ page+", pageSize="+pageSize+"}";
        }
    }

    public PaginationInfo getPagenationInfo() {
        if (state != STATE_PENDDING) {
            Log.w(TAG, "page is not initialed, call reset or nextpage first!");
        }
        return new PaginationInfo(penddingPage, pageSize);
    }

    public void reset(){
        if (state == STATE_PENDDING) {
            Log.w(TAG, "state is dirty, commit or rollback first!");
        }
        this.penddingPage = firstPage;
        this.state = STATE_PENDDING;
    }

    public void nextPage() {
        if (state == STATE_PENDDING) {
            Log.w(TAG, "state is dirty, commit or rollback first!");
        }
        this.penddingPage = currentPage + 1;
        this.state = STATE_PENDDING;
    }



    public void rollback() {
        if (state == STATE_IDLE) {
            Log.w(TAG, "state is clean, no need call rollback");
            return;
        }
        this.penddingPage = -1;
        this.state = STATE_IDLE;
    }

    public void commit() {
        if (state == STATE_IDLE) {
            Log.w(TAG, "state is clean, no need call commit");
            return;
        }
        this.currentPage = penddingPage;
        this.penddingPage = -1;
        this.state = STATE_IDLE;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }
}

