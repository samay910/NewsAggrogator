package com.samay910.screen.Headlines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.ArrowBack
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
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
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import com.shared.Resources.getLogo

class HeadlinesScreen: Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
//Create the viewmodel for the headlines screen
        val viewModel : HeadlinesViewmodel = koinScreenModel()

//Required reffreence to navigator to manage transitions
        val navigator = LocalNavigator.currentOrThrow

//        this is required to maintain a smooth experence accross the application
        val focusManager = LocalFocusManager.current

//Used to handel displaying of the info dialog
        var displayInfo by remember{ mutableStateOf(false) }
        displayInfo=viewModel.displayInfo

//        Used to handel displaying error message
        var displayWarning by remember{mutableStateOf(false)}
        displayWarning=viewModel.displayWarning

//        used to handel the display of article dialog
        var displayArticle by remember{mutableStateOf(false)}
        displayArticle=viewModel.displayArticle

//Ensures the column is scrollable
        val scrollState = rememberScrollState()
        Scaffold (
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        // --- Conditional Back Button Logic ---
                        if (navigator.canPop) { // Check if navigator can pop back
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
                        }else{
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
                    modifier = Modifier.padding(innerPadding).padding(20.dp)
                        .pointerInput(Unit) { // The 'Unit' key means this doesn't restart unnecessarily
                            detectTapGestures(
                                onPress = { /* Optional: Track press state */ },
                                onTap = {
                                    // When tapped, clear focus from the currently focused element
                                    println("Tapped outside TextField - Clearing focus") // Log for confirmation
                                    focusManager.clearFocus()
                                })
                        }

                ) {
//Check if a dialog should be displayed
                    if (displayInfo) {
                        InformationDialog(viewmodel = viewModel)
                    } else if (displayWarning) {
                        WarningDialog(viewmodel = viewModel)
                    } else if (displayArticle) {
                        ArticleDetailsDialog(viewmodel = viewModel)
                    }

//Area to provide a breif description and acess to the info dialog
                    Row(

                    ) {
                        Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                            Text("Below please specify any text that will help provide the latest headlines more relavant to you, if you want genral headlines just click generate")
                        }
                        IconButton(
//display a popup with specification as to how to interact with the screen and what utility is provided
                            onClick = {
//this will display a simple alert dialog box explaining verbally what the screen offers and how to use it
                                viewModel.updateDisplayInfo(true)
                                /* Handle action */
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "More",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(20.dp))
//Provide input field where the default is set to "Global headlines"
                    Row(
                        modifier = Modifier.fillMaxWidth(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //    this is how i can retain the data stored in the viewmodel accross navigations within the app
                        OutlinedTextField(
                            modifier = Modifier.weight(0.6f),
                            value = viewModel.textFilter,
                            onValueChange = { textFilter -> viewModel.updatetextFilter(textFilter) },
                            label = { Text("Enter text to filter headlines") },
                            trailingIcon = {
                                IconButton(onClick = {
//                            clear the input
                                    viewModel.updatetextFilter("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Send"
                                    )
                                }
                            }
                        )
                        Spacer(modifier = Modifier.weight(0.05f))
//                Button used to generate the summary
                        Box(
                            modifier = Modifier.weight(0.35f)
                        ) {
                            GenerateButton(viewmodel = viewModel)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//here the lazy column will be displayed in a fixed position
                    DisplayArticles(viewmodel = viewModel)


                }
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//Here i provide a discription of what the screen offers and how to use it
fun InformationDialog(viewmodel: HeadlinesViewmodel){
//        potentially add images
        var displayInfo by remember { mutableStateOf(false) }
        displayInfo = viewmodel.displayInfo
        BasicAlertDialog(
            onDismissRequest = {
//            this makes it so that if the user clicks outside the box an action is performed
                viewmodel.updateDisplayInfo(false)
            }
        ){
            Surface(
                modifier = Modifier.fillMaxSize(0.8f),
                shape = RoundedCornerShape(8.dp)
            ) {
                //        esures the coulmn is scrollable
                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
                ) {
//                here is where the summary will be displayed and the other components will be involved
                    Row (
                        modifier = Modifier.fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.Start
                    ){
                        IconButton(
                            onClick = {
                                // Define dismiss action & close dialog
                                println("Dismiss button clicked.") // Optional logging
                                viewmodel.updateDisplayInfo(false)
                            },
                            modifier = Modifier.fillMaxWidth(1f)
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
                        Text("Here you will be able to generate a AI summary of the latest headlines related to an inteest of yours." +
                                "Simply add to the filter specifying a keyword or use the preset dropdown menues to better suport the filter used when " +
                                "gatherig relavant articles regarding your interest." +
                                "All article data used within the summary is sourced from newsapi.org services.You can also provide a specific domain.Simply enter" +
                                "a name correlating to a news orginisation/source you trust and we will try to best prioritise articles from that source in the generated summary where possible" +
                                "Once you have applied the desired filters simply select generate and the summary will be generated.To view the generated " +
                                "summary simply click view summary. The summary genrated is through the usage of googles Gemini LLM.It is fed relavant article data and " +
                                "summarieses it.Below the summary is a reffrence to the origional article that you can click on to view the full article."
                        )
                    }
                }}}
    }

@Composable
fun GenerateButton(viewmodel: HeadlinesViewmodel){
    //    specify the button colors
    val lightRedColor = Color(90,216,204)

    Button(
        onClick = {
            // Action for the button
            //to implement API call
            viewmodel.GetArticles()
        },
        modifier = Modifier.fillMaxWidth(1f),
//        change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightRedColor,
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

@Composable
fun DisplayArticles(viewmodel: HeadlinesViewmodel){
    val loading by viewmodel.articlesLoading.collectAsState()
    var articles by remember { mutableStateOf(listOf<Article>()) }
    articles = viewmodel.filteredArticles


    if (articles.isEmpty()){
        Row (modifier = Modifier.fillMaxWidth(1f),horizontalArrangement = Arrangement.Center){
            Text("All headlines will appear here below")
        }
    }
    if (loading){
        Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally){

            CircularProgressIndicator(modifier = Modifier.fillMaxSize(0.2f))
            Spacer(modifier = Modifier.height(10.dp))
            Text("Loading articles...")
        }
    }
    else{
            //Here the lazy column will be created and all values will be displayed
            LazyColumn(
                modifier = Modifier.fillMaxWidth(1f).height(400.dp).border(1.dp, Color.Black,shape = RoundedCornerShape(10.dp)).padding(10.dp)
            ){
//               will loop through article results
//                need to keep track of the index for the display aspect
                itemsIndexed(
                    items=articles,
                    key = { index, item -> index }

                ){ index, it ->
//Each item will be a clickable headline
                    Button(
                        onClick = {
                            // Action for the button
                            //to implement API call
//                            have the index be parsed to display the data
//                            have this simply update the index to display the data
                            viewmodel.updateArticleIndex(index)
                            viewmodel.updateArticleSelected(index)
                            viewmodel.updateDisplayArticle(true)
                        },
                        modifier = Modifier.fillMaxWidth(1f).height(150.dp),
                        border = BorderStroke(width = 1.dp, color = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            // Set background color - already transparent by default for OutlinedButton
                            // containerColor = Color.Transparent, // Usually not needed, it's the default

                            // Set content (text) color
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


            }}
        }


        }
//        function in viewmodel to loop through filtered articles and display the results


@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun WarningDialog(viewmodel: HeadlinesViewmodel){
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
            modifier = Modifier.fillMaxSize(0.8f),
            shape = RoundedCornerShape(8.dp)
        ) {
            //        esures the coulmn is scrollable
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
            ) {
//                here is where the summary will be displayed and the other components will be involved
                Row (
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ){
                    IconButton(
                        onClick = {
                            // Define dismiss action & close dialog
                            println("Dismiss button clicked.") // Optional logging
                            viewmodel.updateDisplayWarning(false)
                        },
                        modifier = Modifier.fillMaxWidth(1f)
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
                            Text("The text filter added has led to an issue, please try a different text", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                        }


                    }
                    else{
                        Text("Please add at least one of the filters to generate a summary(excluding domain)", fontSize = 15.sp, style = TextStyle(color = Color.Red))
                    }
                }
            }}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun ArticleDetailsDialog(viewmodel: HeadlinesViewmodel){
//    handel link click
    val uriHandler = LocalUriHandler.current

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
                modifier = Modifier.padding(10.dp).verticalScroll(scrollState))
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
                            modifier = Modifier.fillMaxWidth(0.3f),
                            border = BorderStroke(width = 1.dp, color = Color.Black)
                        ){
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                Icon(imageVector = Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous")
                                Text("Previous Article")
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
                            modifier = Modifier.fillMaxWidth(0.4f),
                            border = BorderStroke(width = 1.dp, color = Color.Black)
                        ){
//                        go to the next article
                            Row(
                                verticalAlignment = Alignment.CenterVertically,

                                ){
                                Text("Next Article")
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
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
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
                TextButton(
                    onClick = { uriHandler.openUri(currentArticle.url) }
                ){
                    Text("Read full article...")
                }
                Spacer(modifier = Modifier.height(30.dp))



                }

//
            }
        }
}



