package cstdr.ningningcat.util;

import cstdr.ningningcat.constants.Constants;

/**
 * URL工具类
 * @author cstdingran@gmail.com
 */
public class UrlUtil {

    /**
     * 检查输入框的url
     * @param editUrl
     * @return
     */
    public static String checkEditUrl(String editUrl) {
        if(editUrl != null && editUrl.length() > 0) {
            return url2HttpUrl(editUrl);
        }
        return null;
    }

    /**
     * 将url添加协议http/https
     * @param url
     * @return
     */
    public static String url2HttpUrl(String url) {
        if(url.startsWith(Constants.HTTP) || url.startsWith(Constants.HTTPS)) {
            return url;
        } else {
            return Constants.HTTP + url;
        }
    }

    /**
     * 将httpUrl的协议去掉
     * @param httpUrl
     * @return
     */
    public static String httpUrl2url(String httpUrl) {
        if(httpUrl.startsWith(Constants.HTTP)) {
            return httpUrl.substring(Constants.HTTP.length());
        } else if(httpUrl.startsWith(Constants.HTTPS)) {
            return httpUrl.substring(Constants.HTTPS.length());
        } else {
            return httpUrl;
        }
    }
}
