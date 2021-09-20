package android.mohamed.pexelapiproject.adapters

import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.databinding.PhotoItemBinding
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class Holder(private val binding: PhotoItemBinding, private val listener: PhotoCallBack) :
    RecyclerView.ViewHolder(binding.root) {
    private lateinit var photo: Photo

    init {
        itemView.setOnClickListener {
            listener.onClick(photo)
        }
    }

    fun bind(photo: Photo) {
        this.photo = photo
        binding.apply {
            progressBar.visibility = View.VISIBLE
            Glide.with(itemView)
                .load(photo.src.medium)
                .into(imageView)
            progressBar.visibility = View.GONE
        }

    }
}

class PhotoAdapter(private val listener: PhotoCallBack) :
    ListAdapter<Photo, Holder>(object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.url == newItem.url
        }
    }) {
    private lateinit var binding: PhotoItemBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        binding = PhotoItemBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(currentList[position])
    }
}

interface PhotoCallBack {
    fun onClick(photo: Photo)
}