package douzifly.android.qexport.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by douzifly on 13-5-20.
 */
public class QEDatabaseHelper extends SQLiteOpenHelper {

    public final static String DB_NAME = "qe.db";
    public final static int version = 1;

    public QEDatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        VideoCache vc = new VideoCache();
        vc.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        VideoCache vc = new VideoCache();
        vc.onUpgrade(sqLiteDatabase, i, i2);
    }
}
