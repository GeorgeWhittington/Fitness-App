package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

/* Activity Type Entity */
data class ActivityType(
    var id : Int = 0,
    var name : String = "Untitled Activity Type",
    var iconId : Int = 0,
    var gpsEnabled : Boolean = true,
    var setsEnabled : Boolean = false,
)

/*
    ActivityTypeDAO
    Represents / interfaces with ActivityType database table
*/
object ActivityTypeDAO : DAO("activity_type", listOf(
    ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
    ColumnDesc("name", "TEXT", "UNIQUE NOT NULL"),
    ColumnDesc("icon_id", "INTEGER"),
    ColumnDesc("gps_enabled", "BOOLEAN"),
    ColumnDesc("sets_enabled", "BOOLEAN")
)) {


    /* Returns ActivityType from SQLite cursor */
    @SuppressLint("Range")
    private fun cursorToObject(cursor: android.database.Cursor) : ActivityType {
        val ret = ActivityType()
        ret.id = cursor.getInt(cursor.getColumnIndex("id"))
        ret.name = cursor.getString(cursor.getColumnIndex("name"))
        ret.iconId = cursor.getInt(cursor.getColumnIndex("icon_id"))
        ret.gpsEnabled = cursor.getInt(cursor.getColumnIndex("gps_enabled")) == 1
        ret.setsEnabled = cursor.getInt(cursor.getColumnIndex("sets_enabled")) == 1
        return ret
    }

    /* Get a single ActivityType from the database by id */
    fun fetchOne(db: SQLiteDatabase?, id: Int) : ActivityType? {
        var ret: ActivityType? = null
        val cursor = db?.rawQuery("SELECT * FROM $tableName WHERE id = ?", arrayOf(id.toString()))

        if (cursor!!.moveToFirst()) {
            ret = cursorToObject(cursor)
        }
        cursor.close()
        return ret
    }

    /* Get all activity types from the database */
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

    /* Insert a new activity type into the database */
    fun insert(db: SQLiteDatabase?, activityType: ActivityType) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, activityType.name)
        contentValues.put(tableColumns[2].name, activityType.iconId)
        contentValues.put(tableColumns[3].name, activityType.gpsEnabled)
        contentValues.put(tableColumns[4].name, activityType.setsEnabled)
        val result = db?.insert(tableName, null, contentValues)
        return (result != (0).toLong())
    }
}