package app.story.mystoryappneww.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import app.story.mystoryappneww.login.LoginViewModel
import app.story.mystoryappneww.main.MainViewModel
import app.story.mystoryappneww.newstory.NewStoryViewModel
import app.story.mystoryappneww.register.RegisterViewModel

class ViewModelFactory(private val pref: UserPreference) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NewStoryViewModel::class.java) -> {
                NewStoryViewModel(pref) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel() as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}