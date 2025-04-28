package com.samay910.screen.Interests.add

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.samay910.screen.Headlines.HeadlinesViewmodel
import com.samay910.screen.Headlines.InformationDialog
import com.samay910.screen.Home.DisplayLinks
import com.samay910.screen.Home.HomeViewmodel
import com.samay910.screen.Interests.create_feed_form.CreateFeedFormScreen
import com.shared.Resources.getLogo

class AddInterestScreen :Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow
        val viewModel : AddInterestViewmodel = koinScreenModel()
//        get the data from the local database
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

        //        this is required to maintain a smooth experence accross the application
        val focusManager = LocalFocusManager.current


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
                    }
                    //Area to provide a breif description and acess to the info dialog
                    Row(

                    ) {
                        Box(modifier = Modifier.fillMaxWidth(0.9f)) {
                            Text("Here your saved feeds will be displayed, you can also generate new feeds by simply clicking one of the 4 add buttons below")
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

//            list of 4 buttons that either link to a generate feed dialog or a saved feed screen
                    LazyColumn(
                        modifier = Modifier.height(600.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
//                4 areas holding the different interest feeds
                        item {
//                    check if local interest is stored, else display button for adding interest
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).height(150.dp)
                            ) {
//                        if there exists a local interest then link to a feed screen
                                if (interest1) {
                                    IconButton(
                                        onClick = {


                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                            .border(1.dp, Color.Black)
                                    ) {

                                    }
                                } else {
//                            display a creation button for the interest
                                    IconButton(
                                        onClick = {
//                                    open the create dialog
                                            navigator.push(CreateFeedFormScreen(index = 1))
                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Add,
                                                contentDescription = "Add"
                                            )
                                            Text("Add Interest")
                                        }

                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                        }
                        item {
                            //                    check if local interest is stored, else display button for adding interest
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).height(150.dp)
                            ) {
//                        if there exists a local interest then link to a feed screen
                                if (interest2) {
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                            .border(1.dp, Color.Black)
                                    ) {

                                    }
                                } else {
//                            display a creation button for the interest
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Add,
                                                contentDescription = "Add"
                                            )
                                            Text("Add Interest")
                                        }

                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            //                    check if local interest is stored, else display button for adding interest
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).height(150.dp)
                            ) {
//                        if there exists a local interest then link to a feed screen
                                if (interest3) {
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                            .border(1.dp, Color.Black)
                                    ) {

                                    }
                                } else {
//                            display a creation button for the interest
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Add,
                                                contentDescription = "Add"
                                            )
                                            Text("Add Interest")
                                        }

                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            //                    check if local interest is stored, else display button for adding interest
                            Box(
                                modifier = Modifier.fillMaxWidth(1f).height(150.dp)
                            ) {
//                        if there exists a local interest then link to a feed screen
//                        by default display the
                                if (interest4) {
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                            .border(1.dp, Color.Black)
                                    ) {

                                    }
                                } else {
//                            display a creation button for the interest
                                    IconButton(
                                        onClick = {

                                        },
                                        modifier = Modifier.fillMaxSize(1f)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth(1f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.Add,
                                                contentDescription = "Add"
                                            )
                                            Text("Add Interest")
                                        }

                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }


                }
            }
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//Here i provide a discription of what the screen offers and how to use it
fun InformationDialog(viewmodel: AddInterestViewmodel){
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
            }}
    }
}











