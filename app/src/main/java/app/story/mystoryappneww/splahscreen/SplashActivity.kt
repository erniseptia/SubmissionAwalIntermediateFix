package app.story.mystoryappneww.splahscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import app.story.mystoryappneww.R
import app.story.mystoryappneww.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))

            finish()
        }, SPLASH_TIME_OUT)
    }

    companion object {
        private const val SPLASH_TIME_OUT = 3000L
    }
}