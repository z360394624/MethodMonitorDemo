package com.lucius.tec.performance.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.lucius.tec.performance.db.PerformanceDataConstants;
import com.lucius.tec.performance.db.entity.MethodPerformanceData;

@Dao
public interface MethodPerformanceDataDAO {

    @Insert
    public void insert(MethodPerformanceData data);

    @Query("SELECT * FROM "+ PerformanceDataConstants.TABLE_METHOD_PERFORMANCE_DATA + " WHERE " + PerformanceDataConstants.COLUMN_METHOD_DEVICE_ID + " = :deviceId")
    public void queryByDeviceId(long deviceId);

    @Query("SELECT * FROM "+ PerformanceDataConstants.TABLE_METHOD_PERFORMANCE_DATA)
    public void queryAll(long deviceId);

    @Delete
    public void delete(MethodPerformanceData data);


}
