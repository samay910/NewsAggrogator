package com.samay910.networking.api_clients.gemini_api

import com.samay910.networking.Constants
import com.samay910.networking.api_clients.gemini_api.dto.ArticleData
import com.samay910.networking.api_clients.gemini_api.dto.body_json.Content
import com.samay910.networking.api_clients.gemini_api.dto.body_json.GeminiInput
import com.samay910.networking.api_clients.gemini_api.dto.body_json.PartRequest
import com.samay910.networking.api_clients.gemini_api.dto.response.AiSummary
import com.samay910.networking.api_clients.gemini_api.dto.response.PartResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result

class GoogleGeminiApiClient(private val httpClient: HttpClient){
//function to actually get the resulting summary given the prior api response
//this api doesn't take the request using parameters as the text request would be limited, meaning to parse the request using the body
//an object is required to be made with the exact same structure as the body request and then provide the appropriate input
//the dto's within body_json is the objects involved in setting the structure for the body request
    suspend fun GetSummary(articles: ArticleData): Result<PartResponse, NetworkError> {
//first create the object that will form the body of the json request
        val part:PartRequest = PartRequest( text = "${articles.articleDescriptions.joinToString(separator = ".Next Article:")} " +
                "Given the articles mentioned, generate a summary referencing all the mentioned articles.In the generated text,with the summary do not provide an introduction as the result should just be the summary with no formatted text")
        val geminiInput: GeminiInput = GeminiInput(contents = listOf(Content(parts = listOf(part))))

        val response =
            try {
                httpClient.post(
                    urlString = Constants.geminiURL
                )
                {
//essentially provide the json body holding the data i want the LLM to summarise
                    setBody(
                        geminiInput
                    )
                    headers{
                        header("x-goog-api-key", Constants.geminiApiKey)
                    }
                }

            } catch (e: UnresolvedAddressException) {
                return Result.Error(NetworkError.NO_INTERNET)
            } catch (e: SerializationException) {
                return Result.Error(NetworkError.SERIALIZATION)
            }

        return when(response.status.value){
//only issue that can come is from the sources filed
//if response starts with 2 it is successful
            in 200..299 ->{
//the response will just be the text summary
                val summary = response.body<AiSummary>()
                Result.Success(summary.candidates[0].content.parts[0])
            }
//This is the error code for parameter based errors
            400 -> Result.Error(NetworkError.BAD_REQUEST)
//this returns the appropriate error message to be displayed and inform the user
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}