package app.story.mystoryappneww.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import app.story.mystoryappneww.dataclass.ListStoryItem
import app.story.mystoryappneww.dataclass.Stories
import retrofit2.Call

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStories(story: Call<Stories>)

    @Query("SELECT * FROM story")
    fun getAllStories(): PagingSource<Int,ListStoryItem>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}