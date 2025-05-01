package com.samay910.tabs.Interests

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.samay910.screen.Interests.manage_feeds.AddInterestScreen
import com.shared.Resources.getTabIcon

object InterestsTab:Tab{
//the content of the tab should hold the homepage.
    @Composable
    override fun Content() {
//specifying the initial screen to be displayed for the tab
    Navigator(AddInterestScreen()){ navigator ->
        SlideTransition(navigator)
    }
}
    override val options: TabOptions
        @Composable
        get() {
            val title = "Interests"
//this icon itself doesn't need to be cached as it is only used here and no where else.
            val icon = (getTabIcon("interests"))
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }
}