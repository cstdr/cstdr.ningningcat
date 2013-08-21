package cstdr.ningningcat.ui.widget.layout;

import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 收藏列表Layout
 * 
 * @author cstdingran@gmail.com
 */
public class FavoriteLayout extends DRLinearLayout {

	private ListView list;

	private LayoutParams listLP;

	public FavoriteLayout(Context context) {
		super(context);
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		this.setOrientation(VERTICAL);
		initListView();
	}

	private void initListView() {
		listLP = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		list = new ListView(mContext);
		list.setLayoutParams(listLP);
		list.setDividerHeight(getIntScaleX(1));
		list.setSmoothScrollbarEnabled(true);
		// list.setDivider(null);
		this.addView(list);
	}

	public void setOnItemClick(OnItemClickListener listener) {
		list.setOnItemClickListener(listener);
	}

	public void setOnItemLongClick(OnItemLongClickListener listener) {
		list.setOnItemLongClickListener(listener);
	}

	public void setListAdapter(ListAdapter adapter) {
		list.setAdapter(adapter);
	}

}
