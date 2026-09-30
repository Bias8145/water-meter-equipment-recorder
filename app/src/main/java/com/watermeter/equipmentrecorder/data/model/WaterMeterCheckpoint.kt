package com.watermeter.equipmentrecorder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "water_meter_checkpoints",
    foreignKeys = [
        ForeignKey(
            entity = WaterMeterTemplate::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["templateId"])]
)
data class WaterMeterCheckpoint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val templateId: Long,
    val isActive: Boolean = true
)