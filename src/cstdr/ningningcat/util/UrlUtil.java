package cstdr.ningningcat.util;

import android.webkit.URLUtil;
import cstdr.ningningcat.constants.Constants;

/**
 * URL工具类
 * 
 * @author cstdingran@gmail.com
 */
public class UrlUtil {

	/**
	 * 检查输入框的url
	 * 
	 * @param editUrl
	 * @return
	 */
	public static String checkEditUrl(String editUrl) {
		if (editUrl != null && editUrl.length() > 0) {
			if (isWebsite(editUrl)) {
				return url2HttpUrl(editUrl);
			} else { // 不是网址则默认百度搜索，因为有时谷歌不稳定
				// return Constants.GOOGLE_URL + editUrl; // 谷歌
				return Constants.BAIDU_URL + editUrl; // 百度
			}
		}
		return null;
	}

	/**
	 * 将url添加协议http/https
	 * 
	 * @param url
	 * @return
	 */
	public static String url2HttpUrl(String url) {
		if (URLUtil.isNetworkUrl(url)) {
			return url;
		} else { // 只返回http协议的url
			return Constants.HTTP + url;
		}
	}

	/**
	 * 将httpUrl的协议去掉
	 * 
	 * @param httpUrl
	 * @return
	 */
	public static String httpUrl2Url(String httpUrl) {
		if (URLUtil.isHttpUrl(httpUrl)) {
			return httpUrl.substring(Constants.HTTP.length());
		} else if (URLUtil.isHttpsUrl(httpUrl)) {
			return httpUrl.substring(Constants.HTTPS.length());
		} else {
			return httpUrl;
		}
	}

	/**
	 * 判断是否是网址
	 * 
	 * @return
	 */
	private static boolean isWebsite(String url) {
		if (url.contains(".com") || url.contains(".cn") || url.contains(".net") || url.contains(".org")
				|| url.contains(".edu") || url.contains(".gov")) {
			return true;
		}
		return false;
	}

}
