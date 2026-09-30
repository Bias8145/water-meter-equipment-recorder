package com.watermeter.equipmentrecorder.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.watermeter.equipmentrecorder.data.model.PhotoEvidence

@Dao
interface PhotoEvidenceDao {
    @Query("SELECT * FROM photo_evidence WHERE id = :photoId")
    fun getPhotoEvidenceById(photoId: Long): Flow<PhotoEvidence?>

    @Insert
    @Update
    suspend fun updatePhotoEvidence(photo: PhotoEvidence): Int
    suspend fun insertPhotoEvidence(photo: PhotoEvidence): Long
}