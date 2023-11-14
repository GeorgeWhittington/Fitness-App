package com.foxden.fitnessapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {
    @Query("SELECT * from activity")
    fun getAll(): List<Activity>

    @Insert
    fun insertAll(vararg activities: Activity)
}