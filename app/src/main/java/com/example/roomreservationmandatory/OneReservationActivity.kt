package com.example.roomreservationmandatory

import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewDebug
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.io.IOException

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class OneReservationActivity : AppCompatActivity() {
    private var OneReservation: JsonReservationModel? = null
    private var mAuth: FirebaseAuth? = null
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private var conn = "https://anbo-roomreservationv3.azurewebsites.net/api/Reservations/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_one_reservation)
        val intent = intent
        OneReservation = intent.getSerializableExtra(RESERVATION) as JsonReservationModel

        mAuth = FirebaseAuth.getInstance()

        //This is for redirecting to the login activity IF they're not logged in already.
        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                val intent = Intent(baseContext, Activity_Login::class.java)
                startActivity(intent)
            }
        }

        val ReservationFromTimeTextView = findViewById<TextView>(R.id.ReservationFromTimeTextView)
        val ReservationToTimeTextView = findViewById<TextView>(R.id.ReservationToTimeTextView)
        val ReservationUserIDTextView = findViewById<TextView>(R.id.ReservationUserIDTextView)
        val ReservationPurposeTextView = findViewById<TextView>(R.id.ReservationPurposeTextView)
        ReservationFromTimeTextView.text = "Tidspunkt fra: " + OneReservation!!.fromTime
        ReservationToTimeTextView.text = "Tidspunkt til " + OneReservation!!.toTime
        ReservationUserIDTextView.text = "Bruger ID: " + OneReservation!!.userId
        ReservationPurposeTextView.text = "Formål: " + OneReservation!!.purpose

    }

    override fun onStart() {
        super.onStart()
        mAuth!!.addAuthStateListener(mAuthListener!!)

    }

    fun ButtonReservationDelete(view: View) {
        conn += OneReservation!!.id
        val client = OkHttpClient()
        val request = Request.Builder().url(conn).delete().build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, ex: IOException) {
                runOnUiThread {
                    val MessageTextView = findViewById<TextView>(R.id.MessageTextView)
                    MessageTextView.text = ex.message
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@OneReservationActivity, "Reservation slettet!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        })
    }

    companion object {
        val RESERVATION = "RESERVATION"
    }
}
