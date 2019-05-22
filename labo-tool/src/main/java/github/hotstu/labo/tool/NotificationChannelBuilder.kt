package github.hotstu.labo.tool

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 5/13/19
 * @desc
 */
class Config {
    val chidren: ArrayList<NotificationChannelGroup> = ArrayList()
}
//NOTE: extension properies are resolved static, so here is a little mem leak, call clean() when every thing is done
val extendPenddingChannels = HashMap<NotificationChannelGroup, ArrayList<NotificationChannel>>()

val NotificationChannelGroup.penddingChannels: ArrayList<NotificationChannel>
    get() = if (extendPenddingChannels[this] == null) {
        val value = ArrayList<NotificationChannel>()
        extendPenddingChannels[this] = value
        value
    } else {
        extendPenddingChannels[this]!!
    }

inline fun config(init: Config.() -> Unit) {
    val config = Config()
    config.apply(init)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Config.applyTo(notificationManager: NotificationManager) {
    notificationManager.createNotificationChannelGroups(chidren)
    notificationManager.createNotificationChannels(chidren.flatMap { it.penddingChannels })
}

fun Config.clean() {
    extendPenddingChannels.clear()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun Config.group(
        groupId: String = "default",
        groupName: String = "default",
        init: NotificationChannelGroup.() -> Unit) {
    val group = NotificationChannelGroup(groupId, groupName)
    chidren.add(group)
    group.apply(init)
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun NotificationChannelGroup.channel(
        id: String = "default",
        name: String = "default",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
        init: NotificationChannel.() -> Unit){
    val channel = NotificationChannel(id, name, importance)
    channel.group = this.id
    this.penddingChannels.add(channel)
    channel.apply(init)
}
