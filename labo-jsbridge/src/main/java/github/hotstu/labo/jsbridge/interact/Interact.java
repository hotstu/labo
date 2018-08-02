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

import org.json.JSONObject;

/**
 * Java<->js交互基类
 * 主动调用为Request,响应回复为Response
 * <p>
 * Created by huangjun on 2016/10/18.
 */

public abstract class Interact {

    protected static final String TAG = "Interact";

    protected String callbackId; // request,response对应的回调函数ID

    public static boolean isRequest(Interact interact) {
        return interact instanceof Request;
    }

    public static boolean isResponse(Interact interact) {
        return interact instanceof Response;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public abstract void parseFromJson(JSONObject json);

    public abstract void putKeyValue(final String key, final Object value);

    public abstract JSONObject getValues();
}
