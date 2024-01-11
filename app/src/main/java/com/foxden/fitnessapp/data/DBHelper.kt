package com.foxden.fitnessapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

/*
    DBHelper

    Utility class for SQLite database
 */
class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /*

     */
    companion object {
        private val DATABASE_NAME = "fitness_app"
        private val DATABASE_VERSION = 1
    }

    /*
        onCreate()
        Calls create method for each DAO and inserts default data
    */
    override fun onCreate(db: SQLiteDatabase?) {
        Log.e("FITDB", "Creating Database Tables.")

        ActivityTypeDAO.onCreate(db)
        ActivityLogDAO.onCreate(db)
        NutritionMealPresetDAO.onCreate(db)
        NutritionLogDAO.onCreate(db)
        GoalDAO.onCreate(db)

        // Add default activity types
        ActivityTypeDAO.insert(db, ActivityType(name="Jogging", iconId = Constants.ActivityIcons.DIRECTIONS_RUN.ordinal))
        ActivityTypeDAO.insert(db, ActivityType(name="Hiking", iconId = Constants.ActivityIcons.HIKING.ordinal))
        ActivityTypeDAO.insert(db, ActivityType(name="Cycling", iconId = Constants.ActivityIcons.DIRECTIONS_BIKE.ordinal))

        // Add default meal presets
        NutritionMealPresetDAO.insert(db, NutritionMealPreset(name="Banana", calories = 100))
        NutritionMealPresetDAO.insert(db, NutritionMealPreset(name="Apple", calories = 95))

    }

    /*
        onUpgrade()
        Called when user database is a different version to latest schema.
    */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        deleteAndReset()
    }

    /*
        deleteAndReset()
        Calls delete method for each DAO and re-create tables
    */
    fun deleteAndReset() {
        Log.e("FITDB", "Deleting Tables")
        val db = this.writableDatabase

        ActivityTypeDAO.onDelete(db)
        ActivityLogDAO.onDelete(db)
        NutritionMealPresetDAO.onDelete(db)
        NutritionLogDAO.onDelete(db)
        GoalDAO.onDelete(db)

        this.onCreate(db)
    }

}

/*
    ColumnDesc

    Represents a column within the database
 */
class ColumnDesc(colName : String, colType : String, colExtra : String = ""){
    val name = colName
    val type = colType
    val extra = colExtra
}

/*
    DAO Class

    Abstraction for a database table.
*/
abstract class DAO(tblName: String, tblColumns: List<ColumnDesc>, var tblForeignKeys: String = "") {

    var tableName = tblName                     // Table name
    var tableColumns = tblColumns               // List of columns in the table
    var tableForeignKeys = tblForeignKeys       // List of foreign keys

    /*
        onCreate()
        Generates SQLite database create table query and executes
     */
    fun onCreate(db: SQLiteDatabase?) {
        var query = "CREATE TABLE ${tableName} ("

        // for each column
        for (i in tableColumns.indices) {
            if (i > 0)
                query += ", "
            query += "${tableColumns[i].name} ${tableColumns[i].type} ${tableColumns[i].extra}"
        }

        if (tableForeignKeys != "") {
            query += ", $tableForeignKeys"
        }

        query += ")"

        db?.execSQL(query)
    }

    /*
        onDelete()
        Generates SQLite database delete table query and executes
     */
    fun onDelete(db: SQLiteDatabase?) {
        var query = "DROP TABLE IF EXISTS ${tableName}"
        db?.execSQL(query)
    }
}

