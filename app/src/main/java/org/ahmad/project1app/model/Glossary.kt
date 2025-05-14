package org.ahmad.project1app.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "glossary"
)
data class Glossary(
    @PrimaryKey(autoGenerate = true)
    val id: Long ,
    val title: String,
    val desc: String,
    val module_id: Long
)
