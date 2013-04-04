package cstdr.ningningcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;

import cstdr.ningningcat.constants.Constants;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.util.CacheUtil;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ShortcutUtil;
import cstdr.ningningcat.util.ToastUtil;

/**
 * 宁宁猫全局应用参数
 * 
 * @author cstdingran@gmail.com
 */
public class NncApp extends Application {

	private static final String TAG = "NncApp";

	private static NncApp mInstance;

	private Handler handler;

	private SharedPreferences mSp = null;

	private static SQLiteOpenHelper mDBHelper;

	private static ArrayList<Favorite> mFavoriteList;

	private boolean isFirstLaunch = true;

	private boolean isNetworkMode = true;

	private static String mCurrentTitle = null;

	private static String mCurrentUrl = null;

	/** 手机屏幕宽的比例，以1280x720为准 **/
	private static float UI_SCALE_X;

	@Override
	public void onCreate() {
		if (LOG.DEBUG) {
			LOG.cstdr(TAG, "============onCreate============");
		}
		// 友盟在线更新配置
		MobclickAgent.updateOnlineConfig(this);

		mInstance = this;
		handler = new Handler();
		mDBHelper = new DatabaseUtil(mInstance, DatabaseUtil.mDatabaseName,
				null, 1);
		mFavoriteList = new ArrayList<Favorite>();
		mFavoriteList = FavoriteActivity.getFavoriteList(mFavoriteList);
		initSharedPreferences();
		initUMeng();
		new Thread() {

			@Override
			public void run() {
				if (LOG.DEBUG) {
					LOG.cstdr(TAG, "CacheUtil.getCacheSize(mInstance) = "
							+ CacheUtil.getCacheSize(mInstance));
				}
				if (CacheUtil.getCacheSize(mInstance) > Constants.CACHE_MAX_SIZE) {
					CacheUtil.clearCache(mInstance);
				}
			}
		}.start();
	}

	/**
	 * 初始化SharedPreferences
	 */
	private void initSharedPreferences() {
		mSp = SPUtil.getSP(this, getString(R.string.sp_main));
		if (mSp.getString(getString(R.string.spkey_first_launch_time), null) != null) {
			SPUtil.commitStrArrayToSP(
					mSp,
					new String[] { getString(R.string.spkey_last_launch_time) },
					new String[] { String.valueOf(System.currentTimeMillis()) });
		} else {
			isFirstLaunch = false;
			// if(!ShortcutUtil.hasShortcut(mContext)) { // 测试发现某些机型报错
			ShortcutUtil.addShortcut(this);
			// }
			ToastUtil.longToast(this, getString(R.string.msg_first_launch));
			SPUtil.commitStrArrayToSP(
					mSp,
					new String[] { getString(R.string.spkey_first_launch_time) },
					new String[] { String.valueOf(System.currentTimeMillis()) });
		}

		mCurrentUrl = mSp.getString(getString(R.string.spkey_index),
				getString(R.string.index)); // 获取首页
	}

	/**
	 * 初始化友盟组件
	 */
	private void initUMeng() {
		FeedBackListener listener = new FeedBackListener() {

			@Override
			public void onSubmitFB(Activity activity) {
				EditText nameText = (EditText) activity
						.findViewById(R.id.feedback_name);
				EditText emailText = (EditText) activity
						.findViewById(R.id.feedback_email);
				Map<String, String> remarkMap = new HashMap<String, String>();
				remarkMap.put("name", nameText.getText().toString());
				remarkMap.put("email", emailText.getText().toString());
				UMFeedbackService.setRemarkMap(remarkMap);
			}

			@Override
			public void onResetFB(Activity activity,
					Map<String, String> contactMap,
					Map<String, String> remarkMap) {
				EditText nameText = (EditText) activity
						.findViewById(R.id.feedback_name);
				EditText emailText = (EditText) activity
						.findViewById(R.id.feedback_email);
				if (remarkMap != null) {
					nameText.setText(remarkMap.get("name"));
					emailText.setText(remarkMap.get("email"));
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
	 * 
	 * @return
	 */
	public static NncApp getInstance() {
		if (mInstance == null) {
			mInstance = new NncApp();
		}
		return mInstance;
	}

	public Handler getHandler() {
		if (handler == null) {
			handler = new Handler();
		}
		return handler;
	}

	public SharedPreferences getSp() {
		if (mSp == null) {
			mSp = SPUtil.getSP(this, getString(R.string.sp_main));
		}
		return mSp;
	}

	/**
	 * 是否第一次启动
	 * 
	 * @return
	 */
	public boolean isFirstLaunch() {
		return isFirstLaunch;
	}

	/**
	 * 是否网络模式
	 * 
	 * @return
	 */
	public boolean isNetworkMode() {
		return isNetworkMode;
	}

	public void setNetworkMode(boolean isNetworkMode) {
		this.isNetworkMode = isNetworkMode;
	}

	public String getCurrentTitle() {
		return mCurrentTitle;
	}

	public void setCurrentTitle(String currentTitle) {
		mCurrentTitle = currentTitle;
	}

	public String getCurrentUrl() {
		return mCurrentUrl;
	}

	public void setCurrentUrl(String currentUrl) {
		mCurrentUrl = currentUrl;
	}

	public SQLiteDatabase getWritableDB() {
		return mDBHelper.getWritableDatabase();
	}

	public SQLiteDatabase getReadableDB() {
		return mDBHelper.getReadableDatabase();
	}

	public ArrayList<Favorite> getFavoriteList() {
		return mFavoriteList;
	}

	public static float getUI_SCALE_X() {
		return UI_SCALE_X;
	}

	public static void setUI_SCALE_X(float uI_SCALE_X) {
		UI_SCALE_X = uI_SCALE_X;
	}

}
