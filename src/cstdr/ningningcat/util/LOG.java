package cstdr.ningningcat.util;

import android.util.Log;

/**
 * 日志工具类
 * @author cstdingran@gmail.com
 */
public class LOG {

    public static final boolean DEBUG=true;

    public static void cstdr(String tag, Object msg) {
        Log.d(tag, msg + "");
    }

    public static void exception(Exception e) {
        Log.d("exception", "-" + e.getMessage());
    }
}
