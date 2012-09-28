package cstdr.ningningcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FavoriteActivity extends ListActivity {

    private List<Map<String, Object>> mFavoriteList=null;

    private Context mContext=MainActivity.getInstance().getContext();

    private ContentResolver resolver=null;

    public static final Uri URI_BOOKMARKS=android.provider.Browser.BOOKMARKS_URI;

    public static final String COLUMN_WEBICON=android.provider.Browser.BookmarkColumns.FAVICON;

    public static final String COLUMN_WEBTITLE=android.provider.Browser.BookmarkColumns.TITLE;

    public static final String COLUMN_WEBURL=android.provider.Browser.BookmarkColumns.URL;

    public static final String COLUMN_COUNT=android.provider.Browser.BookmarkColumns._COUNT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resolver=getContentResolver();
        mFavoriteList=getFavoriteList();
        ListAdapter adapter=new FavoriteAdapter(mContext);
        setListAdapter(adapter);
    }

    /**
     * 从数据库取得收藏夹数据 TODO
     * @return
     */
    public List<Map<String, Object>> getFavoriteList() {
        List<Map<String, Object>> list=new ArrayList<Map<String, Object>>();

        ContentResolver resolver=getContentResolver();
        Cursor cursor=resolver.query(URI_BOOKMARKS, new String[]{COLUMN_WEBICON, COLUMN_WEBTITLE, COLUMN_WEBURL}, null, null, null);
        while(!cursor.isLast()) {
            Map<String, Object> map=new HashMap<String, Object>();
            int webIcon=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WEBICON));
            String webTitle=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBTITLE));
            String webUrl=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WEBURL));
            map.put(COLUMN_WEBICON, webIcon);
            map.put(COLUMN_WEBTITLE, webTitle);
            map.put(COLUMN_WEBURL, webUrl);
            list.add(map);
        }
        return list;

    }

    public final class ViewHolder {

        public ImageView webIcon;

        public TextView webTitle;

        public TextView webUrl;
    }

    public class FavoriteAdapter extends BaseAdapter {

        private LayoutInflater mInflater=null;

        private ViewHolder holder=null;

        FavoriteAdapter(Context context) {
            mInflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            Cursor cursor=resolver.query(URI_BOOKMARKS, new String[]{COLUMN_COUNT}, null, null, null);
            int count=cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT));
            return count;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                holder=new ViewHolder();

                convertView=mInflater.inflate(R.layout.list_favorite, null);
                holder.webIcon=(ImageView)findViewById(R.id.iv_web_icon);
                holder.webTitle=(TextView)findViewById(R.id.tv_web_title);
                holder.webUrl=(TextView)findViewById(R.id.tv_web_url);

                convertView.setTag(holder);

            } else {
                holder=(ViewHolder)convertView.getTag();
            }

            holder.webIcon.setBackgroundResource((Integer)mFavoriteList.get(position).get("webIcon"));
            holder.webTitle.setText((String)mFavoriteList.get(position).get("webTitle"));
            holder.webUrl.setText((String)mFavoriteList.get(position).get("webUrl"));

            holder.webTitle.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });
            holder.webUrl.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                }
            });

            return convertView;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
    }

}
