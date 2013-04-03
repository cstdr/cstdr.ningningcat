package cstdr.ningningcat.util;

import java.io.File;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebViewDatabase;

/**
 * 缓存工具
 * 
 * @author cstdingran@gmail.com
 */
public class CacheUtil {

	/**
	 * 得到当前所有缓存文件大小
	 * 
	 * @param context
	 */
	public static long getCacheSize(Context context) {
		long length = 0;
		File file = context.getCacheDir();
		length = FileUtil.getFileLength(file);
		File extFile = context.getExternalCacheDir();
		if (extFile != null) {
			length = length + FileUtil.getFileLength(extFile);
		}
		return length;
	}

	/**
	 * 清除网页缓存
	 */
	public static void clearCache(Context context) {
		// 清除手机内存上的Cache
		File file = context.getCacheDir();
		FileUtil.deleteFile(file);
		// 清除SD卡上的Cache
		File extFile = context.getExternalCacheDir();
		if (extFile != null) {
			FileUtil.deleteFile(extFile);
		}
		// context.deleteDatabase("webview.db"); // 和清除表单数据效果类似
		// context.deleteDatabase("webviewCache.db");
	}

	/**
	 * 清除表单数据
	 * 
	 * @param context
	 */
	public static void clearFormData(Context context) {
		WebViewDatabase db = WebViewDatabase.getInstance(context);
		if (db.hasFormData()) {
			db.clearFormData();
		}
		if (db.hasHttpAuthUsernamePassword()) {
			db.clearHttpAuthUsernamePassword();
		}
		if (db.hasUsernamePassword()) {
			db.clearUsernamePassword();
		}
	}

	/**
	 * 清理Cookie
	 * 
	 * @param context
	 */
	public static void clearCookie() {
		CookieManager manager = CookieManager.getInstance();
		if (manager.hasCookies()) {
			manager.removeAllCookie();
		}
	}
}