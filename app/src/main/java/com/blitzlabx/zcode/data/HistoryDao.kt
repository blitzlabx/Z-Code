package com.blitzlabx.zcode.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE type = :type ORDER BY timestamp DESC")
    fun getByType(type: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE isSaved = 1 ORDER BY timestamp DESC")
    fun getSaved(): Flow<List<HistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: HistoryEntity): Long

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM history")
    suspend fun clearAll()

    @Query("UPDATE history SET isSaved = :saved WHERE id = :id")
    suspend fun setSaved(id: Long, saved: Boolean)

    @Query("SELECT COUNT(*) FROM history")
    suspend fun count(): Int
}
