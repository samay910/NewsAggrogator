package com.samay910

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.samay910.module.initKoin
import com.samay910.networking.api_clients.news_api.NewsApiClient
import com.samay910.screen.Home.HomeViewmodel
import com.samay910.tabs.Headlines.HeadlinesTab
import com.samay910.tabs.Home.HomeTab
import com.samay910.tabs.Interests.InterestsTab
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.shared.Resources.getLogo
import org.koin.dsl.module

//    the client is created here, with the json configuration and error handelling as well as respective OS engines
//val ktorApiClient = NewsApiClient(createHttpClient(getEngine()))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(
) {

//koin is initialised natively on both ios and android
    MaterialTheme {
        TabNavigator(HomeTab) {
//            here is how we can keep the shell of the app constant throughout
            Scaffold(
//                the top bar is native to the screens to ensure a backbutton is accessable where neccisary
                content = {
//                    Will ensure the subtree for each tab is used and not a massive overall naviagtion space accross the app
                    CurrentTab()
                },
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(HomeTab)
                        TabNavigationItem(InterestsTab)
                        TabNavigationItem(HeadlinesTab)
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
        icon = { tab.options.icon?.let { Icon(painter = it, contentDescription = tab.options.title , tint=Color.Unspecified) } } ,
        modifier = Modifier.background(color = Color.White).padding(bottom = 15.dp).fillMaxSize(0.1f),
        selectedContentColor = Color.DarkGray,
        unselectedContentColor = Color.LightGray,
        label = { Text(text = tab.options.title, fontSize = 15.sp) }
    )
}







