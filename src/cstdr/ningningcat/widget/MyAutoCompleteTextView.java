package cstdr.ningningcat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class MyAutoCompleteTextView extends AutoCompleteTextView {

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyAutoCompleteTextView(Context context) {
        super(context);
    }

    @Override
    public boolean enoughToFilter() {
        // super.enoughToFilter();
        return true;
    }

}
