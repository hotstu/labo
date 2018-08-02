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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 参数转换工具类
 * <p>
 * Created by huangjun on 2016/10/14.
 */

public class ParamUtil {

    private static Set<Class> supportClassType = new HashSet<>();

    static {
        /**
         * JSONObject类可以存放 JSONObject, JSONArray, String, Boolean, Integer, Long, Double这些类，
         * 但是对于非以上的类,就得需要进行一些转换才可以往 json 中存放,即解析时 needConvert = true
         * 注意：js->java,如果有浮点数,会认为是 double,而不是 float,因此java函数的参数需要用 double or Double 类型
         */
        supportClassType.add(JSONObject.class);
        supportClassType.add(JSONArray.class);
        supportClassType.add(String.class);
        supportClassType.add(Integer.class);
        supportClassType.add(int.class);
        supportClassType.add(Boolean.class);
        supportClassType.add(boolean.class);
        supportClassType.add(Long.class);
        supportClassType.add(long.class);
        supportClassType.add(Double.class);
        supportClassType.add(double.class);
        supportClassType.add(Float.class);
        supportClassType.add(float.class);
    }

    public static boolean isSupportClassType(Class classType) {
        return supportClassType.contains(classType);
    }

    public static <T> List<T> convertToList(final JSONArray array) {
        if (array == null) {
            return null;
        }

        List<T> result = new ArrayList<>(array.length());
        try {
            for (int i = 0; i < array.length(); i++) {
                result.add((T) array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static <T> List<T> convertToList(final List<Object> array) {
        if (array == null) {
            return null;
        }

        List<T> result = new ArrayList<>(array.size());
        for (Object o : array) {
            result.add((T) o);
        }

        return result;
    }
}
