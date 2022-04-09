package com.alexandr_yurchenko.radiobeach

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.alex_yurchenko.radiobeach.model.history.RadioHistory
import com.alexandr_yurchenko.radiobeach.client.Common
import com.alexandr_yurchenko.radiobeach.client.RetrofitServices
import com.alexandr_yurchenko.radiobeach.model.deep.RadioDeep
import com.alexandr_yurchenko.radiobeach.model.main.RadioMain
import com.alexandr_yurchenko.radiobeach.model.online.RadioOnline
import com.alexandr_yurchenko.radiobeach.model.relax.RadioRelax
import com.example.awesomedialog.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mxn.soul.flowingdrawer_core.ElasticDrawer
import com.mxn.soul.flowingdrawer_core.ElasticDrawer.OnDrawerStateChangeListener
import com.mxn.soul.flowingdrawer_core.FlowingDrawer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var imagePlayPause: ImageView
    private lateinit var rotation: ImageView
    private lateinit var mainProgress: ProgressBar
    private lateinit var flowing: FlowingDrawer
    private lateinit var textRadio: TextView
    private lateinit var nameMusicMain: TextView
    private var mediaPlayer: MediaPlayer? = null
    private val INTERNET_PERMISSION_CODE = 100
    private var isPlaying = false
    private val audioUrlGeneral = "https://listen2.myradio24.com/1632"
    private val audioUrlOnline = "https://listen7.myradio24.com/8382"
    private val audioUrlHistory = "https://listen7.myradio24.com/station30"
    private var currentUrl = "https://listen2.myradio24.com/1632"

    private  lateinit var shared : SharedPreferences
    private lateinit var mService: RetrofitServices
    private var allow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkFirebase()

        mService = Common.retrofitService

        shared = getSharedPreferences("MySharedPref" , Context.MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), INTERNET_PERMISSION_CODE);
        }

        imagePlayPause = findViewById(R.id.playPause)
        rotation = findViewById(R.id.rotation)
        mainProgress = findViewById(R.id.mainProgress)
        flowing = findViewById(R.id.drawerlayout)
        textRadio = findViewById(R.id.textRadio)
        nameMusicMain = findViewById(R.id.nameMusicMain)
        flowing.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL)
        flowing.setOnDrawerStateChangeListener(object : OnDrawerStateChangeListener {
            override fun onDrawerStateChange(oldState: Int, newState: Int) {
                if (newState == ElasticDrawer.STATE_CLOSED) {
                    Log.i("MainActivity", "Drawer STATE_CLOSED")
                    println("Drawer STATE_CLOSED")
                }
            }

            override fun onDrawerSlide(openRatio: Float, offsetPixels: Int) {
                Log.i("MainActivity", "openRatio=$openRatio ,offsetPixels=$offsetPixels")
                println("openRatio=$openRatio ,offsetPixels=$offsetPixels")
            }
        })

        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                //your method
                if (currentUrl == audioUrlGeneral){
                    getRadioMain()
                } else if (currentUrl == audioUrlOnline){
                    getRadioOnline()
                } else if (currentUrl == audioUrlHistory){
                    getRadioHistory()
                }
            }
        }, 0, 10000) //put here time 1000 milliseconds=1 second


        imagePlayPause.setOnClickListener {
            if (currentUrl == audioUrlGeneral){
                getRadioMain()
            } else if (currentUrl == audioUrlOnline){
                getRadioOnline()
            } else if (currentUrl == audioUrlHistory){
                getRadioHistory()
            }
            if (isPlaying){
                pause()
            } else{
                play(currentUrl)
            }
            //rotateTheDisk()
            blinkTheDisk()
        }
    }

    /*private fun rotateTheDisk() {
        if (!isPlaying) return
        rotation.animate().setDuration(100).rotation(rotation.rotation + 2f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    rotateTheDisk()
                    super.onAnimationEnd(animation)
                }
            })
    }*/

    private fun blinkTheDisk(){
        if (!isPlaying){
            rotation.clearAnimation()
        } else {
            val animation: Animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.rotate
            )
            rotation.startAnimation(animation)
        }
    }

    fun MenuMain(view: View) {
        flowing.openMenu()
    }

    private fun pause(){
        if (isPlaying){
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            imagePlayPause.setImageResource(R.drawable.play)
            rotation.clearAnimation()
            isPlaying = false
            Toast.makeText(this, "Radio has been paused", Toast.LENGTH_SHORT).show()
        }
    }

    private fun play(url: String){
        mainProgress.visibility = View.VISIBLE
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setDataSource(url)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            mainProgress.visibility = View.INVISIBLE
            imagePlayPause.setImageResource(R.drawable.stop)
            isPlaying = true
            Toast.makeText(this, "Radio started playing..", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /*private fun play(){
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying){
                mediaPlayer!!.stop()
                mediaPlayer!!.reset()
                mediaPlayer!!.release()
                mediaPlayer = null
                imagePlayPause.setImageResource(R.drawable.play)
                //Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    mediaPlayer!!.setDataSource(audioUrl)
                    mediaPlayer!!.prepare()
                    mediaPlayer!!.start()
                    //Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()
                    imagePlayPause.setImageResource(R.drawable.pause)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == INTERNET_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Internet Permission Granted", Toast.LENGTH_SHORT) .show();
            }
            else {
                Toast.makeText(this, "Internet Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun FreeMusic(view: View) {
        currentUrl = audioUrlGeneral
        pause()
        play(currentUrl)
        getRadioMain()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun OnlineMusic(view: View) {
        currentUrl = audioUrlOnline
        pause()
        play(currentUrl)
        getRadioOnline()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun HistoryMusic(view: View) {
        currentUrl = audioUrlHistory
        pause()
        play(currentUrl)
        getRadioHistory()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    private fun stringToDate(aDate: String?, aFormat: String): Date? {
        if (aDate == null) return null
        val pos = ParsePosition(0)
        val simpledateformat = SimpleDateFormat(aFormat)
        return simpledateformat.parse(aDate, pos)
    }

    fun Subscription(view: View) {

        if (shared.getString("Subscription","Empty") == "Success" && allow){
            val currentDate = Date()
            val cal = Calendar.getInstance()
            cal.time = currentDate
            var offTime = shared.getString("offTime","Error")
            if (offTime == "Error"){
                offTime = cal.time.toString()
            }
            val expiredDate: Date = stringToDate(offTime, "EEE MMM d HH:mm:ss zz yyyy")!!
            if (cal.time < expiredDate){
                Toast.makeText(this,"У вас уже есть подписка!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, SubscriptionActivity::class.java)
                startActivity(intent)
            } else if (cal.time == expiredDate){
                AwesomeDialog.build(this)
                    .title("Радио Пляж",
                        titleColor = ContextCompat.getColor(this, android.R.color.black))
                    .icon(R.drawable.money)
                    .body("Сегодня у вас закончиться подписка, хотите сейчас переоформить?",
                        color = ContextCompat.getColor(this, android.R.color.black))
                    .onPositive("Да, хочу сейчас!") {
                        val intent = Intent(this, PayActivity::class.java)
                        startActivity(intent)
                    }.onNegative("Нет, может позже!"){
                        val intent = Intent(this, SubscriptionActivity::class.java)
                        startActivity(intent)
                    }
            } else {
                val intent = Intent(this, PayActivity::class.java)
                startActivity(intent)
            }
        } else {
            if (allow){
                val intent = Intent(this, PayActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@MainActivity, "Coming Soon!",Toast.LENGTH_SHORT).show()
            }
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
        pause()
        //rotateTheDisk()
        blinkTheDisk()
        flowing.closeMenu()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun OurVk(view: View) {
        //    https://vk.com/beachradiorussia
        val url = "https://vk.com/beachradiorussia"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.component =
            ComponentName("com.vkontakte.android", "com.google.android.vkontakte")
        val manager: PackageManager = packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        if (infos.size > 0) {
            startActivity(intent)
        } else {
            val vk = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            startActivity(vk)
        }
        flowing.closeMenu()
    }

    fun OurTelegram(view: View) {
        //  https://t.me/+7qa_gY-Z8rYwMGUy
        val url = "https://t.me/+7qa_gY-Z8rYwMGUy"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.component =
            ComponentName("org.telegram.messenger", "org.telegram.messenger")
        val manager: PackageManager = packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        if (infos.size > 0) {
            startActivity(intent)
        } else {
            val vk = Intent(Intent.ACTION_VIEW,Uri.parse(url))
            startActivity(vk)
        }
        flowing.closeMenu()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun OurYoutubeChannel(view: View) {
        //  https://www.youtube.com/channel/UCk_7AiozK9oAsjgMAuqjBRw
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.youtube.com/channel/UCk_7AiozK9oAsjgMAuqjBRw"))
        intent.component =
            ComponentName("com.google.android.youtube", "com.google.android.youtube.PlayerActivity")
        val manager: PackageManager = packageManager
        val infos = manager.queryIntentActivities(intent, 0)
        if (infos.size > 0) {
            startActivity(intent)
        } else {
            val youtube = Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/channel/UCk_7AiozK9oAsjgMAuqjBRw"))
            startActivity(youtube)
        }
        flowing.closeMenu()
    }

    fun OurWebSite(view: View) {
        // https://radio-beach.net/
        val uri = Uri.parse("https://radio-beach.net/")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
        flowing.closeMenu()
    }

    fun OurPhoneNumber(view: View) {
        // +79782835038
        AwesomeDialog.build(this)
            .title("Радио Пляж",
                titleColor = ContextCompat.getColor(this, android.R.color.black))
            .icon(R.drawable.phone)
            .body("Вы можете связатся с нами! Мы рады свами сотрудничать!",
                color = ContextCompat.getColor(this, android.R.color.black))
            .onPositive("+79782835038") {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:+79782835038")
                startActivity(intent)
            }
        flowing.closeMenu()
    }

    fun OurEmail(view: View) {
        //      radiobeach@yndex.ru           beachradio@yandex.ru
        AwesomeDialog.build(this)
            .title("Радио Пляж",
                titleColor = ContextCompat.getColor(this, android.R.color.black))
            .icon(R.drawable.email)
            .body("Выберите один из способов связи!",
                color = ContextCompat.getColor(this, android.R.color.black))
            .onPositive("radiobeach@yndex.ru") {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "radiobeach@yndex.ru")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Радио Пляж")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
            .onNegative("beachradio@yandex.ru") {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, "beachradio@yandex.ru")
                intent.putExtra(Intent.EXTRA_SUBJECT, "Радио Пляж")
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                }
            }
        flowing.closeMenu()
    }

    private fun getRadioMain() {
        mService.getRadioMain().enqueue(object : Callback<RadioMain> {
            override fun onFailure(call: Call<RadioMain>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<RadioMain>, response: Response<RadioMain>) {
                println(response.body())
                textRadio.text = response.body()!!.title
                nameMusicMain.text = response.body()!!.song
                // http://myradio24.com/artists/fbfd9a834a9f8019d4b2ecaee94fc081.jpg
            }
        })
    }

    private fun getRadioOnline() {
        mService.getRadioOnline().enqueue(object : Callback<RadioOnline> {
            override fun onFailure(call: Call<RadioOnline>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<RadioOnline>, response: Response<RadioOnline>) {
                println(response.body())
                textRadio.text = response.body()!!.title
                nameMusicMain.text = response.body()!!.song
            }
        })
    }

    private fun getRadioDeep() {
        mService.getRadioDeep().enqueue(object : Callback<RadioDeep> {
            override fun onFailure(call: Call<RadioDeep>, t: Throwable) {
                println(t)

            }

            override fun onResponse(call: Call<RadioDeep>, response: Response<RadioDeep>) {
                println(response.body())
                textRadio.text = response.body()!!.title
                nameMusicMain.text = response.body()!!.song
            }
        })
    }

    private fun getRadioHistory() {
        mService.getRadioHistory().enqueue(object : Callback<RadioHistory> {
            override fun onFailure(call: Call<RadioHistory>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<RadioHistory>, response: Response<RadioHistory>) {
                println(response.body())
                textRadio.text = response.body()!!.title
                nameMusicMain.text = response.body()!!.song
            }
        })
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
                Toast.makeText(this@MainActivity, "Failed to Detect Allow",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getRadioRelax() {
        mService.getRadioRelax().enqueue(object : Callback<RadioRelax> {
            override fun onFailure(call: Call<RadioRelax>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<RadioRelax>, response: Response<RadioRelax>) {
                println(response.body())
                textRadio.text = response.body()!!.title
                nameMusicMain.text = response.body()!!.song
            }
        })
    }
    override fun onStop() {
        super.onStop()
        Timer().cancel()
    }
}