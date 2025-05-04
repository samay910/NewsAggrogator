package com.samay910.screen.Home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.samay910.networking.api_clients.gemini_api.GoogleGeminiApiClient
import com.samay910.networking.api_clients.gemini_api.dto.ArticleData
import com.samay910.networking.api_clients.gemini_api.dto.response.PartResponse
import com.samay910.networking.api_clients.news_api.NewsApiClient
import com.samay910.networking.api_clients.news_api.dto.Article
import com.samay910.networking.api_clients.news_api.dto.ArticleList
import com.samay910.networking.api_clients.news_api.dto.InterestInput
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import util.NetworkError
import util.onError
import util.onSuccess
import kotlin.time.Duration.Companion.seconds

class HomeViewmodel(
    private val newsApiClient: NewsApiClient,
    private val geminiApiClient: GoogleGeminiApiClient
):ScreenModel {
    fun clearAll(){
        updatetextFilter("")
        updatePublisher("")
        updatecategory("")
        updatecountry("")
        summaryGenerating=false
    }
//these are variables used to manage state of different composable as well as user inputs
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

//Here are variables used to specifically manage the display of dialogs
    var displayInfo by mutableStateOf(false)
        private set

    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input
    }

    var displayWarning by mutableStateOf(false)
        private set
    fun updateDisplayWarning(input: Boolean) {
        if (input==false){
            _networkError.value = null
        }
        displayWarning = input

    }
    var displaySummary by mutableStateOf(false)
        private set

//<-------------------------Required for API calls------------------------------------->

//the approach below is to ensure thread safety through the use of state flows
//Here there are different variables for recording API responses, state of the request and error messages

    private val _newsResponse = MutableStateFlow<ArticleList?>(null)
    val newsResponse: StateFlow<ArticleList?> = _newsResponse

    private val _geminiResponse = MutableStateFlow<PartResponse?>(null)
    val geminiResponse: StateFlow<PartResponse?> = _geminiResponse

//return the error to be displayed
    private val _networkError = MutableStateFlow<NetworkError?>(null)
    val networkError: StateFlow<NetworkError?> = _networkError
//State management of the request

//newsapi request/response status
    private val _articlesLoading = MutableStateFlow(false)
    val articlesLoading: StateFlow<Boolean> = _articlesLoading

//gemini request/response status
    private val _summaryLoading = MutableStateFlow(false)
    val summaryLoading: StateFlow<Boolean> = _summaryLoading


    fun updateDisplaySummary(input: Boolean) {
        displaySummary = input
    }

//    this will be a notice displayed after the summary is generated to mention if there was an issue related to the input making it not possible to use NewsDataAPI
    var notice by mutableStateOf("The summary below was generated using gemini LLM with newsAPI.org resources, If you scroll, you will be able to access the source articles used for the generation")
        private set
    fun updateNotice(input: String) {
        notice = input
    }

//stored user inputs for api calls and the response from the api calls
    //store the articles that have the domain specified
    var filteredArticles = mutableListOf<Article>()

    var geminiInput = mutableListOf<String>()

    var summaryGenerating by mutableStateOf(false)
        private set

    fun updateSummaryGenerating(input: Boolean) {
//only generate the summary if at least one of the inputs a
        if (textFilter.isEmpty() && country.isEmpty() && category.isEmpty()){
            updateDisplayWarning(true)
        }
//empty all variables
        else{
            _geminiResponse.value=null
            summaryGenerating = input
            geminiInput.clear()
            filteredArticles.clear()
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, If you scroll, you will be able to access the source articles used for the generation")
            _networkError.value = null
            screenModelScope.launch {
                GenerateSummary()
                delay(2.seconds)
            }
        }
    }

//this function gets the date from 2 days prior and formats it for the news api call
    fun get2DaysBefore(): String {
//gets today's date in the system's default time zone
        val systemTimeZone: TimeZone = TimeZone.currentSystemDefault()
        val today: LocalDate = Clock.System.todayIn(systemTimeZone)
//This correctly handles month and year rollovers.
        val twoDaysAgo: LocalDate = today.minus(2, DateTimeUnit.DAY)
        val year = twoDaysAgo.year
        val month = twoDaysAgo.month
        val day = twoDaysAgo.dayOfMonth
        return ("$year-$month-$day")
    }

    fun getToday(): String {
        //gets today's date in the system's default time zone
        val nowInstant = Clock.System.now()
        //get the system's current default timezone
        val systemTimeZone = TimeZone.currentSystemDefault()
        //Convert the instant to the local date and time in that timezone
        val localDateTime: LocalDateTime = nowInstant.toLocalDateTime(systemTimeZone)
        //Convert LocalDateTime to its default ISO string representation
        //This usually looks like YYYY-MM-DDTHH:mm:ss.nanoseconds as required by the API
        val isoTime = localDateTime.toString()
        //    The desired format has exactly 19 characters.
        //    We take the first 19 characters to remove potential nanoseconds.
        return isoTime.take(19)
    }

//Actually gets article data and stores it in the state flow
    suspend fun GetAritcles(){

    //Ensure the prior variables are cleared on an additional click of generate
    if (filteredArticles.isEmpty()==false){
        _articlesLoading.value=false
        updateDisplayWarning(false)
        _newsResponse.value = null
        _networkError.value = null
        filteredArticles.clear()
    }

        val filter = InterestInput(
//the filter will be made from at least a single
            q=textFilter,
            category = category,
            country = country,
//constructed based on the current date and time for newer articles to be considered only
            from = get2DaysBefore(),
            sortBy = "publishedAt",
            pageSize = 30,
            page = 1,
            to = getToday()
        )
//either relevant articles will be found or the user will be informed that the filters are not applicable right now and the summary will not be generated
        _articlesLoading.value = true // Set loading state to true before the API call
        newsApiClient.getNews(filter = filter)
            .onSuccess { _newsResponse.value = it }
            .onError { _networkError.value = it }
//    add the domain filter
//    when the call is complete update
        _articlesLoading.value = false
    }

//Actually gets AI summary and stores it in the state flow
    suspend fun GeminiSummary(){
//here the summary is generated using the results from the news api
//first go through filtered articles and tak out the title or description of the articles
//the api can respond with articles without a description, in this instance the title will be used
        for(article in filteredArticles){
            if (article.description==null){
                article.title?.let { geminiInput.add(it) }
            }
            else{
                geminiInput.add(article.description!!)
            }
        }
        val input = ArticleData(geminiInput)
        geminiApiClient.GetSummary(input)
            .onSuccess { _geminiResponse.value=it
            }
            .onError { _networkError.value = it }
    }

//    this is a reused function across the application that manually filters articles by the publisher desired by the user
//    if after the filter is applied there are no articles a message will be displayed to the user providing that information
    fun FilterDomain(){
//start the loading for gemini summary generation
        _summaryLoading.value = true
        for(article in newsResponse.value?.articles!!){
            if (article.url.contains(publisher) == true){
                filteredArticles.add(article)
                article.description?.let { geminiInput.add(it) }
            }
        }
        if (filteredArticles.isEmpty()){
//this will be used in displaying the references to articles used within the summary
            filteredArticles= newsResponse.value!!.articles.toMutableList()
//if after applying the filter no articles can be sourced then the filter will not be applied
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, when applying the domain filter no articles were found, thus the filter was not applied")
//this is what will be used for the summary and references for each article involved in the summary
//set the gemini input to all articles found initially
        }
        else{
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, when applying the domain filter articles listed below were found,If you would like more articles to be used please clear the domain input")
        }
    }

//This is the core function that executes the api calls required, manages resources,error handling and GUI updates
    suspend fun GenerateSummary(){
        _articlesLoading.value=true
        GetAritcles()
        _articlesLoading.value=false
//    if error response display error and don't generate summary
            if (networkError.value!=null){
//close summary and remove ability to generate
                updateDisplayWarning(true)
                updateDisplaySummary(false)
                summaryGenerating=false
            }
            else{
                _summaryLoading.value = true
//attempt to apply the publisher filter to the articles found
                FilterDomain()
//after the article data is stored then i can use it within the gemini API call
                GeminiSummary()
                _summaryLoading.value = false
//after the summary is generated reset the article data stores
                if (networkError.value!=null){
//handel error with gemini api call and display message
                    updateDisplayWarning(true)
                    updateDisplaySummary(false)
                    summaryGenerating=false
                    _networkError.value = null
                }
            }
    }

//displays the resulting summary
    fun GetSummaryResults(): String {
        return geminiResponse.value?.text.toString()
    }

}
