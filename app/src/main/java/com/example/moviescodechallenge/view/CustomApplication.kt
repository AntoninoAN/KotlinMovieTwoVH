package com.example.moviescodechallenge.view

import android.app.Application
import android.content.Context

class CustomApplication : Application(){

    companion object{
        private var movieApplicationContext : Context? = null

        fun getApplication(): Context{
            return movieApplicationContext!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        movieApplicationContext = baseContext
    }
}