package app.story.mystoryappneww.detail

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import app.story.mystoryappneww.R
import app.story.mystoryappneww.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide

class Detail : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()


        val name = intent.getStringExtra(NAME)
        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val createAt = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)

        Glide.with(this)
            .load(photoUrl)
            .into(binding.ivDetailPhoto)
        binding.tvDetailName.text = name
        binding.tvCreated.text = createAt
        binding.tvDetailDescription.text = description
    }

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }
}