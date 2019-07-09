package github.hotstu.labo.noob

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/9/19
 * @desc
 */
fun NooView.events(array: List<NooAction>): Observable<NooAction> {
    return NooViewObservable(this, array)
}


private class NooViewObservable(private val view: NooView, private val array: List<NooAction>) : Observable<NooAction>() {
    var index = 0

    override fun subscribeActual(observer: Observer<in NooAction>) {
        val listener = ActualListener(view, observer)
        observer.onSubscribe(listener)
        view.setStepListener(listener)
        listener.onNext()
    }

    private inner class ActualListener(
            private val nooView: NooView,
            private val observer: Observer<in NooAction>
    ) : MainThreadDisposable(), NooView.Listener {
        override fun onNext() {
            if (!isDisposed) {
                if (index >= array.size) {
                    observer.onComplete()
                } else {
                    val nooAction = array[index]
                    index += 1
                    observer.onNext(nooAction)
                }
            }
        }

        override fun onDispose() {
            nooView.setStepListener(null)
        }
    }
}

class NooView : FrameLayout {
    private var listener: Listener? = null
    private var aciton: NooAction? = null
    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val color = Color.parseColor("#7f000000")


    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isClickable = true
        setOnClickListener {
            listener?.onNext()
        }
        LayoutInflater.from(context).inflate(R.layout.noo_layout_view, this, true)

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }



    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(color)
        aciton?.also {
            canvas.drawOval(it.rect.toRectF(), mPaint)
        }
    }

    fun archorToAction(aciton: NooAction) {
        this.aciton = aciton
        requestLayout()
        postInvalidate()
    }

    fun setStepListener(l: Listener?) {
        this.listener = l
    }

    interface Listener {
        fun onNext()
    }
}