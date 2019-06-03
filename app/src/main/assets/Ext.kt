/**
 * @author hglf <a href="https://github.com/hotstu">hglf</a>
 * @since 6/3/19
 * @desc
 */
 inline fun String.repeat(t: Int): String {
    if (t < 0) {
        throw IllegalArgumentException("t=${t}")
    }
    val receiver = StringBuilder()
    for (i in 0..t) {
        receiver.append(this)
    }
    return receiver.toString()
}