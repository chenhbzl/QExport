package douzifly.android.qexport.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by douzifly on 13-5-20.
 */
public class CacheManager <E>{

    Storeable<E> storeable;

    public CacheManager(Storeable<E> store){
        storeable = store;
    }

    public List<E> load(Context ctx){

        SQLiteDatabase db = new QEDatabaseHelper(ctx).getReadableDatabase();
        Cursor cur = storeable.getCurosr(db);
        return storeable.loadFrom(cur);
    }

    public void save(Context ctx, List<E> items){
        SQLiteDatabase db = new QEDatabaseHelper(ctx).getReadableDatabase();
        storeable.store(db, items);
    }

}
