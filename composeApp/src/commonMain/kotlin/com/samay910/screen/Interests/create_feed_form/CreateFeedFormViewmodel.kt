package com.samay910.screen.Interests.create_feed_form

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.samay910.database.local.LocalDatabase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateFeedFormViewmodel (
    private val localDatabase: LocalDatabase
): ScreenModel {

    fun clearAll(){
        updatetextFilter("")
        updatePublisher("")
        updatecategory("")
        updatecountry("")
    }

    var index by mutableStateOf(0)

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

    var category by mutableStateOf("")
        private set

    fun updatecategory(input: String) {
        category = input
    }

    var country by mutableStateOf("")
        private set
    fun updatecountry(input: String) {
        country = input
    }



    var displayInfo by mutableStateOf(false)
        private set

    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input
    }

    var displayWarning by mutableStateOf(false)
        private set
    fun updateDisplayWarning(input: Boolean) {
        displayWarning = input
    }

    var displayUpdating by mutableStateOf(false)
        private set
    fun updateDisplayUpdating(input: Boolean) {
        displayUpdating = input
    }

//<--------------------------Required for updating local database-------------------------------------->

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading


    fun addFeed() {
        //only generate the summary if at least one of the inputs a
        if (textFilter.isEmpty() && country.isEmpty() && category.isEmpty()){
            updateDisplayWarning(true)
        }else{
            updateDisplayUpdating(true)

            screenModelScope.launch {
                _loading.value = true
                createFeed()
            }
        }

     }

    suspend fun createFeed(){
        localDatabase.saveInterest(
            id = index.toLong(),
            q = textFilter,
            topic = category,
            location = country,
            source = publisher
        )
//stop loading
        _loading.value = false
    }
//    reset the screen for use again
    fun reset() {
    updateDisplayUpdating(false)
    clearAll()
    updateDisplayInfo(false)
    updateDisplayWarning(false)

}
}

