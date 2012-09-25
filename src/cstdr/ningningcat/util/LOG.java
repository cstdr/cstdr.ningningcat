package cstdr.ningningcat.util;

import android.util.Log;

public class LOG {

    public static final boolean DEBUG=true;

    public static void cstdr(Object msg) {
        Log.d("cstdr", "-" + msg);
    }
}
