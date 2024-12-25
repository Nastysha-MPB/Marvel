package com.example.myapplication.presentation

import com.example.myapplication.data.Superhero

sealed class HeroState {
    data object Loading : HeroState()
    data class Success(
        val superhero: Superhero,
    ) : HeroState()

    data object Error : HeroState()
}