package github.hotstu.labo.tool.util;

/**
 * @author hglf
 * @since 2018/10/8
 */
public class ObjectUtil {
    public static boolean equals(Object a, Object b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }
}
