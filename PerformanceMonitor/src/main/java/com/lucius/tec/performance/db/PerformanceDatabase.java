package com.lucius.tec.performance.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.lucius.tec.performance.db.entity.MethodPerformanceData;



@Database(entities = {MethodPerformanceData.class}, version = 2, exportSchema = false)
public abstract class PerformanceDatabase extends RoomDatabase {

    private static final Object sLock = new Object();

    private static PerformanceDatabase INSTANCE;

    public static PerformanceDatabase getDatabase(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        PerformanceDatabase.class,
                        "PerformanceDB")
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }

        return INSTANCE;
    }
}
