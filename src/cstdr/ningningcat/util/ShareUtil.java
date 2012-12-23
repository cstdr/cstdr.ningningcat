package cstdr.ningningcat.util;

import android.content.Context;
import android.content.Intent;

/**
 * 分享工具
 * @author cstdingran@gmail.com
 */
public class ShareUtil {

    /**
     * 分享网页
     */
    public static void share(Context context, String title, String url) {
        Intent baseIntent=new Intent(Intent.ACTION_SEND);
        baseIntent.setType("text/plain");
        baseIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        String content="我通过@宁宁猫浏览器  分享了网页#" + title + "# " + url + " ";
        baseIntent.putExtra(Intent.EXTRA_TEXT, content);

        Intent shareIntent=Intent.createChooser(baseIntent, "选择你想分享的方式");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }
}
