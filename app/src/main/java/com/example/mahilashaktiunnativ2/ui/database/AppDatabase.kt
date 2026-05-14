package com.example.mahilashaktiunnativ2.ui.database

import android.content.Context
import androidx.room.Database
import androidx.room.migration.Migration
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(

    entities = [

        MemberEntity::class,

        SavingsTransactionEntity::class,

        SavingsEntryEntity::class,

        LoanEntity::class,

        NotificationEntity::class,

        AdminEntity::class
    ],

    version = 10,

    exportSchema = false
)

abstract class AppDatabase :
    RoomDatabase() {

    abstract fun memberDao():
            MemberDao

    abstract fun transactionDao():
            SavingsTransactionDao

    abstract fun savingsEntryDao():
            SavingsEntryDao

    abstract fun loanDao():
            LoanDao

    abstract fun notificationDao():
            NotificationDao

    abstract fun adminDao():
            AdminDao

    companion object {

        private val MIGRATION_8_9 =
            object : Migration(8, 9) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        "ALTER TABLE members ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0"
                    )
                    database.execSQL(
                        "ALTER TABLE members ADD COLUMN archivedDate INTEGER"
                    )
                    database.execSQL(
                        "ALTER TABLE members ADD COLUMN archiveReason TEXT NOT NULL DEFAULT ''"
                    )
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS notifications (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            eventType TEXT NOT NULL,
                            title TEXT NOT NULL,
                            message TEXT NOT NULL,
                            timestamp INTEGER NOT NULL,
                            dateKey TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        private val MIGRATION_9_10 =
            object : Migration(9, 10) {
                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS admins (
                            id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            name TEXT NOT NULL,
                            phoneNumber TEXT NOT NULL,
                            password TEXT NOT NULL,
                            dateOfBirth TEXT NOT NULL,
                            age INTEGER NOT NULL,
                            village TEXT NOT NULL,
                            address TEXT NOT NULL,
                            occupation TEXT NOT NULL,
                            photoUri TEXT,
                            createdAt INTEGER NOT NULL,
                            updatedAt INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )
                }
            }

        @Volatile

        private var INSTANCE:
                AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {

            return INSTANCE ?:
            synchronized(this) {

                val instance = Room.databaseBuilder(

                    context.applicationContext,

                    AppDatabase::class.java,

                    "mahila_shakti_unnati_database"
                )

                    .addMigrations(
                        MIGRATION_8_9,
                        MIGRATION_9_10
                    )
                    .fallbackToDestructiveMigration()

                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}
