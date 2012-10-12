package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cstdr.ningningcat.MainActivity;
import cstdr.ningningcat.util.DatabaseUtil;

/**
 * 页面跳转Receiver
 * @author cstdingran@gmail.com
 */
public class GotoReceiver extends BroadcastReceiver {

    public static final String ACTION_GOTO="cstdr.ningningcat.ACTION_GOTO";

    @Override
    public void onReceive(Context context, Intent intent) {
        String url=intent.getStringExtra(DatabaseUtil.COLUMN_URL);
        MainActivity.getInstance().getWebView().loadUrl(url);
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

}
