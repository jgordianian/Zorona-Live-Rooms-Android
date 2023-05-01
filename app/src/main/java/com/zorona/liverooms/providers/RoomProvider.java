package com.zorona.liverooms.providers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.zorona.liverooms.ClientDatabase;
import com.vaibhavpandey.katora.contracts.MutableContainer;
import com.vaibhavpandey.katora.contracts.Provider;


public class RoomProvider implements Provider {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE drafts ADD COLUMN location TEXT DEFAULT NULL");
            database.execSQL("ALTER TABLE drafts ADD COLUMN latitude REAL DEFAULT NULL");
            database.execSQL("ALTER TABLE drafts ADD COLUMN longitude REAL DEFAULT NULL");
        }
    };

    private final Context mContext;

    public RoomProvider(Context context) {
        mContext = context;
    }

    @Override
    public void provide(MutableContainer container) {
        container.singleton(ClientDatabase.class, c ->
                Room.databaseBuilder(mContext, ClientDatabase.class, "client")
                        .allowMainThreadQueries()
                        .addMigrations(MIGRATION_1_2)
                        .build()
        );
    }
}
