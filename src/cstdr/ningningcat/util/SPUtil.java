package cstdr.ningningcat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cstdr.ningningcat.R;

/**
 * SharedPreferences工具类
 * 
 * @author cstdingran@gmail.com
 */
public class SPUtil {

	private static final String TAG = "SPUtil";

	private static SPUtil mSPUtil;

	private SharedPreferences mSP;

	private Editor mEditor;

	private SPUtil(Context context) {
		if (mSP == null || mEditor == null) {
			mSP = context.getSharedPreferences(context.getString(R.string.sp_main), Context.MODE_PRIVATE);
			mEditor = mSP.edit();
		}
	}

	/**
	 * 得到SPUtil实例
	 * 
	 * @param context
	 * @return
	 */
	public static SPUtil getInstance(Context context) {
		if (mSPUtil == null) {
			mSPUtil = new SPUtil(context);
		}
		return mSPUtil;
	}

	/**
	 * 往SP中写入字符串数组
	 * 
	 * @param keys
	 * @param values
	 */
	public void commitStrArrayToSP(String[] keys, String[] values) {
		if (LOG.DEBUG) {
			if (keys.length == values.length) {
				LOG.cstdr(TAG, "keys.length and values.length is same:" + keys.length);
			} else {
				return;
			}
		}
		for (int i = 0; i < keys.length; i++) {
			mEditor.putString(keys[i], values[i]);
		}
		// mEditor.commit();
		mEditor.apply(); // 异步提交
	}

	/**
	 * 往SP中写入整形数组
	 * 
	 * @param keys
	 * @param values
	 */
	public void commitIntArrayToSP(String[] keys, int[] values) {
		for (int i = 0; i < keys.length; i++) {
			mEditor.putInt(keys[i], values[i]);
		}
		mEditor.apply(); // 异步提交
	}

	/**
	 * 得到一个Int值
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public int getInt(String key, int defValue) {
		return mSP.getInt(key, defValue);
	}

	/**
	 * 得到一个String值
	 * 
	 * @param key
	 * @param defValue
	 * @return
	 */
	public String getString(String key, String defValue) {
		return mSP.getString(key, defValue);
	}
}
