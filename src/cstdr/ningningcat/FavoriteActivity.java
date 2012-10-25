package cstdr.ningningcat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.ToastUtil;

/**
 * “我的收藏”界面
 * @author cstdingran@gmail.com
 */
public class FavoriteActivity extends ListActivity {

    public static List<Map<String, Object>> mFavoriteList=null;

    private Context mContext=null;

    private ListAdapter mAdapter=null;

    private SQLiteDatabase mDB=null;

    public FavoriteActivity() {
        mContext=MainActivity.getInstance().getContext();
        SQLiteOpenHelper mDBHelper=new DatabaseUtil(mContext, DatabaseUtil.mDatabaseName, null, 1);
        mDB=mDBHelper.getWritableDatabase();
        mFavoriteList=getFavoriteList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(getString(R.string.title_favorite));

        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                deleteFavorite(FavoriteActivity.this, position);
                return true;
            }
        });
        if(mAdapter == null) {
            mAdapter=new FavoriteAdapter(mContext);
        }
        setListAdapter(mAdapter);
    }

    /**
     * 添加收藏
     * @param title
     * @param url
     */
    public void insertFavorite(String title, String url) {
        if(hasUrlInDB(url)) {
            ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_insert_same));
        } else {
            DatabaseUtil mDBHelper=new DatabaseUtil(mContext, DatabaseUtil.mDatabaseName, null, 1);
            String sql=
                "insert into " + DatabaseUtil.mTableName + "(" + DatabaseUtil.COLUMN_TITLE + "," + DatabaseUtil.COLUMN_URL
                    + ") values(\"" + title + "\",\"" + url + "\")";
            DatabaseUtil.insert(mDBHelper, sql);
            ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_insert));
        }
    }

    /**
     * 删除收藏完后列表刷新
     * @param mContext
     * @param position
     */
    private void deleteFavorite(Context context, final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.msg_web_delete_confirm))
            .setPositiveButton(context.getString(R.string.btn_ok), new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mDB.delete(DatabaseUtil.mTableName, DatabaseUtil.COLUMN_URL + "=?",
                        new String[]{(String)(mFavoriteList.get(position).get(DatabaseUtil.COLUMN_URL))});
                    if(mFavoriteList.get(position) != null) {
                        mFavoriteList.remove(position);
                    }
                    ((BaseAdapter)mAdapter).notifyDataSetChanged();
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_delete));
                }
            }).setNegativeButton(context.getString(R.string.btn_cancel), new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
    }

    /**
     * 从数据库取得收藏夹数据
     * @return
     */
    private List<Map<String, Object>> getFavoriteList() {
        mFavoriteList=new ArrayList<Map<String, Object>>();
        Cursor cursor=null;
        try {
            cursor=
                mDB.query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_TITLE, DatabaseUtil.COLUMN_URL}, null, null,
                    null, null, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Map<String, Object> map=new HashMap<String, Object>();
                String webTitle=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_TITLE));
                String webUrl=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_URL));
                map.put(DatabaseUtil.COLUMN_TITLE, webTitle);
                map.put(DatabaseUtil.COLUMN_URL, webUrl);
                mFavoriteList.add(map);
            }
        } finally {
            DatabaseUtil.closeCursor(cursor);
        }
        return mFavoriteList;

    }

    /**
     * 查询数据库中是否有相同URL的数据
     * @param url
     * @return
     */
    private boolean hasUrlInDB(String url) {
        Cursor cursor=null;
        try {
            cursor=mDB.query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_URL}, null, null, null, null, null);
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_URL)).equals(url)) {
                    return true;
                }
            }
        } finally {
            DatabaseUtil.closeCursor(cursor);
        }
        return false;
    }

    public static class ViewHolder {

        public RelativeLayout webRL;

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
            return mFavoriteList.size();
        }

        @Override
        public Object getItem(int position) {
            return mFavoriteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null) {

                holder=new ViewHolder();

                convertView=mInflater.inflate(R.layout.list_favorite, null);
                // 这里需要convertView.findViewById()，而不能直接是findViewById()，否则会空指针
                holder.webRL=(RelativeLayout)convertView.findViewById(R.id.rl_favorites);
                holder.webIcon=(ImageView)convertView.findViewById(R.id.iv_web_icon);
                holder.webTitle=(TextView)convertView.findViewById(R.id.tv_web_title);
                holder.webUrl=(TextView)convertView.findViewById(R.id.tv_web_url);

                convertView.setTag(holder);

            } else {
                holder=(ViewHolder)convertView.getTag();
            }

            holder.webIcon.setBackgroundResource(R.drawable.go); // TODO 这里写死了
            holder.webTitle.setText((String)mFavoriteList.get(position).get(DatabaseUtil.COLUMN_TITLE));
            holder.webUrl.setText((String)mFavoriteList.get(position).get(DatabaseUtil.COLUMN_URL));

            return convertView;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(GotoReceiver.ACTION_GOTO);
        intent.putExtra(DatabaseUtil.COLUMN_URL, (String)mFavoriteList.get(position).get(DatabaseUtil.COLUMN_URL));
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        mDB.close();
        super.onDestroy();
    }

}
