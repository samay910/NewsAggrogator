package networking

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

//the core purpose o fthis client implementation is to manage the intiial native requirements of the engines and make API calls from different services more specialised
fun createHttpClient(engine: HttpClientEngine): HttpClient {
    return HttpClient(engine) {
        install(Logging) {
//            allows me to print logs in the temrinal
            logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        println(message)
                    }
            }
            level = LogLevel.ALL
        }
//for json parsing and how the response will be digested/configured
        install(ContentNegotiation) {
            json(
                json = Json {
//makes the response human readable
                    prettyPrint = true
//if the api responds with fields we dont want the app wont crash
                    ignoreUnknownKeys = true
                }
            )
        }
        defaultRequest {
//specify some default attributes
            contentType(ContentType.Application.Json)
            header(HttpHeaders.ContentType, ContentType.Application.Json)
//as i am dealing with different API's i need to consider different keys and will not specify the key here
        }
    }
}