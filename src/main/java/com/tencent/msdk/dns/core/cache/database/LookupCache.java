package com.tencent.msdk.dns.core.cache.database;

import androidx.annotation.NonNull;

import com.tencent.msdk.dns.core.LookupResult;
import com.tencent.msdk.dns.core.rest.share.AbsRestDns;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Entity
public class LookupCache {
    @PrimaryKey
    @NonNull
    public String hostname;

    @ColumnInfo
    public LookupResult lookupResult;

    public LookupCache(String mHostname, LookupResult mLookupResult) {
        this.hostname = mHostname;
        this.lookupResult = mLookupResult;
    }

    public LookupCache() {
    }

    public boolean isExpired() {
        AbsRestDns.Statistics stat = (AbsRestDns.Statistics) lookupResult.stat;
        if (stat != null) {
            return System.currentTimeMillis() > stat.expiredTime;
        }
        return true;
    }
}

