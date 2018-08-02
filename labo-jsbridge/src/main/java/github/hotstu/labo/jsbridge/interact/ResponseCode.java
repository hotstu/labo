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

/**
 * NIMJsBridge 错误码
 * <p>
 * Created by huangjun on 2016/10/18.
 */

public interface ResponseCode {
    /**
     * 成功
     */
    int RES_SUCCESS = 200;

    /**
     * 目标不存在
     */
    int RES_TARGET_NOT_EXIST = 404;

    /**
     * 参数错误
     */
    int RES_PARAM_INVALID = 414;
}
