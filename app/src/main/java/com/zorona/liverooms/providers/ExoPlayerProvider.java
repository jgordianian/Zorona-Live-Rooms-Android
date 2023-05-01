package com.zorona.liverooms.providers;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.vaibhavpandey.katora.contracts.MutableContainer;
import com.vaibhavpandey.katora.contracts.Provider;

public class ExoPlayerProvider implements Provider {

    private static final int CACHE_COUNT = 999;
    private static final long CACHE_SIZE = 512 /* MB */ * 1024 /* KB */ * 1024 /* B */;

    private final Context mContext;

    public ExoPlayerProvider(Context context) {
        mContext = context;
    }

    @Override
    public void provide(MutableContainer container) {
        container.singleton(
                HttpProxyCacheServer.class,
                c -> new HttpProxyCacheServer.Builder(mContext)
                        .maxCacheSize(CACHE_SIZE)
                        .maxCacheFilesCount(CACHE_COUNT)
                        .build()
        );
    }
}
