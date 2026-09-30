package com.watermeter.equipmentrecorder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_meter_templates")
data class WaterMeterTemplate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val areaId: Long,
    val intervalInHours: Long = 1, // default 1 hour
    val isActive: Boolean = true,
    val version: Long = 1
)