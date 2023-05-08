package app.story.mystoryappneww.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.story.mystoryappneww.api.ApiConfig
import app.story.mystoryappneww.dataclass.Login
import app.story.mystoryappneww.dataclass.LoginResult
import app.story.mystoryappneww.utils.UserPreference
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _error = MutableLiveData(true)
    val error: LiveData<Boolean> = _error

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }

    fun login(email: String, password: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().login(email, password)
        client.enqueue(object : Callback<Login> {
            override fun onResponse(
                call: Call<Login>,
                response: Response<Login>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _error.value = false
                    _message.value = responseBody.message
                    _loginResult.value = responseBody.loginResult

                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody.toString()).getString("message")
                    _error.value = true
                    _message.value = errorMessage
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun saveUser(user: LoginResult) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }
}