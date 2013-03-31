package cstdr.ningningcat.widget.item;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils.TruncateAt;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cstdr.ningningcat.widget.layout.DRRelativeLayout;

/**
 * 收藏列表单个收藏item
 * @author cstdingran@gmail.com
 */
public class FavoriteItem extends DRRelativeLayout {

    private TextView icon;

    private TextView title;

    private TextView url;

    private RelativeLayout.LayoutParams iconLP;

    private RelativeLayout.LayoutParams titleLP;

    private RelativeLayout.LayoutParams urlLP;

    private final int ID_ICON=1303312235;

    private final int ID_TITLE=1303312236;

    private final int ID_URL=1303312237;

    public FavoriteItem(Context context) {
        super(context);
        // 这里注意要用ListView.LayoutParams，否则会因为父控件的LayoutParams和子控件不同报错
        this.setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        initIcon();
        initTitle();
        initUrl();
    }

    private void initIcon() {
        iconLP=new LayoutParams(getIntScaleX(12), getIntScaleX(100));
        iconLP.addRule(ALIGN_PARENT_LEFT);
        icon=new TextView(mContext);
        icon.setId(ID_ICON);
        icon.setLayoutParams(iconLP);
        this.addView(icon);
    }

    private void initTitle() {
        titleLP=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        titleLP.addRule(RIGHT_OF, ID_ICON);
        titleLP.setMargins(getIntScaleX(8), getIntScaleX(4), 0, 0);
        title=new TextView(mContext);
        title.setId(ID_TITLE);
        title.setLayoutParams(titleLP);
        title.setSingleLine();
        title.setEllipsize(TruncateAt.END);
        title.setTextColor(Color.BLACK);
        title.setTextSize(getIntScaleX(18));
        this.addView(title);
    }

    private void initUrl() {
        urlLP=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        urlLP.addRule(ALIGN_LEFT, ID_TITLE);
        urlLP.addRule(BELOW, ID_TITLE);
        urlLP.setMargins(0, 0, 0, 0);
        url=new TextView(mContext);
        url.setId(ID_URL);
        url.setLayoutParams(urlLP);
        url.setSingleLine();
        url.setEllipsize(TruncateAt.END);
        url.setTextColor(Color.GRAY);
        url.setTextSize(getIntScaleX(14));
        this.addView(url);
    }

    public void setIcon(int color) {
        icon.setBackgroundColor(color);
    }

    public void setTitle(String str) {
        title.setText(str);
    }

    public void setUrl(String str) {
        url.setText(str);
    }
}
