package com.shared
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import newsaggregatorapplication.shared.generated.resources.Res
import newsaggregatorapplication.shared.generated.resources.help


import newsaggregatorapplication.shared.generated.resources.headlines_TabIcon
import newsaggregatorapplication.shared.generated.resources.ai_summary_TabIcon
import newsaggregatorapplication.shared.generated.resources.ai_summary_button_icon
import newsaggregatorapplication.shared.generated.resources.google_gemini
import newsaggregatorapplication.shared.generated.resources.important_icon
import newsaggregatorapplication.shared.generated.resources.interest_tab_icon
import newsaggregatorapplication.shared.generated.resources.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

object Resources {
    object tab_Icon{
        val home_tab= Res.drawable.ai_summary_TabIcon
        val interests_tab= Res.drawable.interest_tab_icon

        val headlines_tab= Res.drawable.headlines_TabIcon
    }
    object Basic_Icon{
        val help= Res.drawable.help
        val summary= Res.drawable.ai_summary_button_icon

    }

    @Composable
    fun getImageVectorIcon(painter: String):ImageVector{

        if (painter == "summary") {
            val myImage: ImageVector = vectorResource(Basic_Icon.summary)
            return myImage
        }

        else{
            val myImage: ImageVector = vectorResource(Basic_Icon.help)
            return myImage
        }

    }

    @Composable
    fun getGeminilogo():Painter{
        val logo = painterResource(Res.drawable.google_gemini)
        return logo
    }

    @Composable
    fun getLogo():Painter{
        val logo = painterResource(Res.drawable.logo)
        return logo
    }

    @Composable
    fun getImportantImage():Painter{
        val logo = painterResource(Res.drawable.important_icon)
        return logo
    }



    @Composable
    fun getTabIcon(painter: String): Painter {
        if (painter == "home") {
            val myImage: Painter = painterResource(tab_Icon.home_tab )
            return myImage
        }
        else if(painter == "interests"){
            val myImage: Painter = painterResource(tab_Icon.interests_tab)
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