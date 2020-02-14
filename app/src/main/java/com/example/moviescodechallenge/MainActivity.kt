package com.example.moviescodechallenge

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescodechallenge.model.PokoMovieList
import com.example.moviescodechallenge.view.MoviesAdapter
import com.example.moviescodechallenge.viewmodel.MoviesViewModel

class MainActivity : AppCompatActivity() {

    val moviesViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    return MoviesViewModel() as T
                }
            }
        ).get(MoviesViewModel::class.java)
    }

    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        moviesViewModel.getErrorMessage().observe(
            this,
            object : Observer<String>{
                override fun onChanged(t: String?) {
                    Toast.makeText(this@MainActivity,
                        t,
                        Toast.LENGTH_SHORT)
                        .show()
                    recyclerView.layoutManager =
                        GridLayoutManager(this@MainActivity,
                            3)
                    val adapter = MoviesAdapter()
                    recyclerView.adapter = adapter
                }
            }
        )

        moviesViewModel.getDataMovieList().observe(
            this,
            object : Observer<PokoMovieList>{
                override fun onChanged(t: PokoMovieList?) {
                    //todo set RV adapter
                    recyclerView.layoutManager =
                        GridLayoutManager(this@MainActivity,
                            3)
                    val adapter = MoviesAdapter()
                    recyclerView.adapter = adapter
                    adapter.dataSet = t
                }
            }
        )

        recyclerView = findViewById(R.id.recycler_view)
        moviesViewModel.getMovies(this)
    }

}
