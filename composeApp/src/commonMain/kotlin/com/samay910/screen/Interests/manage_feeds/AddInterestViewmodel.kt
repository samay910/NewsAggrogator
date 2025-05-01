package com.samay910.screen.Interests.manage_feeds

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.samay910.database.local.LocalDatabase
import com.samay910.database.local.LocalResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//on init should get the data stored in the remote database
class AddInterestViewmodel(
    private val localDatabase: LocalDatabase
):ScreenModel {

//This will store the local database data
    var savedFeeds by mutableStateOf(listOf<LocalResponse>())
//these variables will be used to manage the ui if the interest has a feed or not stored locally
    var interest1 by mutableStateOf(false)
    var interest2 by mutableStateOf(false)
    var interest3 by mutableStateOf(false)
    var interest4 by mutableStateOf(false)

    var interest1Details by mutableStateOf(LocalResponse(
        id = 0,
        topic = "",
        location = "",
        q = ""
    ))
    var interest2Details by mutableStateOf(LocalResponse(
        id = 0,
        topic = "",
        location = "",
        q = ""
    ))
    var interest3Details by mutableStateOf(LocalResponse(
        id = 0,
        topic = "",
        location = "",
        q = ""
    ))
    var interest4Details by mutableStateOf(LocalResponse(
        id = 0,
        topic = "",
        location = "",
        q = ""
    ))

//This actually gets the data from the local database
    fun GetData(){
        screenModelScope.launch {
            _initLoading.value = true
            GetLocalDatabas()
            _initLoading.value = false
        }
    }

//this is only added on the first load of the screen to ensure the local data is reflected in the gui always
    private val _initLoading = MutableStateFlow(false)
    val initloading: StateFlow<Boolean> = _initLoading

//this function deals with updating the values used to ensure buttons linking to feeds are displayed correctly
    suspend fun GetLocalDatabas(){
//check all 4 set interests, if they exist then load them into the screen, if not then load the default add button

            interest1 = localDatabase.checkFeedExists(1)
            interest2 = localDatabase.checkFeedExists(2)
            interest3 = localDatabase.checkFeedExists(3)
            interest4 = localDatabase.checkFeedExists(4)
//only perform the call if at least 1 exists
            if (interest1||interest2||interest3||interest4){
                savedFeeds=localDatabase.readAllFeed()
                for (i in savedFeeds){
//all cases are handled here for each interest feed
                    if (i.id.toInt() ==1){
//copy the values where the stored id matches the interest feed number
                        interest1Details=i.copy()
//filter the saved values
                        if (i.topic=="unset"){
                            interest1Details.topic=""
                        }
                        if (i.location=="unset"){
                            interest1Details.location=""
                        }
                        if (i.q=="unset"){
                            interest1Details.q="any keyword"
                        }
                        if (i.source=="unset"){
                            interest1Details.source="All sources"
                        }
                    }
                    else if (i.id.toInt() ==2){
                        interest2Details=i.copy()
//                        filter the saved values
                        if (i.topic=="unset"){
                            interest2Details.topic=""
                        }
                        if (i.location=="unset"){
                            interest2Details.location=""
                        }
                        if (i.q=="unset"){
                            interest2Details.q="any keyword"
                        }
                        if (i.source=="unset"){
                            interest2Details.source="All sources"
                        }
                    }
                    else if (i.id.toInt() ==3){
                        interest3Details=i.copy()

//                        filter the saved values
                        if (i.topic=="unset"){
                            interest3Details.topic=""
                        }
                        if (i.location=="unset"){
                            interest3Details.location=""
                        }
                        if (i.q=="unset"){
                            interest3Details.q="any keyword"
                        }
                        if (i.source=="unset"){
                            interest3Details.source="All sources"
                        }
                    }
                    else if (i.id.toInt() ==4){
                        interest4Details=i.copy()

//                        filter the saved values
                        if (i.topic=="unset"){
                            interest4Details.topic=""
                        }
                        if (i.location=="unset"){
                            interest4Details.location=""
                        }
                        if (i.q=="unset"){
                            interest4Details.q="any keyword"
                        }
                        if (i.source=="unset"){
                            interest4Details.source="All sources"
                        }
                    }
                }
            }
        }

//manage loading within dialog
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

//manage display of dialogs

    var displayRemove by mutableStateOf(false)
        private set
    fun updateDisplayRemove(input: Boolean) {
        displayRemove = input
    }

    var displayInfo by mutableStateOf(false)
        private set

    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input}

//used to manage what feed within the local database to remove based on its feedid primary key
    var selectedToRemove by mutableStateOf(0)
        private set

    fun updateSelectedToRemove(input: Int) {
        selectedToRemove = input
    }

//function required to remove the local database entry
    fun DeleteFeed(){
        updateDisplayRemove(true)
        screenModelScope.launch {
            _loading.value = true
            RemoveFeed()
        }
    }
//called within the above function to remove the feed
    suspend fun RemoveFeed(){
        localDatabase.removeSavedInterest(selectedToRemove.toLong())
        _loading.value = false
    }
}