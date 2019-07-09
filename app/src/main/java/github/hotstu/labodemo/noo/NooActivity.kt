package github.hotstu.labodemo.noo

import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import github.hotstu.labo.noob.NooAction
import github.hotstu.labo.noob.NooView
import github.hotstu.labo.noob.events
import github.hotstu.labodemo.R

class NooActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noo)
        val nooView = findViewById<NooView>(R.id.container)
        val subscribe = nooView.events(listOf(
                NooAction(Rect(0, 0, 100, 100), "desc1"),
                NooAction(Rect(100, 100, 200, 200), "desc2"),
                NooAction(Rect(200, 200, 300, 300), "desc3")
        )).subscribe {
            nooView.archorToAction(it)
        }
    }
}
