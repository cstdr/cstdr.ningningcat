package cstdr.ningningcat.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * 
 * @author cstdingran@gmail.com
 */
public class ToastUtil {

	public static void shortToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}

	public static void longToast(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static void toastWithTime(Context context, String text, int time) {
		Toast.makeText(context, text, time).show();
	}

}
