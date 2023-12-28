package com.foxden.fitnessapp.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

data class NutritionMealPreset(
    var id: Int = 0,
    var name: String = "",
    var calories: Int = 0,
    var barcode: Int = 0,
)

object NutritionMealPresetDAO : DAO("nutrition_presets", listOf(
    ColumnDesc("id", "INTEGER", "PRIMARY KEY AUTOINCREMENT"),
    ColumnDesc("name", "TEXT"),
    ColumnDesc("calories", "INTEGER"),
    ColumnDesc("barcode", "INTEGER"))) {

    private fun cursorToObject(cursor: android.database.Cursor) : NutritionMealPreset {
        val ret = NutritionMealPreset()
        ret.id = cursor.getInt(0)
        ret.name = cursor.getString(1)
        ret.calories = cursor.getInt(2)
        ret.barcode = cursor.getInt(3)
        return ret
    }

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
    fun insert(db: SQLiteDatabase?, nutritionPreset: NutritionMealPreset) : Boolean {
        val contentValues = ContentValues()
        contentValues.put(tableColumns[1].name, nutritionPreset.name)
        contentValues.put(tableColumns[2].name, nutritionPreset.calories)
        contentValues.put(tableColumns[3].name, nutritionPreset.barcode)
        val result = db?.insert(tableName, null, contentValues)
        return (result != (0).toLong())
    }
}