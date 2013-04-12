package cstdr.ningningcat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * 自定义webview，监听滑动
 * 
 * @author cstdingran@gmail.com
 */
public class DRWebView extends WebView {

	private ScrollInterface mS;

	public DRWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public DRWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DRWebView(Context context) {
		super(context);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		mS.onScrollChange(l, t, oldl, oldt);
	}

	public void setOnScrollChangedListener(ScrollInterface s) {
		mS = s;
	}

	/**
	 * @author cstdingran@gmail.com
	 */
	public interface ScrollInterface {

		public void onScrollChange(int l, int t, int oldl, int oldt);
	}

}
