package cstdr.ningningcat.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.webkit.JsResult;
import cstdr.ningningcat.R;

public class DialogUtil {

    private static final String ACTION_WIRELESS_SETTINGS="android.settings.WIRELESS_SETTINGS";

    public static void showJsAlertDialog(Context context, String message, final JsResult result) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_alert).setMessage(message).setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        }).create().show();

    }

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

    public static void showAlertDialog(Context context, String message) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_alert).setMessage(message).setPositiveButton(android.R.string.ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();
    }

    public static void showNoConnectDialog(final Context context) {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.title_no_connect).setMessage(R.string.msg_no_connect)
            .setPositiveButton(R.string.btn_ok, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent=new Intent(ACTION_WIRELESS_SETTINGS);
                    context.startActivity(intent);
                }
            }).setNegativeButton(R.string.btn_cancel, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).create().show();
    }
}
