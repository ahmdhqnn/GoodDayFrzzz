package org.ahmad.project1app.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.ahmad.project1app.model.Glossary
import org.ahmad.project1app.model.Module

@Dao
interface BreatheasyDao {

    @Transaction
    @Query("SELECT * FROM module")
    fun getModule(): Flow<List<Module>>

    @Query("SELECT * FROM glossary")
    fun getGlossary(): Flow<List<Glossary>>

    @Query("SELECT * FROM module WHERE id = :id")
    suspend fun getModuleById(id: Long) : Module?

}