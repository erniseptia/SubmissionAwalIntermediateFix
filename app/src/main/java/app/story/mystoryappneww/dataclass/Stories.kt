package app.story.mystoryappneww.dataclass

import com.google.gson.annotations.SerializedName

data class Stories(

    @field:SerializedName("listStory")
    val listStory: List<ListStoryItem>,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("location")
    val location: Location
)

data class ListStoryItem(

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("lon")
    val lon: Any,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("lat")
    val lat: Any
)

data class Location(
    @field:SerializedName("latitude")
    val latitude: Double,
    @field:SerializedName("longtitude")
    val longitude: Double
)

