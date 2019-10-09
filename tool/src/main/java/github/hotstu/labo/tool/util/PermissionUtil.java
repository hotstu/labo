package github.hotstu.labo.tool.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.POWER_SERVICE;

/**
 * @author hglf
 * @desc
 * @since 5/27/19
 */
public class PermissionUtil {

    public static boolean isNotificationEnabled(@NotNull Context ctx) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
        return notificationManagerCompat.areNotificationsEnabled();
    }




    public static boolean isDozeDisabled(@NotNull Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = ctx.getPackageName();
            PowerManager powerManager = (PowerManager) ctx.getSystemService(POWER_SERVICE);
            return powerManager.isIgnoringBatteryOptimizations(packageName);
        } else {
            return true;
        }
    }

    /**
     * 请求doze 忽略电池优化
     * it's the use side responsible for exception handling
     *
     * @param ctx
     */
    public static void requestDozeDisabled(@NotNull Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isDozeDisabled(ctx)) {
            String packageName = ctx.getPackageName();
            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            ctx.startActivity(intent);
        }
    }

    /**
     * 导航到打开gps开关界面
     * it's the use side responsible for exception handling
     *
     * @param ctx
     */
    public static void openLocationSetting(@NotNull Context ctx) {
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ctx.startActivity(myIntent);
    }

    /**
     * 导航到当前app设置界面
     * it's the use side responsible for exception handling
     *
     * @param ctx
     */
    public static void openAppSetting(@NotNull Context ctx) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
        intent.setData(uri);
        ctx.startActivity(intent);

    }
}
