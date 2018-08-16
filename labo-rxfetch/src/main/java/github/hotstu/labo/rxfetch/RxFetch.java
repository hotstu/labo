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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * okhttp 的RxJava封装
 */
public class RxFetch {
    private final OkHttpClient client;
    private final List<TypeAdapter> adapters;

    public RxFetch(OkHttpClient client) {
        this(client, new TypeAdapter[]{});
    }

    public RxFetch(OkHttpClient client, TypeAdapter... adapter) {
        this.client = client;
        this.adapters = new ArrayList<>();
        if (adapter == null || adapter.length == 0) {
            this.adapters.add(new GsonTypeAdapter());
        } else {
            this.adapters.addAll(Arrays.asList(adapter));
        }
    }

    public  Flowable<ProgressMsg> download(final Request request,final String dstPath, boolean useResume) {
        final File targetFile = new File(dstPath).getAbsoluteFile();
        final File tempFile = new File(dstPath + ".tmp").getAbsoluteFile();
        useResume = tempFile.exists() && useResume;
        return Observable.just(useResume).flatMap(use -> {
            if(use) {
                return Observable.create(emitter -> {
                    Response execute = null;
                    try {
                        long current = tempFile.length();
                        Request headreq = request.newBuilder()
                                //.head()
                                .header("RANGE", "bytes=" + current + "-"+ (current + 1024))
                                .build();
                        execute = client.newCall(headreq).execute();
                        int code = execute.code();
                        try {
                            execute.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        emitter.onNext(code == 206);
                    } catch (IOException e) {
                        e.printStackTrace();
                        emitter.onNext(false);
                    }
                    emitter.onComplete();
                });
            } else {
                return Observable.just(use);
            }
        }).flatMap(use -> Observable.<ProgressMsg>create(emitter -> {
            FileUtils.forceMkdirParent(tempFile);
            BufferedSink sink = null;
            BufferedSource source = null;
            Request req = request;
            try {
                long  current;
                if(!use) {
                    current =0;
                    sink = Okio.buffer(Okio.sink(tempFile));
                } else {
                    current = tempFile.length();
                    req = request.newBuilder()
                            .header("RANGE", "bytes=" + current + "-" )
                            .build();
                    sink = Okio.buffer(Okio.appendingSink(tempFile));
                }
                Response response = client.newCall(req).execute();
                ResponseBody body = response.body();
                ProgressResponseBody progressBody = new ProgressResponseBody(body, current);
                progressBody.setListener((totalBytesRead, contentLenth, complete) -> {
                    ProgressMsg value = new ProgressMsg(ProgressMsg.REV_PROGRESS);
                    value.progress = totalBytesRead;
                    value.total = contentLenth;
                    emitter.onNext(value);
                });
                source = progressBody.source();
                Buffer buffer = new Buffer();
                while (!emitter.isDisposed() && source.read(buffer, 8192) != -1) {
                    long emitByteCount = buffer.completeSegmentByteCount();
                    if (emitByteCount > 0) {
                        sink.write(buffer, emitByteCount);
                    }
                }
                if (emitter.isDisposed()) {
                    return;
                }
                if (buffer.size() > 0) {
                    sink.write(buffer, buffer.size());
                }
                sink.flush();
                Util.closeQuietly(source);
                Util.closeQuietly(sink);
                if (targetFile.exists()) {
                    FileUtils.forceDelete(targetFile);
                }
                FileUtils.moveFile(tempFile, targetFile);
                ProgressMsg value = new ProgressMsg(ProgressMsg.RESULT);
                value.result = targetFile.getAbsolutePath();
                emitter.onNext(value);
                emitter.onComplete();
            } finally {
                Util.closeQuietly(source);
                Util.closeQuietly(sink);
            }
        })).toFlowable(BackpressureStrategy.LATEST);
    }


    public  Flowable<ProgressMsg> requestWithProgress(final Request request, final Type clazz) {
        return Flowable.<ProgressMsg>create(emitter -> {
            final CancelableCall cancelableCall = new CancelableCall();
            Call call;
            if (request.body() != null) {
                ProgressRequestBody reqbody = new ProgressRequestBody(request.body());
                reqbody.setListener((totalBytesRead, contentLenth, complete) -> {
                    ProgressMsg reqEvent = new ProgressMsg(ProgressMsg.SEN_PROGRESS);
                    reqEvent.progress = totalBytesRead;
                    reqEvent.total = contentLenth;
                    if (emitter.isCancelled()) {
                        cancelableCall.cancel();
                    }
                    emitter.onNext(reqEvent);
                });
                request.newBuilder().method(request.method(), reqbody);
                call = client.newCall(request.newBuilder().method(request.method(), reqbody).build());
            } else {
                call = client.newCall(request);
            }
            cancelableCall.setCall(call);
            emitter.onNext(new ProgressMsg(ProgressMsg.START));
            Response execute = call.execute();
            ProgressResponseBody body = new ProgressResponseBody(execute.body());
            body.setListener((totalBytesRead, contentLenth, complete) -> {
                ProgressMsg reqEvent = new ProgressMsg(ProgressMsg.REV_PROGRESS);
                reqEvent.progress = totalBytesRead;
                reqEvent.total = contentLenth;
                if (emitter.isCancelled()) {
                    cancelableCall.cancel();
                }
                emitter.onNext(reqEvent);
            });
            try {
                String result = body.string();
                ProgressMsg reqEvent = new ProgressMsg(ProgressMsg.RESULT);
                reqEvent.result = result;
                emitter.onNext(reqEvent);
                body.setListener(null);
            } finally {
                body.close();
            }
            emitter.onComplete();
        }, BackpressureStrategy.LATEST);

    }


    public <T> Flowable<T> request(final Request request, final Type clazz) {
        return Flowable.fromCallable(() -> client.newCall(request).execute())
                .map(response -> {
                    String result;
                    try {
                        result = response.body().string();
                    } finally {
                        response.body().close();
                    }
                    if (response.code() < 300) {
                        return result;
                    } else {
                        throw new HTTPException(response.code(), result);
                    }

                }).map(s -> {
                    for (int i = adapters.size() - 1; i >= 0; i--) {
                        T transform = adapters.get(i).<T>transform(s, clazz);
                        if (transform != null) {
                            return transform;
                        }
                    }
                    if (String.class.equals(clazz)) {
                        return (T) s;
                    } else {
                        throw new RuntimeException("adapter for type" + clazz + "not set");
                    }
                });
    }


    public static class HTTPException extends IOException {
        public int code;
        public String result;

        HTTPException(int code, String result) {
            super();
            this.code = code;
            this.result = result;
        }
    }

    public static class CancelableCall{
        Call mCall;

        public void setCall(Call mCall) {
            this.mCall = mCall;
        }

        public void cancel(){
            if (mCall != null) {
                mCall.cancel();
            }
        }
    }


    public <T> Flowable<T> get(String url, Map<String, String> params, final Type clazz) {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (param.getKey() == null) {
                    throw new IllegalArgumentException("key == null");
                }
                if (param.getValue() == null) {
                    throw new IllegalArgumentException("value == null");
                }
                builder.addQueryParameter(param.getKey(), param.getValue());
            }
        }
        Request req = new Request.Builder()
                .url(builder.build())
                .build();
        return request(req, clazz);
    }

    public <T> Flowable<T> post(String url, Map<String, String> params, final Type clazz) {
        FormBody.Builder form = new FormBody.Builder();

        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                if (param.getKey() == null) {
                    throw new IllegalArgumentException("key == null");
                }
                if (param.getValue() == null) {
                    throw new IllegalArgumentException("value == null");
                }
                form.add(param.getKey(), param.getValue());
            }
        }
        Request req = new Request.Builder()
                .url(url)
                .post(form.build())
                .build();
        return request(req, clazz);
    }

}
