package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZoneId
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException

/*  ActivityLog entity
    Represents an activity that the user can log
*/
data class ActivityLog(
    var id : Int = 0,
    var title : String = "Untitled Activity",
    var activityTypeId: Int = 0,
    var notes : String = "",
    var startTime: Long = 0,         // start time using unix timestamp
    var duration: Int = 0,          // duration, in seconds
    var distance: Float = 0.0f,
    var calories: Int = 0,
    var images: MutableList<String> = mutableListOf<String>(),
    var gpx: String = "",
)

/*  ActivityLogDAO 
    Represents / Interfaces with the ActivityLog table
*/
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
        ColumnDesc("images", "TEXT"),
        ColumnDesc("gpx", "TEXT"),
    )
) {

    /* Returns ActivityLog from SQLite cursor */
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

        var imageString = cursor.getString(cursor.getColumnIndex("images"))

        try {
            var loopIdx = 0
            var imageArr = JSONArray("{$imageString}")
            while (loopIdx < imageArr.length()) {
                ret.images.add(imageArr.getString(loopIdx))
            }
        } catch (e: JSONException) {
            // handler
        } finally {
            // optional finally block
        }

        ret.gpx = cursor.getString(cursor.getColumnIndex("gpx"))

        return ret
    }

    /* Get all activity logs from the database */
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

    /* Get all activity logs between specified date range */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchBetween(db: SQLiteDatabase?, startTime: LocalDateTime, endTime: LocalDateTime): List<ActivityLog> {
        assert(startTime < endTime)


        val startTimestamp = startTime.atZone(ZoneId.systemDefault()).toEpochSecond()
        val endTimestamp = endTime.atZone(ZoneId.systemDefault()).toEpochSecond()

        val ret: MutableList<ActivityLog> = ArrayList()

            val queryCursor = db?.rawQuery(
                "SELECT * FROM $tableName WHERE start_time >= ? AND start_time <= ?",
                arrayOf(startTimestamp.toString(), endTimestamp.toString())
            )

            if (queryCursor != null && queryCursor.moveToFirst()) {
                do {
                    ret.add(cursorToObject(queryCursor, db))
                } while (queryCursor.moveToNext())
            }
            queryCursor?.close()


        return ret
    }

    /* Insert a single activity log into the database */
    fun insert(db: SQLiteDatabase?, activityLog: ActivityLog) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, activityLog.title)
        contentValues.put(tableColumns[2].name, activityLog.activityTypeId)
        contentValues.put(tableColumns[3].name, activityLog.notes)
        contentValues.put(tableColumns[4].name, activityLog.startTime)
        contentValues.put(tableColumns[5].name, activityLog.duration)
        contentValues.put(tableColumns[6].name, activityLog.distance)
        contentValues.put(tableColumns[7].name, activityLog.calories)

        if (activityLog.images.size > 0) {
            val imageArray = JSONArray()
            activityLog.images.forEach { imageArray.put(it) }
            contentValues.put(tableColumns[8].name, imageArray.toString())
        } else {
            contentValues.put(tableColumns[8].name, "")
        }

        contentValues.put(tableColumns[9].name, activityLog.gpx)

        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }

    /* Delete an activity log from database */
    fun delete(db: SQLiteDatabase?, activityLog: ActivityLog): Boolean {
        val affected = db?.delete(tableName, "id = ?", arrayOf(activityLog.id.toString()))
        if (affected != null) {
            return affected > 0
        }
        return false
    }
}