package com.watermeter.equipmentrecorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.watermeter.equipmentrecorder.data.model.WaterMeterCheckpoint
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterMeterCheckpointDao {
    @Query("SELECT * FROM water_meter_checkpoints WHERE templateId = :templateId AND isActive = 1 ORDER BY name")
    fun getCheckpointsByTemplateId(templateId: Long): Flow<List<WaterMeterCheckpoint>>

    @Query("SELECT * FROM water_meter_checkpoints WHERE id = :checkpointId")
    fun getCheckpointById(checkpointId: Long): Flow<WaterMeterCheckpoint?>

    @Insert
    suspend fun insertCheckpoint(checkpoint: WaterMeterCheckpoint): Long

    @Update
    suspend fun updateCheckpoint(checkpoint: WaterMeterCheckpoint): Int
}