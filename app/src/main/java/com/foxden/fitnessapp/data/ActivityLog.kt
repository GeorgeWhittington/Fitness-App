package com.foxden.fitnessapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class ActivityLog {
    var id : Int = 0
    var name : String = "Untitled Activity"
    var distance: Float = 0.0f
    var calories: Int = 0
    var time: Int = 0
    var duration: Int = 0
}

object ActivityLogDAO : DAO("activity_log", listOf(
    ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
    ColumnDesc("name", "TEXT"),
    ColumnDesc("distance", "REAL"),
    ColumnDesc("calories", "INTEGER"),
    ColumnDesc("time", "INTEGER"),
    ColumnDesc("duration", "INTEGER"),
)) {

    private fun cursorToObject(cursor: android.database.Cursor) : ActivityLog {
        val ret = ActivityLog()
        ret.id = cursor.getInt(0)
        ret.name = cursor.getString(1)
        ret.distance = cursor.getFloat(2)
        ret.calories = cursor.getInt(3)
        ret.time = cursor.getInt(4)
        ret.duration = cursor.getInt(5)
        return ret
    }

    fun fetchAll(db: SQLiteDatabase?) : List<ActivityLog> {
        val ret: MutableList<ActivityLog> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM ${tableName}", null)

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }
    fun insert(db: SQLiteDatabase?, activityLog: ActivityLog) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, activityLog.name)
        contentValues.put(tableColumns[2].name, activityLog.distance)
        contentValues.put(tableColumns[3].name, activityLog.calories)
        contentValues.put(tableColumns[4].name, activityLog.time)
        contentValues.put(tableColumns[5].name, activityLog.duration)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }
}