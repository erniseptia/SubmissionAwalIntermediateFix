package app.story.mystoryappneww.repository

import androidx.lifecycle.LiveData
import androidx.paging.*
import app.story.mystoryappneww.api.ApiService
import app.story.mystoryappneww.database.StoryDatabase
import app.story.mystoryappneww.dataclass.ListStoryItem
import app.story.mystoryappneww.paging.StoryPagingSource
import app.story.mystoryappneww.paging.StoryRemoteMediator

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val token: String) {

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
               StoryPagingSource(apiService)
                StoryDatabase.storyDao.getAllStories()
            }
        ).liveData
    }

}