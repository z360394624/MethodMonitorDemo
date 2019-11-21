package com.lucius.tec.performance.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.lucius.tec.performance.db.PerformanceDataConstants;

@Entity(tableName = PerformanceDataConstants.TABLE_METHOD_PERFORMANCE_DATA)
public class Device {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String brand;

    public int sdk_level;

    public String system_version;

    public String device_type;

}
