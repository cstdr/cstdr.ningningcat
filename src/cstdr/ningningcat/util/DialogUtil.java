package cstdr.ningningcat.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;
import cstdr.ningningcat.FavoriteActivity.DialogItemClickListener;
import cstdr.ningningcat.FavoriteActivity.DialogRenameListener;
import cstdr.ningningcat.MainActivity;
import cstdr.ningningcat.R;

/**
 * 弹窗工具类
 * @author cstdingran@gmail.com
 */
public class DialogUtil {

    /** 手机设置页面ACTION **/
    private static final String ACTION_SETTINGS="android.settings.SETTINGS";

    /**
     * 显示JS警告框
     * @param context
     * @param message
     * @param result
     */
    public static void showJsAlertDialog(Context context, String message, final JsResult result) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_alert).setMessage(message).setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        }).create().show();

    }

    /**
     * 显示JS确认框
     * @param context
     * @param message
     * @param result
     */
    public static void showJsConfirmDialog(Context context, String message, final JsResult result) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_confirm).setMessage(message).setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        }).setNegativeButton(android.R.string.cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.cancel();
            }
        }).create().show();

    }

    /**
     * 显示普通提示窗 TODO
     * @param context
     * @param message
     */
    public static void showAlertDialog(Context context, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_alert).setMessage(message).setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

    /**
     * 显示无网状态框
     * @param context
     */
    public static void showNoConnectDialog(final Context context) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_no_connect).setMessage(R.string.msg_no_connect)
            .setPositiveButton(R.string.btn_cancel, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.getInstance().setNetworkMode(true); // 避免了设置完返回界面时二次提示
                    Intent intent=new Intent(ACTION_SETTINGS);
                    context.startActivity(intent);
                }
            }).create().show();
    }

    /**
     * 长按收藏夹Item弹出框
     * @param context
     * @param title
     * @param position
     * @param listener
     */
    public static void showFavoriteDialog(Context context, String title, final int position, final DialogItemClickListener listener) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title).setItems(new String[]{"添加快捷方式到桌面", "设为首页", "重命名", "分享", "删除"}, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onClick(position, which);
            }
        }).create().show();
    }

    /**
     * 显示重命名收藏页面的弹出框
     * @param context
     * @param title
     * @param url
     * @param listener
     */
    public static void showRenameDialog(Context context, String title, final String url, final DialogRenameListener listener) {
        final EditText editText=new EditText(context);
        LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(params);
        editText.setText(title);
        editText.setSelectAllOnFocus(true);
        editText.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setSelection(0, editText.getText().length());
                return false;
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(title).setView(editText).setPositiveButton(R.string.btn_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String titleStr=editText.getText().toString();
                if(TextUtils.isEmpty(titleStr)) {
                    titleStr="宁宁猫的未知网页";
                } else if(titleStr.length() > 20) { // 数据库中定义title长度为20
                    titleStr=titleStr.substring(0, 20);
                }
                listener.onClick(titleStr, url);

            }
        }).create().show();
    }
}
