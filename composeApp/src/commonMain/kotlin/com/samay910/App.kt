package com.samay910

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.samay910.screen.Home.HomeViewmodel
import com.samay910.tabs.Home.HomeTab
import com.samay910.tabs.Interests.InterestsTab
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.dsl.module

//    the client is created here, with the json configuration and error handelling as well as respective OS engines
//val ktorApiClient = NewsApiClient(createHttpClient(getEngine()))


@Composable
@Preview
fun App() {
    MaterialTheme {
        TabNavigator(HomeTab) {
//            here is how we can keep the shell of the app constant throughout
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = "News Aggregator") },
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        elevation = 10.dp,
                        modifier = Modifier.fillMaxHeight(0.15f)
                    )
                },
                content = {
//                    Will ensure the subtree for each tab is used and not a massive overall naviagtion space accross the app
                    CurrentTab()
                },
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(InterestsTab)
                    }
                }
            )
        }
    }
}

//this will actually get the required tabs for a constant display accross screens
@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title) } } ,
        modifier = Modifier.background(color = Color.White).padding(bottom = 15.dp).fillMaxSize(0.1f),
        selectedContentColor = Color.DarkGray,
        unselectedContentColor = Color.LightGray,
        label = { Text(text = tab.options.title, fontSize = 15.sp) }
    )
}





