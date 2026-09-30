package com.watermeter.equipmentrecorder.data.repository

import com.watermeter.equipmentrecorder.data.local.AppDatabase
import com.watermeter.equipmentrecorder.data.model.WaterMeterCheckpoint
import com.watermeter.equipmentrecorder.data.model.WaterMeterReading
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WaterMeterRepository(private val db: AppDatabase) {

    val checkpointDao = db.waterMeterCheckpointDao()
    val readingDao = db.waterMeterReadingDao()
    val photoDao = db.photoEvidenceDao()

    fun getCheckpointsByTemplateId(templateId: Long): Flow<List<WaterMeterCheckpoint>> =
        checkpointDao.getCheckpointsByTemplateId(templateId)

    fun getLatestReadingByCheckpointId(checkpointId: Long): Flow<WaterMeterReading?> =
        readingDao.getLatestReadingByCheckpointId(checkpointId)

    fun getReadingBeforeTimestamp(checkpointId: Long, beforeTimestamp: Long): Flow<WaterMeterReading?> =
        readingDao.getReadingBeforeTimestamp(checkpointId, beforeTimestamp)

    suspend fun saveReading(reading: WaterMeterReading): Long =
        readingDao.insertReading(reading)

    suspend fun savePhotoEvidence(photo: com.watermeter.equipmentrecorder.data.model.PhotoEvidence): Long =
        photoDao.insertPhotoEvidence(photo)
}