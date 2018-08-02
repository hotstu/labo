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

/**
 * @author hglf
 * @since 2018/6/28
 */
public class ProgressMsg {
    public static final int START = 0;
    public static final int SEN_PROGRESS = 1;
    public static final int REV_PROGRESS = 2;
    public static final int RESULT = 3;
    public final int type;
    public long progress;
    public long total;
    public String result;

    public ProgressMsg(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return String.format("%d %d", type, progress);
    }
}
