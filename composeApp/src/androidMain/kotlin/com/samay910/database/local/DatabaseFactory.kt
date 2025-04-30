package com.samay910.database.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.samay910.InterestDatabase

class AndroidDatabaseDriverFactory(
    private val context: Context
): DatabaseFactoryDriver {
    override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = InterestDatabase.Schema,
            context = context,
            name = "feeds.db"

        )
    }

}