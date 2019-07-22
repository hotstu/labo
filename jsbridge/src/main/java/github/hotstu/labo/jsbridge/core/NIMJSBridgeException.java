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

/**
 * JSBridge运行时异常,说明具体的异常原因
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class NIMJSBridgeException extends RuntimeException {

    public NIMJSBridgeException() {
    }

    public NIMJSBridgeException(String detailMessage) {
        super(detailMessage);
    }
}
