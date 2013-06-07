package douzifly.android.qexport.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import douzifly.android.qexport.model.SharedVideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by douzifly on 13-5-20.
 */
public class VideoCache implements Storeable<SharedVideoInfo>{

    public final static String TAG = "VideoCache";

    public final static String TB_NAME = "vidoe_info";

    public final static String COL_ID = "id";
    public final static String COL_TITLE = "title";
    public final static String COL_HASH = "hash";

    @Override
    public Cursor getCurosr(SQLiteDatabase db) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(TB_NAME);
        return builder.query(db, new String[]{"*"}, null, null, null, null, null);
    }

    @Override
    public List<SharedVideoInfo> loadFrom(Cursor cur) {
        List<SharedVideoInfo> videos = new ArrayList<SharedVideoInfo>();
        while(cur.moveToNext()){
            SharedVideoInfo v = new SharedVideoInfo();
            v.id = cur.getInt(cur.getColumnIndex(COL_ID));
            v.title = cur.getString(cur.getColumnIndex(COL_TITLE));
            v.hash = cur.getString(cur.getColumnIndex(COL_HASH));
            videos.add(v);
        }
        return videos;
    }

    @Override
    public boolean store(SQLiteDatabase db, List<SharedVideoInfo> items) {
        if(items == null || items.isEmpty()){
            return false;
        }
        ContentValues values = new ContentValues();
        for(SharedVideoInfo v : items){
            values.put(COL_TITLE, v.title);
            values.put(COL_HASH, v.hash);
        }
        long ret = db.insert(TB_NAME, null, values);
        Log.d(TAG, "insert video, return : " + ret);
        return ret > 0 ? true : false;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TB_NAME + " (id integer primary key, title TEXT, hash TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        String sql = "drop table if exists " + TB_NAME;
        db.execSQL(sql);
        onCreate(db);
    }

    @Override
    public void remove(SQLiteDatabase db, int id) {
        String sql = "delete from " + TB_NAME + " where id = " + id;
        db.execSQL(sql);
    }
}
