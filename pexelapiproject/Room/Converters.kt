package android.mohamed.pexelapiproject.Room

import android.mohamed.pexelapiproject.dataModels.Src
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromSource(source: Src): String {
        return Gson().toJson(source)
    }
    @TypeConverter
    fun toSource(gson: String): Src? {
        return Gson().fromJson(gson, Src::class.java)
    }
}