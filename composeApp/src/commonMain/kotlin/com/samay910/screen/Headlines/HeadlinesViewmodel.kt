package com.samay910.screen.Headlines

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
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

//    the list is built up of some general key words that will hopefully generate a good repersentation of headlines of interest
    var generalHeadlines = listOf(
    "outbreak","war","Business", "Technology", "Entertainment", "Sports", "Science", "Health", "Politics", "World", "Environment"
    )
//managing the user input
    var textFilter by mutableStateOf("")
        private set

    fun updatetextFilter(input: String) {
        textFilter = input
    }


//-------------------------Required for API calls-------------------------------------
//    the approach below is to ensure thread safety through the use of state flows
//    have a way to manage the results from api requests
private val _newsResponse = MutableStateFlow<ArticleList?>(null)
    val newsResponse: StateFlow<ArticleList?> = _newsResponse
    //    used to handel the display of loading composable
//    return the error to be displayed
    private val _networkError = MutableStateFlow<NetworkError?>(null)
    val networkError: StateFlow<NetworkError?> = _networkError

    //newsdata status
    private val _articlesLoading = MutableStateFlow(false)
    val articlesLoading: StateFlow<Boolean> = _articlesLoading

//    keep track of errors and display them when necissary
    var displayWarning by mutableStateOf(false)
        private set
    fun updateDisplayWarning(input: Boolean) {

        displayWarning = input
    }
    fun GetArticles(){
//Ensure the prior variables are cleared on additionall click of generate
        if (filteredArticles.isEmpty()==false){

            updateDisplayWarning(false)
            _newsResponse.value = null
            _networkError.value = null
            filteredArticles.clear()
        }

//        first deal with displaying the loading area
        _articlesLoading.value = true

//        then call the api
        val filter = InterestInput(
//           check if an interest i specified, if not then use the generalised array for general headlines
            q= if (textFilter=="") {
                "outbreak OR war OR Business OR Technology OR Entertainment OR Sports OR Science OR Health OR Politics OR World OR Environment"
            }else{
              textFilter
            },
            category = "",
            country = "",
            from = get1DaysBefore() ,
            sortBy = "relevance",
            pageSize = 100,
            page = 1
        )
        screenModelScope.launch {
            ApiResponse(filter)
            if (networkError.value!=null){
                updateDisplayWarning(true)
            }
            else{

                //        copy the results into a normal array

                FormatResponse()
            }
        }
    }

    suspend fun ApiResponse(filter: InterestInput){
        //Make the request in a coroutine

            newsApiClient.getNews(filter = filter)
                .onSuccess { _newsResponse.value = it }
                .onError { _networkError.value = it }
//end loading


    }
//required to use lazy column
    var filteredArticles = mutableListOf<Article>()

    suspend fun FormatResponse(){
        filteredArticles= newsResponse.value!!.articles.toMutableList()
        delay(2.seconds)
        _articlesLoading.value = false
    }


//return the current day - 1 day prior
    fun get1DaysBefore(): String {
        // 1. Get today's date in the system's default time zone
        val systemTimeZone: TimeZone = TimeZone.currentSystemDefault()
        val today: LocalDate = Clock.System.todayIn(systemTimeZone)
        // 2. Subtract 2 days using the minus() function and DateTimeUnit
        //    This correctly handles month and year rollovers.
        val oneDayPrior: LocalDate = today.minus(1, DateTimeUnit.DAY)

        // 3. Extract components from the resulting date (two days ago)
        val year = oneDayPrior.year
        val month = oneDayPrior.monthNumber
        val day = oneDayPrior.dayOfMonth
        return ("$year-$month-$day")
    }

//<----------------------handel display of article and associative details------------------------------------------------>
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

    @Composable
   fun GetImage(url:String){
        AsyncImage(
            model = url,
            contentDescription = "Image",
            modifier = Modifier.fillMaxWidth(1f)
        )
    }

}
