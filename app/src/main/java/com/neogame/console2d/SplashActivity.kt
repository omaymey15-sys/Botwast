package com.neogame.console2d

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.neogame.console2d.utils.Logger

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Logger.d("SplashActivity created")

        val titleTextView: TextView = findViewById(R.id.splashTitle)
        val subtitleTextView: TextView = findViewById(R.id.splashSubtitle)
        val logoImageView: ImageView? = findViewById<ImageView?>(R.id.splashLogo)

        // Animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        fadeInAnimation.duration = 1000
        titleTextView.startAnimation(fadeInAnimation)
        subtitleTextView.startAnimation(fadeInAnimation)
        logoImageView?.startAnimation(fadeInAnimation)

        // Transition after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            Logger.d("Splash completed, launching MainActivity")
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }
}