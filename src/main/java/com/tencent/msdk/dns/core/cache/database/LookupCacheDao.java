package com.tencent.msdk.dns.core.cache.database;

//import androidx.room.Dao;
//import androidx.room.Delete;
//import androidx.room.Insert;
//import androidx.room.OnConflictStrategy;
//import androidx.room.Query;
//import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LookupCacheDao {
    @Query("Select * from lookupcache")
    List<LookupCache> getAll();

    @Query("Select * from lookupcache where hostname = :hostname")
    LookupCache get(String hostname);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertLookupCache(LookupCache lookupCache);

    @Update
    void updateLookupCache(LookupCache lookupCache);

    @Delete
    void deleteLookupCaches(ArrayList<LookupCache> lookupCaches);

    @Query("delete from lookupcache where hostname = :hostname")
    void delete(String hostname);

    @Query("delete from lookupcache")
    void clear();
}
