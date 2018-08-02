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

package github.hotstu.labo.rxfetch;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.lang.reflect.Type;

/**
 * @author hglf
 * @since 2018/6/28
 */
public class JsoupTypeAdapter implements TypeAdapter {
    @Override
    public <T> T transform(String raw, Type clazz) {
        if(raw != null && raw.trim().startsWith("<") && clazz.equals(ExampleUnitTest.Bean.class)) {
            Document doc = Jsoup.parse(raw);
            ExampleUnitTest.Bean bean = new ExampleUnitTest.Bean();
            bean.msg = doc.select("h1").text();
            bean.code = 1;
            return (T) bean;
        }
        return null;
    }
}
