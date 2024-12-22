package com.example.myapplication.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HeroEntity::class], version = 1)
abstract class ApplicationDb : RoomDatabase() {
    abstract fun getHeroesDao(): HeroDao
}