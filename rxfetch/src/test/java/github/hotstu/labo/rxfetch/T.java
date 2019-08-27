package github.hotstu.labo.rxfetch;

import org.junit.Test;

import io.reactivex.Observable;

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @desc
 * @since 7/31/19
 */
public class T {
    @Test
    public void testRx() {
        Observable<Integer> ob = Observable.create(emitter -> {
            System.out.println("-----1-----");
            emitter.onNext(1);
            emitter.onComplete();
        });

        Observable<String> objectObservable = ob.flatMap(i -> {
            System.out.println("-----2-----");
            return Observable.just("-" + i + "-");
        });

        objectObservable.subscribe(s -> {
            System.out.println(s);
        });

    }
}
