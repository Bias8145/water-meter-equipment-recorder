package com.watermeter.equipmentrecorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.watermeter.equipmentrecorder.data.model.WaterMeterTemplate
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterMeterTemplateDao {
    @Query("SELECT * FROM water_meter_templates WHERE isActive = 1 ORDER BY name")
    fun getAllActiveTemplates(): Flow<List<WaterMeterTemplate>>

    @Query("SELECT * FROM water_meter_templates WHERE id = :templateId")
    fun getTemplateById(templateId: Long): Flow<WaterMeterTemplate?>

    @Insert
    suspend fun insertTemplate(template: WaterMeterTemplate): Long

    @Update
    suspend fun updateTemplate(template: WaterMeterTemplate): Int
}