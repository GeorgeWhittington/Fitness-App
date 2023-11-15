package com.foxden.fitnessapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Activity::class], version = 1)
abstract class FitnessAppDatabase : RoomDatabase() {

    abstract val activityDao: ActivityDao

    companion object {
        @Volatile
        private var INSTANCE: FitnessAppDatabase? = null

        fun getDatabase(context: Context): FitnessAppDatabase{
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitnessAppDatabase::class.java,
                    "fitness_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}