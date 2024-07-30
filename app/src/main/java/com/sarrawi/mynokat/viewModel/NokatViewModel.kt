package com.sarrawi.mynokat.viewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import androidx.paging.*
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.*
import com.sarrawi.mynokat.paging.NokatPaging
import com.sarrawi.mynokat.paging.NokatTypePaging
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.ui.MainActivity
import com.sarrawi.mynokat.ui.frag.nokat.NokatFragment
import com.sarrawi.mynokat.utils.NetworkConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class NokatViewModel constructor(private val nokatRepo: NokatRepo,val context: Context,database:PostDatabase):ViewModel() {


    private val retrofitService = ApiService.provideRetrofitInstance()

    private val _response = MutableLiveData<List<NokatModel>>()
    val responseNokat: LiveData<List<NokatModel>>
        get() = _response

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean>
        get() = _isConnected




    fun checkNetworkConnection(applicationContext: Context) {
        val networkConnection = NetworkConnection(applicationContext)
        networkConnection.observeForever { isConnected ->
            _isConnected.value = isConnected
        }
    }
    val ID_Type_id=0
    val nokatType: LiveData<PagingData<NokatTypeWithCount>> = nokatRepo.getAllNokatsTypes().cachedIn(viewModelScope)
//    val itemss: Flow<PagingData<NokatModel>> = nokatRepo.getAllNokats()
    val itemss: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNokats(ID_Type_id).cachedIn(viewModelScope)
    val newNokat: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNewNokats().cachedIn(viewModelScope)
    val nokatStream: Flow<PagingData<NokatModel>> = nokatRepo.getNokatStream(ID_Type_id)
        .cachedIn(viewModelScope)

    suspend fun refreshNokats(apiService: ApiService, database: PostDatabase) {
        var page = 1 // البدء بالصفحة الأولى
        var nokatList: List<NokatModel> = emptyList()

        try {
            do {
                val response = apiService.getAllNokatPa(page)
                if (response.isSuccessful) {
                    nokatList = response.body()?.results?.NokatModel ?: emptyList()
                    database.nokatDao().insert_Nokat(nokatList)
                    page++
                } else {
                    // تجاهل الاستجابات غير الناجحة
                    // يمكنك هنا إضافة سجل للأخطاء أو تسجيلها بشكل مناسب
                    // أو يمكنك ترك هذا المكان فارغاً إذا كنت لا تريد معالجة الأخطاء هنا
                }
            } while (nokatList.isNotEmpty())
        } catch (e: IOException) {
            throw IOException("Network error", e)
        } catch (e: HttpException) {
            // يمكنك هنا ترك هذا المكان فارغاً إذا كنت لا تريد معالجة الأخطاء هنا
        }
    }


    private val pagingSourceFlowTypes = MutableStateFlow(Unit)

    val nokatTypesFlow: Flow<PagingData<NokatTypeModel>> = pagingSourceFlowTypes.flatMapLatest {
        Pager(
            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
            pagingSourceFactory = { NokatTypePaging(ApiService.provideRetrofitInstance(), database) }
        ).flow
            .cachedIn(viewModelScope)
    }

    fun invalidatePagingSourceTypes() {
        pagingSourceFlowTypes.value = Unit
    }

//    private val pagingSourceFlow = MutableStateFlow(Unit)
//
//    val nokatFlow: Flow<PagingData<NokatModel>> = pagingSourceFlow.flatMapLatest {
//        Pager(
//            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
//            pagingSourceFactory = { NokatPaging(ApiService.provideRetrofitInstance(), database,ID_Type_id) }
//        ).flow
//            .cachedIn(viewModelScope)
//    }
//
//    fun invalidatePagingSource() {
//        pagingSourceFlow.value = Unit
//    }



//    fun getAllImagea(): LiveData<PagingData<ImgsNokatModel>> {
//        return nokatRepo.getAllImgsNokatSerPag()
//    }

    private val _imageCount = MutableLiveData<List<TotalImages>>()
    val imageCount: LiveData<List<TotalImages>> get() = _imageCount






    //    fun getAllImage(): LiveData<PagingData<ItemModel>> {
    fun getAllImage(): LiveData<PagingData<ImgsNokatModel>> {

        var _response = MutableLiveData<PagingData<ImgsNokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllImgsNokatSerPag()
                _response = response as MutableLiveData<PagingData<ImgsNokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }



    val countLiveDataa: LiveData<Int> = nokatRepo.countLiveDataa

    fun fetchImageCount() {
        viewModelScope.launch {
            nokatRepo.getImgCount()
        }
    }

    val countLiveDatanokat: LiveData<Int> get() = nokatRepo.countLiveDataNokat

    fun getAllImageNew(): LiveData<PagingData<ImgsNokatModel>> {

        var _response = MutableLiveData<PagingData<ImgsNokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllImgsNokatSerPagNew()
                _response = response as MutableLiveData<PagingData<ImgsNokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }

//    val ImageStream: Flow<PagingData<ItemModel>> = nokatRepo.getAllImgsNokatSerPa()
//    val ImageStream: Flow<PagingData<ImgsNokatModel>> = nokatRepo.getAllImgsNokatSerPa()
//        .cachedIn(viewModelScope)


    /////////////

    fun update_fav(id:Int,state:Boolean)=viewModelScope.launch {
        nokatRepo.update_fav(id,state)
    }

    fun add_fav(fav: FavNokatModel)= viewModelScope.launch {
        nokatRepo.add_fav(fav)
    }



    fun getFav(): LiveData<PagingData<FavNokatModel>> {
        Log.e("tessst","entred22")
//        viewModelScope.launch {
//          __response.postValue(msgsRepo.getAllFav())
//        }
        return nokatRepo.getAllFav()
    }



    val favNokat: LiveData<PagingData<FavNokatModel>> = nokatRepo.getAllFav().cachedIn(viewModelScope)


    // delete favorite item from db
    fun delete_fav(fav: FavNokatModel)= viewModelScope.launch {
        nokatRepo.deleteFav(fav)
    }
    ////////
    val favNokats: LiveData<PagingData<FavNokatModel>> = nokatRepo.getAllFav().cachedIn(viewModelScope)

    fun update_favs(id: Int, state: Boolean) = viewModelScope.launch {
        nokatRepo.update_favs(id, state)
    }

    fun add_favs(fav: FavNokatModel) = viewModelScope.launch {
        nokatRepo.add_favs(fav)
    }

    fun delete_favs(fav: FavNokatModel) = viewModelScope.launch {
        nokatRepo.delete_favs(fav)
    }
    //////////////////////
    fun update_favs_img(id: Int, state: Boolean) = viewModelScope.launch {
        nokatRepo.update_favs_img(id, state)
    }

    fun add_favs_img(fav: FavImgModel) = viewModelScope.launch {
        nokatRepo.add_favs_img(fav)
    }

    fun delete_favs_img(fav: FavImgModel) = viewModelScope.launch {
        nokatRepo.delete_favs_img(fav)
    }

    val favImg: LiveData<List<FavImgModel>> = nokatRepo.getAllFavImg()
    val favImg2: LiveData<PagingData<FavImgModel>> = nokatRepo.getAllFavImge().cachedIn(viewModelScope)

    fun internetCheck(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // للأجهزة بنظام Android 10+
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let { networkCapabilities ->
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            } ?: false
        } else {
            connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }

//    fun getAllNokat2(): LiveData<PagingData<NokatModel>> {
//        val _response = MutableLiveData<PagingData<NokatModel>>()
//        viewModelScope.launch {
//            try {
//                val responseLiveData = nokatRepo.getAllNokatSerpa()
//                responseLiveData.observeForever { response ->
//                    _response.value = response
//                }
//            } catch (e: Exception) {
//                Log.e("Test", "getAllNokat: Error: ${e.message}")
//            }
//        }
//        return _response
//    }

    /*
    * // Model
data class User(val id: Int, val name: String)

// ApiService
interface ApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User
}

// Repository
class UserRepository(private val apiService: ApiService) {
    suspend fun getUser(userId: Int): User {
        return apiService.getUser(userId)
    }
}

// ViewModel
class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    fun fetchUser(userId: Int) {
        viewModelScope.launch {
            _user.value = repository.getUser(userId)
        }
    }
}

// ViewModelFactory (Optional but recommended)
class UserViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// MainActivity
class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: UserViewModel
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        val apiService = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        val repository = UserRepository(apiService)
        val viewModelFactory = UserViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserViewModel::class.java)

        viewModel.fetchUser(1) // Replace 1 with the desired user ID

        viewModel.user.observe(this, Observer { user ->
            // Update TextView with user's name
            textView.text = user.name
        })
    }
}
*/

}
