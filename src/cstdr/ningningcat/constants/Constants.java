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
    String TITLE_NULL_DEFAULT="宁宁猫的未知网页";

    /** 断网时系统默认标题 **/
    String TITLE_NULL="找不到网页";

    /** “关于”中的微博链接地址 **/
    String WEIBO_URL="http://weibo.com/cstdr";

    /** “关于”中的WIKI链接地址 **/
    String ABOUT_URL="https://github.com/cstdr/cstdr.ningningcat/wiki";

    /** 谷歌搜索前缀 **/
    String GOOGLE_URL="http://www.google.com.hk/m?q=";

    /** APK的文件类型 **/
    String APK_MIMETYPE="application/vnd.android.package-archive";

    /** ZIP的文件类型 **/
    String ZIP_MIMETYPE="application/x-zip-compressed";

    /** 默认首页网站名 **/
    String INDEX_DEFAULT_NAME="hao123导航-上网从这里开始";

    /** 默认首页url **/
    String INDEX_DEFAULT_URL="http://m.hao123.com/";

}
