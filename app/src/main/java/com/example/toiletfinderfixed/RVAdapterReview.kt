package com.example.toiletfinderfixed

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_reveiw.view.*

class RVAdapterReview(private val context: Context, private val arrayList: ArrayList<Reviews>) : RecyclerView.Adapter<RVAdapterReview.Holder>() {

    class Holder(val view:View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_reveiw,parent,false)
        )
    }

    override fun getItemCount():Int= arrayList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {

        //holder.view.lbNimList.text = arrayList?.get(position)?.nim
        holder.view.textViewNama.text = arrayList?.get(position)?.id_user
        //holder.view.textViewRating.text = "id_toilet : "+arrayList?.get(position)?.id_toilet
        holder.view.textViewRating.text = arrayList?.get(position)?.rating
        holder.view.textViewShortDesc.text= arrayList?.get(position)?.review

    }



}