package app.story.mystoryappneww.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.story.mystoryappneww.api.ApiService
import app.story.mystoryappneww.dataclass.ListStoryItem
import app.story.mystoryappneww.dataclass.Stories


class StoryPagingSource(private val apiService: ApiService, private val token: String) : PagingSource<Int, Stories>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Stories>): Int? {
        // mengembalikan `null` agar dapat mengambil data awal pada saat refresh
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Stories> {


        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val response = apiService.getAllStories(bearer = "Bearer $token")
                .execute()
                .body()?.listStory?.map { it.toStory() } ?: emptyList()

            LoadResult.Page(
                data = response,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (response.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    private fun ListStoryItem.toStory(): Stories {
        return Stories(id, name, description, photoUrl)
    }
}

