package com.samay910.screen.Interests.manage_feeds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.samay910.database.local.LocalResponse
import com.samay910.screen.Interests.create_feed_form.CreateFeedFormScreen
import com.samay910.screen.Interests.view_feed.InterestFeedScreen
import com.shared.Resources.getLogo

class AddInterestScreen :Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow
        val viewModel : AddInterestViewmodel = koinScreenModel()
//get the data from the local database
        viewModel.GetData()
//Used to handel displaying of the info dialog
        var displayInfo by remember{ mutableStateOf(false) }
        displayInfo=viewModel.displayInfo

        var interest1 by remember{ mutableStateOf(false) }
        interest1=viewModel.interest1

        var interest2 by remember{ mutableStateOf(false) }
        interest2=viewModel.interest2

        var interest3 by remember{ mutableStateOf(false) }
        interest3=viewModel.interest3

        var interest4 by remember{ mutableStateOf(false) }
        interest4=viewModel.interest4

        var displayRemovedDialog by remember{ mutableStateOf(false) }
        displayRemovedDialog=viewModel.displayRemove

//this is required to maintain a smooth experience across the application
        val focusManager = LocalFocusManager.current
        
        val initLoading by viewModel.initloading.collectAsState()

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
                    }else if(displayRemovedDialog){
                        RemovedSuccessDialog(viewmodel = viewModel)
                    }
//Area to provide a brief description and access to the info dialog
                    Row {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ){
                            Text("Below you can manage multiple feeds saved locally to your device.For more information click the info button to the right ")
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
                    Spacer(modifier = Modifier.height(10.dp))
//list of 4 buttons that either link to a generate feed dialog or a saved feed screen
                    LazyColumn(
                        modifier = Modifier.height(600.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
//display the loading section prior as the display requires details from saved feeds
                        if (initLoading){
                            item {
                                Column(modifier = Modifier.fillMaxWidth(1f).height(400.dp).border(1.dp, Color.Black,shape = RoundedCornerShape(10.dp)).padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ){
                                    Box(modifier = Modifier.fillMaxWidth(0.2f)){
                                        CircularProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Loading stored data...", color = Color.Gray)
                                }
                            }
                        }else {
                            for (i in 1..4) {
                                item {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        "Saved Feed ${i}: ",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(1f).height(150.dp)
                                    ) {
//here as i am dealing with 4 feeds i can consider each possible case for the 4 feeds
                                        if (i == 1) {
//if the interest is connected to a saved feed
                                            if (interest1) {
                                                Box(
                                                    modifier = Modifier.weight(0.5f)
                                                ) {
                                                    ViewFeedButton(viewmodel = viewModel, index = i, navigator = navigator)
                                                }
                                                Spacer(modifier = Modifier.weight(0.01f))
                                                Box(
                                                    modifier = Modifier.weight(0.4f)
                                                ) {
                                                    DeleteFeed(viewmodel = viewModel, index = i)
                                                }
                                            }
                                            else {
//display a creation button for the interest
                                                Box(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    CreateFeedButton(
                                                        navigator = navigator,
                                                        viewmodel = viewModel,
                                                        index = i
                                                    )
                                                }
                                            }
                                        }
                                        else if (i == 2) {
                                            if (interest2) {
                                                Box(
                                                    modifier = Modifier.weight(0.5f)
                                                ) {
                                                    ViewFeedButton(viewmodel = viewModel, index = i, navigator = navigator)
                                                }
                                                Spacer(modifier = Modifier.weight(0.01f))
                                                Box(
                                                    modifier = Modifier.weight(0.4f)
                                                ) {
                                                    DeleteFeed(viewmodel = viewModel, index = i)
                                                }
                                            }
                                            else {
//display a creation button for the interest
                                                Box(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    CreateFeedButton(
                                                        navigator = navigator,
                                                        viewmodel = viewModel,
                                                        index = i
                                                    )
                                                }
                                            }
                                        }
                                        else if (i == 3) {
                                            if (interest3) {
                                                Box(
                                                    modifier = Modifier.weight(0.5f)
                                                ) {
                                                    ViewFeedButton(viewmodel = viewModel, index = i, navigator = navigator)
                                                }
                                                Spacer(modifier = Modifier.weight(0.01f))
                                                Box(
                                                    modifier = Modifier.weight(0.4f)
                                                ) {
                                                    DeleteFeed(viewmodel = viewModel, index = i)
                                                }

                                            } else {
//display a creation button for the interest
                                                Box(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    CreateFeedButton(
                                                        navigator = navigator,
                                                        viewmodel = viewModel,
                                                        index = i
                                                    )
                                                }
                                            }
                                        }
                                        else if (i == 4) {
                                            if (interest4) {
                                                Box(
                                                    modifier = Modifier.weight(0.5f)
                                                ) {
                                                    ViewFeedButton(viewmodel = viewModel, index = i, navigator = navigator)
                                                }
                                                Spacer(modifier = Modifier.weight(0.01f))
                                                Box(
                                                    modifier = Modifier.weight(0.4f)
                                                ) {
                                                    DeleteFeed(viewmodel = viewModel, index = i)
                                                }

                                            } else {
//display a creation button for the interest
                                                Box(
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    CreateFeedButton(
                                                        navigator = navigator,
                                                        viewmodel = viewModel,
                                                        index = i
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        )
    }
}


@Composable
fun CreateFeedButton(navigator: Navigator, viewmodel: AddInterestViewmodel, index:Int){
    Button(
        onClick = {
//open the create dialog
            navigator.push(CreateFeedFormScreen(index = index))
        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
//change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color.Black),
        border = BorderStroke(
            width = 1.dp, // Set the desired border width
            color = Color.Black // Set the border color to black
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Add"
            )
            Text("Add Interest")
        }
    }
}

@Composable
fun ViewFeedButton(viewmodel: AddInterestViewmodel,index:Int,navigator: Navigator){

    var savedFeeds by remember { mutableStateOf(listOf<LocalResponse>()) }
    savedFeeds=viewmodel.savedFeeds.toList()
    Button(
        onClick = {
            navigator.push(InterestFeedScreen(indexRef = index, savedFeeds =  savedFeeds))
        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
//        change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor =Color.Black
        ),
        border = BorderStroke(
            width = 1.dp, // Set the desired border width
            color = Color.Black // Set the border color to black
        )

    ) {
//Here display the current feed filters saved
        Column(
            modifier = Modifier.fillMaxSize(1f),
        ){
            if (index==1){
                Text("Articles Filters :")
                if (viewmodel.interest1Details.q!=""){
                    Text("+${viewmodel.interest1Details.q}")}
                if (viewmodel.interest1Details.topic!=""){
                    Text("+${viewmodel.interest1Details.topic}")}
                if (viewmodel.interest1Details.location!=""){
                    Text("+${viewmodel.interest1Details.location}")
                }
                Text("Articles from: ${viewmodel.interest1Details.source}")
            }
            else if (index==2){
                Text("Articles Filters :")
                if (viewmodel.interest2Details.q!=""){
                    Text("+${viewmodel.interest2Details.q}")}
                if (viewmodel.interest2Details.topic!=""){
                    Text("+${viewmodel.interest2Details.topic}")}
                if (viewmodel.interest2Details.location!=""){
                    Text("+${viewmodel.interest2Details.location}")
                }
                Text("Articles from: ${viewmodel.interest2Details.source}")
            }
            else if (index==3){
                Text("Articles Filters :")
                if (viewmodel.interest3Details.q!=""){
                    Text("+${viewmodel.interest3Details.q}")}
                if (viewmodel.interest3Details.topic!=""){
                    Text("+${viewmodel.interest3Details.topic}")}
                if (viewmodel.interest3Details.location!=""){
                    Text("+${viewmodel.interest3Details.location}")
                }
                Text("Articles from: ${viewmodel.interest3Details.source}")
            }
            else if (index==4) {
                Text("Articles Filters :")
                if (viewmodel.interest4Details.q!=""){
                    Text("+${viewmodel.interest4Details.q}")}
                if (viewmodel.interest4Details.topic!=""){
                    Text("+${viewmodel.interest4Details.topic}")}
                if (viewmodel.interest4Details.location!=""){
                    Text("+${viewmodel.interest4Details.location}")
                }
                Text("Articles from: ${viewmodel.interest4Details.source}")
            }
        }

    }
}

@Composable
fun DeleteFeed(viewmodel: AddInterestViewmodel,index: Int){
    val lightRedColor = Color(251,3,3)
    Button(
        onClick = {
            viewmodel.updateSelectedToRemove(index)
            viewmodel.DeleteFeed()
        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
//        change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightRedColor,
            contentColor = Color.Black
        )
    ){
        Column(
            modifier = Modifier.fillMaxWidth(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Refresh")
            Text("Delete Feed:")
        }
    }
}

//this function essentially displays that a feed has been successfully deleted from the local database
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemovedSuccessDialog(viewmodel: AddInterestViewmodel){
    var displayInfo by remember { mutableStateOf(false) }
    displayInfo = viewmodel.displayRemove

    val loading by viewmodel.loading.collectAsState()

    BasicAlertDialog(
        onDismissRequest = {
//this makes it so that if the user clicks outside the box an action is performed
            viewmodel.updateDisplayInfo(false)
        }
    ){
        Surface(
            modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.4f),
            shape = RoundedCornerShape(8.dp)
        ) {
//ensures the column is scrollable
            val scrollState = rememberScrollState()
//displays a loading section if it takes time to update the local database and success if it has been removed
            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
            ) {
//here is where the summary will be displayed and the other components will be involved
                Row (
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Start
                ){
                    IconButton(
                        onClick = {
// Define dismiss action & close dialog
                            viewmodel.updateDisplayRemove(false)
                        },
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                            Text("Close")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
//this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ) {
//check if the network error is null, if not then display the error
                    Text("Updating local storage: ", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ){
                    if(loading){
//display a loading icon at the center of the page and message below until complete
                        Box(modifier = Modifier.fillMaxWidth(1f), contentAlignment = Alignment.Center){
                            CircularProgressIndicator(modifier = Modifier.fillMaxWidth(0.5f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Removing feed...", color = Color.Gray)
                    }
                    else {
//display success message and a button to return
                        Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally){
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Success!")
//image of success icon
                            Spacer(modifier = Modifier.height(10.dp))
                            Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = "Confirm and Save")
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("To escape the popup click the button below or close above. ")
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(onClick = {
                                viewmodel.updateDisplayRemove(false)
                            }) {
                                Text("Close")
                            }
                            Text("Feed Successfully removed! ")
                        }

                    }

                }
            }}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun InformationDialog(viewmodel: AddInterestViewmodel){
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
                    Text("Here you simply enter some text and the application will search for the latest relevant articles " +
                            "related to that input.To change or update inputs simply change the text and click generate." +
                            "If you are feeling lazy and just want headlines feel free to click generate without entering any text." +
                            "The application will do its best at providing more generic breaking headlines that are likely to be of " +
                            "interest to anyone.To view an article in more detail simply press the article of interest and swipe left and right" +
                            "for as many articles as we found.To exit there will always be an exit button.And if you prefer not to swipe simply press next" +
                            "article or prior for the previous one.All articles displayed are fully referenced and easily accessible from just a click on the link"
                    )
                }
            }}}
}
