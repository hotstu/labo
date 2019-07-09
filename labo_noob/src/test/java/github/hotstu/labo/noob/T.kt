package github.hotstu.labo.noob

import android.graphics.Rect

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/9/19
 * @desc
 */
class T {
    fun ltwh(left: Int, top: Int, w: Int, h: Int): Rect {
        return Rect(left, top, left + w, top + h)
    }
    fun main() {
        val nooView = NooView(null)
        nooView.events(listOf(
                NooAction(ltwh(10,10, 200, 200), "desc1"),
                NooAction(ltwh(110,110, 200, 200), "desc2")
        )).subscribe({
            nooView.archorToAction(it)

        }, {

        }, {

        })
    }
}