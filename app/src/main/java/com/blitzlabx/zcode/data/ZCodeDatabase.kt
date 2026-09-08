package com.blitzlabx.zcode.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class ZCodeDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile private var INSTANCE: ZCodeDatabase? = null

        fun getInstance(context: Context): ZCodeDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZCodeDatabase::class.java,
                    "zcode.db"
                ).fallbackToDestructiveMigration().build().also { INSTANCE = it }
            }
        }
    }
}
