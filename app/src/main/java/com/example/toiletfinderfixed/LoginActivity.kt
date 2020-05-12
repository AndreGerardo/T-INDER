package com.example.toiletfinderfixed

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameET: EditText
    private lateinit var passwordET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameET = findViewById(R.id.etUnameSignIn)
        passwordET = findViewById(R.id.etPasswordSignIn)

        val sharedPref = getSharedPreferences("LoginInformation", 0) ?: return

        btnRegister.setOnClickListener {
            Intent(this, SecondActivity::class.java).also {
                startActivity(it)
            }
        }

        btnSignUp.setOnClickListener {
            val username = usernameET.text.toString()
            val password = passwordET.text.toString()
            val url = "http://10.0.2.2/toilet_finder/user/login.php"

            val params = HashMap<String, String>()
            params["username"] = "$username"
            params["password"] = "$password"

            val jsonObject = JSONObject(params as Map<*, *>)
            val request = JsonObjectRequest(Request.Method.POST, url, jsonObject,
                Response.Listener { response ->
                   // Toast.makeText(this@LoginActivity, "${response["username"]}", Toast.LENGTH_SHORT).show()

                    with(sharedPref.edit()) {
                        putString("username", "${response["username"]}")
                        putString("gender", "${response["gender"]}")
                        putInt("isAdmin", "${response["isAdmin"]}".toInt())
                        commit()
                    }
                    Intent(this, MainActivity::class.java).also{
                        startActivity(it)
                    }
                }, Response.ErrorListener { error ->
                    //Log.d("networkkkk", "${error.networkResponse.data}")
                            Toast.makeText(this@LoginActivity, "Username or password is incorrect!", Toast.LENGTH_SHORT).show()
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
}