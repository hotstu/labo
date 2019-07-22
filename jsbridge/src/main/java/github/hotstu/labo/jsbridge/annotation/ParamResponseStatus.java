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

package github.hotstu.labo.jsbridge.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注java<->js相互调用间的响应状态数据,从而由框架自动解析响应状态数据。
 * java->js->javaCallback,用于在javaCallback上标注需要接收status和msg的值的参数.
 * js-java->javaReplyToJs->js,用于在javaReplyToJs上标注需要发送status和msg值的参数.
 * 响应状态数据格式为{status:200, msg:"success"}。{@link #value()}就是status或msg这些key值</p>
 * <p>
 * Created by huangjun on 2016/10/14.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ParamResponseStatus {
    String value();
}
