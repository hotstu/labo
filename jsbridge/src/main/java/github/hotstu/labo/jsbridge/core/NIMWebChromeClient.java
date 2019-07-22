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

import android.text.TextUtils;
import android.util.Pair;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import github.hotstu.labo.jsbridge.util.JsAssetUtil;
import github.hotstu.labo.jsbridge.util.JsUtil;
import github.hotstu.labo.jsbridge.util.LogUtil;


/**
 * WebChromeClient定制类
 * js->java入口: 通过{@link #onJsPrompt(WebView, String, String, String, JsPromptResult)}对js传递的数据进行处理
 * js加载处理: 在{@link #onProgressChanged(WebView, int)}方法里把内嵌的js文件注入h5页面中。
 * 该类可以包装使用者自己的{@link WebChromeClient}
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class NIMWebChromeClient extends WebChromeClientDelegate {

    private static final String NIM_JS_BRIDGE_BASIC_JS = "nim_js_native_bridge.js";

    private NIMJsBridge jsBridge;


    private boolean hasBasicJsInjected = false;

    private String lastLoadUrl;

    NIMWebChromeClient(WebChromeClient webChromeClient, NIMJsBridge jsBridge) {
        super(webChromeClient);
        this.jsBridge = jsBridge;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        String url = view.getUrl();
        if (!TextUtils.isEmpty(lastLoadUrl) && !lastLoadUrl.equals(url)) {
            hasBasicJsInjected = false; // 地址更换了,要重新注入
        }
        LogUtil.d("webView:" + url + " onProgressChanged process=" + newProgress);

        if (newProgress == 100 && !hasBasicJsInjected) {
            hasBasicJsInjected = JsUtil.callJS(view, JsAssetUtil.assetFile2Str(view.getContext(), NIM_JS_BRIDGE_BASIC_JS));
            lastLoadUrl = view.getUrl(); // 记录最后一次加载的Url
            LogUtil.i("inject nim_js_native_bridge.js, result=" + hasBasicJsInjected + ", url=" + url);
        } else {
            hasBasicJsInjected = false;
        }

        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        Pair<Boolean, String> invokeResult = jsBridge.parseJsDataToCallJava(message);
        // true说明已经找到对应的java方法并调用
        if (invokeResult.first) {
            // 阻塞当前h5页面,需要confirm才能解除阻塞
            if (TextUtils.isEmpty(invokeResult.second)) {
                result.confirm();
            } else {
                result.confirm(invokeResult.second); // js->java同步调用直接返回结果
            }

            return true;
        }


        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

}
