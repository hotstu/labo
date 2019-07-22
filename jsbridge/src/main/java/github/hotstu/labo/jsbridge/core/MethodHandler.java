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

package github.hotstu.labo.jsbridge.core;


import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import github.hotstu.labo.jsbridge.annotation.Param;
import github.hotstu.labo.jsbridge.interact.Interact;
import github.hotstu.labo.jsbridge.param.Params;
import github.hotstu.labo.jsbridge.util.LogUtil;
import github.hotstu.labo.jsbridge.util.ParamUtil;

/**
 * js->java->MethodHandler
 * java->js->javaCallback(MethodHandler)
 * 通过反射来调用java native方法或者java回调函数
 * 如果是js sync invoke java方法,在此针对同步方法,直接解析对象返回值转成jsonString
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class MethodHandler {

    /**
     * js->java sync, return json { result : {}}
     */
    private static final String SYNC_JS_INVOKE_JAVA_RETURN_JSON_KEY = "result";

    /**
     * 方法所对应的对象实例
     */
    private Object instance;

    /**
     * 要调用的方法
     */
    private Method method;

    /**
     * 方法所对应的参数集合
     */
    private Params params;

    public MethodHandler(Object instance, Method method, Params params) {
        this.instance = instance;
        this.method = method;
        this.params = params;
    }

    public static MethodHandler createMethodHandler(Object instance, Method method, NIMJsBridge bridge) {
        if (instance == null || method == null) {
            return null;
        }

        Params params = Params.createParams(method, bridge);
        return new MethodHandler(instance, method, params);
    }

    /**
     * 开始执行方法
     *
     * @param interact 包含了方法的参数所对应的参数值，会把参数值依次解析出来，供方法调用
     * @return 同步调用时直接返回要传递给js的result(json格式)
     */
    public String invoke(Interact interact) {
        String jsonResult = null;
        if (interact != null) {
            Object[] values = params.convertJson2ParamValues(interact);
            try {
                Object ret = method.invoke(instance, values);
                jsonResult = resolveReturnObject2Json(ret);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return jsonResult;
    }

    /**
     * 同步方法,直接解析对象返回值转成jsonString
     *
     * @param obj 反射调用方法产生的返回值
     * @return json string： { result : {} }
     */
    public String resolveReturnObject2Json(Object obj) {
        if (obj == null) {
            return null;
        }

        JSONObject resultJsonObj = new JSONObject();
        Class cl = obj.getClass();
        try {
            if (!ParamUtil.isSupportClassType(cl)) {
                // need convert
                Field[] fields = cl.getDeclaredFields();
                JSONObject jsonObject = new JSONObject();
                for (Field field : fields) {
                    Param p = field.getAnnotation(Param.class);
                    if (p != null) {
                        field.setAccessible(true);
                        Object inst;
                        try {
                            inst = field.get(obj);
                            if (inst != null) {
                                jsonObject.put(p.value(), inst);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
                resultJsonObj.put(SYNC_JS_INVOKE_JAVA_RETURN_JSON_KEY, jsonObject);
            } else {
                // basic type
                resultJsonObj.put(SYNC_JS_INVOKE_JAVA_RETURN_JSON_KEY, obj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtil.e("resolveReturnObject2Json put object error, e=" + e.getMessage());
        }

        return resultJsonObj.toString();
    }
}
