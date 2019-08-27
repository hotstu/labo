package github.hotstu.labo.tool.util;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import java.lang.reflect.Method;
import java.util.List;

import static android.content.Context.POWER_SERVICE;

/**
 * @author hglf
 * @desc
 * @since 5/27/19
 */
public class PermissionUtil {

    public static boolean isNotificationEnabled(Context ctx) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(ctx);
        return notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * 检查设备定位开关
     *
     * @param ctx
     * @return
     */
    public static boolean checkLocationConfigEnabled(Context ctx) {
        // 考虑到大部分App可能还未target到API 28，这里使用反射调用API 28开始提供的检查定位开关的方法
        if (Build.VERSION.SDK_INT >= 28) {
            boolean isLocationEnabled = true;
            LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            try {
                Method isLocationEnabledMethod = locationManager.getClass().getMethod("isLocationEnabled", new Class[]{});
                isLocationEnabled = (boolean) isLocationEnabledMethod.invoke(locationManager);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isLocationEnabled;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int locationMode = Settings.Secure.getInt(ctx.getContentResolver(),
                    Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
            boolean isLocationEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;
            return isLocationEnabled;
        } else {
            LocationManager locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            List<String> allProviders = locationManager.getAllProviders();
            if (allProviders == null || allProviders.isEmpty()) {
                return false;
            }
            String usableProviders = Settings.Secure.getString(ctx.getContentResolver(),
                    Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            boolean isLocationEnabled = !usableProviders.isEmpty();
            return isLocationEnabled;
        }
    }


    public static boolean isDozeDisabled(Context ctx) {
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
    public static void requestDozeDisabled(Context ctx) {
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
    public static void openLocationSetting(Context ctx) {
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        ctx.startActivity(myIntent);
    }

    /**
     * 导航到当前app设置界面
     * it's the use side responsible for exception handling
     *
     * @param ctx
     */
    public static void openAppSetting(Context ctx) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
        intent.setData(uri);
        ctx.startActivity(intent);

    }
}
