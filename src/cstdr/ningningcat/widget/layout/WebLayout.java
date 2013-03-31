package cstdr.ningningcat.widget.layout;

import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cstdr.ningningcat.R;
import cstdr.ningningcat.widget.DRAutoCompleteTextView;
import cstdr.ningningcat.widget.DRWebView;

/**
 * 浏览网页的主Layout
 * @author cstdingran@gmail.com
 */
public class WebLayout extends DRRelativeLayout {

    private DRWebView webview;

    private RelativeLayout navLayout;

    /** 添加收藏按钮 **/
    private ImageView add;

    private RelativeLayout websiteLayout;

    /** 输入框 **/
    private DRAutoCompleteTextView website;

    /** 跳转按钮 **/
    private ImageView gotoView;

    /** 重写按钮 **/
    private ImageView rewrite;

    private RelativeLayout.LayoutParams webviewLP;

    private RelativeLayout.LayoutParams navLayoutLP;

    private RelativeLayout.LayoutParams addLP;

    private RelativeLayout.LayoutParams websiteLayoutLP;

    private RelativeLayout.LayoutParams websiteLP;

    private RelativeLayout.LayoutParams gotoViewLP;

    private RelativeLayout.LayoutParams rewriteLP;

    private final int ID_ADD=1304010026;

    private final int ID_WEBSITE_LAYOUT=1304010027;

    private final int ID_WEBSITE=1304010028;

    private final int ID_GOTO=1304010029;

    private final int ID_REWRITE=1304010030;

    public WebLayout(Context context) {
        super(context);
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        initWebView();
        initNavLayout();
        initAdd();
        initWebsiteLayout();
        initWebsite();
        initGoto();
        initRewrite();
    }

    private void initWebView() {
        webviewLP=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        webview=new DRWebView(mContext);
        webview.setLayoutParams(webviewLP);
        this.addView(webview);
    }

    private void initNavLayout() {
        navLayoutLP=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        navLayoutLP.addRule(ALIGN_PARENT_TOP);
        navLayout=new RelativeLayout(mContext);
        navLayout.setLayoutParams(navLayoutLP);
        navLayout.setBackgroundColor(Color.DKGRAY);
        this.addView(navLayout);
    }

    private void initAdd() {
        addLP=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addLP.addRule(CENTER_VERTICAL);
        addLP.setMargins(0, 0, 0, 0);
        add=new ImageView(mContext);
        add.setId(ID_ADD);
        add.setLayoutParams(addLP);
        add.setImageResource(R.drawable.navigation_add);
        navLayout.addView(add);
    }

    private void initWebsiteLayout() {
        websiteLayoutLP=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        websiteLayoutLP.setMargins(0, 0, 0, 0);
        websiteLayoutLP.addRule(RIGHT_OF, ID_ADD);
        websiteLayout=new RelativeLayout(mContext);
        websiteLayout.setId(ID_WEBSITE_LAYOUT);
        websiteLayout.setLayoutParams(websiteLayoutLP);
        websiteLayout.setBackgroundResource(R.drawable.navigation_background);
        navLayout.addView(websiteLayout);
    }

    private void initWebsite() {
        websiteLP=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        websiteLP.setMargins(0, 0, 0, 0);
        websiteLP.addRule(ALIGN_PARENT_LEFT);
        websiteLP.addRule(LEFT_OF, ID_REWRITE);
        website=new DRAutoCompleteTextView(mContext);
        website.setId(ID_WEBSITE);
        website.setLayoutParams(websiteLP);
        website.setBackgroundColor(Color.TRANSPARENT);
        website.setHint(R.string.website_hint);
        website.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        website.setHighlightColor(Color.parseColor("#0099CC"));
        websiteLayout.addView(website);
    }

    private void initRewrite() {
        rewriteLP=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rewriteLP.addRule(LEFT_OF, ID_GOTO);
        rewriteLP.addRule(CENTER_VERTICAL);
        rewriteLP.setMargins(0, 0, getIntScaleX(4), 0);
        rewrite=new ImageView(mContext);
        rewrite.setId(ID_REWRITE);
        rewrite.setLayoutParams(rewriteLP);
        rewrite.setImageResource(R.drawable.navigation_rewrite);
        websiteLayout.addView(rewrite);
    }

    private void initGoto() {
        gotoViewLP=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        gotoViewLP.addRule(ALIGN_PARENT_RIGHT);
        gotoViewLP.addRule(CENTER_VERTICAL);
        gotoView=new ImageView(mContext);
        gotoView.setId(ID_GOTO);
        gotoView.setLayoutParams(gotoViewLP);
        gotoView.setImageResource(R.drawable.navigation_goto);
        websiteLayout.addView(gotoView);
    }

    public RelativeLayout getNavLayout() {
        return navLayout;
    }

    public ImageView getAdd() {
        return add;
    }

    public DRAutoCompleteTextView getWebsite() {
        return website;
    }

    public ImageView getRewrite() {
        return rewrite;
    }

    public ImageView getGotoView() {
        return gotoView;
    }

    public DRWebView getWebview() {
        return webview;
    }

}
