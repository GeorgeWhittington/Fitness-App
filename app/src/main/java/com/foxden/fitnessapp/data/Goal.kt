package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase


enum class GoalFrequency(val displayName: String)
{
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    companion object {
        fun byName(input: String): GoalFrequency? {
            return values().firstOrNull { it.name.equals(input, true) }
        }
    }
}

enum class GoalType(val displayName: String)
{
    STEPS("Steps"),
    DISTANCE("Distance"),
    DURATION("Duration"),
    SETS("Sets");

    companion object {
        fun byName(input: String): GoalType? {
            return GoalType.values().firstOrNull { it.name.equals(input, true) }
        }
    }
}

data class Goal (
    var id : Int = 0,
    var activityTypeId: Int = 0,
    var frequency: GoalFrequency = GoalFrequency.DAILY,
    var type: GoalType = GoalType.DURATION,
    var value: Int = 1,
    var hours: Int = 0
)

object GoalDAO : DAO(
    "goals",
    listOf(
        ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
        ColumnDesc("activity_type_id", "INTEGER", "NOT NULL"),
        ColumnDesc("frequency", "INTEGER"),
        ColumnDesc("type", "INTEGER"),
        ColumnDesc("value", "INTEGER"),
        ColumnDesc("hours", "INTEGER")
    )
) {

    @SuppressLint("Range")
    private fun cursorToObject(cursor: android.database.Cursor, db: SQLiteDatabase) : Goal {
        val ret = Goal(
            cursor.getInt(cursor.getColumnIndex("id")),
            cursor.getInt(cursor.getColumnIndex("activity_type_id")),
            GoalFrequency.values()[cursor.getInt(cursor.getColumnIndex("frequency"))],
            GoalType.values()[cursor.getInt(cursor.getColumnIndex("type"))],
            cursor.getInt(cursor.getColumnIndex("value")),
            cursor.getInt(cursor.getColumnIndex("hours"))
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
        contentValues.put(tableColumns[4].name, goal.value)
        contentValues.put(tableColumns[5].name, goal.hours)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }

    fun delete(db: SQLiteDatabase?, goal: Goal): Boolean {
        val affected = db?.delete(tableName, "id = ?", arrayOf(goal.id.toString()))
        if (affected != null) {
            return affected > 0
        }
        return false
    }
}