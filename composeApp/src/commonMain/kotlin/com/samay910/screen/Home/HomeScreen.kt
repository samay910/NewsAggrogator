package com.samay910.screen.Home

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class HomeScreen:Screen{
    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HomeViewmodel() } }
}