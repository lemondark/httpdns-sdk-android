package com.tencent.msdk.dns.core.cache.database;

public class RoomDatabaseImpl {

    public static Class RoomDatabase = getRoomDatabaseClass();

    public static Class<?> getRoomDatabaseClass() {
        try {
            return Class.forName("androidx.room.RoomDatabase");
        } catch (Throwable t) {
            return null;
        }
    }
}
