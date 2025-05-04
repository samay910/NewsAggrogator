package com.samay910.screen.Headlines

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import coil3.compose.AsyncImage
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

class HeadlinesViewmodel(
    private val newsApiClient: NewsApiClient
): ScreenModel {

    var displayInfo by mutableStateOf(false)
        private set

    fun updateDisplayInfo(input: Boolean) {
        displayInfo = input}

//managing the user input
    var textFilter by mutableStateOf("")
        private set

    fun updatetextFilter(input: String) {
        textFilter = input
    }

    var displayWarning by mutableStateOf(false)
        private set
    fun updateDisplayWarning(input: Boolean) {
        if (input==false){
            _networkError.value = null
        }
        displayWarning = input

    }

//-------------------------Required for API calls-------------------------------------
//the approach below is to ensure thread safety through the use of state flows
//have a way to manage the results from api requests
    private val _newsResponse = MutableStateFlow<ArticleList?>(null)
    val newsResponse: StateFlow<ArticleList?> = _newsResponse
//used to handel the display of loading composable
//return the error to be displayed
    private val _networkError = MutableStateFlow<NetworkError?>(null)
    val networkError: StateFlow<NetworkError?> = _networkError

    //newsdata status
    private val _articlesLoading = MutableStateFlow(false)
    val articlesLoading: StateFlow<Boolean> = _articlesLoading

//    the function is what actually facilitates the processing of the api call and request/response
    fun GetArticles(){
//Ensure the prior variables are cleared on an additional click of generate
        if (filteredArticles.isEmpty()==false){
            _articlesLoading.value=false
            updateDisplayWarning(false)
            _newsResponse.value = null
            _networkError.value = null
            filteredArticles.clear()
        }
//first deal with displaying the loading area
        _articlesLoading.value = true

//then call the api with either the text or a general array of interests to reflect general topics of interest
        val filter = InterestInput(
//check if an interest i specified, if not then use the generalised array for general headlines
            q= if (textFilter=="") {
                "outbreak OR war OR Business OR Technology OR Entertainment OR Sports OR Science OR Health OR Politics OR World OR Environment"
            }else{
              textFilter
            },
//set the rest and it will be processed within the api client
            category = "",
            country = "",
            from = get1DaysBefore() ,
            sortBy = "publishedAt",
            pageSize = 50,
            page = 1,
            to = getToday()
        )
        screenModelScope.launch {
            ApiResponse(filter)
//handel error display
            if (networkError.value!=null){
                _articlesLoading.value=false
                updateDisplayWarning(true)
            }
            else{
                FormatResponse()
            }
        }
    }
//precess the response from the api call
    suspend fun ApiResponse(filter: InterestInput){
        //Make the request in a coroutine
            newsApiClient.getNews(filter = filter)
                .onSuccess { _newsResponse.value = it }
                .onError { _networkError.value = it }
    }
//required to use lazy column
    var filteredArticles = mutableListOf<Article>()

    suspend fun FormatResponse(){
        filteredArticles= newsResponse.value!!.articles.toMutableList()
        delay(2.seconds)
        _articlesLoading.value = false
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


    //Similar to the function from the homeviewmodel, instead of 2 days it is 1 to limit the results further
    fun get1DaysBefore(): String {
        val systemTimeZone: TimeZone = TimeZone.currentSystemDefault()
        val today: LocalDate = Clock.System.todayIn(systemTimeZone)
        val oneDayPrior: LocalDate = today.minus(1, DateTimeUnit.DAY)
        val year = oneDayPrior.year
        val month = oneDayPrior.monthNumber
        val day = oneDayPrior.dayOfMonth
        return ("$year-$month-$day")
    }

//<----------------------handel display of article and associative details------------------------------------------------>
//all the components below deal with the article display dialog
    var displayArticle by mutableStateOf(false)
    fun updateDisplayArticle(input: Boolean) {
        displayArticle = input
    }

    var articleSelected: Article? by mutableStateOf(Article(
     author = "",
     content = "",
     description = "",
     publishedAt = "",
     source = null,
     title = "",
     url = "",
     urlToImage =""
    ))

    var articleIndex by mutableStateOf(0)

    fun updateArticleIndex(input: Int) {
        articleIndex = input
    }

    fun updateArticleSelected(index: Int) {
        articleSelected = filteredArticles[index]
    }

//this is how i was actually able to display the images given the url derived from the api response
    @Composable
    fun GetImage(url:String){
        AsyncImage(
            model = url,
            contentDescription = "Image",
            modifier = Modifier.fillMaxWidth(1f)
        )
    }

}
