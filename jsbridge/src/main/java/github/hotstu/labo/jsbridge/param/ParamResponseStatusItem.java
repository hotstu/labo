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

package github.hotstu.labo.jsbridge.param;


import github.hotstu.labo.jsbridge.annotation.ParamResponseStatus;
import github.hotstu.labo.jsbridge.interact.Interact;
import github.hotstu.labo.jsbridge.interact.Response;

/**
 * {@link ParamResponseStatus}注解标注的参数解析
 * <p>
 * Created by huangjun on 2016/10/19.
 */

public final class ParamResponseStatusItem extends BaseParamItem {

    public ParamResponseStatusItem(Class paramClass, String paramKey) {
        super(paramClass, paramKey);
    }

    @Override
    public Object convertJson2ParamValue(Interact interact) {
        if (interact == null || !Interact.isResponse(interact)) {
            return null;
        }

        return ((Response) interact).getResponse().opt(paramKey);
    }

    @Override
    public void convertParamValue2Json(Interact interact, Object obj) {
        if (interact == null || !Interact.isResponse(interact) || obj == null) {
            return;
        }

        ((Response) interact).putStatusKeyValue(paramKey, obj);
    }
}
