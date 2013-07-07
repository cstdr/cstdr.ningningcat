package cstdr.ningningcat.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cstdr.ningningcat.NncApp;
import cstdr.ningningcat.ui.widget.item.FavoriteItem;

/**
 * 收藏夹adapter
 * 
 * @author cstdingran@gmail.com
 */
public class FavoriteAdapter extends BaseAdapter {

	private Context mContext;

	private FavoriteItem item;

	// /** 选自谷歌LOGO颜色 **/
	// private int[] mColorArray={Color.BLUE, Color.RED, Color.YELLOW,
	// Color.BLUE, Color.GREEN, Color.RED};

	private int[] mColorArray = {0xFFa7c7c6, 0xFFe4d9bb, 0xFFfcc4b7,
			0xFFdd9598, 0xFFba928a};

	public FavoriteAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return NncApp.getInstance().getFavoriteList().size();
	}

	@Override
	public Object getItem(int position) {
		return NncApp.getInstance().getFavoriteList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			item = new FavoriteItem(mContext);
		} else {
			item = (FavoriteItem) convertView;
		}
		item.setIcon(mColorArray[position % 5]);
		item.setTitle(NncApp.getInstance().getFavoriteList().get(position)
				.getTitle());
		item.setUrl(NncApp.getInstance().getFavoriteList().get(position)
				.getUrl());
		return item;
	}
}