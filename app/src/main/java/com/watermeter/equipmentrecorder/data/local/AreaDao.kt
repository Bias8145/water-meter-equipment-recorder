package com.watermeter.equipmentrecorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.watermeter.equipmentrecorder.data.model.Area
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas WHERE isActive = 1 ORDER BY name")
    fun getAllActiveAreas(): Flow<List<Area>>

    @Query("SELECT * FROM areas WHERE id = :areaId")
    fun getAreaById(areaId: Long): Flow<Area?>

    @Insert
    suspend fun insertArea(area: Area): Long

    @Update
    suspend fun updateArea(area: Area): Int

    // For simplicity, we'll not implement delete; we'll use soft delete via isActive
}