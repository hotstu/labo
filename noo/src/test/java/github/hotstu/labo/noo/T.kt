package github.hotstu.labo.noo

import android.graphics.Rect
import io.reactivex.Observable
import org.junit.Test

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/9/19
 * @desc
 */
class T {
    fun ltwh(left: Int, top: Int, w: Int, h: Int): Rect {
        return Rect(left, top, left + w, top + h)
    }
    @Test
    fun main() {
        Observable.create<String> {
            //it.onNext("1")
            it.onComplete()
        }.flatMap { t: String -> Observable.just("") }.subscribe({ println(it) }, {  }, { println("complete")})


    }
}