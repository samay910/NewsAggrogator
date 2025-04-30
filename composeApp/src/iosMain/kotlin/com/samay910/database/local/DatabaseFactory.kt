package com.samay910.database.local


import app.cash.sqldelight.db.SqlDriver

import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.samay910.InterestDatabase

class IOSDatabaseDriverFactory(): DatabaseFactoryDriver {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = InterestDatabase.Schema,
            name = "feed.db"

        )
    }

}