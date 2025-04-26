package com.samay910.screen.Home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextDecoration
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import sh.calvin.autolinktext.rememberAutoLinkText
import util.NetworkError
import util.onError
import util.onSuccess
import kotlin.time.Duration.Companion.seconds

class HomeViewmodel(
    private val newsApiClient: NewsApiClient,private val geminiApiClient: GoogleGeminiApiClient
):ScreenModel {



    fun clearAll(){
        updatetextFilter("")
        updatePublisher("")
        updatecategory("")
        updatecountry("")
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



//-------------------------Required for API calls-------------------------------------

//    the approach below is to ensure thread safety through the use of state flows
//    have a way to manage the results from api requests
    private val _newsResponse = MutableStateFlow<ArticleList?>(null)
    val newsResponse: StateFlow<ArticleList?> = _newsResponse


    private val _geminiResponse = MutableStateFlow<PartResponse?>(null)
    val geminiResponse: StateFlow<PartResponse?> = _geminiResponse

    var displaySummary by mutableStateOf(false)
        private set
    fun updateDisplaySummary(input: Boolean) {
        displaySummary = input
    }

//    return the error to be displayed
    private val _networkError = MutableStateFlow<NetworkError?>(null)
    val networkError: StateFlow<NetworkError?> = _networkError

//newsdata status
    private val _articlesLoading = MutableStateFlow(false)
    val articlesLoading: StateFlow<Boolean> = _articlesLoading

//gememini status
    private val _summaryLoading = MutableStateFlow(false)
    val summaryLoading: StateFlow<Boolean> = _summaryLoading

    //    this will be a notice displayed after the summary is generated to mention if there was an issue related to the input making it not possible to use NewsDataAPI
    var notice by mutableStateOf("The summary below was generated using gemini LLM with newsAPI.org resources, If you scroll, you will be able to access the source articles used for the generation")
        private set
    fun updateNotice(input: String) {
        notice = input
    }




    //    store the articles that have the domain specified
    var filteredArticles = mutableListOf<Article>()

    var geminiInput = mutableListOf<String>()

    var summaryGenerating by mutableStateOf(false)
        private set
    fun updateSummaryGenerating(input: Boolean) {

        //only generate the summary if at least one of the inputs a
        if (textFilter.isEmpty() && country.isEmpty() && category.isEmpty()){
            updateDisplayWarning(true)
        }
        else{
            _geminiResponse.value=null
            summaryGenerating = input
//            empty all variables
            geminiInput.clear()
            filteredArticles.clear()
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, If you scroll, you will be able to access the source articles used for the generation")
            _networkError.value = null
            screenModelScope.launch {
                GenerateSummary()
                delay(2.seconds)
                _summaryLoading.value = false
            }
        }
    }

    fun GetSummaryResults(): String? {
        return geminiResponse.value?.text.toString()

    }



//    when updating the input object the q variable needs to be a combination
    suspend fun GenerateSummary(){
        GetAritcles()
            if (networkError.value!=null){
        //        close summary and remove abilty to generate
                updateDisplayWarning(true)
                updateDisplaySummary(false)
            }
            else{
                //    attempt to apply the domain filter
                FilterDomain()

        //    after the article data is stored then i can use it within the gemini API call
                GeminiSummary()
//                after the summary is generated reset the article data stores
                //filteredArticles.clear()


            }

    }
// here is some data processing to display appropriate information to the user while generating the summary
    suspend fun GetAritcles(){
        val filter = InterestInput(
//the filter will be made from at least a single
            q=textFilter,
            category = category,
            country = country,

//            constructed based on the current date and time
            from = get2DaysBefore(),
            sortBy = "publishedAt",
            pageSize = 30,
            page = 1
            )
//    if textfilter country and category is empty


        //    ties the call to the screenmodels lifecycle to ensure memory leaks do not occur.
//either relavant articles will be found or the user will be informed that the filters are not applicable right now and the summary will not be generated
        _articlesLoading.value = true // Set loading state to true before the API call
        delay(2.seconds)
        newsApiClient.getNews(filter = filter)
            .onSuccess { _newsResponse.value = it }
            .onError { _networkError.value = it }
//    add the domain filter
//    when the call is complete update
        _articlesLoading.value = false
    }



//    must consider an error message to be displayed if there is an issue with the gemini api call
//    all functions with a api call need suspend
    suspend fun GeminiSummary(){
//        here the summary is generated using the results from the news api
//        first go through filtered articles and tak out the title or description of the articles
//        the api can respond with articles without a description, in this instance the title will be used
            for(article in filteredArticles){
                if (article.description==null){
                    article.title?.let { geminiInput.add(it) }
                }
                else{
                    geminiInput.add(article.description!!)
            }
            }
        val input : ArticleData = ArticleData(geminiInput)
        geminiApiClient.GetSummary(input)
            .onSuccess { _geminiResponse.value=it
            }
            .onError { _networkError.value = it }
    }

    fun FilterDomain(){
//        start the loading for gemini summary generation
        _summaryLoading.value = true
        for(article in newsResponse.value?.articles!!){
            if (article.url?.contains(publisher) == true){
                filteredArticles.add(article)
                article.description?.let { geminiInput.add(it) }
            }

        }
        if (filteredArticles.isEmpty()){
//            this will be used in displaying the references to articles used within the summary
            filteredArticles= newsResponse.value!!.articles.toMutableList()
//            if after applying the filter no articles can be sourced then the filter will not be applied
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, when applying the domain filter no articles were found, thus the filter was not applied")
//            this is what will be used for the summary and reffrences for each article involved in the summary
//            set the gemini input to all articles found initially
        }
        else{
            updateNotice("The summary below was generated using gemini LLM with newsAPI.org resources, when applying the domain filter articles listed below were found,If you would like more articles to be used please clear the domain input")
        }
    }


    fun get2DaysBefore(): String {
        // 1. Get today's date in the system's default time zone
        val systemTimeZone: TimeZone = TimeZone.currentSystemDefault()
        val today: LocalDate = Clock.System.todayIn(systemTimeZone)
        // 2. Subtract 2 days using the minus() function and DateTimeUnit
        //    This correctly handles month and year rollovers.
        val twoDaysAgo: LocalDate = today.minus(2, DateTimeUnit.DAY)

        // 3. Extract components from the resulting date (two days ago)
        val year = twoDaysAgo.year
        val month = twoDaysAgo.monthNumber
        val day = twoDaysAgo.dayOfMonth

        return ("$year-$month-$day")
    }


}
