package cstdr.ningningcat.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import cstdr.ningningcat.CoverActivity;
import cstdr.ningningcat.MainActivity;
import cstdr.ningningcat.R;

/**
 * 手机桌面快捷方式工具类
 * @author cstdingran@gmail.com
 */
public class ShortcutUtil {

    private static final String AUTHORITY="com.android.launcher.settings";

    private static final String CONTENT_URI="content://" + AUTHORITY + "/favorites?notify=true";

    private static final String ACTION_INSTALL_SHORTCUT="com.android.launcher.action.INSTALL_SHORTCUT";

    /**
     * 是否已经有快捷方式
     * @param context
     * @return
     */
    public static boolean hasShortcut(Context context) {
        boolean hasShortcut=false;
        Uri uri=Uri.parse(CONTENT_URI);
        Cursor cursor=
            context.getContentResolver().query(uri, new String[]{"title", "iconResource"}, "title=?",
                new String[]{context.getString(R.string.app_name)}, null);
        if(cursor != null && cursor.getCount() > 0) {
            hasShortcut=true;
        }
        return hasShortcut;
    }

    /**
     * 添加快捷方式
     * @param context
     */
    public static void addShortcut(Context context) {
        Intent shortcut=new Intent(ACTION_INSTALL_SHORTCUT);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getString(R.string.app_name));
        shortcut.putExtra("duplicate", false); // 避免重复添加快捷方式
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));

        Intent shortcutIntent=new Intent(Intent.ACTION_MAIN); // 这样卸载APP的时候，快捷方式也会删除
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER); // 如果堆栈中已经此Activity，则不再new一个新的
        shortcutIntent.setClass(context, CoverActivity.class);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        context.sendBroadcast(shortcut);
    }

    /**
     * 添加网页的快捷方式到桌面
     * @param context
     * @param title
     * @param url
     */
    public static void addShortcutToDesktop(Context context, String title, String url) {
        Intent shortcut=new Intent(ACTION_INSTALL_SHORTCUT);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.icon));

        Intent shortcutIntent=new Intent(Intent.ACTION_MAIN);
        shortcutIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        shortcutIntent.setClass(context, MainActivity.class);
        shortcutIntent.setData(Uri.parse(url));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

        context.sendBroadcast(shortcut);
    }
}
