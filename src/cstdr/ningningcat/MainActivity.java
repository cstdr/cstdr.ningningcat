package cstdr.ningningcat;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CacheManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cstdr.ningningcat.receiver.ConnectivityReceiver;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ToastUtil;
import cstdr.ningningcat.util.UrlUtil;

/**
 * 宁宁猫主界面
 * @author cstdingran@gmail.com
 */
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    private final Context mContext=this;

    private static MainActivity mInstance;

    private EditText mWebsite=null;

    private ImageView mGoto=null;

    private WebView mWebView=null;

    private WebView mNotifyWebView=null;

    private WebSettings mWebSettings=null;

    private String mEditUrl=null;

    private String mCurrentUrl=null;

    private String mCurrentTitle=null;

    private long mLastBackPressTimeMillis=0L;

    private SharedPreferences mSp=null;

    private BroadcastReceiver mConnectitvityReceiver=null;

    private BroadcastReceiver mGotoReceiver=null;

    private boolean isNetworkMode=false;

    private boolean isWebError=false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); //
        // 必须开始就设置
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_main);

        mInstance=this;

        initView();
        initSharedPreferences();
        // if(!NetworkUtil.checkNetwork(mContext)) {
        // DialogUtil.showNoConnectDialog(this);
        // }

        WebIconDatabase.getInstance().open(getDir("icon", MODE_PRIVATE).getPath()); // 允许请求网页icon
        loadUrl(mCurrentUrl); // 加载首页
    }

    /**
     * 初始化BroadcastReceiver
     */
    private void initReceiver() {
        if(mConnectitvityReceiver == null) {
            mConnectitvityReceiver=new ConnectivityReceiver();
        }
        IntentFilter filter=new IntentFilter(ConnectivityReceiver.ACTION_CONNECT_CHANGE);
        registerReceiver(mConnectitvityReceiver, filter);

        if(mGotoReceiver == null) {
            mGotoReceiver=new GotoReceiver();
        }
        filter=new IntentFilter(GotoReceiver.ACTION_GOTO);
        registerReceiver(mGotoReceiver, filter);
    }

    /**
     * 初始化SharedPreferences
     */
    private void initSharedPreferences() {
        mSp=SPUtil.getSP(mContext, getString(R.string.sp_main));
        if(mSp.getString(getString(R.string.spkey_first_launch_time), null) != null) {
            SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_last_launch_time)},
                new String[]{String.valueOf(System.currentTimeMillis())});
        } else {
            ToastUtil.longToast(mContext, getString(R.string.msg_first_launch));
            SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_first_launch_time)},
                new String[]{String.valueOf(System.currentTimeMillis())});
        }

        mCurrentUrl=mSp.getString(getString(R.string.spkey_index), getString(R.string.index)); // 最后一次浏览的页面保存为首页
    }

    private void initView() {

        /** EditText输入框的配置 **/
        mWebsite=(EditText)findViewById(R.id.et_website);
        mWebsite.setImeOptions(EditorInfo.IME_ACTION_GO);
        mWebsite.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(LOG.DEBUG) {
                    LOG.cstdr("actionId=" + actionId);
                }
                if(actionId == EditorInfo.IME_ACTION_GO) {
                    gotoByEditText();
                    return true;
                }
                return false;
            }
        });

        /** 跳转按钮的配置 **/
        mGoto=(ImageView)findViewById(R.id.iv_goto);
        mGoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                gotoByEditText();
            }
        });

        mWebView=(WebView)findViewById(R.id.wv_web);

        /** WebSettings配置 **/
        mWebSettings=mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 支持JavaScript
        mWebSettings.setBuiltInZoomControls(true); // 支持页面放大缩小按钮
        mWebSettings.setSupportZoom(true);
        // mWebSettings.setSupportMultipleWindows(true); // TODO 多窗口
        mWebSettings.setDefaultTextEncodingName("utf-8"); // 页面编码
        // mWebSettings.setAppCacheEnabled(false); // 支持缓存
        // mWebSettings.setAppCacheMaxSize(Constants.CACHE_MAX_SIZE); // 缓存最大值
        // mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 优先使用缓存，在程序退出时清理
        mWebSettings.setPluginState(PluginState.ON); // 若打开flash则需要使用插件
        mWebSettings.setPluginsEnabled(true);
        mWebSettings.setLoadsImagesAutomatically(true); // TODO 当GPRS下提示是否加载图片
        mWebSettings.setUseWideViewPort(true); // 设置页面宽度和屏幕一样
        mWebSettings.setLoadWithOverviewMode(true); // 设置页面宽度和屏幕一样

        /** WebView配置 **/
        mWebView.setScrollbarFadingEnabled(true); // 滚动条自动消失
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // WebView右侧无空隙
        mWebView.setVisibility(View.VISIBLE);
        // mWebView.setInitialScale(100); // 初始缩放比例

        // mWebView.requestFocusFromTouch(); // 接收触摸焦点

        mWebView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mWebsite.hasFocus()) {
                    mWebsite.clearFocus();
                    mWebView.requestFocusFromTouch(); // 不能用requestForcus()，焦点会乱跑
                }
                return false;
            }
        });
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyDownloadListener());

        /** 提示页面 **/
        mNotifyWebView=(WebView)findViewById(R.id.wv_notify);
        mNotifyWebView.setVisibility(View.GONE);
    }

    /**
     * 跳转到在EditText中输入的网址
     */
    private void gotoByEditText() {
        mEditUrl=mWebsite.getText().toString(); // TODO 需要加一个输入内容的检查方法
        String url=UrlUtil.checkEditUrl(mEditUrl); // 只有用户输入的URL才应该检查
        loadUrl(url);
    }

    /**
     * 在WebView中跳转到传入的URL
     * @param url
     */
    private void loadUrl(String url) {
        if(url != null) {
            mWebView.loadUrl(url);
        } else {
            ToastUtil.shortToast(mContext, getString(R.string.msg_no_url));
        }
    }

    class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setProgress(newProgress * 100);
            // setTitle(R.string.msg_progress);
            // setProgressBarVisibility(true);
            // if(newProgress >= 100) {
            // setProgressBarVisibility(false);
            // }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            Drawable drawableIcon=new BitmapDrawable(icon); // Bitmap 转换为 Drawable
            setFeatureDrawable(Window.FEATURE_LEFT_ICON, drawableIcon);
            super.onReceivedIcon(view, icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mCurrentTitle=title;
            setTitle(title + "-" + getString(R.string.app_name));
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            DialogUtil.showJsAlertDialog(mContext, message, result);
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            DialogUtil.showJsConfirmDialog(mContext, message, result);
            return true;
        }

        @Override
        public boolean onJsTimeout() {
            ToastUtil.shortToast(mContext, getString(R.string.msg_timeout));
            return true;
        }

        /**
         * TODO 没有效果
         */
        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
            ToastUtil.shortToast(mContext, getString(R.string.msg_cache_max_size));
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            hideInputWindow(view);
            mWebsite.setText(url.substring(getString(R.string.http).length())); // url除去协议http://
            mCurrentUrl=url;
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            // TODO Auto-generated method stub
            super.doUpdateVisitedHistory(view, url, isReload);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // TODO 不显示默认出错信息，采用自己的出错页面
            setWebError(true);
            mNotifyWebView.loadUrl("file:///android_asset/html/error.html");
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(isWebError()) {
                if(mWebView.getVisibility() == View.VISIBLE) {
                    mWebView.setVisibility(View.GONE);
                    mNotifyWebView.setVisibility(View.VISIBLE);
                }
            } else {
                if(mWebView.getVisibility() == View.GONE) {
                    mNotifyWebView.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }
            setWebError(false);
            super.onPageFinished(view, url);
        }
    }

    /**
     * 监听下载链接
     * @author Administrator
     */
    class MyDownloadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri=Uri.parse(url);
            Intent intent=new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(LOG.DEBUG) {
            LOG.cstdr("itemId = " + item.getItemId());
        }
        switch(item.getItemId()) {
            case R.id.menu_refresh: // 刷新
                loadUrl(mCurrentUrl);
                break;
            case R.id.menu_add: // 添加收藏 TODO 判断数据库是否已有相同数据
                DatabaseUtil mDBHelper=new DatabaseUtil(mContext, DatabaseUtil.mDatabaseName, null, 1);
                String sql=
                    "insert into " + DatabaseUtil.mTableName + "(" + DatabaseUtil.COLUMN_TITLE + "," + DatabaseUtil.COLUMN_URL
                        + ") values(\"" + mCurrentTitle + "\",\"" + mCurrentUrl + "\")";
                DatabaseUtil.insert(mDBHelper, sql);
                ToastUtil.shortToast(mContext, getString(R.string.msg_web_insert));
                break;
            case R.id.menu_favorite: // 查看已收藏页面
                Intent intent=new Intent(MainActivity.this, FavoriteActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_exit: // 退出
                exit();
                break;
        // case R.id.menu_more: // 更多设置 TODO
        //
        // break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mWebView.canGoBack()) {
                mWebView.goBack();
                // mWebsite.setText(mWebView.getUrl()); // 不是很管用
            } else if(System.currentTimeMillis() - mLastBackPressTimeMillis > 2000) {
                ToastUtil.shortToast(mContext, getString(R.string.msg_exit));
                mLastBackPressTimeMillis=System.currentTimeMillis();
            } else {
                exit();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出前处理数据
     */
    private void exit() {
        saveIndexToSP(mCurrentUrl);
        clearCache();
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 清楚网页缓存 deprecated TODO
     */
    private void clearCache() {
        File file=CacheManager.getCacheFileBaseDir();
        for(File item: file.listFiles()) {
            item.delete();
        }
        deleteDatabase("webview.db");
        deleteDatabase("webviewCache.db");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO 切换提示
        super.onConfigurationChanged(newConfig);
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 获取实例
     * @return
     */
    public static MainActivity getInstance() {
        if(mInstance == null) {
            mInstance=new MainActivity();
        }
        return mInstance;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mConnectitvityReceiver);
        unregisterReceiver(mGotoReceiver);
        super.onDestroy();
    }

    /**
     * 设置首页
     * @param url
     */
    private void saveIndexToSP(String url) {
        if(mSp == null) {
            mSp=SPUtil.getSP(mContext, getString(R.string.sp_main));
        }
        SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_index)}, new String[]{url});
    }

    @Override
    protected void onResume() {
        initReceiver();
        super.onResume();

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

    /**
     * 是否处于出错页面
     * @return
     */
    public boolean isWebError() {
        return isWebError;
    }

    public void setWebError(boolean isWebError) {
        this.isWebError=isWebError;
    }

    public WebView getWebView() {
        return mWebView;
    }

    /**
     * 隐藏键盘
     * @param v
     */
    private void hideInputWindow(final View v) {
        InputMethodManager imm=(InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
