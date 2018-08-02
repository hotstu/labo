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

package github.hotstu.labo.tool;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import java.util.Iterator;
import java.util.Set;

/**
 * @author hglf
 * @since 2018/6/27
 */
public class ActivityLifeCircleDumper implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "LifeCircleDump";

    private void dumpItent(Activity activity) {
        Intent i = activity.getIntent();
        if (i == null) {
            Log.e(TAG, "[" + activity.getClass().getSimpleName() + "] intent == null");
            return;
        }
        Bundle bundle = i.getExtras();
        dumpBundle(bundle);
    }

    private void dumpBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        Set<String> keys = bundle.keySet();
        Iterator<String> it = keys.iterator();
        Log.e(TAG, "Dumping Intent start");
        while (it.hasNext()) {
            String key = it.next();
            Log.e(TAG, "[" + key + "=" + bundle.get(key) + "]");
            if (bundle.get(key) != null && bundle.get(key) instanceof Bundle) {
                Bundle temp = (Bundle) bundle.get(key);
                Set<String> keys2 = temp.keySet();
                Iterator<String> it2 = keys2.iterator();
                Log.e(TAG, "Dumping Intent start");
                while (it2.hasNext()) {
                    String key2 = it2.next();
                    Log.e(TAG, "[" + key2 + "=" + temp.get(key2) + "]");
                }
                Log.e(TAG, "Dumping Intent end");
            }
        }
        Log.e(TAG, "Dumping Intent end");

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "[onActivityCreated:" + activity.getClass().getName() +"]");
        dumpItent(activity);
        if (activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
                @Override
                public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                    super.onFragmentCreated(fm, f, savedInstanceState);
                    Log.d(TAG, "[onFragmentCreated:  " + f.getClass().getSimpleName() + " with tag " + f.getTag() + "]");
                    Bundle b = f.getArguments();
                    dumpBundle(b);
                }

                @Override
                public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
                    super.onFragmentViewCreated(fm, f, v, savedInstanceState);
                }

                @Override
                public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                    super.onFragmentDestroyed(fm, f);
                    Log.d(TAG, "[onFragmentDestroyed: " + f.getClass().getSimpleName() + " with tag " + f.getTag() + "]");

                }
            }, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "[onActivityDestroyed:" + activity.getClass().getName() +"]");

    }
}
