package cstdr.ningningcat.data;

/**
 * 一个网页收藏
 * @author cstdingran@gmail.com
 */
public class Favorite {

    private String title;

    private String url;

    public Favorite(String title, String url) {
        if(title == null) {
            title="没名儿网页"; // TODO
        }
        this.title=title;
        this.url=url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title=title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url=url;
    }

}
