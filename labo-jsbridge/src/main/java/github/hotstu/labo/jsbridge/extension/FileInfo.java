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

package github.hotstu.labo.jsbridge.extension;


import github.hotstu.labo.jsbridge.annotation.Param;

/**
 * java->js传递文件数据
 * 主要是mime type + 文件数据的base64编码
 * <p>
 * Created by huangjun on 2016/10/20.
 */

public class FileInfo {
    @Param("name")
    public String name;

    @Param("path")
    public String path;

    @Param("type")
    public String type;

    @Param("size")
    public long size;

    @Param("base64")
    public String base64;

    @Override
    public String toString() {
        return "FileInfo{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", base64='" + base64 + '\'' +
                '}';
    }
}
