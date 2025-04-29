package com.samay910.screen.Interests.create_feed_form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.layout.ContentScale
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
import com.shared.Resources.getImportantImage
import com.shared.Resources.getLogo

class CreateFeedFormScreen(val index: Int):Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(){
        val navigator = LocalNavigator.currentOrThrow

//        this is required to maintain a smooth experence accross the application
        val focusManager = LocalFocusManager.current

//        this ties the scope of the viewmodel to the relative navigation of the tab
//        when the user changes the tab the viewmodel will be reset
        val viewModel : CreateFeedFormViewmodel = koinScreenModel()

        viewModel.index=index

        var displayInfo by remember{mutableStateOf(false)}
        displayInfo=viewModel.displayInfo

        var displayWarning by remember{mutableStateOf(false)}
        displayWarning=viewModel.displayWarning

        var displayUpdating by remember{mutableStateOf(false)}
        displayUpdating=viewModel.displayUpdating


//        esures the coulmn is scrollable
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
                    modifier = Modifier.padding(innerPadding).padding(20.dp).padding(bottom = 50.dp).verticalScroll(scrollState)
                        .pointerInput(Unit) { // The 'Unit' key means this doesn't restart unnecessarily
                            detectTapGestures(
                                onPress = { /* Optional: Track press state */ },
                                onTap = {
                                    // When tapped, clear focus from the currently focused element
                                    println("Tapped outside TextField - Clearing focus") // Log for confirmation
                                    focusManager.clearFocus()
                                })}

                ){


                    if (displayInfo){
                        InformationDialog(viewmodel = viewModel)
                    }else if (displayWarning){
                        WarningDialog(viewmodel = viewModel)
                    }else if(displayUpdating){
                        UpdatingDialog(viewmodel = viewModel,navigator)
                    }

                    //            the initial description and link to the help icon explaining aspects of the app
                    Row {
                        Box(modifier = Modifier.fillMaxWidth(0.9f)){
                            Text("Please specify your interest below to generate an AI summary of current news surrounding your interest.\n" +
                                    "All ")
                        }
                        IconButton(
                            //                    display a popup with specification as to how to interact with the screen and what utility is provided
                            onClick = {
                                //                        this will display a simple alert dialog box explaining verbally what the screen offers and how to use it
                                viewModel.updateDisplayInfo(true)
                                /* Handle action */
                            }

                        ){
                            Icon(imageVector = Icons.Outlined.Info, contentDescription = "More", modifier = Modifier.fillMaxSize())
                        }


                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Please set at least one of the below filters: ",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    //            text field for specifying interest via text
                    Row {
                        //    this is how i can retain the data stored in the viewmodel accross navigations within the app
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            value = viewModel.textFilter,
                            onValueChange = { textFilter -> viewModel.updatetextFilter(textFilter) },
                            label = { Text("Specify interest via text") },
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
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    //            2 dropdown menus for category and country
                    Row {

                        Box(modifier = Modifier.weight(0.4f)){
                            DropdownMenus(viewmodel = viewModel,type = "category")
                        }
                        Spacer(modifier = Modifier.weight(0.1f))

                        Box(modifier = Modifier.weight(0.4f)){
                            DropdownMenus(viewmodel = viewModel,type = "country")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    //            text feild for specifying news publisher by name
                    Row {
                        //    this is how i can retain the data stored in the viewmodel accross navigations within the app
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            value = viewModel.publisher,
                            onValueChange = { publisher -> viewModel.updatePublisher(publisher) },
                            label = { Text("Specify news publisher") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    //                            clear the input
                                    viewModel.updatePublisher("")
                                }) {
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Send"
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    //            Additional warning section to emphisise usage of Gemini to provide the AI response
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(100.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        //Highlight the fact that an unrecognised publisher will not be applied to the filter
                        Column{
                            OutlinedCard(
                                modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                                border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Red),
                            ) {
                                Column (
                                    modifier = Modifier.padding(10.dp).fillMaxWidth(1f)

                                ){
                                    Row{
                                        Image(
                                            painter = getImportantImage(),
                                            contentDescription = "Important",
                                            modifier = Modifier
                                                .fillMaxWidth(0.1f),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier.fillMaxWidth(0.8f)){
                                            Text("The above optional feild that allows you to specify a specific source, " +
                                                    "please understand that that filter will be applied when possible and if it is a recognised publisher" +
                                                    "All articles within the feed are generated through usage of the NewsAPI.org services.")
                                        }
                                    }
                                }
                            }
                        }


                    }

                    Spacer(modifier = Modifier.height(20.dp))

                   //            associative form buttons, including clear and create
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(0.3f),
                        horizontalArrangement = Arrangement.Center,
                    )
                    {
                        //                clear button
                        Box(
                            modifier = Modifier.weight(0.3f)
                        ){
                            ClearButton(viewmodel = viewModel)
                        }

                        Spacer(modifier = Modifier.weight(0.01f))
                        //                generate button
                        Box(
                            modifier = Modifier.weight(0.6f)
                        ){
                            GenerateButton(viewmodel = viewModel)
                        }

                        Spacer(modifier = Modifier.weight(0.01f))
                        //view summary button
                        //summary button implementation is here for easier management of if the view is available or not
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationDialog(viewmodel: CreateFeedFormViewmodel){
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class) // Required for ExposedDropdownMenuBox
@Composable
fun DropdownMenus(viewmodel: CreateFeedFormViewmodel,type:String) {

    var selectedOptionText by remember { mutableStateOf(
        if(type=="category"){
            viewmodel.category
        }
        else {
            viewmodel.country
        }
    ) }
    val categoryMenuItemData = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")
//    Prioritise more larger countries that are more relavant to the average user, as all the inputs are added to the parameters, a more specific region can be specified by the text feild
    val countryMenuItemData = listOf(
        "Australia", "Brazil", "Canada", "China",
        "France", "Germany", "India", "Israel",
        "Italy", "Japan",  "Mexico", "Pakistan", "Russia", "United Arab Emirates", "United Kingdom",
        "United States")
    var expanded by remember { mutableStateOf(false) }
    val options :List<String>
    val label:String

    if (type=="category"){
        options=categoryMenuItemData
        label="category"
    }
    else{
        options=countryMenuItemData
        label="country"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        androidx.compose.material3.OutlinedTextField(
            readOnly = true,
            value =
            if(type=="category"){
                viewmodel.category
            }
            else{
                viewmodel.country
            },
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.outline,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
//                        if reset is called this will change
                        Text(selectionOption)

                    },
                    onClick = {
                        if(type=="category"){
                            viewmodel.updatecategory(selectionOption)
                        }
                        else{
                            viewmodel.updatecountry(selectionOption)
                        }

                        selectedOptionText = selectionOption
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }

    }
}

@Composable
fun ClearButton(viewmodel: CreateFeedFormViewmodel){
//    specify the button colors
    val lightRedColor = Color(251,3,3)

    Button(
        onClick = {
            // Action for the button
            viewmodel.clearAll()
        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
//        change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightRedColor,
            contentColor = androidx.compose.ui.graphics.Color.Black
        )
    ){
        Column(
            // Center the icon and text horizontally within the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Clear") // Or null if Text describes action
            Text("Clear")
        }
    }
}

@Composable
fun GenerateButton(viewmodel: CreateFeedFormViewmodel){
    //    specify the button colors
    val lightRedColor = Color(90,216,204)

    Button(
        onClick = {
//            action to add the data locally and then return back to the prior screen
//            first run the check
                viewmodel.addFeed()
//            here add a check empty field

        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
//        change the shape
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightRedColor,
            contentColor = androidx.compose.ui.graphics.Color.Black
        )
    ){
        Column(
            modifier = Modifier.fillMaxHeight(1f),
            // Center the icon and text horizontally within the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = "Confirm and Save") // Or null if Text describes action
            Text("Confirm and Save")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun WarningDialog(viewmodel: CreateFeedFormViewmodel){
    var displayWarning by remember { mutableStateOf(false) }
    //    keep track of if a netowrk error has occured

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

//                    only possible error is input based
                    Text("Warning:", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ){

                    Text("Please add at least one of the filters to generate a summary(excluding domain)", fontSize = 15.sp, style = TextStyle(color = Color.Red))

                }
            }}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatingDialog(viewmodel: CreateFeedFormViewmodel,navigator: Navigator){
    var displayInfo by remember { mutableStateOf(false) }
    displayInfo = viewmodel.displayInfo

    val loading by viewmodel.loading.collectAsState()


    BasicAlertDialog(
        onDismissRequest = {
//            this makes it so that if the user clicks outside the box an action is performed
            viewmodel.updateDisplayInfo(false)
        }
    ){
        Surface(
            modifier = Modifier.fillMaxWidth(0.8f).fillMaxHeight(0.5f),
            shape = RoundedCornerShape(8.dp)
        ) {
            //        esures the coulmn is scrollable
            val scrollState = rememberScrollState()



            Column(
                modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
            ) {
//                this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ) {
//                    check if the network error is null, if not then display the error
                    Text("Updating local storage: ", fontSize = 20.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))


                }
                Spacer(modifier = Modifier.height(10.dp))
                Row (
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ){
                   if(loading){
                    //                        display a loading icon at the center of the page and message below until complete
                    Box(modifier = Modifier.fillMaxWidth(1f), contentAlignment = Alignment.Center){
                        CircularProgressIndicator(modifier = Modifier.fillMaxWidth(0.5f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Saving preferences...", color = Color.Gray)

                }else {
//                        display success message and a button to return
                    Column(modifier = Modifier.fillMaxWidth(1f), horizontalAlignment = Alignment.CenterHorizontally){
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Success!")
//                            image of success icon
                        Spacer(modifier = Modifier.height(10.dp))

                        Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = "Confirm and Save")


                        Spacer(modifier = Modifier.height(10.dp))
                        Text("please click the button below to access your feeds: ")
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            viewmodel.reset()
                            navigator.pop()
                        }) {
                            Text("Return")
                        }
                        Text("Feed successfully saved!")
                    }

                }

                }
            }}
    }
}




