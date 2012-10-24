package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cstdr.ningningcat.MainActivity;

/**
 * 页面跳转Receiver
 * @author cstdingran@gmail.com
 */
public class GotoReceiver extends BroadcastReceiver {

    public static final String ACTION_GOTO="cstdr.ningningcat.ACTION_GOTO";

    public static final String ACTION_VIEW="android.intent.action.VIEW";

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

}
