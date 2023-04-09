package com.nathalie.replybot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {

//    @Inject
//    lateinit var authService: AuthService
//    var username: String? = null
//
//    fun fetchUser() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val res = authService.getCurrentUser()
//            username = res?.name
//        }
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//        fetchUser()
//    }
}