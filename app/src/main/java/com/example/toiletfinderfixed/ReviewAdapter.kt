package com.example.toiletfinderfixed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reveiw.view.*

class ReviewAdapter(val Reviews : List<Review>) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_reveiw,parent,false)
        )
    }

    override fun getItemCount()= Reviews.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val Review = Reviews[position]
        holder.view.textViewShortDesc.text = Review.reviews
        holder.view.textViewRating.text = Review.rating.toString()
    }
}