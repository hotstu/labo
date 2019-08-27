package github.hotstu.labo.tool.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import github.hotstu.labo.tool.util.ObjectUtil;

/**
 * @author hglf
 * @since 2018/10/19
 */
public class LiveDataUtil {
    /**
     * 当source.value未发生改变的时候不会调用switchMapFunction，避免重复加载
     *
     * @param source
     * @param switchMapFunction
     * @param <X>
     * @param <Y>
     * @return
     */
    @MainThread
    public static <X, Y> LiveData<Y> ignoreDuplicateFlatMap(
            @NonNull LiveData<X> source,
            @NonNull final Function<X, LiveData<Y>> switchMapFunction) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(source, new Observer<X>() {
            Object cache = new Object();
            LiveData<Y> mRealSource;

            @Override
            public void onChanged(@Nullable X x) {
                if (ObjectUtil.equals(cache, x)) {
                    return;
                }
                cache = x;

                mRealSource = switchMapFunction.apply(x);

                result.addSource(mRealSource, y -> {
                    result.setValue(y);
                    result.removeSource(mRealSource);
                    mRealSource = null;
                });

            }
        });
        return result;
    }


    /**
     * 当filterFunction返回true的时候才会调用switchMapFunction，避免重复加载
     *
     * @param source
     * @param switchMapFunction
     * @param <X>
     * @param <Y>
     * @return
     */
    @MainThread
    public static <X, Y> LiveData<Y> filterFlatMap(
            @NonNull LiveData<X> source,
            @NonNull final Function<X, Boolean> filterFunction,
            @NonNull final Function<X, LiveData<Y>> switchMapFunction) {
        final MediatorLiveData<Y> result = new MediatorLiveData<>();
        result.addSource(source, new Observer<X>() {
            LiveData<Y> mRealSource;

            @Override
            public void onChanged(@Nullable X x) {
                if(!filterFunction.apply(x)) {
                    return;
                }
                mRealSource = switchMapFunction.apply(x);

                result.addSource(mRealSource, y -> {
                    result.setValue(y);
                    result.removeSource(mRealSource);
                    mRealSource = null;
                });

            }
        });
        return result;
    }
}
