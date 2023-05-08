package app.story.mystoryappneww.main

import android.util.Log
import androidx.lifecycle.*
import app.story.mystoryappneww.api.ApiConfig
import app.story.mystoryappneww.dataclass.ListStoryItem
import app.story.mystoryappneww.dataclass.LoginResult
import app.story.mystoryappneww.dataclass.Stories
import app.story.mystoryappneww.utils.UserPreference
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private val TAG = MainViewModel::class.java.simpleName
    }


    fun getAllStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getAllStories(bearer = "Bearer $token")
        client.enqueue(object : Callback<Stories> {
            override fun onResponse(
                call: Call<Stories>,
                response: Response<Stories>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _stories.value = responseBody.listStory
                    Log.d(TAG, responseBody.listStory.toString())
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Stories>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}