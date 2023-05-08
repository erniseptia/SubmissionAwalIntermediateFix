package app.story.mystoryappneww.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.story.mystoryappneww.R
import app.story.mystoryappneww.databinding.ActivityMainBinding
import app.story.mystoryappneww.login.LoginActivity
import app.story.mystoryappneww.newstory.NewStoryActivity
import app.story.mystoryappneww.utils.UserPreference
import app.story.mystoryappneww.utils.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this)
        binding.rvUser.layoutManager = layoutManager

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, NewStoryActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupViewModel()
        loginCheck()
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
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[MainViewModel::class.java]
    }

    private fun loginCheck() {

        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                mainViewModel.getAllStories(user.token)
                mainViewModel.stories.observe(this) {
                    adapter = StoryAdapter(it)
                    binding.rvUser.adapter = adapter
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                AlertDialog.Builder(this@MainActivity).apply {
                    setTitle(getString(R.string.logout))
                    setMessage(getString(R.string.logout_validation))
                    setPositiveButton(getString(R.string.yes)) { _, _ ->
                        mainViewModel.logout()
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    setNegativeButton(getString(R.string.no)) { _, _ -> }
                    create()
                    show()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}