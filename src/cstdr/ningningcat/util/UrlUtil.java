package cstdr.ningningcat.util;

import cstdr.ningningcat.R;
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

    public static String url2HttpUrl(String url) {
        if(url.startsWith(Constants.HTTP)) {
            return url;
        } else {
            return Constants.HTTP + url;
        }
    }

    public static String httpUrl2url(String httpUrl) {
        if(httpUrl.startsWith(Constants.HTTP)) {
            return httpUrl.substring(Constants.HTTP.length());
        } else {
            return httpUrl;
        }
    }
}
