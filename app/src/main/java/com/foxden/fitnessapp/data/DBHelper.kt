package com.foxden.fitnessapp.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_NAME = "fitness_app"
        private val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        ActivityTypeDAO.onCreate(db)
        ActivityLogDAO.onCreate(db)
        NutritionPresetDAO.onCreate(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Drop
        // onCreate
    }
}

class ColumnDesc(colName : String, colType : String, colExtra : String = ""){
    val name = colName
    val type = colType
    val extra = colExtra
}
class TableDesc(tableName: String, tableColumns: List<ColumnDesc>) {
    val name = tableName
    var columns =  tableColumns
}
abstract class DAO(tblName: String, tblColumns: List<ColumnDesc>, var tblForeignKeys: String = "") {

    var tableName = tblName
    var tableColumns = tblColumns
    var tableForeignKeys = tblForeignKeys

    fun onCreate(db: SQLiteDatabase?) {
        var query = "CREATE TABLE ${tableName} ("

        // Add columns
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
}

