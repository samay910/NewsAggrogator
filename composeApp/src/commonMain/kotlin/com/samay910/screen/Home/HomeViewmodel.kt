package com.samay910.screen.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel

class HomeViewmodel:ScreenModel {
    override fun onDispose() {
        // ...
    }

    fun clearAll(){
        updatetextFilter("")
        updatePublisher("")
        updateTopic("")
        updateLocation("")
    }

    var textFilter by mutableStateOf("")
        private set

    fun updatetextFilter(input: String) {
        textFilter = input
    }


    var publisher by mutableStateOf("")
        private set

    fun updatePublisher(input: String) {
        publisher = input
    }

    var topic by mutableStateOf("")
        private set

    fun updateTopic(input: String) {
        topic = input
    }

    var location by mutableStateOf("")
        private set
    fun updateLocation(input: String) {
        location = input
    }

    var summaryGenerating by mutableStateOf(false)
        private set
    fun updateSummaryGenerating(input: Boolean) {
        summaryGenerating = input
    }

    var displaySummary by mutableStateOf(false)
        private set
    fun updateDisplaySummary(input: Boolean) {
        displaySummary = input
    }

    var displayInfo by mutableStateOf(false)
        private set
    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input
    }

//    when updating the input object the q variable needs to be a combination




}
