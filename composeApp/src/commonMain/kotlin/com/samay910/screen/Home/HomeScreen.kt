package com.samay910.screen.Home
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shared.Resources.getGeminilogo
import com.shared.Resources.getImageVectorIcon
import com.shared.Resources.getImportantImage
import com.shared.Resources.getLogo
import util.NetworkError

class HomeScreen:Screen{
    @OptIn(ExperimentalMaterial3Api::class)
//all images and icons within the page are either created for the application or provided by material 3's library

    @Composable
    override fun Content(){
//navigator is provided by voyager library and facilitates both tab and nested tab navigation
        val navigator = LocalNavigator.currentOrThrow

//This manages off click events within the app whilst interacting with different composable
        val focusManager = LocalFocusManager.current

//this ties the scope of the viewmodel to the relative navigation of the tab
        val viewModel : HomeViewmodel = koinScreenModel()
//ensures the column is scrollable
        val scrollState = rememberScrollState()

//this set of variables help manage popups and dialogs managed within the viewmodel
        var displaySummary by remember{mutableStateOf(false)}
        displaySummary=viewModel.displaySummary

        var displayInfo by remember{mutableStateOf(false)}
        displayInfo=viewModel.displayInfo

        var displayWarning by remember{mutableStateOf(false)}
        displayWarning=viewModel.displayWarning

//scaffold is a composable layout that provides a basic structure for a screen
        Scaffold (
//this top bar provides return and more options functionality.Primarily used for navigation back within a tab and display of the app logo
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
//inner padding required when using scaffold to ensure no conflicts
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
//logic required to manage dialog display
                    if (displaySummary){
                        SummaryDialog(viewmodel = viewModel)
                    }
                    else if (displayInfo){
                        InformationDialog(viewmodel = viewModel)
                    }
                    else if (displayWarning){
                        WarningDialog(viewmodel = viewModel)
                    }

//Common section amongst screens providing an overview of what the screen offers and interactive button to display a dialog with more details
                    Row {
                        Box(
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ){
                            Text("Please specify your interest below to generate an AI summary of current news surrounding your interest.\n" + "All ")
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
                    Text( "Please set at least one of the below filters: ",
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

//text field for specifying interest via text with an interactive icon for clearing the field
                    Row {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            value = viewModel.textFilter,
                            onValueChange = { textFilter -> viewModel.updatetextFilter(textFilter) },
                            label = { Text("Specify interest via text") },
//clears input
                            trailingIcon = {
                                IconButton(onClick = { viewModel.updatetextFilter("") }
                                ){
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Send"
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//2 dropdown menus for category and country
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

//text field for specifying news publisher by name
                    Row {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(1f),
                            value = viewModel.publisher,
                            onValueChange = { publisher -> viewModel.updatePublisher(publisher) },
                            label = { Text("Specify news publisher") },
                            trailingIcon = {
                                IconButton(onClick = { viewModel.updatePublisher("") }
                                ){
                                    Icon(
                                        imageVector = Icons.Outlined.Clear,
                                        contentDescription = "Send"
                                    )
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//associative form buttons, including clear,generate and view summary
                    Row(
                        modifier = Modifier.fillMaxHeight(0.3f)
                    ){
//clear button
                        Box(
                            modifier = Modifier.weight(0.3f)
                        ){
                            ClearButton(viewmodel = viewModel)
                        }
                        Spacer(modifier = Modifier.weight(0.01f))

//generate button
                        Box(
                            modifier = Modifier.weight(0.4f)
                        ){
                            GenerateButton(viewmodel = viewModel)
                        }
                        Spacer(modifier = Modifier.weight(0.01f))

//view summary button
                        Box(
                            modifier = Modifier.weight(0.4f)
                        ){
                            ViewSummaryButton(viewmodel = viewModel)
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

//Additional warning section to emphasise usage of Gemini to provide the AI response
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(150.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Column{
                            OutlinedCard(
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .fillMaxHeight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface,),
                                border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
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
                                            modifier = Modifier.fillMaxWidth()){
                                            Text("All articles displayed across the application are found using Newsapi.org and their great services, our summaries are also powered by Googles ")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(1f),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Image(
                                            painter = getGeminilogo(),
                                            contentDescription = "App Logo", // Provide a meaningful description
                                            // Add modifiers as needed (e.g., size)
                                            modifier = Modifier.fillMaxWidth(0.4f),
                                            contentScale = ContentScale.Crop

                                        )
                                    }
                                }
                            }
                        }
                    }
//added to avoid conflict with bottom nav bar
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        )
    }
}

//below are separated composable that are used within the screen and dialogs used for popups
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun DropdownMenus(viewmodel: HomeViewmodel,type:String) {
//as the function is used for both dropdowns this ensures the correct variable is used
    var selectedOptionText by remember { mutableStateOf(
        if(type=="category"){
            viewmodel.category
        }
        else {
            viewmodel.country
        }
    ) }
    val categoryMenuItemData = listOf("business", "entertainment", "general", "health", "science", "sports", "technology")
//Prioritise more larger countries that are more relevant to the average user, as all the inputs are added to the parameters, a more specific region can be specified by the text feild
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
        OutlinedTextField(
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
//if reset is called this will change to default
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
fun ClearButton(viewmodel: HomeViewmodel){
//specify the button colors
    val lightRedColor = Color(251,3,3)

    Button(
        onClick = {
// Action for the button
            viewmodel.clearAll()
        },
        modifier = Modifier.fillMaxWidth(1f)
            .fillMaxHeight(1f),
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
fun GenerateButton(viewmodel: HomeViewmodel){
    //    specify the button colors
    val lightBlue = Color(90,216,204)

    Button(
        onClick = {
//implement api call within viewmodel
            viewmodel.updateSummaryGenerating(true)
        },
        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = lightBlue,
            contentColor = Color.Black
        )
    ){
        Column(
            modifier = Modifier.fillMaxHeight(1f),
// Center the icon and text horizontally within the column
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Generate") // Or null if Text describes action
            Text("Generate")
        }
    }
}

@Composable
fun ViewSummaryButton(viewmodel: HomeViewmodel){
//Manage if the button is clickable or not
    var isButtonEnabled by remember { mutableStateOf(false) }
    isButtonEnabled = viewmodel.summaryGenerating
        Button(
            modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
            onClick = {
//if it is enabled display the dialog box with the summary
                viewmodel.updateDisplaySummary(true)
            },
            enabled = isButtonEnabled
        ) {
            Row(
// Row ensures icon and text are horizontally aligned within the button scope
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                Icon(
//                    this is a created icon
                    imageVector = getImageVectorIcon("summary"),
                    contentDescription = "Summary",
                )
                Spacer(Modifier.width(0.5.dp)) // Standard Material spacing

                // Button Text
                Text(text="View summary", fontSize = 10.sp)
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDialog(viewmodel: HomeViewmodel) {

    var displaySummary by remember { mutableStateOf(false) }
//variables required to ensure the ui reflects the state of the api calls and what stage in the process the applicaiton is at
    val loadingArticles by viewmodel.articlesLoading.collectAsState()
//variable to store flow state for summary generation
    val loadingSummary by viewmodel.summaryLoading.collectAsState()
//keep track of if a netowrk error has occured
    val networkError by viewmodel.networkError.collectAsState()

//this will change dependent on if all filters specified could be applied to the summary
    var notice by remember { mutableStateOf("") }
    notice=viewmodel.notice

    displaySummary = viewmodel.displaySummary

    BasicAlertDialog(
//this makes it so that if the user clicks outside the box an action is performed
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false,
            ),
        modifier = Modifier.fillMaxSize(0.85f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier.padding(10.dp).verticalScroll(scrollState)
            ) {
//here is where the summary will be displayed and the other components will be involved
//close button which is reused on different dialogs
                Row (
                    modifier = Modifier.fillMaxWidth(1f),

                ){
                    IconButton(
                        onClick = {
                            // Define dismiss action & close dialog
                            println("Dismiss button clicked.") // Optional logging
                            viewmodel.updateDisplaySummary(false)
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

//here different components will be displayed at different stages of the generation process
                Spacer(modifier = Modifier.height(10.dp))
//this section will display a loading bar while the API calls are being made and update the user at what stage in summary generation the application is at
                Row(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("Summary:", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
//flow is get news articles, if error occurs cancel summary generation, else generate AI summary, if error occurs then, display error
                    if (loadingArticles) {
                        Box(modifier = Modifier.fillMaxWidth(0.2f)) {
                            CircularProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                        Text("Getting articles...", color = Color.Gray)
//                        if the article results is unsuccessful display the warning popup with error message
                        if (networkError != null) {
//                            stop displaying this dialog box and all canceling of requests is handeled in the viewmodel
                            viewmodel.updateDisplaySummary(false)
                        }
//                        ensures that this will not run if there is an error
                        else {
//                            display articles found:
                            Column(
                                modifier = Modifier.fillMaxWidth(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(10.dp))
//                            image of success icon

                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = "Confirm and Save"
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Success! Articles found")

                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
//now on the generating summary portion
                    if (loadingSummary){
                        Box(modifier = Modifier.fillMaxWidth(0.2f)){
                            CircularProgressIndicator(modifier = Modifier.fillMaxWidth(1f))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Generating Summary now...", color = Color.Gray)
                    }


                }
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text("NOTE: "+ notice, fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        style = TextStyle(color = Color.Red)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
//where the summary will be displayed
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp,Color.Black),
                    ) {
                        Column (
                            modifier = Modifier.padding(10.dp).fillMaxWidth(1f)
                        ){
//manage what is displayed while summary is generating
                            (if(loadingSummary||loadingArticles) {
                                Text("summary will appear here...")
                            } else{
                                viewmodel.GetSummaryResults()?.let { Text(it) }
                            })
                        }
                    }
//here i will display the links
//below will be a reference to all the articles included in the summary
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("References:")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row (modifier = Modifier.fillMaxWidth(1f), verticalAlignment = Alignment.CenterVertically){
                        Text("Title", fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.weight(0.2f))
                        Text("URL", fontWeight = FontWeight.Bold,modifier = Modifier.weight(1f),textAlign = TextAlign.Center)
                    }
                   DisplayLinks(viewmodel = viewmodel)
                }
            }
        }
    }
}


@Composable
fun DisplayLinks(viewmodel: HomeViewmodel){
    val summary by viewmodel.geminiResponse.collectAsState()
    val uriHandler = LocalUriHandler.current
    if (summary?.text.isNullOrEmpty()){
        Text("Links will appear below the summary...")
    }else{
//        lazy column requires fixed height constraints to function

           for(it in viewmodel.filteredArticles){
               //               display links
               Row (modifier = Modifier.fillMaxWidth(1f), verticalAlignment = Alignment.CenterVertically){
                   Box(modifier = Modifier.weight(0.3f),contentAlignment = Alignment.TopStart){
                       Text(".", modifier = Modifier,fontSize = 100.sp)
                   }

                   Text(it.title.toString(), modifier = Modifier.weight(1.3f))
                   Spacer(modifier = Modifier.weight(0.2f))
                   TextButton(
                       onClick = { uriHandler.openUri(it.url) },
                       modifier = Modifier.weight(1f)
                   ){
                       Text("View ${it.source?.name} Article In browser...")
                   }
               }
               Spacer(modifier = Modifier.height(10.dp))

//        function in viewmodel to loop through filtered articles and display the results
    }
}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun InformationDialog(viewmodel: HomeViewmodel){
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
                    Text("Here you will be able to generate a AI summary of the latest headlines related to an interest of yours." +
                            "Simply add to the filter specifying a keyword or use the preset dropdown menus to better support the filter used when " +
                            "gathering relevant articles regarding your interest." +
                            "All article data used within the summary is sourced from newsapi.org services.You can also provide a specific domain.Simply enter" +
                            "a name correlating to a news organisation/source you trust and we will try to best prioritise articles from that source in the generated summary where possible" +
                            "Once you have applied the desired filters simply select generate and the summary will be generated.To view the generated " +
                            "summary simply click view summary. The summary generated is through the usage of googles Gemini LLM.It is fed relevant article data and " +
                            "summaries it.Below the summary is a reference to the original article that you can click on to view the full article." +
                            "In summary, add filters to our search for articles you would be interested in, from the found articles an AI generated summary will be provided.Below the summary all the articles found are sourced " +
                            "and there links are provided"
                    )
                }
            }}}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
//here i must specify that the domain input requires for the website
fun WarningDialog(viewmodel: HomeViewmodel){
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