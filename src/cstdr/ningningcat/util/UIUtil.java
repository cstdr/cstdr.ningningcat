package cstdr.ningningcat.util;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import cstdr.ningningcat.R;

/**
 * UI相关工具
 * @author cstdingran@gmail.com
 */
public class UIUtil {

    /**
     * 隐藏键盘
     * @param v
     */
    public static void hideInputWindow(final View v) {
        InputMethodManager imm=(InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * 显示键盘
     * @param v
     */
    public static void showInputWindow(final View v) {
        InputMethodManager imm=(InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 夜间模式变换
     * @param context
     */
    public static void changeNightMode(Context context, int mode) {
        // UiModeManager ui=(UiModeManager)context.getSystemService(Context.UI_MODE_SERVICE);
        // int modeType=ui.getCurrentModeType();
        // if(modeType == Configuration.UI_MODE_TYPE_CAR) {
        // ui.disableCarMode(0);
        // // ui.setNightMode(UiModeManager.MODE_NIGHT_NO);
        // } else {
        // ui.enableCarMode(0);
        // ui.setNightMode(UiModeManager.MODE_NIGHT_YES);
        // }

        if(LOG.DEBUG) {
            LOG.cstdr("changeNightMode---mode : " + mode);
        }
        Theme theme=context.getTheme();
        switch(mode) {
            case 0:
                // context.setTheme(R.style.Theme_Light);
                // context.setTheme(R.style.MainTheme);
                theme.applyStyle(R.style.MainTheme, true);
                break;
            case 1:
                // context.setTheme(R.style.Theme_Black);
                // context.setTheme(R.style.NightMode);
                theme.applyStyle(R.style.NightMode, true);
                break;
        }
    }
}
