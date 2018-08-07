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

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Pair;
import android.webkit.WebView;

import org.json.JSONObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import github.hotstu.labo.jsbridge.annotation.JSInterface;
import github.hotstu.labo.jsbridge.annotation.JavaCallback;
import github.hotstu.labo.jsbridge.annotation.JavaInterface;
import github.hotstu.labo.jsbridge.interact.Interact;
import github.hotstu.labo.jsbridge.interact.InteractBuilder;
import github.hotstu.labo.jsbridge.interact.Request;
import github.hotstu.labo.jsbridge.interact.Response;
import github.hotstu.labo.jsbridge.interact.ResponseCode;
import github.hotstu.labo.jsbridge.interfaces.IJavaCallback;
import github.hotstu.labo.jsbridge.interfaces.InvocationFuture;
import github.hotstu.labo.jsbridge.param.Params;
import github.hotstu.labo.jsbridge.util.Base64;
import github.hotstu.labo.jsbridge.util.JsUtil;
import github.hotstu.labo.jsbridge.util.LogUtil;

/**
 * java<->js交互核心类
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class NIMJsBridge {

    private static final boolean DEBUG_MODE = true;

    //private static NIMJsBridge currentJsBridge;

    /**
     * js为java开放的唯一可调用的方法(java->js loadUrl用，js入口方法)，该方法接收一个json格式的字符串。
     */
    private static final String JS_MAIN_METHOD = "javascript:_JSNativeBridge._handleMessageFromNative(%s)";

    /**
     * java调用js的功能时，java会为js提供回调函数，但是不可能把回调函数传递给js，
     * 所以为回调函数提供一个唯一的id，
     */
    private static int sUniqueCallbackId = 1;

    /**
     * 保证发送给js数据时在UI线程中执行
     */
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 缓存java为js提供的接口(js->java时调用的java方法)
     */
    private HashMap<String, MethodHandler> javaInterfaces = new HashMap<>();

    /**
     * 缓存java调用js时的回调方法(java->js->javaCallback)
     */
    private HashMap<String, MethodHandler> javaCallbacks = new HashMap<>();

    /**
     * 缓存js为java提供的接口(java->js)的动态代理对象，避免反复创建造成的性能开销
     */
    private final Map<Class<?>, Object> jsInterfaces = new HashMap<>();

    /**
     * Builder属性
     */
    private String protocol; // scheme://host:port?
    private WebView webView;
    private NIMWebChromeClient webChromeClient;

    /**
     * 通过NIMJsBridgeBuilder来构造实例
     */
    NIMJsBridge(NIMJsBridgeBuilder builder) {
        if (builder == null) {
            return;
        }

        // 必要的属性
        protocol = builder.getProtocol();
        webView = builder.getWebView();
        webView.getSettings().setJavaScriptEnabled(true);
        webChromeClient = new NIMWebChromeClient(builder.getWebChromeClient(), this);
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(new WebViewClientDelegate(builder.getWebViewClient()));
        saveJavaMethodsForJS(builder.getJavaInterfacesForJS());

        // share NIMJsBridge instance
        //currentJsBridge = this;
    }

    /**
     * 获取JsInterface接口的动态代理对象
     *
     * @param clazz IJsInterfaces接口，接口内的函数用@JSInterface("jsFunctionName")
     * @return proxy
     */
    @SuppressWarnings("unchecked")
    public <T> T getJsInterface(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("only accept interface: " + clazz);
        }
        Object service;
        synchronized (jsInterfaces) {
            service = jsInterfaces.get(clazz);
            if (service == null) {
                service = newProxy(clazz);
                if (service != null) {
                    jsInterfaces.put(clazz, service);
                }
            }
        }
        return (T) service;
    }

    /**
     * 生成动态代理对象
     */
    private Object newProxy(Class clazz) {
        return Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getAnnotation(JSInterface.class) != null) {
                    JSInterface JSInterface = method.getAnnotation(JSInterface.class);
                    String jsMethodName = JSInterface.value();
                    final Request request = InteractBuilder.createRequest(null);
                    request.setInterfaceName(jsMethodName);
                    Params params = Params.createParams(method, NIMJsBridge.this);
                    params.convertParamValues2Json(request, args);
                    sendData2JS(request); // send to js

                    // java->js均为异步，返回InvocationFuture实现
                    return new InvocationFuture() {
                        @Override
                        public void setCallback(IJavaCallback callback) {
                            request.setJavaCallback(callback);
                            saveJavaCallback(request);
                        }
                    };
                }
                return new Object();
            }
        });
    }

    /**
     * 缓存java为js提供的接口,js->java时反射调用这些接口
     */
    private void saveJavaMethodsForJS(ArrayList javaMethods) {
        if (javaMethods != null) {
            for (int i = 0; i < javaMethods.size(); i++) {
                Object instance = javaMethods.get(i);
                if (instance != null) {
                    // 把java提供给js调用的接口放到json中
                    Class bridgeClass = instance.getClass();
                    Method[] allMethod = bridgeClass.getDeclaredMethods();
                    for (Method method : allMethod) {
                        // 说明这是提供给js的接口
                        if (method.getAnnotation(JavaInterface.class) != null) {
                            // 既然是提供给js的接口就得用JavascriptInterfaceKey标注，否则报错
                            JavaInterface jsKey = method.getAnnotation(JavaInterface.class);
                            MethodHandler methodHandler = MethodHandler.createMethodHandler(instance, method, this);
                            javaInterfaces.put(jsKey.value(), methodHandler);
                        }
                    }
                }
            }
        }
    }

    /**
     * 缓存java->js的回调函数:callbackId->JavaCallback MethodHandler
     */
    private void saveJavaCallback(Request request) {
        if (request != null && request.getJavaCallback() != null && !TextUtils.isEmpty(request.getCallbackId())) {
            Class bridgeClass = request.getJavaCallback().getClass();
            Method[] allMethod = bridgeClass.getDeclaredMethods();
            JavaCallback javaCallback;
            for (Method method : allMethod) {
                javaCallback = method.getAnnotation(JavaCallback.class);
                if (javaCallback != null) {
                    LogUtil.d("set java -> js request callback, callbackId=" + request.getCallbackId());
                    javaCallbacks.put(request.getCallbackId(),
                            MethodHandler.createMethodHandler(request.getJavaCallback(), method, this));
                    break; // record first
                }
            }
        }
    }

//    public static NIMJsBridge getCurrentJsBridge() {
//        return currentJsBridge;
//    }

    /**
     * ******************************* java -> js ***********************************
     */

    /**
     * java request js
     * java reply to js
     */
    public void sendData2JS(final Interact interact) {
        if (interact == null) {
            return;
        }

        if (Interact.isRequest(interact)) {
            sendRequest2JS(interact);
        } else {
            replyResponseToJS(interact);
        }
    }

    /**
     * java request js: java->js->javaCallback
     */
    private void sendRequest2JS(Interact interact) {
        if (interact != null && Interact.isRequest(interact)) {
            Request request = (Request) interact;
            // callbackId
            request.setCallbackId(generaUniqueCallbackId());
            // send to js
            startSendData2JS(request.toString());
        }
    }

    /**
     * java reply to js: js->java->replyToJs->js
     */
    private void replyResponseToJS(Interact response) {
        if (response != null) {
            startSendData2JS(response.toString());
        }
    }

    /**
     * java->js 出口：开始发送数据给js
     *
     * @param data 待发送给js的json格式的数据
     */
    private void startSendData2JS(String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }

        final String jsUrl = String.format(JS_MAIN_METHOD, data);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                JsUtil.callJS(webView, jsUrl);
            }
        });
    }

    /**
     * generate unique javaCallbackId
     */
    private static String generaUniqueCallbackId() {
        return ++sUniqueCallbackId + "_" + System.currentTimeMillis();
    }

    /**
     * ******************************* js -> java -> MethodHandler ***********************************
     */

    /**
     * js->java 入口：解析从js传递过来的json数据
     *
     * @param url js->java url
     *            url = nim://dispatch?{"handlerName":"test","params":{"msg":"xxx"},"callbackId":"cb_1_1476242402250"}
     * @return true 代表可以解析当前数据，可调用java方法；否则不可以解析，交给上层; String 为同步调用的返回值(已经转成json格式)
     */
    Pair<Boolean, String> parseJsDataToCallJava(String url) {
        Boolean result = false;
        String syncJsonResult = null;
        if (!TextUtils.isEmpty(url)) {
            if (DEBUG_MODE) {
                LogUtil.i("JS -> JAVA URL：" + url);
            }

            if (url.startsWith(protocol)) {
                result = true;
                try {
                    String json = url.substring(url.indexOf(protocol, 0) + protocol.length());
                    json = new String(Base64.decode(json));
                    if (DEBUG_MODE) {
                        LogUtil.i("JS -> JAVA DECODE DATA：" + json);
                    }

                    // {"handlerName":"xxx","params":{"msg":"xxx"},"callbackId":"cb_6_1476356582848"}
                    final JSONObject data = new JSONObject(json);
                    if (data == null) {
                        return new Pair<>(false, null);
                    }

                    boolean async = data.has(Request.REQUEST_CALLBACK_ID);
                    if (async) {
                        // 异步调用，开始调用java的方法
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                invokeJavaMethod(InteractBuilder.create(data));
                            }
                        });
                    } else {
                        // 同步调用
                        syncJsonResult = invokeJavaMethod(InteractBuilder.create(data));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new Pair<>(result, syncJsonResult);
    }

    /**
     * js->java 开始调用java的方法
     * java->js->javaCallback 开始调用java缓存的回调函数
     *
     * @param interact Request/Response
     */
    private String invokeJavaMethod(Interact interact) {
        if (interact == null) {
            return null;
        }

        String jsonResult = null;
        if (Interact.isResponse(interact)) {
            // java->js->javaCallback：说明这是js响应数据,需要找到java缓存的回调函数,执行java回调函数
            LogUtil.d("invoke java -> js callback method, callbackId=" + interact.getCallbackId());
            MethodHandler methodHandler = javaCallbacks.remove(interact.getCallbackId());
            if (methodHandler != null) {
                methodHandler.invoke(interact);
            } else {
                LogUtil.d("java回调方法不存在");
            }
        } else {
            // js->java：说明是js请求java的请求数据,找到执行的java方法,反射调用
            LogUtil.d("js invoke java method");
            Request request = (Request) interact;

            MethodHandler methodHandler = javaInterfaces.get(request.getInterfaceName());
            if (methodHandler != null) {
                jsonResult = methodHandler.invoke(request);
            } else {
                LogUtil.e("所调用的java接口不存在");
                // build response to reply to js
                Response errorResponse = InteractBuilder.createResponse(null);
                errorResponse.setCallbackId(interact.getCallbackId());
                errorResponse.putStatus(ResponseCode.RES_TARGET_NOT_EXIST, "所调用的Java接口不存在");
                replyResponseToJS(errorResponse);
            }
        }

        return jsonResult;
    }
}
