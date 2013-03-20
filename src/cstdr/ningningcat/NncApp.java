package cstdr.ningningcat;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;

import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ShortcutUtil;
import cstdr.ningningcat.util.ToastUtil;

/**
 * 宁宁猫全局应用参数
 * @author cstdingran@gmail.com
 */
public class NncApp extends Application {

    private static final String TAG="NncApp";

    private static NncApp mInstance;

    private FavoriteActivity mFavoriteActivity;

    private Handler handler;

    private SharedPreferences mSp=null;

    private boolean isNetworkMode=false;

    private static String mCurrentTitle=null;

    private static String mCurrentUrl=null;

    @Override
    public void onCreate() {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onCreate============");
        }
        // 友盟在线更新配置
        MobclickAgent.updateOnlineConfig(this);

        mInstance=this;
        handler=new Handler();
        mFavoriteActivity=new FavoriteActivity();
        initSharedPreferences();
        initUMeng();
    }

    /**
     * 初始化SharedPreferences
     */
    private void initSharedPreferences() {
        mSp=SPUtil.getSP(this, getString(R.string.sp_main));
        if(mSp.getString(getString(R.string.spkey_first_launch_time), null) != null) {
            SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_last_launch_time)},
                new String[]{String.valueOf(System.currentTimeMillis())});
        } else {
            // if(!ShortcutUtil.hasShortcut(mContext)) { // 测试发现某些机型报错
            ShortcutUtil.addShortcut(this);
            // }
            ToastUtil.longToast(this, getString(R.string.msg_first_launch));
            SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_first_launch_time)},
                new String[]{String.valueOf(System.currentTimeMillis())});
        }

        mCurrentUrl=mSp.getString(getString(R.string.spkey_index), getString(R.string.index)); // 获取首页
    }

    /**
     * 初始化友盟组件
     */
    private void initUMeng() {
        FeedBackListener listener=new FeedBackListener() {

            @Override
            public void onSubmitFB(Activity activity) {
                EditText phoneText=(EditText)activity.findViewById(R.id.feedback_phone);
                EditText qqText=(EditText)activity.findViewById(R.id.feedback_qq);
                EditText nameText=(EditText)activity.findViewById(R.id.feedback_name);
                EditText emailText=(EditText)activity.findViewById(R.id.feedback_email);
                Map<String, String> contactMap=new HashMap<String, String>();
                contactMap.put("phone", phoneText.getText().toString());
                contactMap.put("qq", qqText.getText().toString());
                UMFeedbackService.setContactMap(contactMap);
                Map<String, String> remarkMap=new HashMap<String, String>();
                remarkMap.put("name", nameText.getText().toString());
                remarkMap.put("email", emailText.getText().toString());
                UMFeedbackService.setRemarkMap(remarkMap);
            }

            @Override
            public void onResetFB(Activity activity, Map<String, String> contactMap, Map<String, String> remarkMap) {
                EditText phoneText=(EditText)activity.findViewById(R.id.feedback_phone);
                EditText qqText=(EditText)activity.findViewById(R.id.feedback_qq);
                EditText nameText=(EditText)activity.findViewById(R.id.feedback_name);
                EditText emailText=(EditText)activity.findViewById(R.id.feedback_email);
                if(remarkMap != null) {
                    nameText.setText(remarkMap.get("name"));
                    emailText.setText(remarkMap.get("email"));
                }
                if(contactMap != null) {
                    phoneText.setText(contactMap.get("phone"));
                    qqText.setText(contactMap.get("qq"));
                }
            }
        };
        UMFeedbackService.setFeedBackListener(listener);
    }

    // //////////////////////////////////////////////////////////////////////////////////
    // get and set
    // //////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取实例
     * @return
     */
    public static NncApp getInstance() {
        if(mInstance == null) {
            mInstance=new NncApp();
        }
        return mInstance;
    }

    public FavoriteActivity getFavoriteActivity() {
        return mFavoriteActivity;
    }

    public Handler getHandler() {
        if(handler == null) {
            handler=new Handler();
        }
        return handler;
    }

    public SharedPreferences getSp() {
        if(mSp == null) {
            mSp=SPUtil.getSP(this, getString(R.string.sp_main));
        }
        return mSp;
    }

    /**
     * 是否网络模式
     * @return
     */
    public boolean isNetworkMode() {
        return isNetworkMode;
    }

    public void setNetworkMode(boolean isNetworkMode) {
        this.isNetworkMode=isNetworkMode;
    }

    public static String getCurrentTitle() {
        return mCurrentTitle;
    }

    public static void setCurrentTitle(String currentTitle) {
        mCurrentTitle=currentTitle;
    }

    public static String getCurrentUrl() {
        return mCurrentUrl;
    }

    public static void setCurrentUrl(String currentUrl) {
        mCurrentUrl=currentUrl;
    }
}
