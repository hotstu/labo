package github.hotstu.labodemo.tools

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import github.hotstu.labo.tool.*

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/14/19
 * @desc
 */
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val config = config {
                group(groupId = "chatGroup", groupName = "chat") {
                    channel(id = "Ch1") {
                        name = "channel1"
                        description = "when some chat request come"
                    }
                    channel(id = "Ch2") {
                        name = "channel2"
                        description = "when some important chat request come"
                        importance = NotificationManager.IMPORTANCE_HIGH
                    }

                }
                group(groupId = "noticeGroup", groupName = "notice") {
                    channel(id = "Ch3") {
                        name = "personal message"
                        description = "send personal message"
                    }
                    channel(id = "Ch4") {
                        name = "broadcast message"
                        description = "send broadcast"
                    }
                }
            }
            config.applyTo(notificationManager)
            config.clean()
        }
    }
}