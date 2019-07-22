package github.hotstu.labo.rxfetch;

import android.webkit.MimeTypeMap;

import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static junit.framework.Assert.fail;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Config(manifest = Config.NONE, sdk = 23)
@RunWith(RobolectricTestRunner.class)
public class ExampleUnitTest {

    private RxFetch fetch;
    final Object lock = new Object();
    private MockWebServer server;

    @Before
    public void setUp() {
        OkHttpClient build = new OkHttpClient.Builder()
                .build();
        fetch = new RxFetch(build, new GsonTypeAdapter(), new JsoupTypeAdapter());
        server = new MockWebServer();


        // Start the server.
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }

        // Ask the server for its URL. You'll need this to make HTTP requests.
    }

    static class Bean {
        public String msg;
        public int code;
    }

    @Test
    public void testBasic() throws Exception {
        Type type = new TypeToken<String>() {
        }.getType();
        server.enqueue(new MockResponse().setBody("hello, world!"));
        HashMap<String, String> params = new HashMap<>();
        HttpUrl baseUrl = server.url("/v1/chat/");
        System.out.println(baseUrl.toString());
        final boolean[] success = {false};
        fetch.<String>get(baseUrl.toString(),
                params,
                String.class)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(s -> {
                    System.out.println(s);
                    success[0] = true;
                    synchronized (lock) {
                        lock.notify();
                    }
                }, throwable -> fail());
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertTrue(success[0]);
    }

    @Test
    public void testGSONParse() throws Exception {

        server.enqueue(new MockResponse().setBody("{\"msg\":\"hello,world\",\"code\":0}"));
        HashMap<String, String> params = new HashMap<>();
        HttpUrl baseUrl = server.url("/v1/chat/");
        System.out.println(baseUrl.toString());
        final boolean[] success = {false};
        fetch.<Bean>get(baseUrl.toString(),
                params,
                Bean.class)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(s -> {
                    System.out.println(s.msg);
                    success[0] = true;
                    synchronized (lock) {
                        lock.notify();
                    }
                }, throwable -> fail());
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertTrue(success[0]);
    }

    @Test
    public void testHtmlParse() throws Exception {
        final String text = "<html>\n" +
                "<body>\n" +
                "    <h1>hello,world2</h1>\n" +
                "</body>\n" +
                "</html>";
        server.enqueue(new MockResponse().setBody(text));
        HashMap<String, String> params = new HashMap<>();
        HttpUrl baseUrl = server.url("/v1/chat/");
        System.out.println(baseUrl.toString());
        final boolean[] success = {false};
        fetch.<Bean>get(baseUrl.toString(),
                params,
                Bean.class)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(s -> {
                    System.out.println(s.msg);
                    success[0] = true;
                    synchronized (lock) {
                        lock.notify();
                    }
                }, throwable -> fail());
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertTrue(success[0]);
    }

    @Test
    public void testProgress() throws Exception {
        final boolean[] success = {false};
        Request req = new Request.Builder()
                .url("http://httpbin.org/post")
                .post(FormBody.create(MediaType.parse("test/json"), "{\"msg\":\"hello,world\",\"code\":0}"))
                .build();

        fetch.<ProgressMsg>requestWithProgress(req, new TypeToken<ProgressMsg>() {
        }.getType())
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .subscribe(s -> {
                    System.out.println(s.type + ":" + s.progress + "," + s.result);
                    success[0] = true;
                }, throwable -> fail(), () -> {
                    synchronized (lock) {
                        lock.notify();
                    }
                });
        synchronized (lock) {
            lock.wait();
        }
        Assert.assertTrue(success[0]);
    }

    @Test
    public void testFlowable() {
        boolean wantUse = true;
        Flowable.just(wantUse)
                .flatMap(use -> {
                    if (use) {
                        return Flowable.just(Math.random()).map(f -> f > .5f).flatMap(
                                (canUse) -> {
                                    if (canUse) {
                                        return Flowable.just("fly1");
                                    } else {
                                        return Flowable.just("walk2");
                                    }
                                }
                        );
                    } else {
                        return Flowable.just("walk3");
                    }
                }).subscribe(System.out::println);
    }


    @Test
    public void testAnoter() {
        Flowable.<Integer>create(emitter -> {
            emitter.onNext(10);
        }, BackpressureStrategy.LATEST)
                .compose(upstream ->
                        upstream.flatMap(integer -> Flowable.create(emitter -> {
                            if (integer < 10) {
                                emitter.onNext(integer * 10);
                            } else {
                                emitter.onError(new IllegalStateException("n > 10"));
                            }
                        }, BackpressureStrategy.LATEST)))
                .compose((FlowableTransformer<Object, String>) upstream
                        -> upstream.flatMap(integer -> Flowable.<String>just(integer + "")))
                .doOnComplete(() -> System.out.println("doOnComplete"))
                .subscribe(integer -> {
                    System.out.println("on next" + integer);
                }, throwable -> {
                    System.out.println("on err");
                }, () -> {
                    System.out.println("on complete");
                }, subscription -> {
                    //System.out.println(subscription);
                    subscription.request(1);
                });

    }

    @Test
    public void testExt() {
        String url = "/xxx/1.jpg";
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(null);
        System.out.println(MediaType.parse(mimeType));

    }


    @Test
    public void testDownload() {
        String url = "http://dldir1.qq.com/weixin/Windows/WeChatSetup.exe";
        Request req = new Request.Builder()
                .url(url)
                .build();
        Disposable subscribe = fetch.download(req, "wx.exe", true)
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(
                        System.out::println,
                        Throwable::printStackTrace
                );
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        subscribe.dispose();
    }
}