package com.example.myapplication.data

interface HeroesRepository {
    suspend fun getAllHeroes(): List<Superhero>

    suspend fun getHeroById(id: String): Superhero
}