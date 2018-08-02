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

package github.hotstu.labo.jsbridge.interact;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import github.hotstu.labo.jsbridge.interfaces.IJavaCallback;

/**
 * java->js及js->java 请求数据格式：
 * <pre>
 *    {
 *      "handlerName":"test",
 *      "callbackId":"c_1234567",
 *      "params":{
 *          ....
 *      }
 *    }
 *
 *    handlerName 代表java与js之间给对方暴漏的接口的名称，
 *    callbackId 代表对方在发起请求时，会为回调方法生产一个唯一的id值，它就代表这个唯一的id值
 *    params     代表传递的数据
 * </pre>
 * <p>
 * Created by huangjun on 2016/10/14.
 */

public class Request extends Interact {

    public static final String REQUEST_CALLBACK_ID = "callbackId";
    static final String REQUEST_INTERFACE = "handlerName";
    static final String REQUEST_PARAMS = "params";

    private String interfaceName;
    private JSONObject requestValues;
    private IJavaCallback javaCallback;

    public Request(JSONObject json) {
        parseFromJson(json);
    }

    @Override
    public void parseFromJson(JSONObject json) {
        if (json != null) {
            callbackId = json.optString(REQUEST_CALLBACK_ID);
            interfaceName = json.optString(REQUEST_INTERFACE);
            requestValues = json.optJSONObject(REQUEST_PARAMS);
        }
    }

    @Override
    public void putKeyValue(String key, Object value) {
        if (requestValues == null) {
            requestValues = new JSONObject();
        }

        try {
            requestValues.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Request putKeyValue error, e=" + e.getMessage());
        }
    }

    @Override
    public JSONObject getValues() {
        return getRequestValues();
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public JSONObject getRequestValues() {
        return requestValues;
    }

    public IJavaCallback getJavaCallback() {
        return javaCallback;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public void setRequestValues(JSONObject requestValues) {
        this.requestValues = requestValues;
    }

    public void setJavaCallback(IJavaCallback javaCallback) {
        this.javaCallback = javaCallback;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(REQUEST_CALLBACK_ID, callbackId);
            jsonObject.put(REQUEST_INTERFACE, interfaceName);
            if (requestValues != null) {
                jsonObject.put(REQUEST_PARAMS, requestValues);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "'" + jsonObject.toString() + "'";
    }
}
