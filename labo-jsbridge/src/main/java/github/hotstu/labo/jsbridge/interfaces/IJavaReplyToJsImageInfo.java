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


import github.hotstu.labo.jsbridge.annotation.Param;
import github.hotstu.labo.jsbridge.annotation.ParamResponseStatus;
import github.hotstu.labo.jsbridge.extension.ImageInfo;
import github.hotstu.labo.jsbridge.interact.Response;

/**
 * java reply image info/data to js
 * <p>
 * Created by huangjun on 2016/10/20.
 */

public interface IJavaReplyToJsImageInfo {

    String RESPONSE_IMAGE_INFO = "image";

    void replyToJs(@ParamResponseStatus(Response.RESPONSE_DATA_STATUS) int status,
                   @ParamResponseStatus(Response.RESPONSE_DATA_MSG) String msg,
                   @Param(RESPONSE_IMAGE_INFO) ImageInfo imageInfo);
}
