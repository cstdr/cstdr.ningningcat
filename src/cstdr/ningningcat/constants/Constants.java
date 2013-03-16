package cstdr.ningningcat.constants;

/**
 * 常量类
 * @author cstdingran@gmail.com
 */
public interface Constants {

    /** 1M的缓存 **/
    long CACHE_MAX_SIZE=1024 * 1024 * 1;

    /** HTTP前缀 **/
    String HTTP="http://";

    /** HTTPS前缀 **/
    String HTTPS="https://";

    /** 白天模式，R.style.Theme_Light **/
    int MODE_LIGHT=0; //

    /** 夜间模式，R.style.Theme_Black **/
    int MODE_BLACK=1;

    /** 网页标题为空时的默认标题 **/
    String TITLE_NULL="宁宁猫的未知网页";

    /** “关于”中的微博链接地址 **/
    String WEIBO_URL="http://weibo.com/danniesapphire";

    /** 出错页面的链接地址 **/
    String ERROR_URL="file:///android_asset/html/error.html";

}
