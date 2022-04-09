package com.alexandr_yurchenko.radiobeach

import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.alexandr_yurchenko.radiobeach.client.Common
import com.alexandr_yurchenko.radiobeach.client.RetrofitServices
import com.alexandr_yurchenko.radiobeach.model.deep.RadioDeep
import com.alex_yurchenko.radiobeach.model.history.RadioHistory
import com.alexandr_yurchenko.radiobeach.model.main.RadioMain
import com.alexandr_yurchenko.radiobeach.model.online.RadioOnline
import com.alexandr_yurchenko.radiobeach.model.relax.RadioRelax
import com.example.awesomedialog.*
import com.mxn.soul.flowingdrawer_core.ElasticDrawer
import com.mxn.soul.flowingdrawer_core.FlowingDrawer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class SubscriptionActivity : AppCompatActivity() {

    private lateinit var imagePlayPause: ImageView
    private lateinit var rot: ImageView
    private lateinit var flowing: FlowingDrawer
    private lateinit var nameRadio:TextView
    private lateinit var nameMusicSubs:TextView
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private val audioUrlGeneral = "https://listen2.myradio24.com/1632"
    private val audioUrlOnline = "https://listen7.myradio24.com/8382"
    private val audioUrlHistory = "https://listen7.myradio24.com/station30"
    private val audioUrlDeepHouse = "https://listen7.myradio24.com/31825"
    private val audioUrlRelax = "https://listen7.myradio24.com/9427"
    private var currentUrl = "https://listen2.myradio24.com/1632"

    private lateinit var mService: RetrofitServices

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subscription)

        mService = Common.retrofitService

        imagePlayPause = findViewById(R.id.playPauseSubs)
        rot = findViewById(R.id.rot)
        flowing = findViewById(R.id.drawerlayoutSubs)
        nameRadio = findViewById(R.id.nameRadio)
        nameMusicSubs = findViewById(R.id.nameMusicSubs)
        flowing.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL)
        flowing.setOnDrawerStateChangeListener(object : ElasticDrawer.OnDrawerStateChangeListener {
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
                } else if (currentUrl == audioUrlDeepHouse){
                    getRadioDeep()
                } else if (currentUrl == audioUrlRelax){
                    getRadioRelax()
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
            } else if (currentUrl == audioUrlDeepHouse){
                getRadioDeep()
            } else if (currentUrl == audioUrlRelax){
                getRadioRelax()
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
        rot.animate().setDuration(100).rotation(rot.rotation + 2f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    rotateTheDisk()
                    super.onAnimationEnd(animation)
                }
            })
    }*/

    private fun blinkTheDisk(){
        if (!isPlaying){
            rot.clearAnimation()
        } else {
            val animation: Animation = AnimationUtils.loadAnimation(
                applicationContext,
                R.anim.rotate
            )
            rot.startAnimation(animation)
        }
    }

    private fun pause(){
        if (isPlaying){
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            mediaPlayer = null
            imagePlayPause.setImageResource(R.drawable.play)
            rot.clearAnimation()
            isPlaying = false
            Toast.makeText(this, "Radio has been paused", Toast.LENGTH_SHORT).show()
        }
    }

    private fun play(url: String){
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setDataSource(url)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            imagePlayPause.setImageResource(R.drawable.stop)
            isPlaying = true
            Toast.makeText(this, "Radio started playing..", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun MenuSubs(view: View) {
        flowing.openMenu()
    }

    fun OurPhoneNumberSubs(view: View) {
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

    fun FreeMusicSubs(view: View) {
        currentUrl = audioUrlGeneral
        pause()
        play(currentUrl)
        getRadioMain()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun OnlineMusicSubs(view: View) {
        currentUrl = audioUrlOnline
        pause()
        play(currentUrl)
        getRadioOnline()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun HistoryMusicSubs(view: View) {
        currentUrl = audioUrlHistory
        pause()
        play(currentUrl)
        getRadioHistory()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun DeepHouse(view: View) {
        currentUrl = audioUrlDeepHouse
        pause()
        play(currentUrl)
        getRadioDeep()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun Relax(view: View) {
        currentUrl = audioUrlRelax
        pause()
        play(currentUrl)
        getRadioRelax()
        flowing.closeMenu()
        //rotateTheDisk()
        blinkTheDisk()
    }

    fun MusicToFlash(view: View){
        // https://disk.yandex.ru/d/_iWWVURzM-HZkw
        val uri = Uri.parse("https://disk.yandex.ru/d/_iWWVURzM-HZkw")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
        flowing.closeMenu()
    }

    fun Alarm(view: View) {
        val intent = Intent(this, AlarmActivity::class.java)
        startActivity(intent)
        flowing.closeMenu()
    }

    fun Service(view: View) {
        val intent = Intent(this, ServiceActivity::class.java)
        startActivity(intent)
        flowing.closeMenu()
    }

    fun Info(view: View) {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
        flowing.closeMenu()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        pause()
        //rotateTheDisk()
        blinkTheDisk()
    }

    private fun getRadioMain() {
        mService.getRadioMain().enqueue(object : Callback<RadioMain> {
            override fun onFailure(call: Call<RadioMain>, t: Throwable) {
                println(t)
            }

            override fun onResponse(call: Call<RadioMain>, response: Response<RadioMain>) {
                println(response.body())
                nameRadio.text = response.body()!!.title
                nameMusicSubs.text = response.body()!!.song
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
                nameRadio.text = response.body()!!.title
                nameMusicSubs.text = response.body()!!.song
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
                nameRadio.text = response.body()!!.title
                nameMusicSubs.text = response.body()!!.song
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
                nameRadio.text = response.body()!!.title
                nameMusicSubs.text = response.body()!!.song
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
                nameRadio.text = response.body()!!.title
                nameMusicSubs.text = response.body()!!.song
            }
        })
    }

    override fun onStop() {
        super.onStop()
        Timer().cancel()
    }
}