package com.samay910.screen.Interests.viewFeed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.samay910.networking.api_clients.news_api.dto.Article
import util.NetworkError
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import com.samay910.database.local.LocalResponse
import com.samay910.screen.Headlines.HeadlinesViewmodel
import com.shared.Resources.getLogo
import kotlin.math.abs

class InterestFeedScreen(
    val indexRef:Int,
    val savedFeeds: List<LocalResponse>): Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
//Create the viewmodel for the headlines screen
        val viewModel : InterestFeedViewmodel = koinScreenModel()
//get data from the local database
//this is required to ensure the articles displayed are based on the correct feeds saved filters
        viewModel.interestIndex=indexRef
//the actual list of feeds saved locally
        viewModel.savedFeeds=savedFeeds
//Function used to actually get the data on the screen and manage when the loading composable should be displayed
        viewModel.GetData()

//Required reference to navigator to manage transitions
        val navigator = LocalNavigator.currentOrThrow

//        this is required to maintain a smooth experence accross the application
        val focusManager = LocalFocusManager.current

//Used to handel displaying of the info dialog
        var displayInfo by remember{ mutableStateOf(false) }
        displayInfo=viewModel.displayInfo

//Used to handel displaying error message
        var displayWarning by remember{mutableStateOf(false)}
        displayWarning=viewModel.displayWarning

//used to handel the display of article dialog
        var displayArticle by remember{mutableStateOf(false)}
        displayArticle=viewModel.displayArticle

//used to display current filters applied on this screen itself
        var filtersApplied:String=""

//Used for actually displaying only the filters applied on this screen
        if (viewModel.currentFeed.topic!="unset"&&viewModel.currentFeed.topic!=""){
            filtersApplied+=" +${viewModel.currentFeed.topic}"
        }
        if (viewModel.currentFeed.location!="unset"&&viewModel.currentFeed.location!=""){
            filtersApplied+=" +${viewModel.currentFeed.location}"
        }
        if (viewModel.currentFeed.q!="unset"&&viewModel.currentFeed.q!=""){
            filtersApplied+=" +${viewModel.currentFeed.q}"
        }
//Ensures the column is scrollable
        val scrollState = rememberScrollState()
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
//make return within a tabs navigation stack when possible
                        if (navigator.canPop) {
                            IconButton(onClick = { navigator.pop() }, modifier = Modifier.fillMaxWidth(0.2f).padding(top = 10.dp)) { // Action: pop back
                                Column (
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ){
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back" // Accessibility
                                    )
                                    Text("Back")
                                }
                            }
                        }
                        else{
                            Spacer(modifier = Modifier.fillMaxSize(0.2f))
                        }
                    },
                    modifier = Modifier.height(100.dp),
                    title = {Image(
                        painter = getLogo(),
                        contentDescription = "App Logo", // Provide a meaningful description
                        // Add modifiers as needed (e.g., size)
                        modifier = Modifier.fillMaxSize(0.8f)
                    )},
                    actions = {
                        IconButton(
                            onClick = { /* Handle action */ },
                            modifier = Modifier.fillMaxWidth(0.2f).padding(top = 10.dp)
                        ){
                            Icon(imageVector = Icons.Filled.Menu, contentDescription = "More")
                        }}

                )
            },
            content = {innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(20.dp)
                        .padding(bottom = 50.dp)
                        .verticalScroll(scrollState)
                        .pointerInput(Unit){
                            detectTapGestures(
                                onPress = {},
                                onTap = {
// When tapped, clear focus from the currently focused element
                                    focusManager.clearFocus()
                                }
                            )
                        }
                ){
//Check if a dialog should be displayed
                    if (displayInfo) {
                        InformationDialog(viewmodel = viewModel)
                    } else if (displayWarning) {
                        WarningDialog(viewmodel = viewModel)
                    } else if (displayArticle) {
                        ArticleDetailsDialog(viewmodel = viewModel)
                    }

//Area to provide a brief description and access to the info dialog
                    Row {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ){
                            Text("Below simply enter details within the fields below to be saved for feed generation ")
                        }
                        IconButton(
                            onClick = {
                                viewModel.updateDisplayInfo(true)
                            }
                        ){
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "More",
                                modifier = Modifier.fillMaxSize())
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//Provide input field where the default is set to "Global headlines"
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//this is how i can retain the data stored in the viewmodel across navigation's within the app
                        Column(
                            modifier = Modifier.weight(0.5f)
                        ){
                            Text("Filters applied: ")
                            Text(filtersApplied)
                            if (viewModel.currentFeed.source!="unset"||viewModel.currentFeed.source==""){
                                Text("+ Source = All sources")
                            }else{
                                Text("+ Source = ${viewModel.currentFeed.source}")
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.05f))
//Button used to generate the summary
                        Box(
                            modifier = Modifier.weight(0.35f)
                        ) {
//refresh button
                            GenerateButton(viewmodel = viewModel)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//here the lazy column will be displayed in a fixed position where the user simply scrolls for different pieces of data
                    DisplayArticles(viewmodel = viewModel)

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        )
    }
}

//composable is reused from different screens
@Composable
fun GenerateButton(viewmodel: InterestFeedViewmodel){
//specify the button colors
    val lightBlue = Color(90,216,204)

    Button(
        onClick = {
//to implement API call
            viewmodel.GetArticles()
        },
        modifier = Modifier.fillMaxWidth(1f),
//change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightBlue,
            contentColor = Color.Black
        )
    ){
//here i display a centered loading composable if the articles are being loaded
        Column(
// Center the icon and text horizontally within the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Generate") // Or null if Text describes action
            Text("Generate")
        }
    }
}

//reused and slightly modified from the headlines screen
@Composable
fun DisplayArticles(viewmodel: InterestFeedViewmodel){
    val loading by viewmodel.articlesLoading.collectAsState()
    var articles by remember { mutableStateOf(listOf<Article>()) }
    articles = viewmodel.filteredArticles

    Row (modifier = Modifier.fillMaxWidth(1f),horizontalArrangement = Arrangement.Center){
            Text("Articles Found:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
    Spacer(modifier = Modifier.height(10.dp))
    if (loading){
        Column(modifier = Modifier.fillMaxWidth(1f).height(400.dp).border(1.dp, Color.Black,shape = RoundedCornerShape(10.dp)).padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Box(modifier = Modifier.fillMaxWidth(0.2f)){
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
            }
            Spacer(modifier = Modifier.height(100.dp))
            Text("Getting articles...", color = Color.Gray)
        }
    }
    else{
//Here the lazy column will be created and all values will be displayed
            LazyColumn(
                modifier = Modifier.fillMaxWidth(1f).height(400.dp).border(1.dp, Color.Black,shape = RoundedCornerShape(10.dp)).padding(10.dp)
            ){
                if (articles.isEmpty()){
                    item {
                        Row(modifier = Modifier.fillMaxWidth(1f).height(300.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically) {
                            Text("Latest articles found will appear here.",fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
//will loop through article results
//need to keep track of the index for the display aspect
                itemsIndexed(
                    items=articles,
                    key = { index, item -> index }
                ){ index, it ->
//Each item will be a clickable headline and opens the article dialog providing ability to go through all articles
                    Button(
                        onClick = {
//have the index be parsed to display the data
//have this simply update the index to display the data
                            viewmodel.updateArticleIndex(index)
                            viewmodel.updateArticleSelected(index)
                            viewmodel.updateDisplayArticle(true)
                        },
                        modifier = Modifier.fillMaxWidth(1f).height(150.dp),
                        border = BorderStroke(width = 1.dp, color = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ){

//Here is the design for each headline button result
                        Column(modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f)){
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Title: ", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(it.title.toString(), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 15.sp, fontWeight = FontWeight.Bold)//set a string display limit to 1 line and make bold
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Published on: ${it.publishedAt.toString()}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Source of article : ${it.source?.name}", fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
}

//reused and slightly modified from the headlines screen
//function in viewmodel to loop through filtered articles and display the results including an image and the link to the article itself
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsDialog(viewmodel: InterestFeedViewmodel){
//handel link click
    val uriHandler = LocalUriHandler.current
//track user gesture on the dialog
    // Threshold in pixels to consider it a swipe
    val swipeThreshold = 50f
    // Keep track of total drag distance
    var totalDrag = remember { 0f }

    var displayArticleDetails by remember { mutableStateOf(false) }
    //    keep track of if a netowrk error has occured
    var currentArticle by remember { mutableStateOf(Article(
        author = "",
        content = "",
        description = "",
        publishedAt = "",
        source = null,
        title = "",
        url = "",
        urlToImage = "",
    )) }

    displayArticleDetails = viewmodel.displayArticle
    currentArticle = viewmodel.articleSelected!!
    BasicAlertDialog(
        onDismissRequest = {
//            this makes it so that if the user clicks outside the box an action is performed
            //viewmodel.updateDisplaySummary(false)
        }
        ,properties = DialogProperties(
            usePlatformDefaultWidth = false,
        ), modifier = Modifier.fillMaxSize(0.9f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Black)
        ) {
//esures the coulmn is scrollable
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .verticalScroll(scrollState)
                    .pointerInput(Unit) { // Key = Unit means it runs once
                        detectHorizontalDragGestures(
                            onDragStart = {
                                // Reset total drag distance on new drag start
                                totalDrag = 0f
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                change.consume() // Consume the event
                                totalDrag += dragAmount // Accumulate drag amount
                            },
                            onDragEnd = {
                                // Check threshold and direction after drag ends
                                if (abs(totalDrag) > swipeThreshold) {
                                    if (totalDrag > 0 && viewmodel.articleIndex-1!=-1&&viewmodel.articleIndex!=0) {
                                        // Positive drag -> Swipe Right
                                        viewmodel.updateArticleIndex(viewmodel.articleIndex-1)
                                        viewmodel.updateArticleSelected(viewmodel.articleIndex)
                                    } else {
                                        if (viewmodel.articleIndex+1<viewmodel.filteredArticles.size-1){
                                            viewmodel.updateArticleIndex(viewmodel.articleIndex+1)
                                            viewmodel.updateArticleSelected(viewmodel.articleIndex)
                                        }else{
//                                            do nothing
                                        }
                                        // Negative drag -> Swipe Left

                                    }
                                }})}
            )
            {
//                here is where the summary will be displayed and the other components will be involved
                Row (
                    modifier = Modifier.fillMaxWidth(1f),
//cancel button
                ){
                    IconButton(
                        onClick = {
                            // Define dismiss action & close dialog
                            println("Dismiss button clicked.") // Optional logging
                            viewmodel.updateDisplayArticle(false)
                            viewmodel.updateArticleSelected(0)
                            viewmodel.updateArticleIndex(0)

                        },
                        modifier = Modifier.fillMaxWidth(0.3f),

                        ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                            Text("Close")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    if (viewmodel.articleIndex-1!=-1){
                        OutlinedIconButton(
                            onClick = {
//                            go to the previous article
                                viewmodel.updateArticleIndex(viewmodel.articleIndex-1)
                                viewmodel.updateArticleSelected(viewmodel.articleIndex)

                            },
                            modifier = Modifier.fillMaxWidth(0.3f)
                        ){
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
                                Text("Back")
                            }
                        }
                    }else{
                        Spacer(modifier = Modifier.fillMaxWidth(0.3f))
                    }

                    if (viewmodel.articleIndex+1<viewmodel.filteredArticles.size-1){
                        OutlinedIconButton(
                            onClick = {
                                viewmodel.updateArticleIndex(viewmodel.articleIndex+1)
                                viewmodel.updateArticleSelected(viewmodel.articleIndex)
                            },
                            modifier = Modifier.fillMaxWidth(0.4f)
                        ){
//                        go to the next article
                            Row(
                                verticalAlignment = Alignment.CenterVertically,

                                ){
                                Text("Next")
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Next")

                            }
                        }
                    }else{
                        Spacer(modifier = Modifier.fillMaxWidth(0.4f))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
//                this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f)
                    , horizontalArrangement = Arrangement.Center
                ) {
                    Text(currentArticle.title.toString(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("Published on: "+currentArticle.publishedAt.toString(),fontWeight = FontWeight.Thin, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
//                here the image will be displayed where possible
                viewmodel.GetImage(url = currentArticle.urlToImage.toString())

                Spacer(modifier = Modifier.height(10.dp))
                Text("Description: ",fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(currentArticle.description.toString())
                Spacer(modifier = Modifier.height(10.dp))

                Text("Source: "+currentArticle.source?.name.toString(),fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Author: "+currentArticle.author.toString(),fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f)
                    , horizontalArrangement = Arrangement.Center
                ){
                    Button(
                        onClick = { uriHandler.openUri(currentArticle.url) },
                        modifier = Modifier.fillMaxWidth(0.8f),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(90,216,204),
                            contentColor = Color.Black)
                    ){


                        Text("Read full article...")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))



            }

//
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun InformationDialog(viewmodel: InterestFeedViewmodel){
    var displayInfo by remember { mutableStateOf(false) }
    displayInfo = viewmodel.displayInfo
    BasicAlertDialog(
        onDismissRequest = {
//this makes it so that if the user clicks outside the box an action is performed
            viewmodel.updateDisplayInfo(false)
        }
    ){
        Surface(
            modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(0.5f),
            shape = RoundedCornerShape(8.dp)
        ) {
//ensures the column is scrollable
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
            ) {
//close button which is reused on different dialogs
                Row (
                    modifier = Modifier.fillMaxWidth(1f),

                    ){
                    IconButton(
                        onClick = {
                            // Define dismiss action & close dialog
                            println("Dismiss button clicked.") // Optional logging
                            viewmodel.updateDisplayInfo(false)
                        },
                        modifier = Modifier.fillMaxWidth(0.3f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                            Text("Close")
                        }
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
//                this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ) {
//                    check if the network error is null, if not then display the error
                    Text("Info: ", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))

                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ){
                    Text("Within this screen similar to the AI summary generation screen, just add at least one of the filters below and it will be saved locally." +
                            "These filters will be applied when viewing the news feed.When the feed has been successfully saved you will be directed the the prior screen and " +
                            "can access the newly created screen from there.If any error is encountered just follow the on screen prompts"
                    )
                }
            }}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun WarningDialog(viewmodel: InterestFeedViewmodel){
    var displayWarning by remember { mutableStateOf(false) }
    //    keep track of if a netowrk error has occured
    val networkError by viewmodel.networkError.collectAsState()

    displayWarning = viewmodel.displayWarning
    BasicAlertDialog(
        onDismissRequest = {
//            this makes it so that if the user clicks outside the box an action is performed
            //viewmodel.updateDisplaySummary(false)
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(0.5f),
            shape = RoundedCornerShape(8.dp)
        ) {
            //        esures the coulmn is scrollable
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
            ) {
//close button which is reused on different dialogs
                Row (
                    modifier = Modifier.fillMaxWidth(1f),

                    ){
                    IconButton(
                        onClick = {
                            // Define dismiss action & close dialog
                            println("Dismiss button clicked.") // Optional logging
                            viewmodel.updateDisplayWarning(false)
                        },
                        modifier = Modifier.fillMaxWidth(0.3f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,

                            ) {
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                            Text("Close")
                        }
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
//                this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ) {
//                    check if the network error is null, if not then display the error
                    if(networkError!=null){
                        Text("Network Error has occurred : $networkError", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))
                    }
                    else{
                        Text("Warning:", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ){
                    //                    check if the network error is null, if not then display the error
                    if(networkError!=null){
//                        handle the error message
                        if (networkError== NetworkError.NO_INTERNET){
                            Text("No internet connection", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                        }
                        else if (networkError== NetworkError.SERIALIZATION){
                            Text("Serialization error, please try again later", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                        }
                        else if (networkError== NetworkError.UNKNOWN || networkError== NetworkError.BAD_REQUEST){
                            Text("The text filter added has led to an issue, please try a different set of filters", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                        }
                        else{
                            Text("An error has occurred with the services used to generating the summary, please try again later", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                        }
                    }
                    else{
                        Text("Please add at least one of the filters to generate a summary(excluding domain)", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                    }
                }
            }}}
}

