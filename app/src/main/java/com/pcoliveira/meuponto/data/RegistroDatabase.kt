package com.pcoliveira.meuponto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pcoliveira.meuponto.model.AjustePonto
import com.pcoliveira.meuponto.model.Registro

@Database(entities = [Registro::class, AjustePonto::class], version = 1)
abstract class RegistroDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
    abstract fun ajustePontoDao(): AjustePontoDao

    companion object {
        @Volatile private var INSTANCE: RegistroDatabase? = null

        fun getDatabase(context: Context): RegistroDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    RegistroDatabase::class.java,
                    "registro_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
