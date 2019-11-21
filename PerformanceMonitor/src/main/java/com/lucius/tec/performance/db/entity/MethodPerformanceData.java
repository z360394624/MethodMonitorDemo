package com.lucius.tec.performance.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.lucius.tec.performance.db.PerformanceDataConstants;

@Entity(tableName = PerformanceDataConstants.TABLE_METHOD_PERFORMANCE_DATA)
public class MethodPerformanceData {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String methodName;

    public long performTime;

    public int sdk_level;

    public String brand;

    public String system_version;
}
