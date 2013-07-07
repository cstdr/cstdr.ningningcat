package cstdr.ningningcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.webkit.CookieSyncManager;
import android.widget.EditText;

import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;
import com.umeng.fb.util.FeedBackListener;

import cstdr.ningningcat.constants.Constants;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.ui.FavoriteActivity;
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

	private static SQLiteOpenHelper mDBHelper;

	private static ArrayList<Favorite> mFavoriteList;

	private boolean isNetworkMode = true;

	private static String mCurrentTitle = null;

	private static String mCurrentUrl = null;

	/** 手机屏幕宽的比例，以1280x720为准 **/
	private static float UI_SCALE_X;

	/** 是否第一次运行 **/
	public static boolean IS_FIRST_LAUNCH;

	/** SDK版本 **/
	public static int SDK_INT;

	private FeedBackListener fbListener = new FeedBackListener() {

		@Override
		public void onSubmitFB(Activity activity) {
			EditText name = (EditText) activity
					.findViewById(R.id.feedback_name);
			Map<String, String> remarkMap = new HashMap<String, String>();
			remarkMap.put("name", name.getText().toString());
			UMFeedbackService.setRemarkMap(remarkMap);
		}

		@Override
		public void onResetFB(Activity activity,
				Map<String, String> contactMap, Map<String, String> remarkMap) {
			EditText name = (EditText) activity
					.findViewById(R.id.feedback_name);
			if (remarkMap != null) {
				name.setText(remarkMap.get("name"));
			}
		}
	};

	@Override
	public void onCreate() {
		if (LOG.DEBUG) {
			LOG.cstdr(TAG, "============onCreate============");
		}
		// 友盟在线更新配置
		MobclickAgent.updateOnlineConfig(this);

		SDK_INT = android.os.Build.VERSION.SDK_INT;
		mInstance = this;
		handler = new Handler();
		mDBHelper = new DatabaseUtil(mInstance, DatabaseUtil.mDatabaseName,
				null, 1);
		mFavoriteList = new ArrayList<Favorite>();
		mFavoriteList = FavoriteActivity.getFavoriteList(mFavoriteList);
		initSharedPreferences();
		// The CookieSyncManager is used to synchronize the browser cookie store
		// between RAM and permanent storage. To get the best performance,
		// browser cookies are saved in RAM.
		CookieSyncManager.createInstance(this);
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
		UMFeedbackService.setFeedBackListener(fbListener);
	}

	/**
	 * 初始化SharedPreferences
	 */
	private void initSharedPreferences() {
		if (SPUtil.getInstance(mInstance).getString(
				getString(R.string.spkey_first_launch_time), null) != null) {
			IS_FIRST_LAUNCH = false;
			SPUtil.getInstance(mInstance).commitStrArrayToSP(
					new String[]{getString(R.string.spkey_last_launch_time)},
					new String[]{String.valueOf(System.currentTimeMillis())});
		} else {
			IS_FIRST_LAUNCH = true;
			ShortcutUtil.addShortcut(this);
			ToastUtil.longToast(this, getString(R.string.msg_first_launch));
			SPUtil.getInstance(mInstance).commitStrArrayToSP(
					new String[]{getString(R.string.spkey_first_launch_time)},
					new String[]{String.valueOf(System.currentTimeMillis())});
		}

		mCurrentUrl = SPUtil.getInstance(mInstance).getString(
				getString(R.string.spkey_index), Constants.INDEX_DEFAULT_URL); // 获取首页
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
