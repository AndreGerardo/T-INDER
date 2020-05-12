package com.example.toiletfinderfixed

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.json.JSONArray
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener  {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private lateinit var currentLatLng: LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        val sharedPref = getSharedPreferences("LoginInformation", 0) ?: return
        val isAdminFromPref = sharedPref.getInt("isAdmin", 0)


        if(isAdminFromPref == 0) {
            fabForAdmin.visibility = View.GONE
        }
        fabMyAccount.setOnClickListener{
            val usernameFromSharedPref = sharedPref.getString("username", "")
         //   val genderFromSharedPref = sharedPref.getString("gender", "")
                   // Toast.makeText(this@MapsActivity, "$usernameFromSharedPref", Toast.LENGTH_SHORT).show()
                if(usernameFromSharedPref == "") {
                    Intent(this, LoginActivity::class.java).also {
                        startActivity(it)
                    }
                } else {
                    Intent(this, MainActivity::class.java).also {
                        startActivity(it)
                    }

                }
//            val queue = Volley.newRequestQueue(this)
//            val url = "http://10.0.2.2/toilet_finder/read.php"
//
//            val stringRequest = StringRequest(Request.Method.GET, url,
//                Response.Listener<String>{response ->
//                    Toast.makeText(this@MapsActivity, "$response", Toast.LENGTH_SHORT).show()
//                    Log.d("REV", "$response")
//                }, Response.ErrorListener { error ->
//                    Toast.makeText(this@MapsActivity, "$error", Toast.LENGTH_LONG).show()
//                    Log.e("ERR", "$error")}
//                )
//            queue.add(stringRequest)
        }

        fabForAdmin.setOnClickListener{
            Intent(this, AddToiletActivity::class.java).also {
                startActivity(it)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

        createLocationRequest()

    }



    private var mToilets: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        cvMap.visibility = View.GONE

        val url = "http://10.0.2.2/toilet_finder/toilet/read.php"

        val jsonObject = JSONArray()
        val request = JsonArrayRequest(Request.Method.POST, url, jsonObject,
            Response.Listener { response ->
                Log.d("LOCATION", "${response.getJSONObject(0)["latitude"]}")

                //Toast.makeText(this@MapsActivity, "${response[0]}", Toast.LENGTH_LONG).show()

                for(x in 0 until response.length()){
                    var latitude = "${response.getJSONObject(x)["latitude"]}".toDouble()
                    var longitude = "${response.getJSONObject(x)["longitude"]}".toDouble()
                    Log.d("LATLNG", "$latitude")
                    Log.d("LATLNG", "$longitude")
                    var nama = response.getJSONObject(x)["nama"]
                    var toilets = LatLng(latitude, longitude)
                    var gender = response.getJSONObject(x)["gender"]
                    Log.d("LATLNG", "$toilets")

                    mToilets = map.addMarker(
                        MarkerOptions()
                            .position(toilets)
                            .title(nama as String?)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
                            .snippet("$gender")
                    )
//                    val customInfo = HashMap<String, String>()
//                    customInfo["gender"] = "$gender"


                }

            }, Response.ErrorListener { error ->
                Log.d("LOCERROR", "$error")
                Toast.makeText(this@MapsActivity, "$error", Toast.LENGTH_SHORT).show()
            })

        request.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            // 0 means no retry
            0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
            1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)

        // Add a marker in Sydney and move the camera
        map.getUiSettings().setZoomControlsEnabled(true)
        map.setOnMarkerClickListener(this)

        setUpMap()

        map.isMyLocationEnabled = true

// 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))
            }
        }

    }

    override fun onMarkerClick(p0: Marker) : Boolean{
        cvMap.visibility = View.VISIBLE
        Log.d("ISI", "$p0")
        tvCvNama.text = p0.getTitle()
        tvCvGender.text = p0.snippet

//        val result = arrayOf<Float>().toFloatArray()
        var result = FloatArray(1)
        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude, p0.position.latitude, p0.position.longitude, result)
        Log.d("LATLNG", "${result[0]}")

        tvCvJarak.text = "${"%.2f".format(result[0])} meter"
        map!!.animateCamera(CameraUpdateFactory.newLatLng(p0.position))

        btnCvDetails.setOnClickListener {
            Intent(this, DetailActivity::class.java).also {
                startActivity(it)
            }
        }

        return true
    }

    private lateinit var locationCallback: LocationCallback
    // 2
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
    }


    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        // 2
        val titleStr = getAddress(location)  // add these two lines
        markerOptions.title(titleStr)
        map.addMarker(markerOptions)
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geocoder = Geocoder(this)
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && !addresses.isEmpty()) {
                address = addresses[0]
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(i)
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }

        return addressText
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    // 1
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
        }
    }

    // 2
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // 3
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    fun onSearch(view: View){
        if(view.id == R.id.btnSearch){
            val location = etSearchHere.text.toString()
            var addressList : List<Address>? = null
            val options = MarkerOptions()

            if(location != ""){
                val geocoder = Geocoder(this)
                try{
                    addressList = geocoder.getFromLocationName(location,5)
                } catch (e :IOException){
                    e.printStackTrace()
                }

                for(i in addressList!!.indices){

                    val address = addressList[i]
                    val latLng = LatLng(address.latitude,address.longitude)
                    options.position(latLng)
                    options.title("$location")
                    map!!.addMarker(options)
                    map!!.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                }

            }

        }
    }


}
