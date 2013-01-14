package cstdr.ningningcat.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import cstdr.ningningcat.R;
import cstdr.ningningcat.data.Favorite;

/**
 * 分享工具
 * @author cstdingran@gmail.com
 */
public class ShareUtil {

    /**
     * 分享网页
     */
    public static void shareFavorite(Context context, String title, String url) {
        Intent baseIntent=new Intent(Intent.ACTION_SEND);
        baseIntent.setType("text/plain");
        baseIntent.putExtra(Intent.EXTRA_SUBJECT, "分享网页");
        String content="我通过@宁宁猫浏览器  分享了网页#" + title + "# " + url + " ";
        baseIntent.putExtra(Intent.EXTRA_TEXT, content);
        Intent shareIntent=Intent.createChooser(baseIntent, "选择你想分享的方式");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    /**
     * 分享收藏夹
     * @param context
     * @param list
     */
    public static void shareFavoriteList(Context context, List<Favorite> list) {
        if(list.isEmpty()) {
            ToastUtil.shortToast(context, context.getString(R.string.msg_no_favorite));
            return;
        }
        Intent baseIntent=new Intent(Intent.ACTION_SEND);
        baseIntent.setType("text/plain");
        baseIntent.putExtra(Intent.EXTRA_SUBJECT, "分享收藏夹");
        StringBuffer content=new StringBuffer();
        content.append("我通过@宁宁猫浏览器  分享了收藏夹：\n");
        for(Favorite favorite: list) {
            content.append("#").append(favorite.getTitle()).append("# ").append(favorite.getUrl()).append(" \n");
        }
        baseIntent.putExtra(Intent.EXTRA_TEXT, content.toString());
        Intent shareIntent=Intent.createChooser(baseIntent, "选择你想分享的方式");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }
}
