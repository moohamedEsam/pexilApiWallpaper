package android.mohamed.pexelapiproject.Room

import android.mohamed.pexelapiproject.dataModels.Photo
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = [Photo::class], version = 1)
@TypeConverters(Converters::class)
abstract class PhotoDataBase : RoomDatabase() {
    abstract fun getDao(): PhotoDao
}