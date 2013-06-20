package cstdr.ningningcat.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.umeng.analytics.MobclickAgent;

import cstdr.ningningcat.NncApp;
import cstdr.ningningcat.R;
import cstdr.ningningcat.constants.Constants;
import cstdr.ningningcat.constants.EventConstant;
import cstdr.ningningcat.data.Favorite;
import cstdr.ningningcat.receiver.GotoReceiver;
import cstdr.ningningcat.ui.adapter.FavoriteAdapter;
import cstdr.ningningcat.ui.widget.layout.FavoriteLayout;
import cstdr.ningningcat.util.DatabaseUtil;
import cstdr.ningningcat.util.DialogUtil;
import cstdr.ningningcat.util.LOG;
import cstdr.ningningcat.util.SPUtil;
import cstdr.ningningcat.util.ShareUtil;
import cstdr.ningningcat.util.ShortcutUtil;
import cstdr.ningningcat.util.ToastUtil;

/**
 * “我的收藏”界面
 * @author cstdingran@gmail.com
 */
public class FavoriteActivity extends Activity implements EventConstant {

    private static final String TAG="FavoriteActivity";

    private Context mContext=this;

    private FavoriteLayout layout;

    private static FavoriteAdapter mAdapter;

    private static ArrayList<Favorite> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "============onCreate============");
        }
        super.onCreate(savedInstanceState);
        initFavoriteLayout();
    }

    /**
     * 初始化导航栏
     */
    private void initFavoriteLayout() {
        layout=new FavoriteLayout(mContext);
        layout.setOnItemClick(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                Intent intent=new Intent(GotoReceiver.ACTION_GOTO);
                intent.putExtra(DatabaseUtil.COLUMN_URL, NncApp.getInstance().getFavoriteList().get(position).getUrl());
                sendBroadcast(intent);
            }
        });
        layout.setOnItemLongClick(new OnItemLongClickListener() {

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
                                DialogUtil.deleteFavorite(mContext, mAdapter, position);
                                break;
                        }
                    }
                });
                return true;
            }
        });
        mAdapter=new FavoriteAdapter(mContext);
        layout.setListAdapter(mAdapter);
        this.setContentView(layout);
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
                DialogUtil.deleteFavoriteList(mContext, mAdapter);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 设置首页
     * @param url
     */
    private void saveIndexToSP(String url) {
        SPUtil.getInstance(mContext).commitStrArrayToSP(new String[]{getString(R.string.spkey_index)}, new String[]{url});
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
     * 点击收藏按钮
     * @param title
     * @param url
     */
    public static void addFavorite(Context context, String title, String url) {
        ContentValues values=new ContentValues();
        if(title.equals(Constants.TITLE_NULL)) {
            title=Constants.TITLE_NULL_DEFAULT;
        }
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

    /**
     * 删除收藏
     */
    public static void deleteFavorite(Context context, String url, FavoriteAdapter adapter) {
        int id=
            NncApp.getInstance().getWritableDB().delete(DatabaseUtil.mTableName, DatabaseUtil.COLUMN_URL + "=?", new String[]{url});
        if(id > 0) {
            ArrayList<Favorite> list=NncApp.getInstance().getFavoriteList();
            list=FavoriteActivity.getFavoriteList(list);
            if(adapter != null) {
                adapter.notifyDataSetChanged();
            }
            ToastUtil.shortToast(context, context.getString(R.string.msg_web_delete));
        } else {
            ToastUtil.shortToast(context, context.getString(R.string.msg_database_fail));
        }
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
                        null, null, DatabaseUtil.COLUMN_PAGEVIEW + " DESC"); // 按照浏览量从高到低排序

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
    public static boolean hasUrlInDB(String url) {
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

    /**
     * 得到该收藏的总浏览量
     * @param url
     * @return
     */
    private static int getPageview(String url) {
        Cursor cursor=null;
        try {
            String selection=DatabaseUtil.COLUMN_URL + "=?";
            String[] selectionArgs=new String[]{url};
            cursor=
                NncApp
                    .getInstance()
                    .getReadableDB()
                    .query(DatabaseUtil.mTableName, new String[]{DatabaseUtil.COLUMN_PAGEVIEW}, selection, selectionArgs, null,
                        null, null);
            if(cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(DatabaseUtil.COLUMN_PAGEVIEW));
            }
        } finally {
            DatabaseUtil.closeCursor(cursor);
        }
        return 0;
    }

    /**
     * 增加一个浏览量
     * @param url
     */
    public static void addPageview(String url) {
        int pageview=getPageview(url);
        if(LOG.DEBUG) {
            LOG.cstdr(TAG, "url = " + url);
            LOG.cstdr(TAG, "pageview = " + pageview);
        }
        if(pageview == Integer.MAX_VALUE) { // 防止超出Integer范围
            pageview=pageview / 10;
        }
        ContentValues values=new ContentValues();
        values.put(DatabaseUtil.COLUMN_PAGEVIEW, pageview + 1);
        String whereClause=DatabaseUtil.COLUMN_URL + "=?";
        String[] whereArgs=new String[]{url};
        int id=NncApp.getInstance().getWritableDB().update(DatabaseUtil.mTableName, values, whereClause, whereArgs);
        if(id > 0) {
            list=NncApp.getInstance().getFavoriteList();
            list=getFavoriteList(list);
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
