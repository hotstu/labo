package github.hotstu.labo.nott

import android.app.Notification
import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/30/19
 * @desc
 */
fun NotificationCompat.Builder.inboxStyle(init: NotificationCompat.InboxStyle.()->Unit): NotificationCompat.Builder {
    val inboxStyle = NotificationCompat.InboxStyle()
    inboxStyle.init()
    setStyle(inboxStyle)
    return this
}

fun NotificationCompat.Builder.bigTextStyle(init: NotificationCompat.BigTextStyle.()->Unit): NotificationCompat.Builder {
    val bigTextStyle = NotificationCompat.BigTextStyle()
    bigTextStyle.init()
    setStyle(bigTextStyle)
    return this
}

/**
 * @param user myself
 */
fun NotificationCompat.Builder.messagingStyle(user: Person, init: NotificationCompat.MessagingStyle.()->Unit): NotificationCompat.Builder {
    val bigTextStyle = NotificationCompat.MessagingStyle(user)
    bigTextStyle.init()
    setStyle(bigTextStyle)
    return this
}
fun NotificationCompat.Builder.bigPictureStyle(init: NotificationCompat.BigPictureStyle.()->Unit): NotificationCompat.Builder {
    val bigTextStyle = NotificationCompat.BigPictureStyle()
    bigTextStyle.init()
    setStyle(bigTextStyle)
    return this
}

fun NotificationCompat.Builder.action(icon: Int, title: CharSequence, intent: PendingIntent, init: NotificationCompat.Action.()->Unit): NotificationCompat.Builder {
    val bigTextStyle = NotificationCompat.Action(icon, title, intent)
    bigTextStyle.init()
    addAction(bigTextStyle)
    return this
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Notification.Builder.mediaStyle(icon: Int, title: CharSequence, intent: PendingIntent, init: Notification.MediaStyle.()->Unit): Notification.Builder {
    val media = Notification.MediaStyle()
    media.init()
    style = media
    return this
}
