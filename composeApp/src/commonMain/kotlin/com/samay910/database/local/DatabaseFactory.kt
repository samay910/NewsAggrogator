package com.samay910.database.local

import app.cash.sqldelight.db.SqlDriver
import com.samay910.InterestDatabase

interface DatabaseFactoryDriver {
    fun createDriver(): SqlDriver
}

class LocalDatabase(
    databaseFactoryDriver: DatabaseFactoryDriver
){
    private val database = InterestDatabase(databaseFactoryDriver.createDriver())

    private val queries = database.interestDatabaseQueries

    fun saveInterest(id: Long, q: String?, topic: String?, location: String?, source: String?) {
       queries.saveInterest(id, q, topic, location, source)
    }
    fun removeSavedInterest(id: Long) {
        queries.removeSavedInterest(id)
    }
    fun checkFeedExists(id: Long): Boolean {
        return queries.checkFeedExists(id).executeAsOne()
    }
}