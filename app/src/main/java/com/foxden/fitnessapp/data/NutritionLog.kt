package com.foxden.fitnessapp.data

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/* NutritionType enum - represents the time of day for a nutrition log */
enum class NutritionType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}

/* Nutrition log entity */
@RequiresApi(Build.VERSION_CODES.O)
class NutritionLog() {
    var id: Int = 0
    var type: NutritionType = NutritionType.LUNCH
    var date: LocalDate? = null
    var calories: Int = 0
}

/*
    NutritionLogDAO
    Represents nutrition table
*/
object NutritionLogDAO : DAO(
    "nutrition_log",
    listOf(
        ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
        ColumnDesc("type", "INTEGER", "NOT NULL"),
        ColumnDesc("date", "DATE"),
        ColumnDesc("calories", "INTEGER"),
    )
) {

    /* Convert SQLite cursor to NutritionLog object */
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("Range")
    private fun cursorToObject(cursor: android.database.Cursor, db: SQLiteDatabase) : NutritionLog {
        val ret = NutritionLog()
        ret.id = cursor.getInt(cursor.getColumnIndex("id"))
        val typeid = cursor.getInt(cursor.getColumnIndex("type"))
        ret.type = NutritionType.values()[typeid]
        ret.date = LocalDate.parse(cursor.getString(cursor.getColumnIndex("date")))
        ret.calories = cursor.getInt(cursor.getColumnIndex("calories"))
        return ret
    }

    /* Get nutrition logs by date range */
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchRange(db: SQLiteDatabase?, start: LocalDate, end: LocalDate) : List<NutritionLog> {
        val ret: MutableList<NutritionLog> = ArrayList()
        val startStr = start.format(DateTimeFormatter.ISO_DATE)
        val endStr = end.format(DateTimeFormatter.ISO_DATE)
        val queryCursor = db?.rawQuery("SELECT * FROM $tableName WHERE date BETWEEN  ? AND ?", arrayOf(startStr, endStr))

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(NutritionLogDAO.cursorToObject(queryCursor, db)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }

    /* Insert a single nutrition log into the database */
    @RequiresApi(Build.VERSION_CODES.O)
    fun insert(db: SQLiteDatabase?, nutritionLog: NutritionLog) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, nutritionLog.type.ordinal)
        contentValues.put(tableColumns[2].name,
            nutritionLog.date?.format(DateTimeFormatter.ISO_DATE)
        )
        contentValues.put(tableColumns[3].name, nutritionLog.calories)
        val result = db?.insert(tableName, null, contentValues)
        db?.close()
        return (result != (0).toLong())
    }

}