package com.samay910.screen.Interests.Home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.samay910.screen.Interests.Add.InterestsAddScreen

class InterestsHomeScreen:Screen{
    @Composable
    override fun Content() {
//        this variable ensures the navigator state is managed
        val navigator = LocalNavigator.current



        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                navigator?.push(InterestsAddScreen())


            }) {
                Text("Add Interest")
            }
        }
    }
}