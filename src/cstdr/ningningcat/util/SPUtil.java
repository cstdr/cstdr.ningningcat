package cstdr.ningningcat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences工具类
 * @author cstdingran@gmail.com
 */
public class SPUtil {

    private static final String TAG="SPUtil";

    /**
     * 得到SharedPreferences
     * @param context
     * @param name
     * @return
     */
    public static SharedPreferences getSP(Context context, String name) {
        if(context == null || name == null) {
            return null;
        }
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    /**
     * 往SP中写入字符串数组
     * @param sp
     * @param keys
     * @param values
     */
    public static void commitStrArrayToSP(SharedPreferences sp, String[] keys, String[] values) {
        if(LOG.DEBUG) {
            if(keys.length == values.length) {
                LOG.cstdr(TAG, "keys.length and values.length is same:" + keys.length);
            } else {
                return;
            }
        }
        Editor editor=sp.edit();
        for(int i=0; i < keys.length; i++) {
            editor.putString(keys[i], values[i]);
        }
        editor.commit();
    }

    /**
     * 往SP中写入整形数组
     * @param sp
     * @param keys
     * @param values
     */
    public static void commitIntArrayToSP(SharedPreferences sp, String[] keys, int[] values) {
        Editor editor=sp.edit();
        for(int i=0; i < keys.length; i++) {
            editor.putInt(keys[i], values[i]);
        }
        editor.commit();
    }
}
