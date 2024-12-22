package com.example.myapplication.data

import com.example.myapplication.data.api.CharactersResponse
import com.example.myapplication.data.api.MarvelApi
import com.example.myapplication.data.database.HeroDao
import com.example.myapplication.data.database.HeroEntity
import java.math.BigInteger
import java.security.MessageDigest

private const val PUBLIC_API_KEY = "d8ba7622c7fa52efad42ef8ae6adf313"
private const val PRIVATE_API_KEY = "143958aaf2089ece9d9481b3bb0434e9c85130ea"
private const val MILLIS_IN_SECOND = 1000L

class HeroesRepositoryImplementation (
    private val api:MarvelApi,
    private val dao: HeroDao,
): HeroesRepository {

    override suspend fun getAllHeroes(): List<Superhero> {
        val cacheData = dao.getAll()
        if (cacheData.isEmpty()) {
            val networkData = getAllHeroesFromNetwork()
            dao.insert(networkData.map { hero -> mapToHeroEntity(hero) })
            return networkData
        }

        return cacheData.map { heroEntity -> mapToHero(heroEntity) }
    }

    override suspend fun getHeroById(id: String): Superhero {
        val cacheData = dao.getById(id)
        if (cacheData == null) {
            val networkData = getHeroByIdFromNetwork(id)
            dao.insert(mapToHeroEntity(networkData))
            return networkData
        }

        return mapToHero(cacheData)
    }

    private suspend fun getAllHeroesFromNetwork(): List<Superhero> {
        val timestamp = getCurrentTimestamp()
        val charactersResponse = api.getSuperheroes(
            apiKey = PUBLIC_API_KEY,
            timeStamp = timestamp,
            hash = getHash(timestamp),
        )
        return mapToHeroes(charactersResponse)
    }

    private suspend fun getHeroByIdFromNetwork(id: String): Superhero {
        val timestamp = getCurrentTimestamp()
        val charactersResponse = api.getSuperhero(
            heroId = id,
            apiKey = PUBLIC_API_KEY,
            timeStamp = timestamp,
            hash = getHash(timestamp),
        )
        return mapToHeroes(charactersResponse)[0]
    }

    private fun mapToHeroes(charactersResponse: CharactersResponse): List<Superhero> {
        return charactersResponse.data.results.map { hero ->
            val imagePath = if (hero.image.path.startsWith("http://")) {
                hero.image.path.replace("http", "https")
            } else {
                hero.image.path
            }

            Superhero(
                id = hero.id,
                imageUrl = (imagePath + "." + hero.image.extension),
                name = hero.name,
                description = hero.description,
            )
        }
    }

    private fun mapToHero(entity: HeroEntity): Superhero {
        return Superhero(
            id = entity.id,
            imageUrl = entity.imageUrl,
            name = entity.name,
            description = entity.description,
        )
    }

    private fun mapToHeroEntity(superhero: Superhero): HeroEntity {
        return HeroEntity(
            id = superhero.id,
            name = superhero.name,
            description = superhero.description,
            imageUrl = superhero.imageUrl,
        )
    }

    private fun getHash(timestamp: Long): String {
        return md5(timestamp.toString() + PRIVATE_API_KEY + PUBLIC_API_KEY)
    }

    private fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis() / MILLIS_IN_SECOND
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(
            1,
            md.digest(input.toByteArray())
        ).toString(16).padStart(32, '0')
    }
}