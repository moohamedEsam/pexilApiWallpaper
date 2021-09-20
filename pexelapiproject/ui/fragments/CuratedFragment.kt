package android.mohamed.pexelapiproject.ui.fragments

import android.content.Intent
import android.mohamed.pexelapiproject.adapters.PhotoAdapter
import android.mohamed.pexelapiproject.adapters.PhotoCallBack
import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.databinding.FragmentCuratedBinding
import android.mohamed.pexelapiproject.ui.PhotoActivity
import android.mohamed.pexelapiproject.utility.Constants
import android.mohamed.pexelapiproject.utility.Resource
import android.mohamed.pexelapiproject.viewModels.PhotoViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class CuratedFragment : Fragment(), PhotoCallBack {
    private lateinit var binding: FragmentCuratedBinding
    private val viewModel by viewModel<PhotoViewModel>()
    private lateinit var listAdapter: PhotoAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCuratedBinding.inflate(inflater, container, false)
        setupList()
        viewModel.getCuratedPhotos()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.curatedPhotosStateFlow.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            resource.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        resource.data?.let { photoResponse ->
                            isLastPage = photoResponse.next_page.isBlank()
                            listAdapter.submitList(photoResponse.photos.toList())
                            if (isLastPage)
                                binding.curatedPhotosList.setPadding(0, 0, 0, 0)
                        }
                    }
                    else -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                }

            }
        }
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            isScrolling = newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = binding.curatedPhotosList.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isScrolling
            if (shouldPaginate) {
                viewModel.getCuratedPhotos()
                isScrolling = false
            }


        }
    }

    private fun setupList() {
        listAdapter = PhotoAdapter(this)
        binding.curatedPhotosList.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addOnScrollListener(this@CuratedFragment.scrollListener)
        }
    }

    override fun onClick(photo: Photo) {
        val intent = Intent(requireContext(), PhotoActivity::class.java).apply {
            putExtra(Constants.PHOTO_url_VALUE, photo.src.portrait)
            putExtra(Constants.PHOTO_ID, photo.id)
        }
        startActivity(intent)
    }
}