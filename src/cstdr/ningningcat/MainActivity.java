package cstdr.ningningcat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cstdr.ningningcat.constants.Constants;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.LOG;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {

    private Context mContext=this;

    private EditText mWebsite=null;

    private ImageView mGoto=null;

    private WebView mWebView=null;

    private WebSettings mWebSettings=null;

    private String mEditUrl;

    private String mCurrentUrl;

    private long mLastBackPressTimeMillis=0L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); // 必须开始就设置
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.activity_main);
        initView();
        WebIconDatabase.getInstance().open(getDir("icon", MODE_PRIVATE).getPath()); // 允许请求网页icon
        mCurrentUrl=getString(R.string.index);
        mWebView.loadUrl(mCurrentUrl);
    }

    private void initView() {
        mWebsite=(EditText)findViewById(R.id.et_website);
        mWebsite.setSelectAllOnFocus(true);

        mGoto=(ImageView)findViewById(R.id.tv_goto);
        mGoto.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mEditUrl=mWebsite.getText().toString(); // TODO 需要加一个输入内容的检查方法
                if(mEditUrl.startsWith(getString(R.string.http))) {
                    mWebView.loadUrl(mEditUrl);
                } else {
                    mWebView.loadUrl(getString(R.string.http) + mEditUrl);
                }
            }
        });

        mWebView=(WebView)findViewById(R.id.wv_web);
        mWebSettings=mWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true); // 支持JavaScript
        mWebSettings.setBuiltInZoomControls(true); // 支持页面放大缩小按钮
        mWebSettings.setSupportZoom(true);
        mWebSettings.setSupportMultipleWindows(true); // TODO 多窗口
        mWebSettings.setDefaultTextEncodingName("utf-8"); // 页面编码
        mWebSettings.setAppCacheEnabled(true); // 支持缓存
        mWebSettings.setAppCacheMaxSize(Constants.CACHE_MAX_SIZE); // 缓存最大值
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); // 缓存模式
        mWebSettings.setLoadsImagesAutomatically(true); // TODO 当GPRS下提示是否加载图片
        mWebSettings.setUseWideViewPort(true); // 设置页面宽度和屏幕一样
        mWebSettings.setLoadWithOverviewMode(true); // 设置页面宽度和屏幕一样

        mWebView.setScrollbarFadingEnabled(true); // 滚动条自动消失
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY); // WebView右侧无空隙
        // mWebView.setInitialScale(100); // 初始缩放比例

        // mWebView.requestFocusFromTouch(); // 接收触摸焦点

        mWebView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mWebView.requestFocusFromTouch(); // 不能用requestForcus()，焦点会乱跑
                return false;
            }
        });
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setDownloadListener(new MyDownloadListener());
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
            Toast.makeText(mContext, getString(R.string.msg_timeout), Toast.LENGTH_SHORT).show();
            return true;
        }

        /**
         * TODO
         */
        @Override
        public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
            Toast.makeText(mContext, getString(R.string.msg_cache_max_size), Toast.LENGTH_SHORT).show();
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
        }

        /**
         * TODO 接收到有URL的图片后
         */
        @Override
        public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
            super.onReceivedTouchIconUrl(view, url, precomposed);
        }
    }

    class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
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
            // TODO Auto-generated method stub
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

    }

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
            LOG.dev(item.getItemId());
        }
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                mWebView.loadUrl(mCurrentUrl);
                break;
            case R.id.menu_add: // TODO

                break;
            case R.id.menu_favorite: // TODO
                break;
            case R.id.menu_exit:
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
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
                Toast.makeText(mContext, R.string.msg_exit, Toast.LENGTH_SHORT).show();
                mLastBackPressTimeMillis=System.currentTimeMillis();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO 切换提示
        super.onConfigurationChanged(newConfig);
    }

}
