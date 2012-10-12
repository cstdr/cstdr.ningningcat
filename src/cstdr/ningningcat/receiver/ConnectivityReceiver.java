package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import cstdr.ningningcat.MainActivity;
import cstdr.ningningcat.util.DialogUtil;

/**
 * 网络连接相关Receiver
 * @author cstdingran@gmail.com
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    public static final String ACTION_CONNECT_CHANGE="android.net.conn.CONNECTIVITY_CHANGE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_CONNECT_CHANGE)) {
            if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                if(!MainActivity.getInstance().isNetworkMode()) { // 这里有点复杂的判断，漏掉了在运行中网络改变的情况
                    DialogUtil.showNoConnectDialog(MainActivity.getInstance()); // 这里必须传Activity，若传Context则报错
                } else {
                    MainActivity.getInstance().setNetworkMode(false);
                }
            } else {
                MainActivity.getInstance().setNetworkMode(true);
                // ToastUtil.shortToast(context, "可以自由自在的上网啦~");
            }
        }
    }

}
