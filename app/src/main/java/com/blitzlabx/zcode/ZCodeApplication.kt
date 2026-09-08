package com.blitzlabx.zcode

import android.app.Application
import com.blitzlabx.zcode.data.SettingsRepository
import com.blitzlabx.zcode.data.ZCodeDatabase

class ZCodeApplication : Application() {
    lateinit var database: ZCodeDatabase
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = ZCodeDatabase.getInstance(this)
        settingsRepository = SettingsRepository(this)
    }
}
