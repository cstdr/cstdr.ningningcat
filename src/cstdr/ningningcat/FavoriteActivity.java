package cstdr.ningningcat;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.umeng.analytics.MobclickAgent;

import cstdr.ningningcat.constants.EventConstant;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ShareUtil;
import cstdr.ningningcat.util.ShortcutUtil;
import cstdr.ningningcat.util.ToastUtil;
import cstdr.ningningcat.widget.item.FavoriteItem;

/**
 * “我的收藏”界面
 * @author cstdingran@gmail.com
 */
public class FavoriteActivity extends ListActivity implements EventConstant {

    private static final String TAG="FavoriteActivity";

    private Context mContext=this;

    private BaseAdapter mAdapter;

    private static ArrayList<Favorite> list;

    // /** 选自谷歌LOGO颜色 **/
    // private int[] mColorArray={Color.BLUE, Color.RED, Color.YELLOW, Color.BLUE, Color.GREEN, Color.RED};

    private int[] mColorArray={0xFFa7c7c6, 0xFFe4d9bb, 0xFFfcc4b7, 0xFFdd9598, 0xFFba928a};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onCreate============");
        }
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_favorite);
        getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View view, int position, long id) {
                Favorite favorite=NncApp.getInstance().getFavoriteList().get(position);
                final String title=favorite.getTitle();
                final String url=favorite.getUrl();
                DialogUtil.showFavoriteDialog(mContext, title, position, new DialogItemClickListener() {

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
                                DialogUtil.showRenameDialog(mContext, title, url, new DialogRenameListener() {

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
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onDestroy============");
        }
        NncApp.getInstance().getWritableDB().close();
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
                ShareUtil.shareFavoriteList(mContext, NncApp.getInstance().getFavoriteList());
                break;
            case R.id.menu_delete_favorite_list: // 清空收藏夹
                MobclickAgent.onEvent(mContext, MENU_DELETE_FAVORITE_LIST);
                deleteFavoriteList();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class FavoriteAdapter extends BaseAdapter {

        private Context mContext;

        private FavoriteItem item;

        FavoriteAdapter(Context context) {
            mContext=context;
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

            if(convertView == null) {
                item=new FavoriteItem(mContext);
            } else {
                item=(FavoriteItem)convertView;
            }
            item.setIcon(mColorArray[position % 5]);
            item.setTitle(NncApp.getInstance().getFavoriteList().get(position).getTitle());
            item.setUrl(NncApp.getInstance().getFavoriteList().get(position).getUrl());
            return item;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent=new Intent(GotoReceiver.ACTION_GOTO);
        intent.putExtra(DatabaseUtil.COLUMN_URL, NncApp.getInstance().getFavoriteList().get(position).getUrl());
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
    public static void insertFavorite(Context context, String title, String url) {
        if(hasUrlInDB(url)) {
            ToastUtil.shortToast(context, context.getString(R.string.msg_web_insert_same));
            return;
        } else {
            ContentValues values=new ContentValues();
            values.put(DatabaseUtil.COLUMN_TITLE, title);
            values.put(DatabaseUtil.COLUMN_URL, url);
            int id=(int)NncApp.getInstance().getWritableDB().insert(DatabaseUtil.mTableName, null, values);
            if(id > 0) {
                ToastUtil.shortToast(context, context.getString(R.string.msg_web_insert));
                list=NncApp.getInstance().getFavoriteList();
                list=getFavoriteList(list);
            } else {
                ToastUtil.shortToast(context, context.getString(R.string.msg_web_insert_error));
            }
        }
    }

    /**
     * 删除收藏完后列表刷新
     * @param position
     */
    private void deleteFavorite(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.msg_web_delete_confirm).setPositiveButton(R.string.btn_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=
                    NncApp
                        .getInstance()
                        .getWritableDB()
                        .delete(DatabaseUtil.mTableName, DatabaseUtil.COLUMN_URL + "=?",
                            new String[]{NncApp.getInstance().getFavoriteList().get(position).getUrl()});
                if(id > 0) {
                    list=NncApp.getInstance().getFavoriteList();
                    list=getFavoriteList(list);
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
        if(NncApp.getInstance().getFavoriteList().isEmpty()) {
            ToastUtil.shortToast(mContext, mContext.getString(R.string.msg_no_favorite));
            return;
        }
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.msg_list_delete_confirm).setPositiveButton(R.string.btn_cancel, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton(R.string.btn_ok, new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id=NncApp.getInstance().getWritableDB().delete(DatabaseUtil.mTableName, null, null);
                if(id > 0) {
                    list=NncApp.getInstance().getFavoriteList();
                    list=getFavoriteList(list);
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
    public static ArrayList<Favorite> getFavoriteList(ArrayList<Favorite> list) {
        if(!list.isEmpty()) {
            list.clear();
        }
        Cursor cursor=null;
        try {
            cursor=
                NncApp
                    .getInstance()
                    .getReadableDB()
                    .query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_TITLE, DatabaseUtil.COLUMN_URL}, null, null,
                        null, null, null);

            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                String webTitle=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_TITLE));
                String webUrl=cursor.getString(cursor.getColumnIndexOrThrow(DatabaseUtil.COLUMN_URL));
                Favorite favorite=new Favorite(webTitle, webUrl);
                list.add(favorite);
            }
        } finally {
            DatabaseUtil.closeCursor(cursor);
        }
        return list;

    }

    /**
     * 查询数据库中是否有相同URL的数据
     * @param url
     * @return
     */
    private static boolean hasUrlInDB(String url) {
        Cursor cursor=null;
        try {
            cursor=
                NncApp.getInstance().getReadableDB()
                    .query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_URL}, null, null, null, null, null);
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
        int id=NncApp.getInstance().getWritableDB().update(DatabaseUtil.mTableName, values, whereClause, whereArgs);
        if(id > 0) {
            list=NncApp.getInstance().getFavoriteList();
            list=getFavoriteList(list);
            mAdapter.notifyDataSetChanged();
            ToastUtil.shortToast(mContext, getString(R.string.msg_rename));
        } else {
            ToastUtil.shortToast(mContext, getString(R.string.msg_rename_error));
        }
    }

    @Override
    protected void onPause() {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onPause============");
        }
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onResume============");
        }
        super.onResume();
        MobclickAgent.onResume(this);
    }

}
