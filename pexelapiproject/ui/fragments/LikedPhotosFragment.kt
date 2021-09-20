package android.mohamed.pexelapiproject.ui.fragments

import android.content.Intent
import android.mohamed.pexelapiproject.adapters.PhotoAdapter
import android.mohamed.pexelapiproject.adapters.PhotoCallBack
import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.databinding.FragmentLikedPhotosBinding
import android.mohamed.pexelapiproject.ui.PhotoActivity
import android.mohamed.pexelapiproject.utility.Constants
import android.mohamed.pexelapiproject.viewModels.PhotoViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedPhotosFragment : Fragment(), PhotoCallBack {
    private lateinit var binding: FragmentLikedPhotosBinding
    private lateinit var photoAdapter: PhotoAdapter
    private val viewModel by viewModel<PhotoViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedPhotosBinding.inflate(inflater, container, false)
        setupAdapter()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLikedPhotos().observe(viewLifecycleOwner){
            photoAdapter.submitList(it)
        }
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val photo = photoAdapter.currentList[position]
                viewModel.deletePhoto(photo)
                Snackbar.make(binding.root, "photo deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("undo"){
                        viewModel.insertPhoto(photo)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.likedPhotosList)
    }

    private fun setupAdapter(){
        photoAdapter = PhotoAdapter(this)
        binding.likedPhotosList.apply {
            adapter = photoAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    override fun onClick(photo: Photo) {
        val intent = Intent(requireContext(), PhotoActivity::class.java).apply {
            putExtra(Constants.PHOTO_ID, photo.id)
            putExtra(Constants.PHOTO_url_VALUE, photo.src.portrait)
        }
        startActivity(intent)
    }
}