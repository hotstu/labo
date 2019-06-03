package github.hotstu.labodemo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import androidx.databinding.ViewDataBinding
import github.hotstu.labo.nott.bigPictureStyle
import github.hotstu.labo.nott.bigTextStyle
import github.hotstu.labo.nott.inboxStyle
import github.hotstu.labo.nott.messagingStyle


class NotificationActivity : AppCompatActivity() {

    class Presentor(val activity: AppCompatActivity) {
        val withAction: ObservableBoolean = ObservableBoolean()
        fun normal() {
            with(NotificationCompat.Builder(activity, "Ch1")) {
                val from = NotificationManagerCompat.from(activity)
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setTicker("normal message")
                setContentTitle("title")
                setContentText("content".repeat(100))
                setLargeIcon(BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher_round))
                setOngoing(true)
                from.notify(99, this.build())
            }
        }

        fun bigText() {
            with(NotificationCompat.Builder(activity, "Ch1")) {
                this.bigTextStyle {
                    //this.bigText("bigText".repeat(100))
                }
                val from = NotificationManagerCompat.from(activity)
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setTicker("normal message")
                setContentTitle("title")
                setContentText("content".repeat(100))
                setSubText("summary")
                setOngoing(true)
                from.notify(99, this.build())
            }
        }

        fun messaging() {
            with(NotificationCompat.Builder(activity, "Ch1")) {
                val john = Person.Builder().apply {
                    setBot(false)
                    setIcon(IconCompat.createWithResource(activity, R.mipmap.ic_launcher_round))
                    setName("john")
                }.build()
                this.messagingStyle(john) {
                    //this.bigText("bigText".repeat(100))
                    //this.addMessage()
                    conversationTitle = "小米发布会"
                    addMessage("are u ok", System.currentTimeMillis(), "jun lei")
                    addMessage("are u ok", System.currentTimeMillis(), "stan li")
                }
                val from = NotificationManagerCompat.from(activity)
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setOngoing(true)
                from.notify(99, this.build())
            }
        }

        fun bigPicture() {
            with(NotificationCompat.Builder(activity, "Ch1")) {
                bigPictureStyle {
                    bigPicture(BitmapFactory.decodeResource(activity.resources, R.drawable.sample1))
                }
                val from = NotificationManagerCompat.from(activity)
                setSmallIcon(R.drawable.ic_launcher_foreground)
                setTicker("normal message")
                setContentTitle("title")
                setContentText("content".repeat(100))
                setLargeIcon(BitmapFactory.decodeResource(activity.resources, R.mipmap.ic_launcher_round))
                setOngoing(true)
                from.notify(99, this.build())
            }
        }

        fun inbox() {

            with(NotificationCompat.Builder(activity, "Ch1")) {
                inboxStyle {
                    addLine("message 1")
                    addLine("message 2")
                    setContentTitle("")
                    setSummaryText("+3 more")
                }
                setContentTitle("5 New mails from john")
                setContentText("RUOK")
                setSmallIcon(R.drawable.ic_launcher_foreground)
                val from = NotificationManagerCompat.from(activity)
                setOngoing(true)
                from.notify(99, this.build())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_notification)
        binding.setVariable(BR.presentor, Presentor(this))
    }
}
