package com.alexandr_yurchenko.radiobeach

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIMER: Long = 3000L
    private  lateinit var shared : SharedPreferences
    private var allow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        shared = getSharedPreferences("MySharedPref" , Context.MODE_PRIVATE)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        checkFirebase()

        Handler().postDelayed(
            {
                if (shared.getString("Subscription","Empty") == "Success"){
                    val currentDate = Date()
                    val cal = Calendar.getInstance()
                    cal.time = currentDate
                    var offTime = shared.getString("offTime","Error")
                    if (offTime == "Error"){
                        offTime = cal.time.toString()
                    }
                    val expiredDate: Date = stringToDate(offTime, "EEE MMM d HH:mm:ss zz yyyy")!!
                    if (cal.time < expiredDate){
                        //Toast.makeText(this,"У вас уже есть подписка!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, SubscriptionActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    /*AwesomeDialog.build(this)
                        .title("Радио Пляж",
                            titleColor = ContextCompat.getColor(this, android.R.color.black))
                        .icon(R.drawable.money)
                        .body("Выберите один из способов платежа за Подписку!",
                            color = ContextCompat.getColor(this, android.R.color.black))
                        .onPositive("Перевод картой") {
                            val intent = Intent(this,PayActivity::class.java)
                            intent.putExtra("url","Card Number")
                            startActivity(intent)
                        }*/
                }

                /*val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)*/
                finish()
            }, SPLASH_TIMER
        )
    }

    private fun stringToDate(aDate: String?, aFormat: String): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat(aFormat)
        return simpledateformat.parse(aDate, pos)
    }

    private fun checkFirebase(){
        // Write a message to the database
        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("radio")

        //myRef.setValue("allow")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue(String::class.java)
                if (value == "allow"){
                    allow = true
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Toast.makeText(this@SplashActivity, "Failed to Detect Allow",Toast.LENGTH_SHORT).show()
            }
        })
    }
}