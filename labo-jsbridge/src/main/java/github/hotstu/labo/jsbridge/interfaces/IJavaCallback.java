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

package github.hotstu.labo.jsbridge.interfaces;

/**
 * java->js->javaCallback, 所有java回调函数必须继承此接口
 * example:
 * new IJavaCallback() {
 *
 * @JavaCallback public void onCallback(
 * @Param("key") param,
 * @ParamResponseStatus("status")int status,
 * @ParamResponseStatus("msg")String msg) {
 * ...
 * }
 * }
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class IJavaCallback {
}
