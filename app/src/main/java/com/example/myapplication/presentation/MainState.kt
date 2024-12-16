package com.example.myapplication.presentation

import com.example.myapplication.data.Superhero

sealed class MainState {

    data object Loading : MainState()

    data class Success(
        val superheroes: List<Superhero>,
    ) : MainState()

    data object Error : MainState()
}