package cstdr.ningningcat.data;

import cstdr.ningningcat.constants.Constants;

/**
 * 一个网页收藏
 * 
 * @author cstdingran@gmail.com
 */
public class Favorite {

	private String title;

	private String url;

	public Favorite(String title, String url) {
		if (title == null) {
			title = Constants.TITLE_NULL_DEFAULT;
		}
		this.title = title;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
