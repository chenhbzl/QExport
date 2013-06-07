package douzifly.android.qexport.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by douzifly on 13-5-20.
 */
public interface Storeable <E>{

    Cursor getCurosr(SQLiteDatabase db);
    List<E> loadFrom(Cursor cur);
    boolean store(SQLiteDatabase db, List<E> items);
    void onCreate(SQLiteDatabase db);
    void onUpgrade(SQLiteDatabase db, int oldVer, int newVer);
    void remove(SQLiteDatabase db, int id);

}
