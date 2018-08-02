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

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.util.ArrayList;

/**
 * NIMJSBridge实例构造器
 * <p>
 * Created by huangjun on 2016/10/18.
 */

public class NIMJsBridgeBuilder {

    private static final String PROTOCOL_SCHEMA = "nim";
    private static final String PROTOCOL_HOST = "dispatch";
    private static final int PROTOCOL_PORT = 1;

    private String protocol;
    private WebChromeClient webChromeClient;
    private ArrayList javaInterfacesForJS;
    private WebView webView;

    public NIMJsBridgeBuilder() {
    }

    public NIMJsBridge create() {
        checkProtocol();
        if (webView == null) {
            throw new IllegalArgumentException("必须调用setWebView(WebView)方法设置WebView");
        }
        return new NIMJsBridge(this);
    }

    public NIMJsBridgeBuilder setWebChromeClient(WebChromeClient webChromeClient) {
        this.webChromeClient = webChromeClient;
        return this;
    }

    public NIMJsBridgeBuilder addJavaInterfaceForJS(Object javaInterface) {
        if (javaInterface == null) {
            return this;
        }
        if (javaInterfacesForJS == null) {
            javaInterfacesForJS = new ArrayList();
        }
        javaInterfacesForJS.add(javaInterface);
        return this;
    }

    public NIMJsBridgeBuilder setWebView(WebView webView) {
        this.webView = webView;
        return this;
    }

    public WebChromeClient getWebChromeClient() {
        return webChromeClient;
    }

    public ArrayList getJavaInterfacesForJS() {
        return javaInterfacesForJS;
    }

    public WebView getWebView() {
        return webView;
    }

    public String getProtocol() {
        return protocol;
    }

    /**
     * 检测协议是否符合要求
     */
    private void checkProtocol() {
        protocol = PROTOCOL_SCHEMA + "://" + PROTOCOL_HOST + ":" + PROTOCOL_PORT + "?";
        Uri uri = Uri.parse(protocol);
        if (TextUtils.isEmpty(uri.getScheme()) || TextUtils.isEmpty(uri.getHost()) || uri.getPort() < 0
                || !protocol.endsWith("?")) {
            throw new IllegalArgumentException("协议的格式必须是 scheme://host:port?");
        }
    }
}
