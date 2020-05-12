package com.example.toiletfinderfixed

import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_second.*
import org.json.JSONObject


class SecondActivity : AppCompatActivity(){

    private lateinit var usernameET : EditText
    private lateinit var emailET : EditText
    private lateinit var passwordET : EditText
    private lateinit var confirmPasswordET : EditText
    private lateinit var genderRadioGrp : RadioGroup
    private var gender  = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        usernameET = findViewById(R.id.etUsername)
        emailET = findViewById(R.id.etEmail)
        passwordET = findViewById(R.id.etPassword)
        confirmPasswordET = findViewById(R.id.etConfirmPassword)
        genderRadioGrp = findViewById(R.id.radioGrp)

//        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
//        with (sharedPref.edit()) {
//            putInt("SCORE", 2000)
//            commit()
//        }

        btnSignUp.setOnClickListener{
//            val score = sharedPref.getInt("SCORE", 0)
//            Toast.makeText(this@SecondActivity, "$score", Toast.LENGTH_SHORT).show()
            val username = usernameET.text.toString()
            val email = emailET.text.toString()
            val password = passwordET.text.toString()
            val confirmPassword = confirmPasswordET.text.toString()
            val url = "http://10.0.2.2/toilet_finder/user/create.php"

            val params = HashMap<String,String>()
            params["username"] = "$username"
            params["email"] = "$email"
            params["password"] = "$password"
            params["gender"] = "$gender"

            val jsonObject = JSONObject(params as Map<*, *>)
            // Volley post request with parameters
            val request = JsonObjectRequest(Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    // Process the json
                    try {
                        val responseMessage = response["message"]

                        Log.d("RES", "$response")
                        if (!response.has("error")) {

                             Toast.makeText(this@SecondActivity, "$responseMessage", Toast.LENGTH_SHORT).show()
                             Intent(this, LoginActivity::class.java).also{ startActivity(it)}
                        } else {
                             val responseError = response["error"]
                             Toast.makeText(this@SecondActivity, "$responseMessage\n$responseError", Toast.LENGTH_SHORT).show()
                        }


                    }catch (e:Exception){
                        Toast.makeText(this@SecondActivity, "$e", Toast.LENGTH_SHORT).show()
                        Log.e("RESER", "$e")
                    }

                }, Response.ErrorListener{ error ->
                    // Error in request
                    //val errorNetwork = $error["networkResponse"]
                    Toast.makeText(this@SecondActivity, "Required field is empty", Toast.LENGTH_SHORT).show()
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
            }

        }

    }

}

