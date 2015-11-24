package im.point.torgash.virtualbrest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by torgash on 04.03.15.
 */
public class MySQLiteSingleton {

    private static final String TAG = "VIRTBREST";
    static Cursor cursor;
    // The index (key) column name for use in where clauses.
    static final String KEY_ID = "_id";
    // The name and column index of each column in your database.
// These should be descriptive.
    public static final String KEY_RSS_NAME =
            "RSS_NAME_COLUMN";
    static final String KEY_NEWS_TITLE =
            "TITLE_COLUMN";
    static final String KEY_NEWS_LINK =
            "LINK_COLUMN";
    static final String KEY_NEWS_READ =
            "READ";
    static final String KEY_NEWS_PICTURE =
            "PICTURE_LINK";
    static final String KEY_NEWS_TIME =
            "TIME";
    static String[] result_columns = new String[] {
            KEY_ID, KEY_RSS_NAME, KEY_NEWS_TITLE, KEY_NEWS_LINK, KEY_NEWS_READ, KEY_NEWS_PICTURE, KEY_NEWS_TIME };
    // Specify the where clause that will limit our results.
    static String where = KEY_NEWS_TITLE + "= *" ;
    // Replace these with valid SQL statements as necessary.
    static String[] whereArgs = null;
    static String groupBy = null;
    static String having = null;
    static String order = null;
    static MyDBHelper myDBOpenHelper;
    static SQLiteDatabase db;
    private static Context contex;
    public static MySQLiteSingleton singleton;
    public MySQLiteSingleton() {
        //Let's first create a database
        if(null != contex){
            myDBOpenHelper = new MyDBHelper(contex,
            MyDBHelper.DATABASE_NAME, null,
            MyDBHelper.DATABASE_VERSION);
            db = myDBOpenHelper.getWritableDatabase();

        }

    }
    public static MySQLiteSingleton getInstance(){
        if(null == singleton) {
            singleton = new MySQLiteSingleton();
            return singleton;

        }else return singleton;

    }
    public static SQLiteDatabase getDataBase(Context _contex){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        return MySQLiteSingleton.getInstance().db;
    }
    public static Cursor getDefaultCursor(Context _contex, String feedLinkHash){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        cursor = MySQLiteSingleton.getInstance().db.query(feedLinkHash,
                result_columns, where,
                whereArgs, groupBy, having, order);
        return cursor;
    }
    public static Cursor getCursorByLink(Context _contex, String feedLinkHash, String newsLink){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;

            cursor = MySQLiteSingleton.getInstance().db.query(feedLinkHash,
                    result_columns, KEY_NEWS_LINK + "=" + newsLink,
                    whereArgs, groupBy, having, order);

        return cursor;
    }
    public static boolean makeNewDBRecord(Context _contex, String feedLinkHash, MyRssItem rssItem, long time){

        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        ContentValues values = new ContentValues();
        values.put(MyDBHelper.KEY_RSS_NAME, feedLinkHash);
        values.put(MyDBHelper.KEY_NEWS_TITLE, rssItem.mTitle);
        values.put(MyDBHelper.KEY_NEWS_LINK, rssItem.mNewsLink);
        values.put(MyDBHelper.KEY_NEWS_PICTURE, rssItem.mPictureLink);
        values.put(MyDBHelper.KEY_NEWS_TIME, time);
        Log.d(TAG, "Making db-insert query of " + values.valueSet().toString());
        MySQLiteSingleton.getInstance().db.beginTransaction();
        long result = -1;
        try {
            result = MySQLiteSingleton.getInstance().db.insert(myDBOpenHelper.DATABASE_TABLE, null, values);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        if(result == -1) {
            Log.d(TAG, "Couldn't insert into table");
            return false;
        }
        else return true;
    }
    public static boolean doesRecordExistByNewsLinkHash(Context _contex, String feedLinkHash, String newsLinkHash){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        cursor = MySQLiteSingleton.getInstance().db.query(feedLinkHash,
                result_columns, KEY_NEWS_LINK + "='" + newsLinkHash + "'",
                whereArgs, groupBy, having, order);
        if(cursor.moveToFirst()) return true;
        else return false;
    }
    public static boolean isNewsReadByNewsLinkHash(Context _contex, String feedLinkHash, String newsLinkHash){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        Log.d(TAG, "Quering " + KEY_RSS_NAME + "='" + feedLinkHash + "' AND " + KEY_NEWS_LINK + "='" + newsLinkHash + "'");
        cursor = MySQLiteSingleton.getInstance().db.query(myDBOpenHelper.DATABASE_TABLE,
                result_columns, KEY_RSS_NAME + "='" + feedLinkHash + "' AND " + KEY_NEWS_LINK + "='" + newsLinkHash + "'",
                whereArgs, groupBy, having, order);
        if(cursor.moveToFirst()) {
            Log.d(TAG, "isRead: " + cursor.getInt(4));
            if (cursor.getInt(4) == 1) return true;
            else return false;

        }
        else return false;
    }
    public static boolean markNewsAsReadByNewsLinkHash(Context _contex, String feedLinkHash, String newsLinkHash){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        cursor = MySQLiteSingleton.getInstance().db.query(myDBOpenHelper.DATABASE_TABLE,
                result_columns, KEY_NEWS_LINK + "= '" + newsLinkHash + "'",
                whereArgs, groupBy, having, order);
        if(cursor.moveToFirst()) {
            ContentValues values = new ContentValues();
            values.put(MyDBHelper.KEY_RSS_NAME, cursor.getString(1));
            values.put(MyDBHelper.KEY_NEWS_TITLE, cursor.getString(2));
            values.put(MyDBHelper.KEY_NEWS_LINK, cursor.getString(3));
            values.put(MyDBHelper.KEY_NEWS_READ, 1);
            values.put(MyDBHelper.KEY_NEWS_PICTURE, cursor.getString(5));
            values.put(MyDBHelper.KEY_NEWS_TIME, cursor.getLong(6));
            MySQLiteSingleton.getInstance().db.beginTransaction();
            long result = -1;
            try {
                result = MySQLiteSingleton.getInstance().db.replace(myDBOpenHelper.DATABASE_TABLE, null, values);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
            if(result == -1) {
                Log.d(TAG, "Couldn't mark news as read: some trouble");
                return false;
            }
            else {
                Log.d(TAG, "Successfully marked news as read");
                return true;
            }


        }
        else return false;
    }
    public static ArrayList<MyRssItem> getNewsOffline(Context _contex, String rssDBName, int feedRequiredNumber){
        if(contex != null && !contex.equals(_contex)){
            singleton = null;
            contex = _contex;
        }else contex = _contex;
        ArrayList<MyRssItem> rssList = new ArrayList<>();
//        order = KEY_NEWS_TIME + " DESC LIMIT " + String.valueOf(feedRequiredNumber);
        order = KEY_ID + " DESC LIMIT " + String.valueOf(feedRequiredNumber);
        cursor = MySQLiteSingleton.getInstance().db.query(myDBOpenHelper.DATABASE_TABLE,
                result_columns, KEY_RSS_NAME + "='" + rssDBName + "'",
                whereArgs, groupBy, having, order);
        order = null;
        if (cursor.moveToLast()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                rssList.add(new MyRssItem(cursor.getString(2), "", cursor.getString(3), cursor.getString(5), cursor.getString(1)));
                if(cursor.moveToPrevious()) continue; else break;
            }
        }
        return rssList;
    }
}
