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

/**
 * Created by zhangshaowen on 16/3/8.
 */


import android.app.Application
import android.content.Context
import android.content.res.Configuration

/**
 * This interface is used to delegate calls from main Application object.
 *
 * Implementations of this interface must have a one-argument constructor that takes
 * an argument of type [Application].
 */
interface ApplicationLifeCycle {

    /**
     * Same as [Application.onCreate].
     */
    fun onCreate()

    /**
     * Same as [Application.onLowMemory].
     */
    fun onLowMemory()

    /**
     * Same as [Application.onTrimMemory].
     * @param level
     */
    fun onTrimMemory(level: Int)

    /**
     * Same as [Application.onTerminate].
     */
    fun onTerminate()

    /**
     * Same as [Application.onConfigurationChanged].
     */
    fun onConfigurationChanged(newConfig: Configuration)

    /**
     * Same as [Application.attachBaseContext].
     */
    fun onBaseContextAttached(base: Context)
}
