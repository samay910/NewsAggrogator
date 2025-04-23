package com.samay910.screen.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.samay910.networking.api_clients.news_api.NewsApiClient
import com.samay910.networking.api_clients.news_api.dto.Article_list
import com.samay910.networking.api_clients.news_api.dto.interest_input
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import util.NetworkError
import util.onError
import util.onSuccess
import kotlin.time.Duration.Companion.seconds

class HomeViewmodel(
    private val newsApiClient: NewsApiClient
):ScreenModel {

    fun clearAll(){
        updatetextFilter("")
        updatePublisher("")
        updateTopic("")
        updateLocation("")
    }

//    this will be a notice displayed after the summary is generated to mention if there was an issue related to the input making it not possible to use NewsDataAPI
    var notice by mutableStateOf("")
        private set
    fun updateNotice(input: String) {
        notice = input
    }

    var summary by mutableStateOf("")
        private set
    fun updateSummary(input: String) {
        summary = input
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



//-------------------------Required for API calls-------------------------------------

//    the approach below is to ensure thread safety through the use of state flows
//    have a way to manage the results from api requests
    private val _newsResponse = MutableStateFlow<Article_list?>(null)
    val newsResponse: StateFlow<Article_list?> = _newsResponse


    private val _geminiResponse = MutableStateFlow<Article_list?>(null)
    val geminiResponse: StateFlow<Article_list?> = _geminiResponse


//    return the error to be displayed
    private val _networkError = MutableStateFlow<NetworkError?>(null)
    val networkError: StateFlow<NetworkError?> = _networkError
//newsdata status
    private val _articlesLoading = MutableStateFlow(false)
    val articlesLoading: StateFlow<Boolean> = _articlesLoading
//gememini status
    private val _summaryLoading = MutableStateFlow(false)
    val summaryLoading: StateFlow<Boolean> = _summaryLoading


    var summaryGenerating by mutableStateOf(false)
        private set
    fun updateSummaryGenerating(input: Boolean) {
        summaryGenerating = input
        updateSummary("Your summary will appear here when ready")
        screenModelScope.launch {
            GenerateSummary()
         }

    }



//    when updating the input object the q variable needs to be a combination
    suspend fun GenerateSummary(){
        GetAritcles()

//    after the article data is stored then i can use it within the gemini API call
        GeminiSummary()
    }
// here is some data processing to display appropriate information to the user while generating the summary
    suspend fun GetAritcles(){
        val filter = interest_input(
            q = "$textFilter AND $location",
            pageNumber = 1,
            domain = publisher,
            pageSize = 20)
//    ties the call to the screenmodels lifecycle to ensure memory leaks do not occur.

        _articlesLoading.value = true // Set loading state to true before the API call
        delay(2.seconds)
        newsApiClient.getNews(filter = filter)
            .onSuccess { _newsResponse.value = it }
            .onError { _networkError.value = it }

//    when the call is complete update
        _articlesLoading.value = false
    }

//    must consider an error message to be displayed if there is an issue with the gemini api call
    suspend fun GeminiSummary(){
//        consider case where there are no valid articles and structure a different query to be sent to gemini
        if (newsResponse.value?.totalResults ==0){
            updateNotice("The summary below was generated using Googles Gemini for AI summary capabilities, An attempt was made to use a dedicated news filtering service but saddly an issue occured at some point")
        }
        else{
//            here i set the notice to mention where the articles were from
            updateNotice("The summary below was generated using a dedicated news article API in conjunction with Googles Gemini for AI summary capabilities")

        }
    }
}
