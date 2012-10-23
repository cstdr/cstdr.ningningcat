package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cstdr.ningningcat.MainActivity;

/**
 * 接收系统上网广播Receiver
 * @author cstdingran@gmail.com
 */
public class BrowseReceiver extends BroadcastReceiver {

    public static final String ACTION_BROWSE="android.intent.action.VIEW";

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

}
