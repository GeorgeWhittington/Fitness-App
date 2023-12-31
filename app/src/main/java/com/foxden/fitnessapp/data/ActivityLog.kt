package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class ActivityLog(
    var id : Int = 0,
    var title : String = "Untitled Activity",
    var activityTypeId: Int = 0,
    var notes : String = "",
    var startTime: Long = 0,         // start time using unix timestamp
    var duration: Int = 0,          // duration, in seconds
    var distance: Float = 0.0f,
    var calories: Int = 0,
)

object ActivityLogDAO : DAO(
    "activity_log",
    listOf(
        ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
        ColumnDesc("title", "TEXT"),
        ColumnDesc("activity_type_id", "INTEGER", "NOT NULL"),
        ColumnDesc("notes", "TEXT"),
        ColumnDesc("start_time", "INTEGER"),
        ColumnDesc("duration", "INTEGER"),
        ColumnDesc("distance", "REAL"),
        ColumnDesc("calories", "INTEGER"),
    )
) {
    @SuppressLint("Range")
    private fun cursorToObject(cursor: android.database.Cursor, db: SQLiteDatabase) : ActivityLog {
        val ret = ActivityLog()
        ret.id = cursor.getInt(cursor.getColumnIndex("id"))
        ret.title = cursor.getString(cursor.getColumnIndex("title"))
        ret.activityTypeId = cursor.getInt(cursor.getColumnIndex("activity_type_id"))
        ret.notes = cursor.getString(cursor.getColumnIndex("notes"))
        ret.startTime = cursor.getLong(cursor.getColumnIndex("start_time"))
        ret.duration = cursor.getInt(cursor.getColumnIndex("duration"))
        ret.distance = cursor.getFloat(cursor.getColumnIndex("distance"))
        ret.calories = cursor.getInt(cursor.getColumnIndex("calories"))
        return ret
    }

    fun fetchAll(db: SQLiteDatabase?) : List<ActivityLog> {
        val ret: MutableList<ActivityLog> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM $tableName", null)

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor, db)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBetween(db: SQLiteDatabase?, startTime: LocalDateTime, endTime: LocalDateTime) : List<ActivityLog> {
        assert(startTime < endTime)
        val endTimeDur: Long = endTime.toEpochSecond(OffsetDateTime.now().offset) - startTime.toEpochSecond(OffsetDateTime.now().offset)
        val ret: MutableList<ActivityLog> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM $tableName WHERE start_time >= ? AND duration <= ?",
            arrayOf( startTime.format(DateTimeFormatter.BASIC_ISO_DATE), endTimeDur.toString() ))

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor, db)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }

    fun insert(db: SQLiteDatabase?, activityLog: ActivityLog) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, activityLog.title)
        contentValues.put(tableColumns[2].name, activityLog.activityTypeId)
        contentValues.put(tableColumns[3].name, activityLog.notes)
        contentValues.put(tableColumns[4].name, activityLog.startTime)
        contentValues.put(tableColumns[5].name, activityLog.duration)
        contentValues.put(tableColumns[6].name, activityLog.distance)
        contentValues.put(tableColumns[7].name, activityLog.calories)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }

    fun delete(db: SQLiteDatabase?, activityLog: ActivityLog): Boolean {
        val affected = db?.delete(tableName, "id = ?", arrayOf(activityLog.id.toString()))
        if (affected != null) {
            return affected > 0
        }
        return false
    }
}