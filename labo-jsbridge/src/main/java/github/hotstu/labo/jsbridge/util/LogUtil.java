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

package github.hotstu.labo.jsbridge.util;

import android.util.Log;

/**
 * Created by huangjun on 2016/10/14.
 */

public class LogUtil {

    private static final String TAG = "NIMJSBridge";

    private static final String LOG_PREFIX = "=== ";

    private static final String LOG_SUFFIX = " ===";

    public static void i(String msg) {
        i(TAG, wrap(msg));
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void d(String msg) {
        d(TAG, wrap(msg));
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    private static final String wrap(String msg) {
        return LOG_PREFIX + msg + LOG_SUFFIX;
    }
}
