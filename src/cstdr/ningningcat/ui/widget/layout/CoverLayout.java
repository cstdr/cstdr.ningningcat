package cstdr.ningningcat.ui.widget.layout;

import android.content.Context;
import android.graphics.Color;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cstdr.ningningcat.R;

/**
 * 封面Layout
 * 
 * @author cstdingran@gmail.com
 */
public class CoverLayout extends DRRelativeLayout {

	private TextView logo;

	private TextView name;

	private RelativeLayout.LayoutParams logoLP;

	private RelativeLayout.LayoutParams nameLP;

	private final int ID_LOGO = 1303311640;

	private final int ID_NAME = 1303311641;

	public CoverLayout(Context context) {
		super(context);
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		this.setBackgroundResource(R.drawable.cover);
		Animation animCover = AnimationUtils.loadAnimation(mContext,
				android.R.anim.fade_in); // 简单的渐进动画效果，显示更平滑
		this.setAnimation(animCover);
		initLogo();
		initName();
	}

	private void initName() {
		logoLP = new LayoutParams(getIntScaleX(200), getIntScaleX(200));
		logoLP.addRule(CENTER_IN_PARENT);
		logo = new TextView(mContext);
		logo.setId(ID_LOGO);
		logo.setLayoutParams(logoLP);
		logo.setBackgroundResource(R.drawable.icon);
		this.addView(logo);
	}

	private void initLogo() {
		nameLP = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		nameLP.addRule(CENTER_HORIZONTAL);
		nameLP.addRule(BELOW, ID_LOGO);
		name = new TextView(mContext);
		name.setId(ID_NAME);
		name.setLayoutParams(nameLP);
		name.setText("宁宁猫浏览器");
		name.setTextColor(Color.WHITE);
		name.setTextSize(getIntScaleX(20));
		this.addView(name);
	}

}
