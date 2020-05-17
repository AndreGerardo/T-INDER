package com.example.toiletfinderfixed

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_add_toilet.*
import org.json.JSONObject

class AddToiletActivity: AppCompatActivity() {

    private lateinit var tname : EditText
    private lateinit var topenTime : EditText
    private lateinit var tcloseTime : EditText
    private lateinit var genderRadioGrp : RadioGroup
    private lateinit var tipeRadioGrp : RadioGroup
    private lateinit var currentLatLng: Location
    private var tipe  = ""
    private var gender  = ""

    //Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_toilet)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        tname = findViewById(R.id.addToiletName)
        topenTime = findViewById(R.id.addWaktuBuka)
        tcloseTime = findViewById(R.id.addWaktuTutup)
        genderRadioGrp = findViewById(R.id.radioGrp)
        tipeRadioGrp = findViewById(R.id.radioGrpType)

        getLastKnownLocation()

        btnAddToilet.setOnClickListener{
            val nama = tname.text.toString()
            val jam_buka = topenTime.text.toString()
            val jam_tutup = tcloseTime.text.toString()
            val latitude = currentLatLng.latitude.toString()
            val longitude = currentLatLng.longitude.toString()
            val url = "http://10.0.2.2/toilet_finder/toilet/create.php"

            val params = HashMap<String,String>()
            params["nama"] = "$nama"
            params["jam_buka"] = "$jam_buka"
            params["jam_tutup"] = "$jam_tutup"
            params["gender"] = "$gender"
            params["tipe"] = "$tipe"
            params["latitude"] = "$latitude"
            params["longitude"] = "$longitude"

            val jsonObject = JSONObject(params as Map<*, *>)

            val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    // Process the json
                    try {
                        val responseMessage = response["message"]

                        Log.d("RES", "$response")
                        if (!response.has("error")) {

                            Toast.makeText(this@AddToiletActivity, "$responseMessage", Toast.LENGTH_SHORT).show()
                            Intent(this, MapsActivity::class.java).also{ startActivity(it)}
                        } else {
                            val responseError = response["error"]
                            Toast.makeText(this@AddToiletActivity, "$responseMessage\n$responseError", Toast.LENGTH_SHORT).show()
                        }


                    }catch (e:Exception){
                        Toast.makeText(this@AddToiletActivity, "$e", Toast.LENGTH_SHORT).show()
                        Log.e("RESER", "$e")
                    }

                }, Response.ErrorListener{ error ->
                    // Error in request
                    //val errorNetwork = $error["networkResponse"]
                    Toast.makeText(this@AddToiletActivity, "Required field is empty", Toast.LENGTH_SHORT).show()
                    Intent(this, MapsActivity::class.java).also{ startActivity(it)}
                    Log.e("RESER", "$error")
                })

            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            VolleySingleton.getInstance(this).addToRequestQueue(request)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(){
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLatLng = location
                    currentLatLng = location
                }
            }
    }

    fun onRadioButtonClicked(view: View){
        if(view is RadioButton){
            val checked = view.isChecked

            when (view.getId()){
                R.id.radioMale ->
                    if (checked){
                        gender = "Male"
                    }
                R.id.radioFemale ->
                    if (checked){
                        gender = "Female"
                    }
                R.id.radioBoth ->
                    if (checked){
                        gender = "Male/Female"
                    }
            }

        }

    }

    fun onRadioButtonClickedType(view: View){
        if(view is RadioButton){
            val checked = view.isChecked

            when (view.getId()){
                R.id.radioPublic ->
                    if (checked){
                        tipe = "Public"
                    }
                R.id.radioStaff ->
                    if (checked){
                        tipe = "Staff"
                    }
            }

        }

    }

}