package com.watermeter.equipmentrecorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.watermeter.equipmentrecorder.data.model.WaterMeterReading
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterMeterReadingDao {
    @Query("SELECT * FROM water_meter_readings WHERE checkpointId = :checkpointId ORDER BY readingTimestamp DESC")
    fun getReadingsByCheckpointId(checkpointId: Long): Flow<List<WaterMeterReading>>

    @Query("SELECT * FROM water_meter_readings WHERE checkpointId = :checkpointId ORDER BY readingTimestamp DESC LIMIT 1")
    fun getLatestReadingByCheckpointId(checkpointId: Long): Flow<WaterMeterReading?>

    @Query("SELECT * FROM water_meter_readings WHERE checkpointId = :checkpointId AND readingTimestamp < :beforeTimestamp ORDER BY readingTimestamp DESC LIMIT 1")
    fun getReadingBeforeTimestamp(checkpointId: Long, beforeTimestamp: Long): Flow<WaterMeterReading?>

    @Insert
    suspend fun insertReading(reading: WaterMeterReading): Long

    @Update
    suspend fun updateReading(reading: WaterMeterReading): Int
}