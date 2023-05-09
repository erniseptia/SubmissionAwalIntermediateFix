package app.story.mystoryappneww.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import app.story.mystoryappneww.api.ApiService
import app.story.mystoryappneww.database.StoryDatabase
import app.story.mystoryappneww.dataclass.Stories
import app.story.mystoryappneww.paging.StoryPagingSource

class StoryRepository(private val storyDatabase: StoryDatabase, private val apiService: ApiService) {
    fun getStory(): LiveData<PagingData<Stories>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}