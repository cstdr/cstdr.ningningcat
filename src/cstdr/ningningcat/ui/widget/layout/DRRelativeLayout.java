package cstdr.ningningcat.ui.widget.layout;

import android.content.Context;
import android.widget.RelativeLayout;
import cstdr.ningningcat.NncApp;

/**
 * 自定义基类RelativeLayout
 * 
 * @author cstdingran@gmail.com
 */
public class DRRelativeLayout extends RelativeLayout {

	protected Context mContext;

	private float scaleX;

	public DRRelativeLayout(Context context) {
		super(context);
		mContext = context;
		scaleX = NncApp.getUI_SCALE_X();
	}

	protected int getIntScaleX(int x) {
		return (int) (x * scaleX);
	}
}
