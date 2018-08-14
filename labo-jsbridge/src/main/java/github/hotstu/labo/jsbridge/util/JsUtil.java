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

package github.hotstu.labo.jsbridge.util;

import android.os.Build;
import android.webkit.WebView;

/**
 * Created by huangjun on 2016/10/12.
 */

public class JsUtil {

    private static final String JAVA_SCRIPT = "javascript:";

    public static boolean callJS(final WebView webView, final String url) {
        if (webView != null) {
            final String jsUrl = url.startsWith(JAVA_SCRIPT) ? url : JAVA_SCRIPT + url;
            LogUtil.i("JAVA -> JS URL：" + jsUrl);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.evaluateJavascript(jsUrl, null);
            } else {
                webView.loadUrl(jsUrl);
            }

            return true;
        }

        return false;
    }

    public static String escape(String paramString) {
        if (paramString == null || "".equals(paramString)) {
            return paramString;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        int i = paramString.length();
        for (int j = 0; j < i; j++) {
            char c1 = paramString.charAt(j);
            switch (c1) {
                case '"':
                case '\'':
                case '/':
                case '\\':
                    localStringBuilder.append('\\').append(c1);
                    break;
                case '\t':
                    localStringBuilder.append("\\t");
                    break;
                case '\b':
                    localStringBuilder.append("\\b");
                    break;
                case '\n':
                    localStringBuilder.append("\\n");
                    break;
                case '\r':
                    localStringBuilder.append("\\r");
                    break;
                case '\f':
                    localStringBuilder.append("\\f");
                    break;
                default:
                    //不可打印的控制字符，http://defindit.com/ascii.html
                    if (c1 <= '\037') {
                        localStringBuilder.append(String.format("\\u%04x", new Object[]{Integer.valueOf(c1)}));
                    } else {
                        localStringBuilder.append(c1);
                    }
                    break;
            }
        }
        return localStringBuilder.toString();
    }
}
