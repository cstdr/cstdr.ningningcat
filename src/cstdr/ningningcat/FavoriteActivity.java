package cstdr.ningningcat;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import cstdr.ningningcat.constants.EventConstant;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ShareUtil;
import cstdr.ningningcat.util.ShortcutUtil;
import cstdr.ningningcat.util.ToastUtil;

/**
 * “我的收藏”界面
 * @author cstdingran@gmail.com
 */
public class FavoriteActivity extends ListActivity implements EventConstant {

    private static List<Favorite> mFavoriteList=null;

    private Context mContext=null;

    private FavoriteActivity activity=this;

    private BaseAdapter mAdapter=null;

    private SQLiteDatabase mDB=null;

    // /** 选自谷歌LOGO颜色 **/
    // private int[] mColorArray={Color.BLUE, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED};

    private int[] mColorArray={0xFFa7c7c6, 0xFFe4d9bb, 0xFFfcc4b7, 0xFFdd9598, 0xFFba928a};

    public FavoriteActivity() {
        mContext=MainActivity.getInstance().getContext();
        SQLiteOpenHelper mDBHelper=new DatabaseUtil(mContext, DatabaseUtil.mDatabaseName, null, 1);
        mDB=mDBHelper.getWritableDatabase();
        mFavoriteList=new ArrayList<Favorite>();
        mFavoriteList=getFavoriteList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_favorite);

        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                Favorite favorite=mFavoriteList.get(position);
                final String title=favorite.getTitle();
                final String url=favorite.getUrl();
                DialogUtil.showFavoriteDialog(activity, title, position, new DialogItemClickListener() {

                    @Override
                    public void onClick(int position, int which) {
                        switch(which) {
                            case 0: // 添加快捷方式到桌面
                                MobclickAgent.onEvent(mContext, FAVORITE_MENU_ADD_SHORTCUT);
                                ShortcutUtil.addShortcutToDesktop(mContext, title, url);
                                break;
                            case 1: // 设为首页
                                MobclickAgent.onEvent(mContext, FAVORITE_MENU_SAVE_INDEX);
                                saveIndexToSP(url);
                                break;
                            case 2: // 重命名
                                MobclickAgent.onEvent(mContext, FAVORITE_MENU_RENAME);
                                DialogUtil.showRenameDialog(activity, title, url, new DialogRenameListener() {

                                    @Override
                                    public void onClick(String title, String url) {
                                        renameFavorite(title, url);
                                    }
                                });
                                break;
                            case 3: // 分享
                                MobclickAgent.onEvent(mContext, FAVORITE_MENU_SHARE);
                                ShareUtil.shareFavorite(mContext, title, url);
                                break;
                            case 4: // 删除
                                MobclickAgent.onEvent(mContext, FAVORITE_MENU_DELETE);
                                deleteFavorite(position);
                                break;
                        }
                    }
                });
                return true;
            }
        });
        if(mAdapter == null) {
            mAdapter=new FavoriteAdapter(mContext);
        }
        setListAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        mDB.close();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_share_favorite_list: // 分享收藏夹
                MobclickAgent.onEvent(mContext, MENU_SHARE_FAVORITE_LIST);
                ShareUtil.shareFavoriteList(mContext, mFavoriteList);
                break;
            case R.id.menu_delete_favorite_list: // 清空收藏夹
                MobclickAgent.onEvent(mContext, MENU_DELETE_FAVORITE_LIST);
                deleteFavoriteList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ViewHolder {

        public RelativeLayout webRL;

        public TextView webIcon;

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
                holder.webIcon=(TextView)convertView.findViewById(R.id.iv_web_icon);
                holder.webTitle=(TextView)convertView.findViewById(R.id.tv_web_title);
                holder.webUrl=(TextView)convertView.findViewById(R.id.tv_web_url);

                convertView.setTag(holder);

            } else {
                holder=(ViewHolder)convertView.getTag();
            }
            // holder.webIcon.setBackgroundResource(R.drawable.go); // 这里写死了
            // holder.webIcon.setBackgroundColor(0xFF0340FF); // 若控件为ImageView则无效果
            holder.webIcon.setBackgroundColor(mColorArray[position % 5]);
            holder.webTitle.setText(mFavoriteList.get(position).getTitle());
            holder.webUrl.setText(mFavoriteList.get(position).getUrl());

            return convertView;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(GotoReceiver.ACTION_GOTO);
        intent.putExtra(DatabaseUtil.COLUMN_URL, mFavoriteList.get(position).getUrl());
        sendBroadcast(intent);
    }

    /**
     * 设置首页
     * @param url
     */
    private void saveIndexToSP(String url) {
        SharedPreferences mSp=SPUtil.getSP(mContext, getString(R.string.sp_main));
        SPUtil.commitStrArrayToSP(mSp, new String[]{getString(R.string.spkey_index)}, new String[]{url});
        ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_save_index));
    }

    // ///////////////////////////////////////监听器/////////////////////////////
    /**
     * 长按Item后弹出框选项监听器
     * @author cstdingran@gmail.com
     */
    public interface DialogItemClickListener {

        void onClick(int position, int which);
    }

    /**
     * 重命名监听器
     * @author cstdingran@gmail.com
     */
    public interface DialogRenameListener {

        void onClick(String title, String url);
    }

    // //////////////////////////数据库操作//////////////////////////////////////
    /**
     * 添加收藏
     * @param title
     * @param url
     */
    public void insertFavorite(String title, String url) {
        if(hasUrlInDB(url)) {
            ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_insert_same));
            return;
        } else {
            ContentValues values=new ContentValues();
            values.put(DatabaseUtil.COLUMN_TITLE, title);
            values.put(DatabaseUtil.COLUMN_URL, url);
            int id=(int)mDB.insert(DatabaseUtil.mTableName, null, values);
            if(id > 0) {
                ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_insert));
            } else {
                ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_insert_error));
            }
        }
    }

    /**
     * 删除收藏完后列表刷新
     * @param position
     */
    private void deleteFavorite(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(R.string.msg_web_delete_confirm).setPositiveButton(R.string.btn_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=
                    mDB.delete(DatabaseUtil.mTableName, DatabaseUtil.COLUMN_URL + "=?", new String[]{mFavoriteList.get(position)
                        .getUrl()});
                if(id > 0) {
                    if(mFavoriteList.get(position) != null) {
                        mFavoriteList.remove(position);
                    }
                    mAdapter.notifyDataSetChanged();
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_web_delete));
                } else {
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_database_fail));
                }
            }
        }).create().show();
    }

    /**
     * 清空收藏夹
     */
    private void deleteFavoriteList() {
        if(mFavoriteList.isEmpty()) {
            ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_no_favorite));
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(R.string.msg_list_delete_confirm).setPositiveButton(R.string.btn_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=mDB.delete(DatabaseUtil.mTableName, null, null);
                if(id > 0) {
                    if(!mFavoriteList.isEmpty()) {
                        mFavoriteList.clear();
                    }
                    mAdapter.notifyDataSetChanged();
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_list_delete));
                } else {
                    ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_database_fail));
                }
            }
        }).create().show();
    }

    /**
     * 从数据库取得收藏夹数据
     * @return
     */
    private List<Favorite> getFavoriteList() {
        if(!mFavoriteList.isEmpty()) {
            mFavoriteList.clear();
        }
        Cursor cursor=null;
        try {
            cursor=
                mDB.query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_TITLE, DatabaseUtil.COLUMN_URL}, null, null,
                    null, null, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String webTitle=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_TITLE));
                String webUrl=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_URL));
                Favorite favorite=new Favorite(webTitle, webUrl);
                mFavoriteList.add(favorite);
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

    /**
     * 重命名收藏的网页
     * @param title
     * @param url
     */
    private void renameFavorite(String title, String url) {
        ContentValues values=new ContentValues();
        values.put(DatabaseUtil.COLUMN_TITLE, title);
        String whereClause=DatabaseUtil.COLUMN_URL + "=?";
        String[] whereArgs=new String[]{url};
        int id=mDB.update(DatabaseUtil.mTableName, values, whereClause, whereArgs);
        if(id > 0) {
            mFavoriteList=getFavoriteList();
            mAdapter.notifyDataSetChanged();
            ToastUtil.shortToast(activity, getString(R.string.msg_rename));
        } else {
            ToastUtil.shortToast(activity, getString(R.string.msg_rename_error));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
