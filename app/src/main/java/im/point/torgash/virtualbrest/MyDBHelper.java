package im.point.torgash.virtualbrest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by torgash on 04.03.15.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    // The index (key) column name for use in where clauses.
    public static final String KEY_ID = "_id";
    // The name and column index of each column in your database.
// These should be descriptive.
    public static final String KEY_RSS_NAME =
            "RSS_NAME_COLUMN";
    public static final String KEY_NEWS_TITLE =
            "TITLE_COLUMN";
    public static final String KEY_NEWS_LINK =
            "LINK_COLUMN";
    public static final String KEY_NEWS_READ =
            "READ";
    public static final String KEY_NEWS_PICTURE =
            "PICTURE_LINK";
    static final String KEY_NEWS_TIME =
            "TIME";

    // TODO: Create public field for each column in your table.

    public static final String DATABASE_NAME = "myDatabase.db";
    public static  String DATABASE_TABLE = "VirtualBrest";
    public static final int DATABASE_VERSION = 1;
    // SQL Statement to create a new database.
    private static String DATABASE_CREATE= "create table " +
            DATABASE_TABLE + " (" + KEY_ID +
            " integer primary key autoincrement, " +
            KEY_RSS_NAME + " text not null, " +
            KEY_NEWS_TITLE + " text not null, " +

            KEY_NEWS_LINK + " text not null unique, " +
            KEY_NEWS_READ + " boolean DEFAULT 0, " +
            KEY_NEWS_PICTURE + " text default '', " +
            KEY_NEWS_TIME + " integer not null);";
    private static final String TAG = "VIRTBREST";

    public MyDBHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

    }

    // Called when no database exists in disk and the helper class needs
// to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database table " + DATABASE_TABLE);
        db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that
// the version of the database on disk needs to be upgraded to
// the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
// Log the version upgrade.
        Log.d(TAG, "Upgrading from version " +
                oldVersion + " to " +
                newVersion + ", which will destroy all old data");
// Upgrade the existing database to conform to the new
// version. Multiple previous versions can be handled by
// comparing oldVersion and newVersion values.
// The simplest case is to drop the old table and create a new one.
        db.execSQL("DROP TABLE IF IT EXISTS " + DATABASE_TABLE);
// Create a new one.
        onCreate(db);
    }

}
