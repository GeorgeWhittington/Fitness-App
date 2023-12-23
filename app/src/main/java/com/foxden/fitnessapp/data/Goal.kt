package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

enum class GoalFrequency
{
    DAILY,
    WEEKLY,
    MONTHLY,
}

enum class GoalType
{
    STEPS,
    DISTANCE,
    SETS,
}

data class Goal (
    var id : Int = 0,
    var activityTypeId: Int = 0,
    var frequency: GoalFrequency,
    var type: GoalType,
)

object GoalDAO : DAO(
    "goals",
    listOf(
        ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
        ColumnDesc("activity_type_id", "INTEGER", "NOT NULL"),
        ColumnDesc("frequency", "INTEGER"),
        ColumnDesc("type", "INTEGER"),
    )
) {

    @SuppressLint("Range")
    private fun cursorToObject(cursor: android.database.Cursor, db: SQLiteDatabase) : Goal {
        val ret = Goal(
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getInt(cursor.getColumnIndex("activity_type_id")),
            GoalFrequency.values()[cursor.getInt(cursor.getColumnIndex("frequency"))],
            GoalType.values()[cursor.getInt(cursor.getColumnIndex("type"))],
        )
        return ret
    }

    fun fetchAll(db: SQLiteDatabase?) : List<Goal> {
        val ret: MutableList<Goal> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM $tableName", null)

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor, db)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }

    fun insert(db: SQLiteDatabase?, goal: Goal) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, goal.activityTypeId)
        contentValues.put(tableColumns[2].name, goal.frequency.ordinal)
        contentValues.put(tableColumns[3].name, goal.type.ordinal)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }
}