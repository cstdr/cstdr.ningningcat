package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import cstdr.ningningcat.MainActivity;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.ToastUtil;

public class ConnectivityReceiver extends BroadcastReceiver {

    private static final String ACTION_CONNECT_CHANGE="android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_CONNECT_CHANGE)) {
            if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                DialogUtil.showNoConnectDialog(MainActivity.getInstance()); // 这里必须传Activity，若传Context则报错
            } else {
                ToastUtil.shortToast(context, "可以自由自在的上网啦~");
            }
        }
    }

}
