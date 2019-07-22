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

package github.hotstu.labo.jsbridge.interfaces;

import github.hotstu.labo.jsbridge.annotation.ParamResponseStatus;
import github.hotstu.labo.jsbridge.interact.Response;

/**
 * js->java, java reply to js, 在此通过@Param定义需要回调的参数类型
 * 默认回调 int status, String msg
 * 如果需要回复更多类型的数据，可以参照此接口扩展
 * <p>
 * Created by huangjun on 2016/10/14.
 */

public interface IJavaReplyToJs {
    void replyToJs(@ParamResponseStatus(Response.RESPONSE_DATA_STATUS) int status,
                   @ParamResponseStatus(Response.RESPONSE_DATA_MSG) String msg);
}
