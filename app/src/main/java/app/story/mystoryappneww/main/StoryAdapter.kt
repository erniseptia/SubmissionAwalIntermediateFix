package app.story.mystoryappneww.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import app.story.mystoryappneww.databinding.ListStoryBinding
import app.story.mystoryappneww.dataclass.ListStoryItem
import app.story.mystoryappneww.detail.Detail
import app.story.mystoryappneww.response.withDateFormat
import com.bumptech.glide.Glide

class StoryAdapter(private val listStories: List<ListStoryItem>):
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val stories = listStories[position]
        holder.bind(stories)
    }

    override fun getItemCount(): Int = listStories.size


    class ListViewHolder(private val binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stories: ListStoryItem) {
            Glide.with(itemView)
                .load(stories.photoUrl)
                .into(binding.ivItemPhoto)
            binding.tvItemName.text = stories.name
            binding.tvItemCreated.text = stories.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, Detail::class.java)
                intent.putExtra(Detail.NAME, stories.name)
                intent.putExtra(Detail.PHOTO_URL, stories.photoUrl)
                intent.putExtra(Detail.CREATE_AT, stories.createdAt.withDateFormat())
                intent.putExtra(Detail.DESCRIPTION, stories.description)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.iconUser, "icon_user"),
                        Pair(binding.ivItemPhoto, "image"),
                        Pair(binding.tvItemName, "name"),
                        Pair(binding.tvItemCreated, "created"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}