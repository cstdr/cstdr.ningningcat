package cstdr.ningningcat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CacheManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cstdr.ningningcat.receiver.ConnectivityReceiver;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ToastUtil;
import cstdr.ningningcat.util.UrlUtil;
import cstdr.ningningcat.widget.MyWebView;
import cstdr.ningningcat.widget.MyWebView.ScrollInterface;

/**
 * 宁宁猫主界面
 * @author cstdingran@gmail.com
 */
@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    private final Context mContext=this;

    private static MainActivity mInstance;

    private FavoriteActivity mFavorite;

    private RelativeLayout mWebsiteNavigation;

    private AutoCompleteTextView mWebsite=null;

    private ImageView mGoto=null;

    private MyWebView mWebView=null;

    private WebView mNotifyWebView=null;

    private WebSettings mWebSettings=null;

    private String mEditUrl=null;

    private String mCurrentUrl=null;

    private String mCurrentTitle=null;

    private long mLastBackPressTimeMillis=0L;

    private long mLastScrollTimeMillis=0L;

    private SharedPreferences mSp=null;

    private BroadcastReceiver mConnectitvityReceiver=null;

    private BroadcastReceiver mGotoReceiver=null;

    private boolean isNetworkMode=false;

    private boolean isWebError=false;

    private static WebBackForwardList mWebBackForwardList;

    private static ArrayAdapter<String> mAutoCompleteAdapter;

    private static List<String> mHistoryUrlList;

    private Animation animFadeOut;

    private Animation animFadeIn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 必须开始就设置
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_main);

        mInstance=this;
        mFavorite=new FavoriteActivity();

        initView();
        initSharedPreferences();

        WebIconDatabase.getInstance().open(getDir("icon", MODE_PRIVATE).getPath()); // 允许请求网页icon

        processData();

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

        /** RelativeLayout导航栏 **/
        mWebsiteNavigation=(RelativeLayout)findViewById(R.id.rl_website_navigation);
        mWebsiteNavigation.setVisibility(View.VISIBLE);
        animFadeOut=AnimationUtils.loadAnimation(mContext, R.anim.fade_out);
        animFadeIn=AnimationUtils.loadAnimation(mContext, R.anim.fade_in);
        animFadeOut.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mWebsiteNavigation.setVisibility(View.GONE);
            }
        });
        animFadeIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                mWebsiteNavigation.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        // animFadeOut.setFillAfter(true);
        // animFadeOut.setFillBefore(false);
        // animFadeIn.setFillAfter(true);
        // animFadeIn.setFillBefore(false);

        /** EditText输入框的配置 **/
        mWebsite=(AutoCompleteTextView)findViewById(R.id.actv_website);
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
        mAutoCompleteAdapter=new ArrayAdapter<String>(mContext, R.layout.list_autocomplete);
        mHistoryUrlList=new LinkedList<String>();
        mWebsite.setThreshold(1); // 最小匹配字符为1个字符
        // mWebsite.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer()); //
        // 用户必须提供一个MultiAutoCompleteTextView.Tokenizer用来区分不同的子串
        mWebsite.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // 设置点击后全选
                if(hasFocus) {
                    mWebsite.setSelection(0, mWebsite.getText().length());
                }
            }
        });
        mWebsite.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                String url=(String)adapter.getItemAtPosition(position);
                if(LOG.DEBUG) {
                    LOG.cstdr("onItemClick -> " + UrlUtil.httpUrl2url(url));
                }
                loadUrl(UrlUtil.url2HttpUrl(url));
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

        mWebView=(MyWebView)findViewById(R.id.wv_web);

        /** WebSettings配置 **/
        mWebSettings=mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 支持JavaScript
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true); // JS打开新窗口
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
        // mWebSettings.setNeedInitialFocus(true); // （无效）当webview调用requestFocus时为webview设置节点，这样系统可以自动滚动到指定位置
        mWebSettings.setSaveFormData(true); // 保存表单数据
        mWebSettings.setSavePassword(true); // 保存密码

        /** WebView配置 **/
        mWebView.setScrollbarFadingEnabled(true); // 滚动条自动消失
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // WebView右侧无空隙
        mWebView.setVisibility(View.VISIBLE);
        // mWebView.setInitialScale(100); // 初始缩放比例

        // mWebView.requestFocusFromTouch(); // 接收触摸焦点

        mWebView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mWebView.hasFocus() || mWebsite.hasFocus()) {
                    mWebsite.clearFocus();
                    mWebView.requestFocusFromTouch(); // 不能用requestFocus()，焦点会乱跑
                }
                return false;
            }
        });
        mWebView.setOnScrollChangedListener(new ScrollInterface() { // TODO

                @Override
                public void onScrollChange(int l, int t, int oldl, int oldt) {
                    if((t - oldt) > 10) {
                        if(mWebsiteNavigation.isShown() && (System.currentTimeMillis() - mLastScrollTimeMillis) > 1000) {
                            mWebsiteNavigation.startAnimation(animFadeOut);
                            mLastScrollTimeMillis=System.currentTimeMillis();
                        }
                    } else if((oldt - t) > 10) {
                        if(!mWebsiteNavigation.isShown() && (System.currentTimeMillis() - mLastScrollTimeMillis) > 1000) {
                            mWebsiteNavigation.startAnimation(animFadeIn);
                            mLastScrollTimeMillis=System.currentTimeMillis();
                        }
                    }
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
     * 自定义WebView类
     * @author ran.ding@downjoy.com
     */

    /**
     * 跳转到在EditText中输入的网址
     */
    private void gotoByEditText() {
        mEditUrl=mWebsite.getText().toString().trim();
        String url=UrlUtil.checkEditUrl(mEditUrl); // 只有用户输入的URL才应该检查
        loadUrl(url);
    }

    /**
     * 在WebView中跳转到传入的URL
     * @param url
     */
    private void loadUrl(String url) {
        hideInputWindow(mWebView);
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

    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if(LOG.DEBUG) {
                LOG.cstdr("onPageStarted -> url = " + url);
            }
            mWebsite.setText(UrlUtil.httpUrl2url(url)); // url除去协议http://
            mCurrentUrl=url;
            super.onPageStarted(view, url, favicon);
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
            setAutoComplete(); // TODO 这个位置需要考虑
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
            if(LOG.DEBUG) {
                LOG.cstdr("MyDownloadListener : mimetype -> " + mimetype);
            }
            Intent intent=new Intent(Intent.ACTION_VIEW);
            Uri uri=Uri.parse(url);
            if(mimetype.equals("application/vnd.android.package-archive")) { // 下载链接时调用系统浏览器下载
                intent.setData(uri);
                startActivity(intent);
            } else {
                intent.setDataAndType(uri, mimetype); // 只用setType方法会清除先前放入的data数据
                startActivity(intent);
            }
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
            // LOG.cstdr("itemId = " + item.getItemId());
        }
        switch(item.getItemId()) {
            case R.id.menu_refresh: // 刷新
                loadUrl(mCurrentUrl);
                break;
            case R.id.menu_add: // 添加收藏
                mFavorite.insertFavorite(mCurrentTitle, mCurrentUrl);
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
        hideInputWindow(mWebView);
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

    /**
     * 得到浏览历史记录
     */
    private void setAutoComplete() {
        mWebBackForwardList=mWebView.copyBackForwardList();
        String url;
        for(int i=0; i < mWebBackForwardList.getSize(); i++) {
            url=mWebBackForwardList.getItemAtIndex(i).getUrl();
            if(!mHistoryUrlList.contains(url)) {
                mHistoryUrlList.add(url);
                if(LOG.DEBUG) {
                    LOG.cstdr("setAutoComplete -> " + UrlUtil.httpUrl2url(url));
                }
                mAutoCompleteAdapter.add(UrlUtil.httpUrl2url(url));
            }
        }
        mWebsite.setAdapter(mAutoCompleteAdapter);
    }

    /**
     * 因为singleTask模式，启动时会调用此类
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        processData();

    }

    /**
     * 处理intent传来的数据
     * @param intent
     */
    private void processData() {
        Intent intent=getIntent();
        String action=intent.getAction();
        if(LOG.DEBUG) {
            LOG.cstdr("processData : action =  " + action);
        }
        if(action != null) {
            if(action.equals(GotoReceiver.ACTION_GOTO)) {
                String url=intent.getStringExtra(DatabaseUtil.COLUMN_URL);
                loadUrl(url);
            } else if(action.equals(GotoReceiver.ACTION_VIEW)) { // TODO
                Uri uri=intent.getData();
                if(LOG.DEBUG) {
                    LOG.cstdr("processData : uri =  " + uri);
                }
                if(uri != null) {
                    loadUrl(UrlUtil.url2HttpUrl(uri.toString()));
                } else {
                    loadUrl(getString(R.string.index)); // 加载首页
                }
            }
        } else {
            loadUrl(mCurrentUrl); // 加载首页
        }

    }
}
