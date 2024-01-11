package com.foxden.fitnessapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

/*  NutritionMealPreset Entity
    Represents a single meal the user can re-use when logging nutrition
*/
data class NutritionMealPreset(
    var id: Int = 0,
    var name: String = "",
    var calories: Int = 0,
    var barcode: Int = 0,
)

/*  NutritionMealPresetDAO 
    Represents nutrition table
*/
object NutritionMealPresetDAO : DAO("nutrition_presets", listOf(
    ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
    ColumnDesc("name", "TEXT"),
    ColumnDesc("calories", "INTEGER"),
    ColumnDesc("barcode", "INTEGER"))) {

    /* Returns NutritionMealPreset from SQLite cursor object */
    private fun cursorToObject(cursor: android.database.Cursor) : NutritionMealPreset {
        val ret = NutritionMealPreset()
        ret.id = cursor.getInt(0)
        ret.name = cursor.getString(1)
        ret.calories = cursor.getInt(2)
        ret.barcode = cursor.getInt(3)
        return ret
    }

    /* Get all nutrition meal presets from the database */
    fun fetchAll(db: SQLiteDatabase?) : List<NutritionMealPreset> {
        val ret: MutableList<NutritionMealPreset> = ArrayList()
        val queryCursor = db?.rawQuery("SELECT * FROM ${tableName}", null)

        if (queryCursor!!.moveToFirst()) {
            do { ret.add(cursorToObject(queryCursor)) }
            while (queryCursor!!.moveToNext())
        }
        queryCursor.close()
        return ret
    }

    /* Insert a NutritionMealPreset into the database */
    fun insert(db: SQLiteDatabase?, nutritionPreset: NutritionMealPreset) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, nutritionPreset.name)
        contentValues.put(tableColumns[2].name, nutritionPreset.calories)
        contentValues.put(tableColumns[3].name, nutritionPreset.barcode)
        val result = db?.insert(tableName, null, contentValues)
        return (result != (0).toLong())
    }
}