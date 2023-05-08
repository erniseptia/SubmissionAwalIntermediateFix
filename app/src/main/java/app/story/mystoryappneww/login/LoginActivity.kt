package app.story.mystoryappneww.login

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
import app.story.mystoryappneww.main.MainActivity
import app.story.mystoryappneww.R
import app.story.mystoryappneww.databinding.ActivityLoginBinding
import app.story.mystoryappneww.dataclass.LoginResult
import app.story.mystoryappneww.register.RegisterActivity
import app.story.mystoryappneww.utils.UserPreference
import app.story.mystoryappneww.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding.accountTextView.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
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
    }

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[LoginViewModel::class.java]

        loginViewModel.error.observe(this) { error ->
            loginViewModel.message.observe(this) { message ->
                if (!error) {
                    loginViewModel.loginResult.observe(this) { loginResult ->
                        val name = loginResult.name
                        val userId = loginResult.userId
                        val token = loginResult.token

                        loginViewModel.saveUser(LoginResult(name, userId, token, true))
                        loginViewModel.login()
                    }
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.login))
                    builder.setMessage(message)
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }, 2000L)
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.login))
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

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = getString(R.string.empty_email)
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = getString(R.string.empty_password)
                }
                password.length < 8 -> {
                    binding.edLoginPassword.error = getString(R.string.invalid_password)
                }
                else -> {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title =
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditText =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditText =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f)
                .setDuration(500)
        val accountTextView =
            ObjectAnimator.ofFloat(binding.accountTextView, View.ALPHA, 1f)
                .setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                title,
                emailTextView,
                emailEditText,
                passwordTextView,
                passwordEditText,
                accountTextView,
                login
            )
            startDelay = 500
        }.start()
    }
}