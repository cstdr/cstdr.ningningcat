package cstdr.ningningcat.util;

import android.util.Log;

public class LOG {

    public static final boolean DEBUG=true;

    public static void dev(Object msg) {
        Log.d("dev", "-" + msg);
    }
}
