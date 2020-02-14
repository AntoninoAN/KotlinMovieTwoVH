package com.example.moviescodechallenge.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.util.TimeUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviescodechallenge.model.MoviesRoomDB
import com.example.moviescodechallenge.model.PokoMovieList
import com.example.moviescodechallenge.model.RemoteNetworkConnection
import com.example.moviescodechallenge.view.CustomApplication
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalTime
import java.util.*
import kotlin.coroutines.CoroutineContext

class MoviesViewModel : ViewModel() {
    //todo show all movies
    //todo receives input filter and show movies
    //todo error message
    //todo show no data filter input

    //network call ->
    // checks cache->
    //create SP and if time not valid
    // save into room DB -> show data
    // if time is valid -> show data from cache...
    //10 minutes timestamp
    private val errorMessage = MutableLiveData<String>()
    private val dataMovieList = MutableLiveData<PokoMovieList>()

    fun getErrorMessage(): LiveData<String> = errorMessage
    fun getDataMovieList(): LiveData<PokoMovieList> = dataMovieList


    fun getMovies(context: Context) {
        if (reUseCachedData()) {
            Log.d(TAG, "Reading from Cache")
            readFromCache()
        } else {
            Log.d(TAG, "Reading from Remote")
            readFromRemoteAndCacheResponse()
        }
    }

    private fun readFromCache() {
        val moviesDao = MoviesRoomDB.getDatabase(
            CustomApplication.getApplication()
        ).movieDao()
        Log.d(TAG, moviesDao.getMoviesCache().size.toString())
        moviesDao.getMoviesCache()?.let {
            dataMovieList.value = PokoMovieList(it)
        }
    }

    private fun readFromRemoteAndCacheResponse() {
        RemoteNetworkConnection.initRetrofit2().getAllMovies().enqueue(
            object : Callback<PokoMovieList> {
                override fun onFailure(call: Call<PokoMovieList>, t: Throwable) {
                    t.printStackTrace()
                    errorMessage.value = t.message
                }

                override fun onResponse(
                    call: Call<PokoMovieList>,
                    response: Response<PokoMovieList>
                ) {
                    saveTimestampSP()
                    val moviesDao = MoviesRoomDB.getDatabase(
                        CustomApplication.getApplication()
                    ).movieDao()
                    viewModelScope.launch {
                        moviesDao.saveCacheData(response.body()!!.data)
                    }
                    readFromCache()
                }
            }
        )
    }

    fun reUseCachedData(): Boolean {
        val sharedPreferences = CustomApplication.getApplication()
            .getSharedPreferences("Network_Calls", 0)
        val previousTime = sharedPreferences
            .getLong("last_network_call", 0)
        return (Date().time - previousTime) <= 1000 * 60 * 10
    }

    private fun saveTimestampSP() {
        val time = Date()

        val sharedPreferences = CustomApplication.getApplication()
            .getSharedPreferences("Network_Calls", 0)
        sharedPreferences.edit().putLong(
            "last_network_call",
            time.time
        ).commit()
    }

    companion object {
        const val TAG = "MoviesViewModel"
    }
}






