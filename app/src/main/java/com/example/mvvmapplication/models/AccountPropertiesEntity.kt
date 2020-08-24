package com.example.mvvmapplication.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "account_properties")
data class AccountPropertiesEntity(
    @SerializedName("pk")
    @Expose
    @PrimaryKey(autoGenerate = false) // getting from server -> dont have to auto generate
    @ColumnInfo(name = "pk") // column name for room
    var pk: Int,

    @SerializedName("email")
    @Expose
    @ColumnInfo(name = "email") // column name for room
    var email: String,

    @SerializedName("username")
    @Expose
    @ColumnInfo(name = "username") // column name for room
    var username: String
)