package cstdr.ningningcat.util;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import cstdr.ningningcat.NncApp;

/**
 * 下载管理
 * 
 * @author cstdingran@gmail.com
 */
public class DownloadUtil {

	private static final String TAG = "DownloadUtil";

	private static DownloadUtil mDownloadUtil;

	private DownloadManager mDownloadManager;

	private DownloadUtil() {
		if (mDownloadManager == null) {
			mDownloadManager = (DownloadManager) NncApp.getInstance()
					.getSystemService(Context.DOWNLOAD_SERVICE);
		}
	}

	public static DownloadUtil getInstance() {
		if (mDownloadUtil == null) {
			mDownloadUtil = new DownloadUtil();
		}
		return mDownloadUtil;
	}

	public DownloadManager getDownloadManager() {
		return mDownloadManager;
	}

	/**
	 * 开始下载
	 * 
	 * @param url
	 * @param userAgent
	 * @param contentDisposition
	 * @param mimeType
	 * @param contentLength
	 */
	public void startDownload(String url, String userAgent,
			String contentDisposition, String mimeType, long contentLength) {
		if (LOG.DEBUG) {
			LOG.cstdr(TAG, "url = " + url);
			LOG.cstdr(TAG, "userAgent = " + userAgent);
			LOG.cstdr(TAG, "contentDisposition = " + contentDisposition);
			LOG.cstdr(TAG, "mimetype = " + mimeType);
			LOG.cstdr(TAG, "contentLength = " + contentLength);
		}
		Uri uri = Uri.parse(url);
		Request request = new Request(uri);
		request.addRequestHeader("User-Agent", userAgent);
		request.setMimeType(mimeType);
		String fileName;
		if (TextUtils.isEmpty(contentDisposition)) {
			fileName = uri.getLastPathSegment();
		} else {
			fileName = contentDisposition.replaceFirst("attachment; filename=",
					"");
			fileName = fileName.replace("\"", "");
		}
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, fileName);
		// request.setAllowedNetworkTypes(Request.NETWORK_WIFI); // 只允许WIFI下
		// request.setAllowedOverRoaming(false);
		request.setTitle(fileName);
		String description = "";
		if (contentLength > 0) { // 同一个APK文件，在不同的WIFI环境下载，有时获取不到文件长度，返回值为-1
			description = UIUtil.changeSize(contentLength) + "-"
					+ uri.getHost();
		} else {
			description = uri.getHost();
		}
		request.setDescription(description);
		request.setVisibleInDownloadsUi(true); // 在下载管理中可见
		mDownloadManager.enqueue(request);
	}

	/**
	 * 得到下载完的文件名 TODO
	 * 
	 * @return
	 */
	public String getDownloadedFileName() {
		String fileName = "";
		Query query = new Query();
		query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
		Cursor cursor = null;
		try {
			cursor = mDownloadManager.query(query);
			if (cursor.moveToFirst()) {
				fileName = cursor.getString(cursor
						.getColumnIndex(DownloadManager.COLUMN_TITLE));
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return fileName;
	}

	/**
	 * 打开文件
	 * 
	 * @param filePath
	 * @param mimeType
	 */
	public void openFile(Context context, String fileUri, String mimeType) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(fileUri), mimeType);
		context.startActivity(intent);
	}

}
