package android.mohamed.pexelapiproject.viewModels

import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.mohamed.pexelapiproject.dataModels.Photo
import android.mohamed.pexelapiproject.dataModels.PhotoResponse
import android.mohamed.pexelapiproject.repository.Repository
import android.mohamed.pexelapiproject.utility.ApplicationClass
import android.mohamed.pexelapiproject.utility.Resource
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

import java.io.IOException
import java.io.OutputStream


class PhotoViewModel(private val repository: Repository, app: Application) :
    AndroidViewModel(app) {
    private val _curatedPhotosStateFlow: MutableStateFlow<Resource<PhotoResponse>> =
        MutableStateFlow(Resource.Initialized())
    private var curatedPhotoResponse: PhotoResponse? = null

    val curatedPhotosStateFlow: StateFlow<Resource<PhotoResponse>> = _curatedPhotosStateFlow

    var curatedPageNumber = 1

    private val _searchPhotosMutableStateFlow: MutableStateFlow<Resource<PhotoResponse>> =
        MutableStateFlow(Resource.Initialized())

    val searchPhotosStateFlow: StateFlow<Resource<PhotoResponse>> = _searchPhotosMutableStateFlow
    private var searchPhotoResponse: PhotoResponse? = null
    var searchPhotoPageNumber = 1

    private val _photoMutableStateFlow = MutableStateFlow<Resource<Photo>>(Resource.Initialized())
    val photoStateFlow: StateFlow<Resource<Photo>> = _photoMutableStateFlow


    fun getCuratedPhotos(per_page: Int = 15) = viewModelScope.launch {
        safeCallCuratedPhotos(per_page, curatedPageNumber)
    }

    fun getCategory(searchQuery: String) = viewModelScope.launch {
        safeSearchPhotoCall(searchQuery, searchPhotoPageNumber)
    }

    fun getPhoto(id: Int) = viewModelScope.launch {
        safeGetPhotoCall(id)
    }

    fun insertPhoto(photo: Photo) = viewModelScope.launch {
        repository.insertPhoto(photo)
    }

    fun deletePhoto(photo: Photo) = viewModelScope.launch {
        repository.deletePhoto(photo)
    }

    fun getLikedPhotos() = repository.getPhotos()


    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun safeGetPhotoCall(id: Int) {
        _photoMutableStateFlow.emit(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.getPhoto(id)
                _photoMutableStateFlow.emit(handlePhoto(response))
            } else
                _photoMutableStateFlow.emit(Resource.Error("no internet connection"))
        } catch (T: Throwable) {
            val errorMessage = when (T) {
                is IOException -> "network failure"
                else -> "conversion error"
            }
            _photoMutableStateFlow.emit(Resource.Error(errorMessage))
        }

    }

    private fun handlePhoto(response: Response<Photo>): Resource<Photo> {
        if (response.isSuccessful) {
            response.body()?.let { photo ->
                return Resource.Success(photo)
            }
        }
        return Resource.Error(response.message())
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun safeCallCuratedPhotos(perPage: Int, page: Int) {
        _curatedPhotosStateFlow.emit(Resource.Loading())
        if (hasInternetConnection()) {
            val errorMessage: String
            try {
                val response = repository.getCuratedPhotos(perPage, page)
                _curatedPhotosStateFlow.emit(handleCuratedPhoto(response))
            } catch (T: Throwable) {
                errorMessage = when (T) {
                    is IOException -> "network failure"
                    else -> "converting error"
                }
                _curatedPhotosStateFlow.emit(Resource.Error(errorMessage))
            }
        } else
            _curatedPhotosStateFlow.emit(Resource.Error("no internet connection"))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun safeSearchPhotoCall(searchQuery: String, page: Int) {
        _searchPhotosMutableStateFlow.emit(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = repository.searchPhoto(searchQuery, page)
                _searchPhotosMutableStateFlow.emit(handleSearchPhoto(response))
            } else
                _searchPhotosMutableStateFlow.emit(Resource.Error("no internet connection"))
        } catch (T: Throwable) {
            val errorMessage: String = when (T) {
                is IOException -> "network failure"
                else -> "conversion error"
            }
            _searchPhotosMutableStateFlow.emit(Resource.Error(errorMessage))
        }

    }

    private fun handleCuratedPhoto(response: Response<PhotoResponse>): Resource<PhotoResponse> {
        if (response.isSuccessful) {
            response.body()?.let { photoResponse ->
                if (photoResponse.photos.size == 0)
                    return Resource.Error("we are sorry no photos found")
                curatedPageNumber++
                if (curatedPhotoResponse == null) { //first curated call
                    curatedPhotoResponse = photoResponse
                } else {
                    val oldPhotos = curatedPhotoResponse?.photos
                    val newPhotos = photoResponse.photos
                    oldPhotos?.addAll(newPhotos)
                }
                return Resource.Success(curatedPhotoResponse ?: photoResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchPhoto(response: Response<PhotoResponse>): Resource<PhotoResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (it.photos.size == 0)
                    return Resource.Error("we are sorry no photos found")
                searchPhotoPageNumber++
                if (searchPhotoResponse == null) {
                    searchPhotoResponse = it
                } else {
                    val oldPhotos = searchPhotoResponse?.photos
                    val newPhotos = it.photos
                    oldPhotos?.addAll(newPhotos)
                }
                return Resource.Success(searchPhotoResponse ?: it)

            }
        }
        return Resource.Error(response.message())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            getApplication<ApplicationClass>().getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    fun downloadImage(
        photoDrawable: BitmapDrawable,
        id: Int,
        context: Context,
    ) {
        val bitmap = photoDrawable.bitmap
        var stream: OutputStream? = null
        val fileName = "pexelPhoto$id"
        val contentResolver = context.contentResolver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentResolver?.apply {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? =
                    this.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                stream = imageUri?.let {
                    this.openOutputStream(it)
                }
            }

        } else {

            val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, fileName)
            stream = FileOutputStream(image)
        }
        stream?.use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(context, "photo saved", Toast.LENGTH_SHORT).apply {
                setGravity(Gravity.TOP, 0, 0)
            }.show()
        }


    }
}