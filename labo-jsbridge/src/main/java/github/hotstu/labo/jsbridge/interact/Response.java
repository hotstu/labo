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

/**
 * java->js->javaCallback(response)
 * js->java->replyToJs(response)
 * response数据格式：
 * <pre>
 *  {
 *      "responseId":"c_12345678",
 *      "data":{
 *          "status":200,
 *          "msg":"success",
 *          "values":{
 *              ......
 *          }
 *      }
 *  }
 *
 *  responseId 代表request中的callbackId
 *  data       代表响应的数据
 *  status     代表响应状态码,成功200
 *  msg        代表响应状态对应的消息,非200情况下的错误原因
 *  values     代表响应数据包含的内容数据
 * </pre>
 * <p>
 * Created by huangjun on 2016/10/18.
 */

public class Response extends Interact {

    static final String RESPONSE_ID = "responseId";
    static final String RESPONSE_DATA = "data";
    static final String RESPONSE_DATA_VALUE = "values";
    public static final String RESPONSE_DATA_STATUS = "status";
    public static final String RESPONSE_DATA_MSG = "msg";

    private JSONObject response = new JSONObject();
    private JSONObject responseValues;

    public Response(JSONObject json) {
        parseFromJson(json);
    }

    @Override
    public void parseFromJson(JSONObject json) {
        if (json != null) {
            callbackId = json.optString(RESPONSE_ID);
            response = json.optJSONObject(RESPONSE_DATA);
            if (response != null) {
                responseValues = response.optJSONObject(RESPONSE_DATA_VALUE);
            }
        }
    }

    @Override
    public void putKeyValue(String key, Object value) {
        if (responseValues == null) {
            responseValues = new JSONObject();
        }

        try {
            responseValues.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Response putKeyValue error, e=" + e.getMessage());
        }
    }

    public void putStatusKeyValue(String key, Object value) {
        try {
            response.put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Response putStatusKeyValue error, e=" + e.getMessage());
        }
    }

    public void putStatus(int status, String msg) {
        try {
            response.put(RESPONSE_DATA_STATUS, status);
            response.put(RESPONSE_DATA_MSG, msg);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Response putStatus error, e=" + e.getMessage());
        }
    }

    @Override
    public JSONObject getValues() {
        return getResponseValues();
    }

    public JSONObject getResponse() {
        return response;
    }

    public JSONObject getResponseValues() {
        return responseValues;
    }

    public void setResponseValues(JSONObject responseValues) {
        this.responseValues = responseValues;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(RESPONSE_ID, callbackId); // responseId
            if (responseValues != null) {
                response.put(RESPONSE_DATA_VALUE, responseValues);
            }
            jsonObject.put(RESPONSE_DATA, response);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "'" + jsonObject.toString() + "'";
    }
}
