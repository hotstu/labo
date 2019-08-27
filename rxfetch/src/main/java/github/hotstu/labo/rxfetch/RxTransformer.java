package github.hotstu.labo.rxfetch;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import io.reactivex.FlowableTransformer;
import io.reactivex.ObservableTransformer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 通常不推荐使用Rxjava提供的#Schedulers.io()进行长时间的io操作
 * 因为通常observable被dispose之后http请求并不能立即中断，这个被占用的线程
 * Schedulers.io重用，会造成线程阻塞
 * https://github.com/ReactiveX/RxJava/issues/4230
 *
 * @author hglf
 * @since 2018/12/10
 */
public class RxTransformer {

    private final FlowableTransformer<?, ?> mTransformer;
    private final ObservableTransformer<?, ?> mObTransformer;

    public RxTransformer(ThreadFactory factory) {
        Scheduler scheduler = Schedulers.from(Executors.newCachedThreadPool(factory));
        mTransformer = upstream -> upstream.subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread());
        mObTransformer = upstream -> upstream.subscribeOn(scheduler)
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressWarnings("unchecked")
    public  <T> FlowableTransformer<T, T> io_main() {
        return (FlowableTransformer<T, T>) mTransformer;
    }

    @SuppressWarnings("unchecked")
    public  <T> ObservableTransformer<T, T> io_main_ob() {
        return (ObservableTransformer<T, T>) mObTransformer;
    }

}
