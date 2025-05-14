package org.ahmad.project1app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "module")
data class Module(
    @PrimaryKey(autoGenerate = true)
    val id: Long ,
    val title: String,
    val content: String
)
