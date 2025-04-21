package com.samay910

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.samay910.networking.api_clients.news_api.NewsApiClient
import io.ktor.client.engine.okhttp.OkHttp
import networking.createHttpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(
                //            here the Http clients used within the applicatoion are specified for native implementation
//            newsdata client

                NewsApiClient= remember{
                NewsApiClient(createHttpClient(OkHttp.create()))
            }

            )



        }
    }
}

//@Preview
//@Composable
//fun AppAndroidPreview() {
//    App()
//}