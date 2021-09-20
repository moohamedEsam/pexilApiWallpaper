package android.mohamed.pexelapiproject.ui.fragments

import android.content.Intent
import android.mohamed.pexelapiproject.R
import android.mohamed.pexelapiproject.adapters.CategoryAdapter
import android.mohamed.pexelapiproject.adapters.CategoryItemCallBack
import android.mohamed.pexelapiproject.databinding.FragmentCategoriesBinding
import android.mohamed.pexelapiproject.ui.CategoryActivity
import android.mohamed.pexelapiproject.utility.Constants
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

class CategoriesFragment : Fragment(), CategoryItemCallBack {
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var categoryAdapter: CategoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        setupAdapter()
        return binding.root
    }

    private fun setupAdapter(){
        categoryAdapter = CategoryAdapter(this)
        binding.categoriesList.apply {
            adapter= categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
            hasFixedSize()
        }
    }

    override fun onClick(category: String) {
        val intent = Intent(requireContext(),CategoryActivity::class.java).apply {
            putExtra(Constants.CATEGORY_NAME, category)
        }
        startActivity(intent)
    }
}