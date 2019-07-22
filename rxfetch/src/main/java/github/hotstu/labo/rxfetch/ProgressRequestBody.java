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

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;


class ProgressRequestBody extends RequestBody {
    private final RequestBody mRequestBody;
    private ProgressListener progressListener;


    public ProgressRequestBody(RequestBody requestBody) {
        this.mRequestBody = requestBody;
    }

    public void setListener(ProgressListener listener) {
        this.progressListener = listener;
    }
    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (progressListener == null) {
            mRequestBody.writeTo(sink);
            return;
        }
        final long totalBytes = contentLength();
        BufferedSink progressSink = Okio.buffer(new ForwardingSink(sink) {
            private long bytesWritten = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                bytesWritten += byteCount;
                progressListener.update(bytesWritten, totalBytes, bytesWritten>= totalBytes);
                super.write(source, byteCount);
            }
        });
        mRequestBody.writeTo(progressSink);
        progressSink.flush();
    }

}