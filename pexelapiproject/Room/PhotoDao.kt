package android.mohamed.pexelapiproject.Room

import android.mohamed.pexelapiproject.dataModels.Photo
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: Photo):Long

    @Query("select * from photo")
    fun getPhotos():LiveData<List<Photo>>

    @Delete
    suspend fun deletePhoto(photo: Photo)
}