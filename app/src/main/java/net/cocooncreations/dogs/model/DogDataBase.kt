package net.cocooncreations.dogs.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DogBreed::class), version = 1)
abstract class DogDataBase:RoomDatabase() {

    abstract fun dogDao():DogDao

    companion object {
        @Volatile private var instance:DogDataBase? = null
        private val LOCK = Any()

        operator fun invoke(context:Context) = instance ?: synchronized(LOCK){
            instance?: buildDataBase(context).also {
                instance = it
            }
        }

        private fun buildDataBase(context: Context) = Room.databaseBuilder(context.applicationContext,DogDataBase::class.java,"dogdatabase").build()
    }

}