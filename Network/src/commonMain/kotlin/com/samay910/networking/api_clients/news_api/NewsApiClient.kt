package com.samay910.networking.api_clients.news_api

import com.samay910.networking.api_clients.news_api.dto.Article_list
import com.samay910.networking.api_clients.news_api.dto.interest_input
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

import io.ktor.http.headers
import io.ktor.http.parameters
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result

class NewsApiClient(
    private val httpClient: HttpClient,
) {
    suspend fun getNews(filter: interest_input):Result<Article_list,NetworkError>{
        val api_key="598d979561414adfb32916550d4d8791"
//            this is a single edndpoint as within this project i use a single api call using th enews.api
            val response =
                try{
                        httpClient.get(
//                            as in my application i dont use different APIs from NewsData i opt to use a string literal
                        urlString = "https://newsapi.org/v2/everything"
                    ){
        //                this is a less error prone way of making a request with multiple parameters.
                            parameters {
                                parameter("q", filter.q)
                                parameter("domains", filter.domain)
                                parameter("pageSize", filter.pageSize)
                                parameter("page", filter.pageNumber)
                                parameter("language", "en")
                                parameter("sortBy","publishedAt")
                                parameter("searchin","title,description")
                            }
                            headers{
//                                the other headers are set as a default
                                header("X-Api-Key",api_key)
                            }

        //                here you can add a filter such that it provides incremental updates on the request progression so a progress indicator can be displayed while the data is loading
                    }
//                    deal with particular netowrk error responses liek no internet connection or URL unrecognised
                } catch (e: UnresolvedAddressException){
                    return Result.Error(NetworkError.NO_INTERNET)
                }catch (e: SerializationException){
                    return Result.Error(NetworkError.SERIALIZATION)
                }catch (e: Exception){
                    return Result.Error(NetworkError.UNKNOWN)
                }
//        if it gets to this point essentially check the status response code of the request
//        if response code shows successful then continue, else return the error
        return when(response.status.value){
//            only issue that can come is from the sources filed

//            if response starts with 2 it is successful
            in 200..299 ->{
//                here we need a DTO(data transfer object) to store the response
//                all DTO's need to be serializable by default.
                val articles = response.body<Article_list>()


//              if the reuslt is empty it will be handeled on the client side.

                Result.Success(articles)
            }
//            This is the error code for parameter based errors
            400 -> Result.Error(NetworkError.BAD_REQUEST)
//            this returns the appropriate error message to be displayed and inform the user
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}