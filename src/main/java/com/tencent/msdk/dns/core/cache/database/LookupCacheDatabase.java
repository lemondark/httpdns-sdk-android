package com.tencent.msdk.dns.core.cache.database;

import android.content.Context;

//import androidx.room.Database;
//import androidx.room.Room;
import androidx.room.RoomDatabase;
//import androidx.room.TypeConverters;

import com.tencent.msdk.dns.base.log.DnsLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



@Database(entities = {LookupCache.class}, version = 2, exportSchema = false)
@TypeConverters(LookupResultConverter.class)
public abstract class LookupCacheDatabase extends RoomDatabase {
    private static final String ROOM_DATABASE_NAME = LookupCacheDatabase.class.getSimpleName();
    private Object ttt = RoomDatabaseImpl.getRoomDatabaseClass();
    private static final String ROOM_CLASS_NAME = "androidx.room.Room";

    private static final String DB_NAME = "lookup_result_db";

    private static LookupCacheDatabase instance;

    public abstract LookupCacheDao lookupCacheDao();

    public static synchronized LookupCacheDatabase getInstance(Context context) {
        if (instance == null) {
            creat(context);
        }
        return instance;
    }

    public static void creat(Context context) {
        try {
            Class UserAction = Class.forName(ROOM_CLASS_NAME);
            DnsLog.d("room database ---" + ROOM_DATABASE_NAME + "---" + UserAction);
            Method BuilderMethod = UserAction.getMethod("databaseBuilder", Context.class, Class.class, String.class);
            Method Fall = Builder.class.getMethod("fallbackToDestructiveMigration");
            Method Build = Builder.class.getMethod("build");
            Builder  builder = (Builder) BuilderMethod.invoke(null, context.getApplicationContext(), LookupCacheDatabase.class, DB_NAME);
            Fall.invoke(builder);
            instance = (LookupCacheDatabase) Build.invoke(builder);
            DnsLog.d("room instance --- " + instance);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        instance = Room.databaseBuilder(context.getApplicationContext(), LookupCacheDatabase.class, DB_NAME)
//                .fallbackToDestructiveMigration()
//                .build();
    }

//    Class RoomDatabase = Class.forName("androidx.room.RoomDatabase");
}
