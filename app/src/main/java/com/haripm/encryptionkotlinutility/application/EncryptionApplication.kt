package com.haripm.encryptionkotlinutility.application

import android.app.Application
import io.realm.Realm

class EncryptionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}