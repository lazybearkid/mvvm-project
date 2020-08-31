package com.example.mvvmapplication.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mvvmapplication.models.AccountPropertiesEntity
import com.example.mvvmapplication.models.AuthToken

// must be included to generate SQLite database
@Database(
    entities = [
        AuthToken::class, AccountPropertiesEntity::class
    ],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object{
        const val DATABASE_NAME ="app_db"
    }
}