package android.mohamed.pexelapiproject.ui

import android.app.SearchManager
import android.content.Intent
import android.mohamed.pexelapiproject.adapters.PhotoAdapter
import android.mohamed.pexelapiproject.adapters.PhotoCallBack
import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.databinding.ActivityCategoryBinding
import android.mohamed.pexelapiproject.utility.Constants
import android.mohamed.pexelapiproject.utility.Resource
import android.mohamed.pexelapiproject.viewModels.PhotoViewModel
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryActivity : AppCompatActivity(), PhotoCallBack {
    private lateinit var binding: ActivityCategoryBinding
    private lateinit var listAdapter: PhotoAdapter
    private lateinit var category: String
    private val viewModel: PhotoViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAdapter()
        category = if (intent.action == Intent.ACTION_SEARCH)
            intent.getStringExtra(SearchManager.QUERY)!!
        else
            intent.getStringExtra(Constants.CATEGORY_NAME)!!

        viewModel.getCategory(category)


    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            viewModel.searchPhotosStateFlow.collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        binding.categoryListProgressBar.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            resource.message.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Success -> {
                        binding.categoryListProgressBar.visibility = View.GONE
                        resource.data?.let { photoResponse ->
                            isLastPage = photoResponse.next_page.isBlank()
                            if (isLastPage)
                                binding.categoryListProgressBar.setPadding(0, 0, 0, 0)

                            listAdapter.submitList(photoResponse.photos.toList())
                        }
                    }
                    else -> {
                        binding.categoryListProgressBar.visibility = View.VISIBLE
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
            val layoutManager = binding.categoryList.layoutManager as GridLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val shouldPaginate = isNotLoadingAndNotLastPage && isLastItem && isScrolling
            if (shouldPaginate) {
                viewModel.getCategory(category)
                isScrolling = false
            }


        }
    }

    private fun setupAdapter() {
        listAdapter = PhotoAdapter(this)
        binding.categoryList.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(this@CategoryActivity, 3)
            addOnScrollListener(this@CategoryActivity.scrollListener)
        }
    }

    override fun onClick(photo: Photo) {
        val intent = Intent(this, PhotoActivity::class.java).apply {
            putExtra(Constants.PHOTO_url_VALUE, photo.src.portrait)
            putExtra(Constants.PHOTO_ID, photo.id)

        }
        startActivity(intent)
    }
}