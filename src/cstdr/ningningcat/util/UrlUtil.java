package cstdr.ningningcat.util;

import cstdr.ningningcat.constants.Constants;

public class UrlUtil {

	/**
	 * 检查输入框的url
	 * 
	 * @param editUrl
	 * @return
	 */
	public static String checkEditUrl(String editUrl) {
		if (editUrl != null && editUrl.length() > 0) {
			if (editUrl.startsWith(Constants.HTTP)) {
				return editUrl;
			} else {
				return Constants.HTTP + editUrl;
			}
		}
		return null;
	}
}
