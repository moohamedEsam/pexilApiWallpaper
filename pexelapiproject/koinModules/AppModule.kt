package android.mohamed.pexelapiproject.koinModules

import android.mohamed.pexelapiproject.Room.PhotoDataBase
import android.mohamed.pexelapiproject.api.PhotoApi
import android.mohamed.pexelapiproject.repository.Repository
import android.mohamed.pexelapiproject.utility.Constants
import android.mohamed.pexelapiproject.viewModels.PhotoViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val module = module {
    single{ provideRetrofitInstance() }
    single { provideDaoInstance() }
    single { providePhotoDataBase(get()) }
    single { Repository(get(), get()) }
    viewModel { PhotoViewModel(get(), get()) }
}

fun providePhotoDataBase(dataBase: PhotoDataBase) = dataBase.getDao()

private fun Scope.provideDaoInstance() =
    Room.databaseBuilder(
        androidApplication(),
        PhotoDataBase::class.java,
        Constants.DATABASE_NAME
    ).build()


fun provideRetrofitInstance(): PhotoApi = Retrofit.Builder()
    .baseUrl(Constants.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(PhotoApi::class.java)
