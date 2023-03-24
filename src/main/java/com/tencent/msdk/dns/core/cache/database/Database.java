package com.tencent.msdk.dns.core.cache.database;

public @interface Database {
    Class<LookupCache>[] entities();
    int version() default  0;
    boolean exportSchema();
}
