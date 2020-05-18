@file:Suppress("DEPRECATION")

package com.example.toiletfinderfixed

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.textclassifier.TextLinks
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONObject

class DetailActivity : AppCompatActivity() {
    var arrayList = ArrayList<Reviews>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)


        recycleViewReview.setHasFixedSize(true)
        recycleViewReview.layoutManager = LinearLayoutManager(this)

        val reviews = listOf<Review>(
            Review(1, "sugoi", 4.0F),
            Review(2, "mantap njay bersih banget", 4.5F),
            Review(3, "Puas nongkrong sisana", 4.0F),
            Review(4, "mabar bang", 4.5F),
            Review(5, "gila", 4.5F),
            Review(6, "suka saya", 4.5F),
            Review(7, "sedang mencari pacar", 4.5F),
            Review(8, "mancing mania.....", 4.5F)
            )


        backButton.setOnClickListener{
            Intent(this, MapsActivity::class.java).also{ startActivity(it) }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAllStudents()
    }

    private fun loadAllStudents() {
        val loading = ProgressDialog(this)
        loading.setMessage("Memuat data...")
        loading.show()

        AndroidNetworking.get(ApiEndPoint.READ)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {

                override fun onResponse(response: JSONObject?) {
                    arrayList.clear()

                    val jsonArray = response?.optJSONArray("result")

                    if (jsonArray?.length() == 0) {
                        loading.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Student data is empty, Add the data first",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    for (i in 0 until jsonArray?.length()!!) {

                        val jsonObject = jsonArray.optJSONObject(i)
                        arrayList.add(
                            Reviews(
                                jsonObject.getString("id_user"),
                                jsonObject.getString("id_toilet"),
                                jsonObject.getString("rating"),
                                jsonObject.getString("review")
                            )
                        )

                        if (jsonArray.length() - 1 == i) {

                            loading.dismiss()
                            val adapter = RVAdapterReview(applicationContext, arrayList)
                            adapter.notifyDataSetChanged()
                            recycleViewReview.adapter = adapter

                        }

                    }

                }

                override fun onError(anError: ANError?) {
                    loading.dismiss()
                    Log.d("ONERROR", anError?.errorDetail?.toString())
                    Toast.makeText(applicationContext, "Connection Failure", Toast.LENGTH_SHORT)
                        .show()
                }
            })


    }



}