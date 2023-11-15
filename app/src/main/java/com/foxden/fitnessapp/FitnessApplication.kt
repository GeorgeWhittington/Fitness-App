package com.foxden.fitnessapp

import android.app.Application
import com.foxden.fitnessapp.data.ActivityRepository
import com.foxden.fitnessapp.data.FitnessAppDatabase

class FitnessApplication : Application() {
    val database by lazy { FitnessAppDatabase.getDatabase(this) }
    val activityRepository by lazy { ActivityRepository(database.activityDao) }
}