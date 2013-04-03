package cstdr.ningningcat.receiver;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import cstdr.ningningcat.R;
import cstdr.ningningcat.util.DownloadUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.ToastUtil;

/**
 * 下载完成Receiver
 * 
 * @author cstdingran@gmail.com
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = "DownloadCompleteReceiver";

	public static final String ACTION_DOWNLOAD_COMPLETE = DownloadManager.ACTION_DOWNLOAD_COMPLETE;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION_DOWNLOAD_COMPLETE)) {
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Query query = new Query();
			query.setFilterById(downloadId);
			Cursor c = DownloadUtil.getDownloadManager().query(query);
			if (c.moveToFirst()) {
				if (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
					ToastUtil.shortToast(context,
							context.getString(R.string.msg_download_complete));
					String fileUri = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
					// FIXME
					// 这里获取的竟然和onDownloadStart()方法中得到的mimeType不同，例如糗事百科APK
					// String mimeType=Constants.APK_MIMETYPE;
					String mimeType = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
					if (LOG.DEBUG) {
						LOG.cstdr(TAG, "fileUri = " + fileUri);
						LOG.cstdr(TAG, "mimeType = " + mimeType);
					}
					DownloadUtil.openFile(context, fileUri, mimeType);
				}
			}
		}
	}

}
