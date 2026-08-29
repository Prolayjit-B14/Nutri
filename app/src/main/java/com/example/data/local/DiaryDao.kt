package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary_entries WHERE date = :date ORDER BY loggedAtTimestamp ASC")
    fun getEntriesForDate(date: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries ORDER BY loggedAtTimestamp DESC LIMIT 20")
    fun getRecentEntries(): Flow<List<DiaryEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: DiaryEntryEntity): Long

    @Update
    suspend fun updateEntry(entry: DiaryEntryEntity)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("DELETE FROM diary_entries WHERE date = :date")
    suspend fun clearDateEntries(date: String)
}
