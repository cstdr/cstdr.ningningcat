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
 * @author cstdingran@gmail.com
 */
public class DownloadUtil {

    private static final String TAG="DownloadUtil";

    private static DownloadManager mDownloadManager;

    /**
     * 开始下载
     * @param url
     * @param userAgent
     * @param contentDisposition
     * @param mimetype
     * @param contentLength
     */
    public static void startDownload(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "url = " + url);
            LOG.cstdr(TAG, "userAgent = " + userAgent);
            LOG.cstdr(TAG, "contentDisposition = " + contentDisposition);
            LOG.cstdr(TAG, "mimetype = " + mimetype);
            LOG.cstdr(TAG, "contentLength = " + contentLength);
        }
        if(mDownloadManager == null) {
            mDownloadManager=(DownloadManager)NncApp.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        }
        Uri uri=Uri.parse(url);
        Request request=new Request(uri);
        request.addRequestHeader("User-Agent", userAgent);
        String fileName;
        if(TextUtils.isEmpty(contentDisposition)) {
            fileName=uri.getLastPathSegment();
        } else {
            fileName=contentDisposition.replaceFirst("attachment;filename=", "");
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // request.setAllowedNetworkTypes(Request.NETWORK_WIFI); // 只允许WIFI下
        // request.setAllowedOverRoaming(false);
        request.setShowRunningNotification(true);
        request.setTitle(fileName);
        request.setDescription(UIUtil.changeSize(contentLength) + "-" + uri.getHost());
        request.setVisibleInDownloadsUi(true); // 在下载管理中可见
        mDownloadManager.enqueue(request);
    }

    /**
     * 得到下载完的文件名 TODO
     * @return
     */
    public static String getDownloadedFileName() {
        String fileName="";
        if(mDownloadManager == null) {
            mDownloadManager=(DownloadManager)NncApp.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        }
        Query query=new Query();
        query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
        Cursor c=mDownloadManager.query(query);
        if(c.moveToFirst()) {
            fileName=c.getString(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        return fileName;
    }

    /**
     * 打开文件
     * @param filePath
     * @param mimeType
     */
    public static void openFile(Context context, String fileUri, String mimeType) {
        Intent intent=new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(fileUri), mimeType);
        context.startActivity(intent);
    }

    public static DownloadManager getDownloadManager() {
        if(mDownloadManager == null) {
            mDownloadManager=(DownloadManager)NncApp.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
        }
        return mDownloadManager;
    }

}
