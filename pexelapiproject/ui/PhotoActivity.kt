package android.mohamed.pexelapiproject.ui

import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.databinding.ActivityPhotoBinding
import android.mohamed.pexelapiproject.utility.Constants
import android.mohamed.pexelapiproject.utility.Resource
import android.mohamed.pexelapiproject.viewModels.PhotoViewModel
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    private val viewModel by viewModel<PhotoViewModel>()
    private var photo: Photo? = null
    private var hasPermission = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        val id = intent.extras!!.getInt(Constants.PHOTO_ID)
        val url = intent.extras!!.getString(Constants.PHOTO_url_VALUE)

        setContentView(binding.root)
        viewModel.getPhoto(id)
        binding.photoProgressBar.visibility = View.VISIBLE
        Glide.with(this).load(url).into(binding.photo)
        binding.photoProgressBar.visibility = View.GONE

        binding.savePhotoButton.setOnClickListener {
            photo?.let {
                viewModel.insertPhoto(it)
                Snackbar.make(binding.root, "photo saved successfully", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
        binding.downloadPhotoButton.setOnClickListener {
            checkPermission(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Constants.PERMISSION_REQUEST_CODE
            )
            if (hasPermission) {
                photo?.let {
                    viewModel.downloadImage(
                        binding.photo.drawable as BitmapDrawable,
                        it.id,
                        this
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            viewModel.photoStateFlow.collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { photo ->
                            this@PhotoActivity.photo = photo
                        }
                    }
                }
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> {
                    hasPermission = true
                }
                else -> ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        hasPermission =
            !(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
    }
}

