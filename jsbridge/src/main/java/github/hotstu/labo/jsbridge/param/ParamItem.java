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

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import github.hotstu.labo.jsbridge.annotation.Param;
import github.hotstu.labo.jsbridge.interact.Interact;

/**
 * {@link Param}注解标注的参数解析
 * <p>
 * Created by Administrator on 2016/10/19.
 */

public final class ParamItem extends BaseParamItem {

    private boolean needConvert;

    public ParamItem(String paramKey, Class paramClass, boolean needConvert) {
        super(paramClass, paramKey);
        this.needConvert = needConvert;
    }

    @Override
    public Object convertJson2ParamValue(Interact interact) {
        if (interact == null || interact.getValues() == null) {
            return null;
        }

        JSONObject jsonObject = interact.getValues();
        if (needConvert) {
            try {
                // JsonArray -> List<Object>
                if (!TextUtils.isEmpty(paramKey) && paramType.isAssignableFrom(List.class)) {
                    JSONArray array = jsonObject.optJSONArray(paramKey);
                    if (array != null) {
                        List<Object> objects = new ArrayList<>(array.length());
                        for (int i = 0; i < array.length(); i++) {
                            objects.add(array.opt(i));
                        }
                        return objects;
                    }

                    return null;
                }

                // parse @param T, { t:{f1:xxx, f2:xxx}, v1:xxx, v2:xxx}
                JSONObject value = !TextUtils.isEmpty(paramKey) ? (JSONObject) jsonObject.opt(paramKey) : jsonObject;
                if (value == null) {
                    return null;
                }
                Object instance = paramType.newInstance();
                Field[] fields = paramType.getDeclaredFields();
                for (Field field : fields) {
                    Param p = field.getAnnotation(Param.class);
                    if (p != null) {
                        field.setAccessible(true);
                        field.set(instance, value.opt(p.value()));
                    }
                }
                return instance;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            // basic type
            return jsonObject.opt(paramKey);
        }
    }

    @Override
    public void convertParamValue2Json(Interact interact, Object obj) {
        if (interact == null || obj == null) {
            return;
        }

        if (needConvert) {
            JSONObject objectParamJson = null;
            if (!TextUtils.isEmpty(paramKey)) {
                objectParamJson = new JSONObject();
            }

            Class cl = obj.getClass();
            Field[] fields = cl.getDeclaredFields();
            for (Field field : fields) {
                Param p = field.getAnnotation(Param.class);
                if (p != null) {
                    field.setAccessible(true);
                    Object inst;
                    try {
                        inst = field.get(obj);
                        if (inst != null) {
                            if (objectParamJson != null) {
                                objectParamJson.put(p.value(), inst);
                            } else {
                                interact.putKeyValue(p.value(), inst);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (objectParamJson != null) {
                interact.putKeyValue(paramKey, objectParamJson);
            }
        } else {
            interact.putKeyValue(paramKey, obj);
        }
    }
}
