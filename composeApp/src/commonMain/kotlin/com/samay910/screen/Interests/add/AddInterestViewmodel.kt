package com.samay910.screen.Interests.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.samay910.database.local.LocalDatabase
import com.samay910.networking.api_clients.news_api.dto.InterestInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//on init should get the data stored in the remote database
class AddInterestViewmodel(
    private val localDatabase: LocalDatabase
):ScreenModel {

    var interest1 by mutableStateOf(false)
    var interest2 by mutableStateOf(false)
    var interest3 by mutableStateOf(false)
    var interest4 by mutableStateOf(false)

    fun GetData(){
        screenModelScope.launch {
            GetLocalDatabas()
        }
    }
    suspend fun GetLocalDatabas(){
//            check all 4 set interests, if they exist then load them into the screen, if not then load the default add button

            interest1 = localDatabase.checkFeedExists(1)
            interest2 = localDatabase.checkFeedExists(2)
            interest3 = localDatabase.checkFeedExists(3)
            interest4 = localDatabase.checkFeedExists(4)
    }

    var displayRemove by mutableStateOf(false)
        private set
    fun updateDisplayRemove(input: Boolean) {
        displayRemove = input
    }
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading



    var displayInfo by mutableStateOf(false)
        private set

    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input}
    var selectedToRemove by mutableStateOf(0)
        private set
    fun updateSelectedToRemove(input: Int) {
        selectedToRemove = input
    }
// function reuqired to remove the local database entry
    fun DeleteFeed(){

        updateDisplayRemove(true)
        screenModelScope.launch {
            _loading.value = true
            RemoveFeed()
        }
    }

    suspend fun RemoveFeed(){
        localDatabase.removeSavedInterest(selectedToRemove.toLong())
        _loading.value = false
    }





}