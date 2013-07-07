package cstdr.ningningcat.util;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.JsResult;
import android.widget.EditText;
import android.widget.RelativeLayout.LayoutParams;

import com.umeng.fb.UMFeedbackService;

import cstdr.ningningcat.NncApp;
import cstdr.ningningcat.R;
import cstdr.ningningcat.constants.Constants;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.ui.FavoriteActivity;
import cstdr.ningningcat.ui.FavoriteActivity.DialogItemClickListener;
import cstdr.ningningcat.ui.FavoriteActivity.DialogRenameListener;
import cstdr.ningningcat.ui.WebActivity.ExitListener;
import cstdr.ningningcat.ui.adapter.FavoriteAdapter;

/**
 * 弹窗工具类
 * 
 * @author cstdingran@gmail.com
 */
public class DialogUtil {

	private static Dialog jsAlertDialog; // 页面JS警告框

	private static Dialog jsConfirmDialog; // 页面JS确认框

	private static Dialog settingOpenDialog; // 无网状态框

	private static Dialog favoriteDialog; // 长按收藏夹Item弹出框

	private static Dialog renameDialog; // 显示重命名收藏页面的弹出框

	private static Dialog deleteFavoriteListDialog; // 显示清空收藏夹的弹出框

	private static Dialog deleteFavoriteDialog; // 显示删除收藏的弹出框

	private static Dialog wifiOpenDialog; // 显示Wifi打开的弹出框

	private static Dialog feedbackDialog; // 显示是否反馈的弹出框

	/** 手机设置页面ACTION **/
	private static final String ACTION_SETTINGS = "android.settings.SETTINGS";

	/**
	 * 显示JS警告框
	 * 
	 * @param context
	 * @param message
	 * @param result
	 */
	public static void showJsAlertDialog(Context context, String message,
			final JsResult result) {
		if (jsAlertDialog != null && jsAlertDialog.isShowing()) {
			return;
		}
		jsAlertDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.title_alert).setMessage(message)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				}).create();
		jsAlertDialog.show();
	}

	/**
	 * 显示JS确认框
	 * 
	 * @param context
	 * @param message
	 * @param result
	 */
	public static void showJsConfirmDialog(Context context, String message,
			final JsResult result) {
		if (jsConfirmDialog != null && jsConfirmDialog.isShowing()) {
			return;
		}
		jsConfirmDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.title_confirm)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						result.confirm();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								result.cancel();
							}
						}).create();
		jsConfirmDialog.show();
	}

	/**
	 * 显示无网状态框
	 * 
	 * @param context
	 */
	public static void showSettingOpenDialog(final Context context) {
		if (settingOpenDialog != null && settingOpenDialog.isShowing()) {
			return;
		}
		settingOpenDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.title_no_connect)
				.setMessage(R.string.msg_no_connect_setting)
				.setPositiveButton(R.string.btn_noconnect,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setNegativeButton(R.string.btn_settings,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent(ACTION_SETTINGS);
								context.startActivity(intent);
							}
						}).create();
		settingOpenDialog.show();
	}

	/**
	 * 长按收藏夹Item弹出框
	 * 
	 * @param context
	 * @param title
	 * @param position
	 * @param listener
	 */
	public static void showFavoriteDialog(Context context, String title,
			final int position, final DialogItemClickListener listener) {
		if (favoriteDialog != null && favoriteDialog.isShowing()) {
			return;
		}
		favoriteDialog = new AlertDialog.Builder(context)
				.setTitle(title)
				.setItems(new String[]{"添加快捷方式到桌面", "设为首页", "重命名", "分享", "删除"},
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								listener.onClick(position, which);
							}
						}).create();
		favoriteDialog.show();
	}

	/**
	 * 显示重命名收藏页面的弹出框
	 * 
	 * @param context
	 * @param title
	 * @param url
	 * @param listener
	 */
	public static void showRenameDialog(Context context, String title,
			final String url, final DialogRenameListener listener) {
		if (renameDialog != null && renameDialog.isShowing()) {
			return;
		}
		final EditText editText = new EditText(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		editText.setLayoutParams(params);
		editText.setText(title);
		editText.setMaxHeight(1);
		renameDialog = new AlertDialog.Builder(context).setTitle(title)
				.setView(editText)
				.setPositiveButton(R.string.btn_cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.setNegativeButton(R.string.btn_rename, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						String titleStr = editText.getText().toString();
						if (TextUtils.isEmpty(titleStr)) {
							titleStr = Constants.TITLE_NULL_DEFAULT;
						} else if (titleStr.length() > 20) { // 数据库中定义title长度为20
							titleStr = titleStr.substring(0, 20);
						}
						listener.onClick(titleStr, url);
					}
				}).create();
		renameDialog.show();
	}

	/**
	 * 清空收藏夹
	 * 
	 * @param context
	 * @param adapter
	 */
	public static void deleteFavoriteList(final Context context,
			final FavoriteAdapter adapter) {
		if (deleteFavoriteListDialog != null
				&& deleteFavoriteListDialog.isShowing()) {
			return;
		}
		if (NncApp.getInstance().getFavoriteList().isEmpty()) {
			ToastUtil.shortToast(context,
					context.getString(R.string.msg_no_favorite));
			return;
		}
		deleteFavoriteListDialog = new AlertDialog.Builder(context)
				.setMessage(R.string.msg_list_delete_confirm)
				.setPositiveButton(R.string.btn_cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton(R.string.btn_delete_favorite_list,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								int id = NncApp
										.getInstance()
										.getWritableDB()
										.delete(DatabaseUtil.mTableName, null,
												null);
								if (id > 0) {
									ArrayList<Favorite> list = NncApp
											.getInstance().getFavoriteList();
									list = FavoriteActivity
											.getFavoriteList(list);
									adapter.notifyDataSetChanged();
									ToastUtil.shortToast(
											context,
											context.getString(R.string.msg_list_delete));
								} else {
									ToastUtil.shortToast(
											context,
											context.getString(R.string.msg_database_fail));
								}
							}
						}).create();
		deleteFavoriteListDialog.show();
	}

	/**
	 * 删除收藏完后列表刷新
	 * 
	 * @param context
	 * @param adapter
	 * @param position
	 */
	public static void deleteFavorite(final Context context,
			final FavoriteAdapter adapter, final int position) {
		if (deleteFavoriteDialog != null && deleteFavoriteDialog.isShowing()) {
			return;
		}
		deleteFavoriteDialog = new AlertDialog.Builder(context)
				.setMessage(R.string.msg_web_delete_confirm)
				.setPositiveButton(R.string.btn_cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				})
				.setNegativeButton(R.string.btn_delete_favorite,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String url = NncApp.getInstance()
										.getFavoriteList().get(position)
										.getUrl();
								FavoriteActivity.deleteFavorite(context, url,
										adapter);
							}
						}).create();
		deleteFavoriteDialog.show();
	}

	/**
	 * Wifi打开对话框，打开后会直接连接已配置Wifi
	 * 
	 * @param context
	 */
	public static void showWifiOpenDialog(final Context context) {
		if (wifiOpenDialog != null && wifiOpenDialog.isShowing()) {
			return;
		}
		wifiOpenDialog = new AlertDialog.Builder(context)
				.setTitle(R.string.title_no_connect)
				.setMessage(R.string.msg_no_connect_wifiopen)
				.setPositiveButton(R.string.btn_noconnect,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setNegativeButton(R.string.btn_wifiopen,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								if (NetworkUtil.openWifi(context)) {
									ToastUtil.longToast(context, context
											.getString(R.string.msg_wifi_open));
								}
							}
						}).create();
		wifiOpenDialog.show();
	}

	/**
	 * 无网状态下应该显示的对话框
	 * 
	 * @param context
	 */
	public static void showNoConnectDialog(Context context) {
		if (NetworkUtil.isWifiEnabled(context)) { // 判断是Wifi未打开还是已打开但是没有连接Wifi
			showSettingOpenDialog(context);
		} else {
			showWifiOpenDialog(context);
		}
	}

	/**
	 * 第一次退出APP时显示是否反馈对话框
	 * 
	 * @param context
	 */
	public static void showFeedbackDialog(final Context context,
			final ExitListener listener) {
		if (feedbackDialog != null && feedbackDialog.isShowing()) {
			return;
		}
		feedbackDialog = new AlertDialog.Builder(context)
				.setMessage(R.string.msg_first_launch_feedback)
				.setPositiveButton(R.string.btn_exit, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						listener.doExit();
					}
				})
				.setNegativeButton(R.string.btn_feedback,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								UMFeedbackService.openUmengFeedbackSDK(context);
							}
						}).create();
		feedbackDialog.show();
	}
}
