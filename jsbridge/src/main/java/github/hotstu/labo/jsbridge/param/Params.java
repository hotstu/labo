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


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import github.hotstu.labo.jsbridge.annotation.Param;
import github.hotstu.labo.jsbridge.annotation.ParamCallback;
import github.hotstu.labo.jsbridge.annotation.ParamResponseStatus;
import github.hotstu.labo.jsbridge.core.NIMJsBridge;
import github.hotstu.labo.jsbridge.interact.Interact;
import github.hotstu.labo.jsbridge.util.ParamUtil;

/**
 * 一个方法对应一个Params
 * 主要实现解析方法参数中的@ParamXXX, json->params && params -> json
 * <p>
 * 该类会把{@link Method}的用{@link Param},{@link ParamCallback},{@link ParamResponseStatus}这几个注解标注的param解析出来，
 * {@link Param}解析为{@link ParamItem},{@link ParamCallback}解析为{@link ParamCallbackItem},{@link ParamResponseStatus}
 * 解析为{@link ParamResponseStatusItem}。
 * <p>
 * Created by huangjun on 2016/10/14.
 */
public class Params {

    /**
     * 解析出来的所有注解item
     */
    private BaseParamItem[] paramItems;

    Params() {
    }

    /**
     * 构造Params,解析方法中的参数,解析@Param,@ParamCallback,@PramResponseStatus,生成BasePramItem[]
     */
    public static Params createParams(Method method, NIMJsBridge bridge) {
        if (method == null) {
            return null;
        }

        // 一个参数可能有多个@annotation,至少有一个@Param
        Annotation[][] annotations = method.getParameterAnnotations();
        // 每个参数对应的Class
        Class[] parameters = method.getParameterTypes();
        if (annotations != null) {
            Params params = new Params();
            params.paramItems = new BaseParamItem[annotations.length];
            BaseParamItem paramItem;
            for (int i = 0; i < annotations.length; i++) {
                Annotation annotation;
                // 一个参数对应的所有@annotation
                if (annotations[i].length == 0) {
                    throw new IllegalArgumentException("方法的所有参数必须都得用" + Param.class.getSimpleName()
                            + "," + ParamCallback.class.getSimpleName()
                            + "," + ParamResponseStatus.class.getSimpleName()
                            + " 中的任意一个注解进行标注");
                }

                for (int j = 0; j < annotations[i].length; j++) {
                    annotation = annotations[i][j];
                    if (annotation != null && annotation instanceof Param) {
                        // 自动识别是否需要转换(非JsonObject能存储的类都需要转换)
                        boolean needConvert = !ParamUtil.isSupportClassType(parameters[i]);
                        Param paramKey = (Param) annotation;
                        paramItem = new ParamItem(paramKey.value(), parameters[i], needConvert);
                        params.paramItems[i] = paramItem;
                    } else if (annotation instanceof ParamCallback) {
                        paramItem = new ParamCallbackItem(parameters[i], null, bridge);
                        params.paramItems[i] = paramItem;
                    } else if (annotation instanceof ParamResponseStatus) {
                        ParamResponseStatus paramResponseStatus = (ParamResponseStatus) annotation;
                        paramItem = new ParamResponseStatusItem(parameters[i], paramResponseStatus.value());
                        params.paramItems[i] = paramItem;
                    }
                }
            }

            return params;
        }
        return null;
    }

    /**
     * json->params
     */
    public Object[] convertJson2ParamValues(Interact interact) {
        if (interact == null || paramItems == null) {
            return null;
        }
        Object[] result = new Object[paramItems.length];
        BaseParamItem paramItem;
        for (int i = 0; i < paramItems.length; i++) {
            paramItem = paramItems[i];
            if (paramItem != null) {
                result[i] = paramItem.convertJson2ParamValue(interact);
            }
        }
        return result;

    }

    /**
     * params->json
     */
    public void convertParamValues2Json(Interact interact, Object[] paramValues) {
        if (interact == null || paramValues == null) {
            return;
        }
        BaseParamItem paramItem;
        for (int i = 0; i < paramItems.length; i++) {
            paramItem = paramItems[i];
            if (paramItem != null) {
                paramItem.convertParamValue2Json(interact, paramValues[i]);
            }
        }
    }
}
