package cstdr.ningningcat.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
}
