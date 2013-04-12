package cstdr.ningningcat.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * 自动完成输入框
 * 
 * @author cstdingran@gmail.com
 */
public class DRAutoCompleteTextView extends AutoCompleteTextView {

	public DRAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DRAutoCompleteTextView(Context context) {
		super(context);
	}

	@Override
	public boolean enoughToFilter() {
		// return super.enoughToFilter();
		return true; // 可以保证在输入框为空时，显示所有历史记录
	}

}
