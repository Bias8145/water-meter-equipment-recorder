package com.watermeter.equipmentrecorder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.watermeter.equipmentrecorder.data.model.Area
import com.watermeter.equipmentrecorder.data.model.PhotoEvidence
import com.watermeter.equipmentrecorder.data.model.WaterMeterCheckpoint
import com.watermeter.equipmentrecorder.data.model.WaterMeterReading
import com.watermeter.equipmentrecorder.data.model.WaterMeterTemplate

@Database(entities = [Area::class, WaterMeterTemplate::class, WaterMeterCheckpoint::class, WaterMeterReading::class, PhotoEvidence::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
    abstract fun waterMeterTemplateDao(): WaterMeterTemplateDao
    abstract fun waterMeterCheckpointDao(): WaterMeterCheckpointDao
    abstract fun waterMeterReadingDao(): WaterMeterReadingDao
    abstract fun photoEvidenceDao(): PhotoEvidenceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "water_meter_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}