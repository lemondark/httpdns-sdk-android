package com.tencent.msdk.dns.core.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tencent.msdk.dns.base.log.DnsLog;
import com.tencent.msdk.dns.core.cache.database.LookupCache;
import com.tencent.msdk.dns.core.cache.database.LookupResultConverter;

import java.util.ArrayList;
import java.util.List;

public class CacheDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "LookupCache.db";

    private SQLiteDatabase mDb;

    private final Object mLock = new Object();

    static class DB {
        static final String TABLE_NAME = "lookupDB";

        static final String HOST = "host";

        static final String RESULT = "result";

        static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + DB.TABLE_NAME + " ("
                + HOST + " TEXT PRIMARY KEY,"
                + RESULT + " TEXT)";

        static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public CacheDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private SQLiteDatabase getDb() {
        if (mDb == null) {
            try{
                mDb = getWritableDatabase();
            } catch (Exception e){

            }
        }
        return mDb;
    }

    public List<LookupCache> readFromDb() {
        synchronized (mLock) {
            ArrayList<LookupCache> lists = new ArrayList<>();

            SQLiteDatabase db = null;
            Cursor cursor = null;

            try {
                db = getDb();
                cursor = db.query(DB.TABLE_NAME, null, null, null, null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    do {
                        DnsLog.d("testt----" + cursor.getString(cursor.getColumnIndex(DB.HOST))+ "result ---" + LookupResultConverter.toLookupResult(cursor.getBlob(cursor.getColumnIndex(DB.RESULT))));
                        lists.add(new LookupCache(
                                cursor.getString(cursor.getColumnIndex(DB.HOST)),
                                LookupResultConverter.toLookupResult(cursor.getBlob(cursor.getColumnIndex(DB.RESULT)))
                        ));
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                DnsLog.e("read from db fail " + e);
            } finally {
                try {
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Exception ignored) {
                }
            }
            return lists;
        }
    }

    public void insertLookupCache(LookupCache lookupCache) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = getDb();
                db.beginTransaction();
                ContentValues cv = new ContentValues();
                cv.put(DB.HOST, lookupCache.hostname);
                cv.put(DB.RESULT, LookupResultConverter.fromLookupResult(lookupCache.lookupResult));
                db.insert(DB.TABLE_NAME, null, cv);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                DnsLog.e("insert lookupCache fail " + e);
            } finally {
                if (db != null) {
                    try {
                        db.endTransaction();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public void deleteLookupCaches(ArrayList<LookupCache> lookupCaches) {
        for(LookupCache lookupCache: lookupCaches) {
            delete(lookupCache.hostname);
        }
    }

    public void delete(String hostname) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = getDb();
                db.beginTransaction();
                db.delete(DB.TABLE_NAME, DB.HOST + "= ? ", new String[] {hostname});
                db.setTransactionSuccessful();
            } catch (Exception e) {
                DnsLog.e("delete by hostname fail" + e);
            } finally {
                if (db != null) {
                    try {
                        db.endTransaction();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    public void clear() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = getDb();
                db.beginTransaction();
                db.delete(DB.TABLE_NAME, null, null);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                DnsLog.e("clear cache fail" + e);
            } finally {
                if (db != null) {
                    try {
                        db.endTransaction();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DB.SQL_CREATE_ENTRIES);
        }catch (Exception e) {
            DnsLog.e("create db fail " + e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            try {
                db.execSQL(DB.SQL_DELETE_ENTRIES);
                onCreate(db);
            } catch (Exception e) {
                DnsLog.e("upgrade db fail " + e);
            }
        }
    }
}
