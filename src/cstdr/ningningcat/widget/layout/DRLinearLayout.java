package cstdr.ningningcat.widget.layout;

import android.content.Context;
import android.widget.LinearLayout;
import cstdr.ningningcat.NncApp;

/**
 * 自定义基类LinearLayout
 * 
 * @author cstdingran@gmail.com
 */
public class DRLinearLayout extends LinearLayout {

	protected Context mContext;

	private float scaleX;

	public DRLinearLayout(Context context) {
		super(context);
		mContext = context;
		scaleX = NncApp.getUI_SCALE_X();
	}

	protected int getIntScaleX(int x) {
		return (int) (x * scaleX);
	}
}
