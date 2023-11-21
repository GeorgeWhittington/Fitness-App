package com.foxden.fitnessapp.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase




class Activity {

    var id : Int = 0
    var title : String = ""

    companion object {

        private val TABLE_NAME = "activities"
        private val COLUMN_ID = "id"
        private val COLUMN_TITLE = "title"

        fun onCreate(db: SQLiteDatabase?) {
            val query = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_TITLE VARCHAR(256))"
            db?.execSQL(query)
        }

        // TODO: OnUpgrade

        fun insert(db: SQLiteDatabase?, activity: Activity) : Boolean {
            val contentValues = ContentValues()
            contentValues.put(COLUMN_TITLE, activity.title)
            val result = db?.insert(TABLE_NAME, null, contentValues)
            db?.close()
            return (result != (0).toLong())
        }

        fun getAll(db: SQLiteDatabase?) : List<Activity> {
            val ret: MutableList<Activity> = ArrayList()
            val queryCursor = db?.rawQuery("SELECT * FROM $TABLE_NAME", null)

            if (queryCursor!!.moveToFirst()) {
                do {
                    val activity = Activity()
                    activity.id = queryCursor.getInt(0)
                    activity.title = queryCursor.getString(1)
                    ret.add(activity)
                }
                while (queryCursor!!.moveToNext())
            }
            queryCursor.close()
            return ret
        }

        fun deleteAll(db: SQLiteDatabase?) {
            val query = "DELETE FROM $TABLE_NAME"
            db?.execSQL(query)
        }
    }

}