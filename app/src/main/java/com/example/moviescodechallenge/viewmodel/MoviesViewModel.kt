package com.example.moviescodechallenge.viewmodel

import android.content.Context
import android.util.Log
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
import java.util.*

class MoviesViewModel : ViewModel() {

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
            readFromRemoteAndSavesResponse()
        }
    }

    private fun readFromCache() {
        val moviesDao = MoviesRoomDB.getDatabase(
            CustomApplication.getApplication()
        ).movieDao()
        viewModelScope.launch {
            moviesDao.getMoviesCache()?.let {
                dataMovieList.value = PokoMovieList(it)
            }
        }
    }

    private fun readFromRemoteAndSavesResponse() {
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

    private fun reUseCachedData(): Boolean {
        val sharedPreferences = CustomApplication.getApplication()
            .getSharedPreferences("Network_Calls", 0)
        val previousTime = sharedPreferences
            .getLong("last_network_call", 0)
        return (Date().time - previousTime) <= timeStampTest
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
        const val timeStampCache = 1000 * 60 * 10
        const val timeStampTest = 1000 * 60 * 2
    }
}






