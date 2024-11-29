package com.app.liverooms;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {Draft.class}, version = 3, exportSchema = false)
public abstract class ClientDatabase extends RoomDatabase {

    public abstract DraftDao drafts();
}
