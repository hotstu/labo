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

package github.hotstu.labo.tool

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * @author hglf
 * @since 2018/6/27
 */
open class ActivityLifeCircleDumper : Application.ActivityLifecycleCallbacks {

    private fun dumpItent(activity: Activity) {
        val i = activity.intent
        if (i == null) {
            Log.e(TAG, "[" + activity.javaClass.simpleName + "] intent == null")
            return
        }
        val bundle = i.extras
        dumpBundle(bundle)
    }

    private fun dumpBundle(bundle: Bundle?) {
        if (bundle == null) {
            return
        }
        val keys = bundle.keySet()
        val it = keys.iterator()
        Log.e(TAG, "Dumping Intent start")
        while (it.hasNext()) {
            val key = it.next()
            Log.e(TAG, "[" + key + "=" + bundle.get(key) + "]")
            if (bundle.get(key) != null && bundle.get(key) is Bundle) {
                val temp = bundle.get(key) as Bundle
                val keys2 = temp.keySet()
                val it2 = keys2.iterator()
                Log.e(TAG, "Dumping Intent start")
                while (it2.hasNext()) {
                    val key2 = it2.next()
                    Log.e(TAG, "[" + key2 + "=" + temp.get(key2) + "]")
                }
                Log.e(TAG, "Dumping Intent end")
            }
        }
        Log.e(TAG, "Dumping Intent end")

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "[onActivityCreated:" + activity.javaClass.name + "]")
        dumpItent(activity)
        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
                    super.onFragmentCreated(fm, f, savedInstanceState)
                    Log.d(TAG, "[onFragmentCreated:  " + f.javaClass.simpleName + " with tag " + f.tag + "]")
                    val b = f.arguments
                    dumpBundle(b)
                }

                override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentDestroyed(fm, f)
                    Log.d(TAG, "[onFragmentDestroyed: " + f.javaClass.simpleName + " with tag " + f.tag + "]")

                }
            }, true)
        }
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "[onActivityDestroyed:" + activity.javaClass.name + "]")

    }

    companion object {
        private val TAG = "LifeCircleDump"
    }
}
