package cstdr.ningningcat.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 下载中点击通知receiver
 * 
 * @author cstdingran@gmail.com
 */
public class DownloadNotificationClickReceiver extends BroadcastReceiver {

	public static final String ACTION_NOTIFICATION_CLICK = DownloadManager.ACTION_NOTIFICATION_CLICKED;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_NOTIFICATION_CLICK)) {
			Intent downloadsIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
			context.startActivity(downloadsIntent);
		}
	}

}
