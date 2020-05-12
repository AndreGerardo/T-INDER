package com.example.toiletfinderfixed

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameET = findViewById(R.id.etUsername)


        btnTakePhoto.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type = "image/*"
                startActivityForResult(it, 0)
            }
        }

        btnBack.setOnClickListener{
            Intent(this, MapsActivity::class.java).also{ startActivity(it) }
        }

        val sharedPref = getSharedPreferences("LoginInformation", 0) ?: return
        val usernameFromSharedPref = sharedPref.getString("username", "")
        val genderFromSharedPref = sharedPref.getString("gender", "")


        etUsername.setText(usernameFromSharedPref)
        tvGender.text = "Gender : $genderFromSharedPref"

        btnSave.setOnClickListener {
            val username = usernameET.text.toString()
            val url = "http://10.0.2.2/toilet_finder/user/update.php"

            val params = HashMap<String, String>()
            params["usernameBaru"] = "$username"
            params["username"] = "$usernameFromSharedPref"

            val jsonObject = JSONObject(params as Map<*, *>)
            val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener {response ->
                    with(sharedPref.edit()) {
                        putString("username", "$username")
                        //putInt("isAdmin", "${response["isAdmin"]}".toInt())
                        commit()
                    }
                    Toast.makeText(this@MainActivity, "${response["message"]}", Toast.LENGTH_LONG).show()
                }, Response.ErrorListener {  error ->
                    Toast.makeText(this@MainActivity, "$error", Toast.LENGTH_LONG).show()
                })
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            VolleySingleton.getInstance(this).addToRequestQueue(request)
            Intent(this, MapsActivity::class.java).also {
                startActivity(it)
            }
        }


        btnLogOut.setOnClickListener {

            with(sharedPref.edit()) {
                clear()
                apply()
                finish()
            }
            Intent(this, MapsActivity::class.java).also {
                startActivity(it)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 0){
            val uri = data?.data
            btnTakePhoto.setImageURI(uri)
        }
    }



}
