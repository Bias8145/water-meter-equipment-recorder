package com.watermeter.equipmentrecorder.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "water_meter_readings",
    foreignKeys = [
        ForeignKey(
            entity = WaterMeterCheckpoint::class,
            parentColumns = ["id"],
            childColumns = ["checkpointId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["checkpointId", "readingTimestamp"]),
        Index(value = ["readingTimestamp"])
    ]
)
data class WaterMeterReading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val checkpointId: Long,
    val previousReading: Long? = null, // nullable if no previous
    val currentReading: Long,
    val usage: Long? = null, // calculated: current - previous, null if no previous
    val readingTimestamp: Long = System.currentTimeMillis(), // epoch ms
    val inputMethod: String = "MANUAL", // MANUAL, CAMERA
    val ocrConfidence: Float? = null, // if CAMERA
    val photoId: Long? = null, // reference to PhotoEvidence
    val validationStatus: String = "PENDING", // PENDING, VALID, WARNING (if current < previous)
    val operatorId: Long = 0, // foreign key to User (we'll simplify for now)
    val notes: String? = null
)