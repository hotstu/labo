package github.hotstu.labodemo.noo

import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.doOnLayout
import github.hotstu.labo.noob.NooAction
import github.hotstu.labo.noob.NooView
import github.hotstu.labo.noob.events
import github.hotstu.labodemo.R
import io.reactivex.Observable

class NooActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noo)
        val container = findViewById<ViewGroup>(R.id.container)
        container.doOnLayout {
            startIntroduce()
        }
    }

    private fun startIntroduce() {
        val nooView = NooView.attach2Window(this)
        val container = findViewById<ViewGroup>(R.id.container)
        val subscribe = Observable.just(container.children.map {
            println(it)
            it.setOnClickListener { startIntroduce() }

            val ret = Rect()
            //在窗口中的位置
            it.getGlobalVisibleRect(ret)
            val location = IntArray(2)
            container.getLocationInWindow(location)
            //r的位置是相对window，但draw的xy是相对parent的，所以要变换回来
            ret.offset(-location[0], -location[1])
            ret
        }).flatMap {
            val list = it.map { NooAction(it, "窗前明月光") }.toMutableList()
            list.add(0, NooAction(Rect(), "start"))
            list.add(NooAction(Rect(), "stop"))
            nooView.events(list)
        }.subscribe {
            if (it.desc == "start") {
                nooView.anchorToAction(it)
            } else if (it.desc == "stop") {
                nooView.detachFromWindow(this)
            } else {
                nooView.anchorToAction(it)
            }
        }
    }
}
