package app.story.mystoryappneww.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import app.story.mystoryappneww.R
import app.story.mystoryappneww.databinding.ActivityRegisterBinding
import app.story.mystoryappneww.login.LoginActivity
import app.story.mystoryappneww.utils.UserPreference
import app.story.mystoryappneww.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding.accountTextView.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        registerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[RegisterViewModel::class.java]

        registerViewModel.error.observe(this) { error ->
            registerViewModel.message.observe(this) { message ->
                if (!error) {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.register))
                    builder.setMessage(message)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }, 2000L)
                } else {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.register))
                    builder.setMessage(message)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            when {
                name.isEmpty() -> {
                    binding.edRegisterName.error = getString(R.string.empty_name)
                }
                email.isEmpty() -> {
                    binding.edRegisterEmail.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.edRegisterPassword.error = getString(R.string.empty_password)
                }
                password.length < 8 -> {
                    binding.edRegisterPassword.error = getString(R.string.invalid_password)
                }
                else -> {

                    registerViewModel.register(name, email, password)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {

        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditText =
            ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val accountTextView =
            ObjectAnimator.ofFloat(binding.accountTextView, View.ALPHA, 1f).setDuration(500)
        val signup =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)



        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditText,
                emailTextView,
                emailEditText,
                passwordTextView,
                passwordEditText,
                accountTextView,
                signup
            )
            startDelay = 500
        }.start()
    }
}