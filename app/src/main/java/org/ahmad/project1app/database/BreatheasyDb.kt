package org.ahmad.project1app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.ahmad.project1app.model.Glossary
import org.ahmad.project1app.model.Module

@Database(entities = [Module::class, Glossary::class], version = 1)
abstract class BreatheasyDb: RoomDatabase() {
    abstract val dao : BreatheasyDao

    companion object{
        @Volatile
        private var INSTANCE: BreatheasyDb? = null

        fun getInstance(context: Context): BreatheasyDb{
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        BreatheasyDb::class.java,
                        "datamodul.db"
                    ).createFromAsset("datamodul.db").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}