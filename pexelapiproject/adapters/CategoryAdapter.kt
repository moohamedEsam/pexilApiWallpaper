package android.mohamed.pexelapiproject.adapters

import android.mohamed.pexelapiproject.R
import android.mohamed.pexelapiproject.databinding.CategoryItemBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CategoryHolder(private val binding: CategoryItemBinding, private val listener: CategoryItemCallBack) :
    RecyclerView.ViewHolder(binding.root) {
    private lateinit var categoryName: String
    init {
        itemView.setOnClickListener {
            listener.onClick(categoryName)
        }
    }
    fun bind(name: String) {
        categoryName = name
        binding.apply {
            categoryItemTextView.text = name
            when (name) {
                "abstract" -> categoryImage.setBackgroundResource(R.drawable.abstract_photo)
                "animals" -> categoryImage.setBackgroundResource(R.drawable.animals)
                "anime" -> categoryImage.setBackgroundResource(R.drawable.anime)
                "architecture" -> categoryImage.setBackgroundResource(R.drawable.architecture)
                "art" -> categoryImage.setBackgroundResource(R.drawable.art)
                "beach" -> categoryImage.setBackgroundResource(R.drawable.beach)
                "black" -> categoryImage.setBackgroundResource(R.drawable.black)
                "broken screen" -> categoryImage.setBackgroundResource(R.drawable.broken_screen)
                "car" -> categoryImage.setBackgroundResource(R.drawable.car)
                "city" -> categoryImage.setBackgroundResource(R.drawable.city)
                "comics" -> categoryImage.setBackgroundResource(R.drawable.comics)
                " forest" -> categoryImage.setBackgroundResource(R.drawable.forest)
                "fantasy" -> categoryImage.setBackgroundResource(R.drawable.fantasy)
                "fire" -> categoryImage.setBackgroundResource(R.drawable.fire)
                "food" -> categoryImage.setBackgroundResource(R.drawable.food)
                "games" -> categoryImage.setBackgroundResource(R.drawable.games)
                "nature" -> categoryImage.setBackgroundResource(R.drawable.nature)
                else -> categoryImage.setBackgroundResource(R.drawable.games)
            }

        }

    }

}


class CategoryAdapter(private val listener: CategoryItemCallBack) : RecyclerView.Adapter<CategoryHolder>() {
    private lateinit var binding: CategoryItemBinding
    private val categories = listOf(
        "abstract",
        "animals",
        "anime",
        "architecture",
        "art",
        "beach",
        "black",
        "broken screen",
        "car",
        "city",
        "comics",
        " forest",
        "fantasy",
        "fire",
        "food",
        "games",
        "nature"
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryHolder {
        binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context))
        return CategoryHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CategoryHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int {
        return categories.size
    }
}

interface CategoryItemCallBack{
    fun onClick(category: String)
}