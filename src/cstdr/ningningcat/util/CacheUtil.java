package cstdr.ningningcat.util;

import java.io.File;

import android.content.Context;
import android.webkit.CacheManager;

/**
 * 缓存工具
 * @author cstdingran@gmail.com
 */
public class CacheUtil {

    /**
     * 清楚网页缓存 deprecated TODO
     */
    public static void clearCache(Context context) {
        File file=CacheManager.getCacheFileBaseDir();
        for(File item: file.listFiles()) {
            item.delete();
        }
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
    }
}
