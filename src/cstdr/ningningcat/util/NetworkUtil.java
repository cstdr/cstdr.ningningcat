package cstdr.ningningcat.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * 网络相关工具类
 * 
 * @author cstdingran@gmail.com
 */
public class NetworkUtil {

	/**
	 * 检查网络状态
	 * 
	 * @param context
	 * @return
	 */
	public static boolean checkNetwork(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检查Wifi是否打开
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifiEnabled(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.isWifiEnabled();
	}

	/**
	 * 打开Wifi
	 * 
	 * @param context
	 * @return
	 */
	public static boolean openWifi(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		return wifiManager.setWifiEnabled(true);
	}

}
