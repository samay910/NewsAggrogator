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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
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
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.shared.Resources.getGeminilogo
import com.shared.Resources.getImageVectorIcon
import com.shared.Resources.getImportantImage

class HomeScreen:Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(){
//        this is required to maintain a smooth experence accross the application
        val focusManager = LocalFocusManager.current

        val navigator = LocalNavigator.currentOrThrow
//        this ties the scope of the viewmodel to the relative navigation of the tab
//        when the user changes the tab the viewmodel will be reset
        val viewModel : HomeViewmodel = koinScreenModel()

        var displaySummary by remember{mutableStateOf(false)}
        displaySummary=viewModel.displaySummary

        var displayInfo by remember{mutableStateOf(false)}
        displayInfo=viewModel.displayInfo

//        esures the coulmn is scrollable
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.padding(20.dp).verticalScroll(scrollState)
                .pointerInput(Unit) { // The 'Unit' key means this doesn't restart unnecessarily
                detectTapGestures(
                    onPress = { /* Optional: Track press state */ },
                    onTap = {
                        // When tapped, clear focus from the currently focused element
                        println("Tapped outside TextField - Clearing focus") // Log for confirmation
                        focusManager.clearFocus()
                    })}

        ){
            if (displaySummary){
                SummaeyDialog(viewmodel = viewModel)
            }
            else if (displayInfo){
                InformationDialog(viewmodel = viewModel)
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
                    label = { Text("Specify topic via text") },
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
//            2 dropdown menus for topic and location
            Row {

                Box(modifier = Modifier.weight(0.4f)){
                    DropdownMenus(viewmodel = viewModel,type = "topic")
                }
                Spacer(modifier = Modifier.weight(0.1f))

                Box(modifier = Modifier.weight(0.4f)){
                    DropdownMenus(viewmodel = viewModel,type = "location")
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

//            associative form buttons, including clear,generate and view summary
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.3f))
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
                    modifier = Modifier.weight(0.4f)
                ){
                    GenerateButton(viewmodel = viewModel)
                }

                Spacer(modifier = Modifier.weight(0.01f))
//                view summary button
//                summary button implementation is here for easier management of if the view is available or not
                Box(
                    modifier = Modifier.weight(0.4f)
                ){
                    ViewSummaryButton(viewmodel = viewModel)
                }


            }
            Spacer(modifier = Modifier.height(20.dp))

//            Additional warning section to emphisise usage of Gemini to provide the AI response
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.3f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column{
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
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
                                        .fillMaxSize(0.1f),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(10.dp))

                                Box(
                                    modifier = Modifier.fillMaxWidth(0.8f)){
                                    Text("As you may have realized, our summaries are powered by Googles ")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(1f),
                                horizontalArrangement = Arrangement.End
                            ) {

                                Image(
                                    painter = getGeminilogo(),
                                    contentDescription = "App Logo", // Provide a meaningful description
                                    // Add modifiers as needed (e.g., size)
                                    modifier = Modifier.fillMaxSize(0.6f),
                                    contentScale = ContentScale.Crop

                                )
                            }
                        }
                    }
                }


            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class) // Required for ExposedDropdownMenuBox
@Composable
fun DropdownMenus(viewmodel: HomeViewmodel,type:String) {

    var selectedOptionText by remember { mutableStateOf(
        if(type=="topic"){
            viewmodel.topic
        }
        else {
            viewmodel.location
        }
    ) }
    val TopicMenuItemData = listOf("unspecified","business", "entertainment", "general", "health", "science", "sports", "technology")
    val LocationMenuItemData = listOf("unspecified","usa","uk","canada","india","australia","russia","china")
    var expanded by remember { mutableStateOf(false) }
    val options :List<String>
    val label:String

    if (type=="topic"){
        options=TopicMenuItemData
        label="Topic"
    }
    else{
        options=LocationMenuItemData
        label="Location"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            readOnly = true,
            value =
            if(type=="topic"){
                viewmodel.topic
            }
            else{
                viewmodel.location
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
                        if(type=="topic"){
                            viewmodel.updateTopic(selectionOption)
                        }
                        else{
                            viewmodel.updateLocation(selectionOption)
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
fun GenerateButton(viewmodel: HomeViewmodel){
    //    specify the button colors
    val lightRedColor = Color(90,216,204)

    Button(
        onClick = {
            // Action for the button
            //to implement API call
            viewmodel.updateSummaryGenerating(true)
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
            Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Generate") // Or null if Text describes action
            Text("Generate")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSummaryButton(viewmodel: HomeViewmodel){
    // 1. State to control if the button is enabled
    var isButtonEnabled by remember { mutableStateOf(false) }
    isButtonEnabled = viewmodel.summaryGenerating


        // The Button itself
        Button(
            modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
            onClick = {
//if it is enabled display the dialog box with the summary
                viewmodel.updateDisplaySummary(true)
            },
            // 2. Control clickability using the state variable
            enabled = isButtonEnabled
        ) {
            Row( // Row ensures icon and text are horizontally aligned within the button scope
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading Icon
                Icon(
//                    this is a created icon
                    imageVector = getImageVectorIcon("summary"),
                    contentDescription = "Summary",
                )

                // Spacer between icon and text
                Spacer(Modifier.width(0.5.dp)) // Standard Material spacing

                // Button Text
                Text(text="View summary", fontSize = 10.sp)
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaeyDialog(viewmodel: HomeViewmodel) {
    var displaySummary by remember { mutableStateOf(false) }

    //        this is a variable used to monitor status of API call progression and allow fo displaying of loading bar
    val getNewsDataStatus by viewmodel.articlesLoading.collectAsState()
//    variable to store flow state for summary generation
    val getSummaryStatus by viewmodel.summaryLoading.collectAsState()

    var notice by remember { mutableStateOf("") }
    notice=viewmodel.notice
    var summary by remember { mutableStateOf("") }
    summary=viewmodel.summary

    displaySummary = viewmodel.displaySummary

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
                            viewmodel.updateDisplaySummary(false)
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
                    Text("Summary:")
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(1f), horizontalArrangement = Arrangement.Center
                ) {
//                   while the NewsAPI is being called
                    if (getNewsDataStatus){
                        Text("Getting articles...")
                        CircularProgressIndicator()
                        if (getSummaryStatus){
                            Text("Generating Summary now...")
                            CircularProgressIndicator()
                        }
                    }
//                    what happens after article data is loads
//                    here the results will be displayed
                }
                Spacer(modifier = Modifier.height(10.dp))
//
                Column(
                    modifier = Modifier.fillMaxWidth(1f)
                ) {
                    Text(notice, fontSize = 15.sp, fontWeight = FontWeight.Bold, style = TextStyle(color = Color.Red))

                Spacer(modifier = Modifier.height(10.dp))

                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(1f).fillMaxHeight(1f),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color.Black),
                    ) {
                        Column (
                            modifier = Modifier.padding(10.dp).fillMaxWidth(1f)
                        ){
                            Text(summary)
                        }
                    }
//                    here i will display the links
                }
            }
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
                IconButton(
                    onClick = {
                        // Define dismiss action & close dialog
                        println("Dismiss button clicked.") // Optional logging
                        viewmodel.updateDisplayInfo(false)
                    },
                    modifier = Modifier.fillMaxSize(0.2f)
                ){
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                    Text("Close")
                }
            }
        }
    }
}