package com.samay910.tabs.Home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator

import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.samay910.screen.Home.HomeScreen
import com.shared.Resources
import com.shared.Resources.getTabIcon

object HomeTab:Tab{
//the content of the tab should hold the homepage.
    @Composable
    override fun Content() {
//specifying the initial screen to be displayed for the tab
        Navigator(HomeScreen()){ navigator ->
            SlideTransition(navigator)
        }
    }
    override val options: TabOptions
        @Composable
        get() {
            val title = "AI summary"
//this icon itself doesn't need to be cached as it is only used here and no where else.
            val icon = (getTabIcon("home"))
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

}