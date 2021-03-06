package com.example.moviescodechallenge.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moviescodechallenge.R
import com.example.moviescodechallenge.model.PokoMovieList

class MoviesAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataSet: PokoMovieList? = null
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) =
        when (viewType) {
            1 -> MoviesViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_layout,
                        parent,
                        false
                    )
            )
            else-> MoviesViewHolderNoData(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_layout_no_data,
                        parent,
                        false
                    )
            )
        }

    override fun getItemCount() =
        //when filter returns no matches...
        dataSet?.data?.size ?: 0

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is MoviesViewHolder -> {
                holder.tvGenre.text =
                    dataSet?.data?.get(position)?.genre
                        ?: "N/A"

                dataSet?.let {
                    holder.tvGenre.text = it.data[position].genre
                }
            }
            is MoviesViewHolderNoData -> {

            }
        }


    }


    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)
        return if(dataSet != null)
            1
        else 2
    }

}


