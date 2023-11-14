package com.foxden.fitnessapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title") val title: String?,
    @ColumnInfo(name = "type") val type: Int?,
    @ColumnInfo(name = "start_time") val startTime: Int?,
    @ColumnInfo(name = "duration") val duration: Float?,
    @ColumnInfo(name = "kcal_burned") val kcalBurned: Float?,
)