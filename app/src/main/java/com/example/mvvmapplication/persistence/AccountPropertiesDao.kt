package com.example.mvvmapplication.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mvvmapplication.models.AccountPropertiesEntity

@Dao
interface AccountPropertiesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAndReplace(accountPropertiesEntity: AccountPropertiesEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAndIgnore(accountPropertiesEntity: AccountPropertiesEntity): Long //return the row number inserted

    @Query("SELECT * FROM account_properties WHERE pk = :pk")
    fun searchByPk(pk: Int): AccountPropertiesEntity?

    @Query("SELECT * FROM account_properties WHERE email = :email")
    suspend fun searchByEmail(email: String): AccountPropertiesEntity?
}