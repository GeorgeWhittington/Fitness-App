package com.foxden.fitnessapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class ActivityType {
    var id : Int = 0
    var name : String = "Untitled Activity Type"
    var iconId : Int = 0
    var gpsEnabled : Boolean = true
    var setsEnabled : Boolean = false
}

object ActivityTypeDAO : DAO("activity_type", listOf(
    ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
    ColumnDesc("name", "TEXT", "UNIQUE NOT NULL"),
    ColumnDesc("icon_id", "INTEGER"),
    ColumnDesc("gps_enabled", "BOOLEAN"),
    ColumnDesc("sets_enabled", "BOOLEAN")
)) {

    private fun cursorToObject(cursor: android.database.Cursor) : ActivityType {
        val ret = ActivityType()
        ret.id = cursor.getInt(0)
        ret.name = cursor.getString(1)
        ret.iconId = cursor.getInt(2)
        ret.gpsEnabled = cursor.getInt(3) == 1
        ret.setsEnabled = cursor.getInt(4) == 1
        return ret
    }

    fun fetchOne(db: SQLiteDatabase?, id: Int) : ActivityType? {
        var ret: ActivityType? = null
        val cursor = db?.rawQuery("SELECT * FROM $tableName WHERE id = ?", arrayOf(id.toString()))

        if (cursor!!.moveToFirst()) {
            ret = cursorToObject(cursor)
        }
        cursor.close()
        return ret
    }

    fun fetchAll(db: SQLiteDatabase?) : List<ActivityType> {
        val ret: MutableList<ActivityType> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM $tableName", null)

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }
    fun insert(db: SQLiteDatabase?, activityType: ActivityType) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, activityType.name)
        contentValues.put(tableColumns[2].name, activityType.iconId)
        contentValues.put(tableColumns[3].name, activityType.gpsEnabled)
        contentValues.put(tableColumns[4].name, activityType.setsEnabled)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }
}