package github.hotstu.labo.noob

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import androidx.core.graphics.toRectF
import io.reactivex.Observable
import java.util.*

/**
 * @author hglf [hglf](https://github.com/hotstu)
 * @since 7/9/19
 * @desc
 */
fun NooView.events(array: List<NooAction>): Observable<NooAction> = Observable.create {
    val emitter = it
    val quene = LinkedList(array)
    val first = quene.poll()
    if (first == null) {
        it.onComplete()
    } else {
        it.onNext(first)
    }
    this.setOnClickListener {
        val item = quene.poll()
        if (item == null) {
            this@events.setOnClickListener(null)
            emitter.onComplete()
        } else {
            emitter.onNext(item)
        }
    }

}


data class NooAction(val rect: Rect, val desc: String)

class NooView(context: Context) : FrameLayout(context) {
    private var action: NooAction? = null
    private val dreamRect = Rect()
    private val mHelperPaint = Paint().apply {
        style = Paint.Style.STROKE
    }
    private val mPaint = Paint().apply {
        isAntiAlias = true
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val color = Color.parseColor("#7f333333")


    init {
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        isClickable = true
    }

    fun detachFromWindow(activity: Activity) {
        val rootView = activity.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        rootView.removeView(this)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        //TODO 只负责layout指定的child， 其他委托给super处理
        dreamRect.set(0, 0, right - left, bottom - top)
        if (action != null) {
            val rect = action!!.rect
            //rect.offset(left, top)
            if (rect.centerY() > dreamRect.centerY()) {
                dreamRect.bottom = rect.top
            } else {
                dreamRect.top = rect.bottom
            }
        }
        layoutChildren(dreamRect.left, dreamRect.top, dreamRect.right, dreamRect.bottom)

    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentLeft = left + paddingLeft
        val parentRight = right - paddingRight

        val parentTop = top + paddingTop
        val parentBottom = bottom - paddingBottom

        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility != View.GONE) {
                val lp = child.layoutParams as LayoutParams

                val width = child.measuredWidth
                val height = child.measuredHeight

                val childLeft: Int
                val childTop: Int

                var gravity = lp.gravity
                if (gravity == -1) {
                    gravity = Gravity.TOP or Gravity.START
                }

                val layoutDirection = layoutDirection
                val absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection)
                val verticalGravity = gravity and Gravity.VERTICAL_GRAVITY_MASK
                val horizontalGravity = absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK

                when (horizontalGravity) {
                    Gravity.CENTER_HORIZONTAL -> childLeft = parentLeft + (parentRight - parentLeft - width) / 2 +
                            lp.leftMargin - lp.rightMargin
                    Gravity.RIGHT -> childLeft = parentRight - width - lp.rightMargin

                    Gravity.LEFT -> childLeft = parentLeft + lp.leftMargin
                    else -> childLeft = parentLeft + lp.leftMargin
                }

                when (verticalGravity) {
                    Gravity.TOP -> childTop = parentTop + lp.topMargin
                    Gravity.CENTER_VERTICAL -> childTop = parentTop + (parentBottom - parentTop - height) / 2 +
                            lp.topMargin - lp.bottomMargin
                    Gravity.BOTTOM -> childTop = parentBottom - height - lp.bottomMargin
                    else -> childTop = parentTop + lp.topMargin
                }

                child.layout(childLeft, childTop, childLeft + width, childTop + height)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //TODO measure child with cliped rect
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(color)
        //canvas.drawRect(dreamRect.toRectF(), mHelperPaint)
        action?.also {
            canvas.drawOval(it.rect.toRectF(), mPaint)
        }
    }

    fun anchorToAction(action: NooAction) {
        this.action = action
        requestLayout()
        postInvalidate()
    }

    companion object {
        fun attach2Window(activity: Activity): NooView {
            val rootView = activity.findViewById<ViewGroup>(Window.ID_ANDROID_CONTENT)
            val v = NooView(activity)
            rootView.addView(v, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
            return v
        }
    }
}