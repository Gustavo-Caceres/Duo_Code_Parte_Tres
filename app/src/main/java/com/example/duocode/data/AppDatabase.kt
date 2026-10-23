package com.example.duocode.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.duocode.model.Linguagem
import com.example.duocode.model.Questao

// Versão 2 adicionou as alternativas erradas; a migração mantém as questões que já estavam no banco
private val MIGRACAO_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE questoes ADD COLUMN alternativasErradas TEXT NOT NULL DEFAULT ''")
    }
}

@Database(entities = [Linguagem::class, Questao::class], version = 2)
@TypeConverters(Conversores::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linguagemDao(): LinguagemDao
    abstract fun questaoDao(): QuestaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "duocode_database"
                ).addMigrations(MIGRACAO_1_2).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
