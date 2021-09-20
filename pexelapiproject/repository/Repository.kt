package android.mohamed.pexelapiproject.repository

import android.mohamed.pexelapiproject.Room.PhotoDao
import android.mohamed.pexelapiproject.api.PhotoApi
import android.mohamed.pexelapiproject.dataModels.Photo

class Repository(private val photoApi: PhotoApi, private val photoDao: PhotoDao) {

    suspend fun getCuratedPhotos(per_page: Int, page: Int) = photoApi.getCuratedPhotos(per_page, page)

    suspend fun searchPhoto(searchQuery: String, page: Int) = photoApi.searchPhoto(searchQuery, page)

    suspend fun getPhoto(id: Int) = photoApi.getPhoto(id)

    suspend fun insertPhoto(photo: Photo) = photoDao.insertPhoto(photo)

    suspend fun deletePhoto(photo: Photo) = photoDao.deletePhoto(photo)

    fun getPhotos() = photoDao.getPhotos()
}