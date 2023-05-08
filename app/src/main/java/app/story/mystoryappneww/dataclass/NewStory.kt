package app.story.mystoryappneww.dataclass

import com.google.gson.annotations.SerializedName

data class NewStory(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)
