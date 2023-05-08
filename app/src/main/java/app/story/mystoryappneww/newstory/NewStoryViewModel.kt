package app.story.mystoryappneww.newstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import app.story.mystoryappneww.dataclass.LoginResult
import app.story.mystoryappneww.utils.UserPreference

class NewStoryViewModel(private val pref: UserPreference) : ViewModel()  {
    fun getUser(): LiveData<LoginResult> {
        return pref.getUser().asLiveData()
    }
}