package com.shared
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import newsaggregatorapplication.shared.generated.resources.Res
import newsaggregatorapplication.shared.generated.resources.help
import newsaggregatorapplication.shared.generated.resources.home_tab_icon
import newsaggregatorapplication.shared.generated.resources.interest_tab_icon
import newsaggregatorapplication.shared.generated.resources.headlines_TabIcon
import newsaggregatorapplication.shared.generated.resources.ai_summary_TabIcon
import newsaggregatorapplication.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource


object Resources {
    object tab_Icon{
        val home_tab= Res.drawable.ai_summary_TabIcon
        val interest_tab= Res.drawable.interest_tab_icon
        val headlines_tab= Res.drawable.headlines_TabIcon
    }
    object Basic_Icon{
        val help= Res.drawable.help
    }

    @Composable
    fun getImageVectorIcon(painter: String):ImageVector{

        if (painter == "home") {
            val myImage: ImageVector = vectorResource(Basic_Icon.help)
            return myImage
        }
        else{
            val myImage: ImageVector = vectorResource(Basic_Icon.help)
            return myImage
        }

    }

    @Composable
    fun getLogo():Painter{
        val logo = painterResource(Res.drawable.logo)
        return logo
    }

    @Composable
    fun getTabIcon(painter: String): Painter {


        if (painter == "home") {
            val myImage: Painter = painterResource(tab_Icon.home_tab )
            return myImage
        }
        else if(painter == "interests"){
            val myImage: Painter = painterResource(tab_Icon.interest_tab)
            return myImage
        }
        else if(painter == "headlines"){
            val myImage: Painter = painterResource(tab_Icon.headlines_tab)
            return myImage
        }

        else{
            val myImage: Painter = painterResource(tab_Icon.home_tab)
            return myImage
        }

    }
}