package com.samay910.module

import com.samay910.database.local.AndroidDatabaseDriverFactory
import com.samay910.database.local.DatabaseFactoryDriver
import com.samay910.database.local.LocalDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule= module {
    single<DatabaseFactoryDriver> { AndroidDatabaseDriverFactory(androidContext()) }
    single<LocalDatabase> { LocalDatabase(get()) }
}