package com.alexandr_yurchenko.radiobeach

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
    }

    fun InfoVK(view: View) {
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
            val vk = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(vk)
        }
    }

    fun InfoTG(view: View) {
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
    }

    fun InfoYoutube(view: View) {
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
    }

    fun InfoWhatsApp(view: View) {
        // https://wa.me/789787267801
        val url = "https://wa.me/789787267801"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}