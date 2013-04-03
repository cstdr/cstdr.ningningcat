package cstdr.ningningcat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import cstdr.ningningcat.WebActivity;
import cstdr.ningningcat.NncApp;
import cstdr.ningningcat.util.DialogUtil;

/**
 * 网络连接相关Receiver
 * 
 * @author cstdingran@gmail.com
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	public static final String ACTION_CONNECT_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_CONNECT_CHANGE)) {
			NncApp nncApp = NncApp.getInstance();
			if (intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
				if (nncApp.isNetworkMode()) { // 这里有点复杂的判断，漏掉了在运行中网络改变的情况
					nncApp.setNetworkMode(false);
					DialogUtil.showNoConnectDialog(context); // 这里必须传Activity，若传Context则报错
				}
			} else {
				if (!nncApp.isNetworkMode()) { // 重新加载当前网址，用于网络重新连通后
					nncApp.setNetworkMode(true);
					Intent reloadIntent = new Intent(context, WebActivity.class);
					context.startActivity(reloadIntent);
				}
			}
		}
	}

}
