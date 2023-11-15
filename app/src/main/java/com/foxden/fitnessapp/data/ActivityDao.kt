package com.foxden.fitnessapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * from activity")
    fun getAll(): Flow<List<Activity>>

    @Insert
    suspend fun insertAll(vararg activities: Activity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activity: Activity)

    @Delete
    suspend fun deleteSingle(activity: Activity)
}