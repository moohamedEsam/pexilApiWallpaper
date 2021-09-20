package android.mohamed.pexelapiproject

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.mohamed.pexelapiproject.adapters.ViewPagerAdapter
import android.mohamed.pexelapiproject.databinding.ActivityMainBinding
import android.mohamed.pexelapiproject.ui.fragments.CategoriesFragment
import android.mohamed.pexelapiproject.ui.fragments.CuratedFragment
import android.mohamed.pexelapiproject.ui.fragments.LikedPhotosFragment

import android.os.Bundle
import android.view.Menu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val TAG = "mainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fragmentList = arrayListOf(
            CuratedFragment(),
            CategoriesFragment(),
            LikedPhotosFragment()
        )


        adapter = ViewPagerAdapter(this, fragmentList)

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Recent"
                1 -> tab.text = "Category"
                2 -> tab.text = "saved"
            }
        }.attach()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.app_bar_search)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isSubmitButtonEnabled = true
            isQueryRefinementEnabled = true
        }
        return true
    }

}