package com.samay910.database.local

import app.cash.sqldelight.db.SqlDriver
import com.samay910.InterestDatabase

interface DatabaseFactoryDriver {
    fun createDriver(): SqlDriver
}
//this is the single database instance that is used throughout the app
class LocalDatabase(
    databaseFactoryDriver: DatabaseFactoryDriver
){
//aspects sqldelight required for the processing of queries and database creation
    private val database =InterestDatabase(databaseFactoryDriver.createDriver())
    private val queries = database.interestDatabaseQueries

//this function simply updates the local database with a new feed connected to the interest screen
    fun saveInterest(id: Long, q: String, topic: String, location: String, source: String) {
        queries.insertFeed(id, q, topic, location, source)
    }

    fun removeSavedInterest(id: Long) {
        queries.deleteFeed(id)
    }

    fun checkFeedExists(id: Long): Boolean {
        return queries.checkFeedExists(id).executeAsOne()
    }

    fun readAllFeed(): List<LocalResponse> {
//println("INFO: Reading the cached data from the local database...")
        return queries.getFeeds().executeAsList().map {
            LocalResponse(
                id = it.feedId,
                q = it.q,
                topic = it.topic,
                location = it.location,
                source = it.source
            )
        }
    }
}