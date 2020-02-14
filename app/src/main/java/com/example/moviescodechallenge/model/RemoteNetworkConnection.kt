package com.example.moviescodechallenge.model

import android.content.Context
import android.util.Log
import com.example.moviescodechallenge.view.CustomApplication
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import kotlin.math.max

interface RemoteNetworkConnection{
    @GET("movies")
    fun getAllMovies() : Call<PokoMovieList>

    @GET("forecast")
    fun getWeather(
        @Query("zip") zipCode: String,
        @Query("appid") appKey: String
    ): Call<Any>


//https://samples.openweathermap.org/data/2.5/
// forecast?zip=232323&appid=b6907d289e10d714a6e88b30761fae22
    companion object{

        val baseUrl = "https://movies-sample.herokuapp.com/api/"

        fun initRetrofit(): RemoteNetworkConnection{
            val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(RemoteNetworkConnection::class.java)
        }

        fun  initRetrofit2() =
            Retrofit.Builder()
                .client(checkCacheAge(CustomApplication.getApplication()))
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RemoteNetworkConnection::class.java)

        /**
         * Functions that evaluates if a Network call
         * needs to happen
         */
        fun checkCacheAge(context: Context): OkHttpClient{
            val maxSize: Long = 1 * 1024 * 1024

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS



            val cache =  Cache(context.cacheDir,
                maxSize)
            val client: OkHttpClient =
                OkHttpClient.Builder()
                    //.cache(cache)
                    .addNetworkInterceptor {
                        var request = it.request()

                        Log.d("InterfaceRemoste",request.cacheControl().onlyIfCached().toString())

                        request.newBuilder()
                            .header("Cache-Control",
                                "public, max-age=" + 60 * 1)
                            .build()
                        it.proceed(request)
                    }
                    .addInterceptor(httpLoggingInterceptor)
                    .build()
            return client
            //true means read from Room
            //false means save and read from Room
        }
    }
}