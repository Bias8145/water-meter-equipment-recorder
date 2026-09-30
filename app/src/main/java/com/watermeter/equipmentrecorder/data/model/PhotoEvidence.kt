package com.watermeter.equipmentrecorder.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_evidence")
data class PhotoEvidence(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String, // path to the saved image file
    val takenAt: Long = System.currentTimeMillis()
)