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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

/**
 * @author hglf
 * @since 2018/6/27
 */
public class GsonTypeAdapter implements TypeAdapter {

    private final Gson g;

    public GsonTypeAdapter(Gson g) {
        this.g = g;
    }

    public GsonTypeAdapter() {
        this(new GsonBuilder().create());
    }

    @Override
    public <T> T transform(String raw, Type clazz) {
        if (String.class.equals(clazz)) {
            return null;//String 类型不转换
        }
        return g.fromJson(raw, clazz);
    }
}
