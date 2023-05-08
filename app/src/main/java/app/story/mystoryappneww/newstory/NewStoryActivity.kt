package app.story.mystoryappneww.newstory

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import app.story.mystoryappneww.main.MainActivity
import app.story.mystoryappneww.R
import app.story.mystoryappneww.api.ApiConfig
import app.story.mystoryappneww.databinding.ActivityNewStoryBinding
import app.story.mystoryappneww.dataclass.NewStory
import app.story.mystoryappneww.response.createCustomTempFile
import app.story.mystoryappneww.response.reduceFileImage
import app.story.mystoryappneww.response.rotateFile
import app.story.mystoryappneww.response.uriToFile
import app.story.mystoryappneww.utils.UserPreference
import app.story.mystoryappneww.utils.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NewStoryActivity : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private var getFile: File? = null
    private lateinit var binding: ActivityNewStoryBinding
    private lateinit var newStoryViewModel: NewStoryViewModel

    companion object {
        const val CAMERA_X_RESULT = 200

        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, getString(R.string.not_permission), Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityNewStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        binding.cameraButton.setOnClickListener { startTakePhoto() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }

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
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@NewStoryActivity, "app.story.mystoryappneww", it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun uploadImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val desc = binding.edAddDescription.text.toString()
            if (desc.isEmpty()) {
                binding.descEditTextLayout.error = getString(R.string.empty_description)
            } else {
                val description = desc.toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )

                newStoryViewModel = ViewModelProvider(
                    this,
                    ViewModelFactory(UserPreference.getInstance(dataStore))
                )[NewStoryViewModel::class.java]

                newStoryViewModel.getUser().observe(this) { user ->
                    val client =
                        ApiConfig.getApiService()
                            .addStory(bearer = "Bearer ${user.token}", imageMultipart, description)
                    client.enqueue(object : Callback<NewStory> {
                        override fun onResponse(
                            call: Call<NewStory>,
                            response: Response<NewStory>
                        ) {
                            if (response.isSuccessful) {
                                val responseBody = response.body()
                                if (responseBody != null && !responseBody.error) {
                                    Toast.makeText(
                                        this@NewStoryActivity,
                                        responseBody.message,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent =
                                        Intent(this@NewStoryActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                Toast.makeText(
                                    this@NewStoryActivity,
                                    response.message(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<NewStory>, t: Throwable) {
                            Toast.makeText(this@NewStoryActivity, t.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    })
                }
            }

        } else {
            Toast.makeText(
                this@NewStoryActivity,
                getString(R.string.empty_img),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private lateinit var currentPhotoPath: String

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                rotateFile(file)
                getFile = myFile
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@NewStoryActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }
}