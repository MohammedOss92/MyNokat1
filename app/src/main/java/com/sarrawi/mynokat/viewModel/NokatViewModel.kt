package com.sarrawi.mynokat.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.db.PostDatabase
import com.sarrawi.mynokat.model.FavImgModel
import com.sarrawi.mynokat.model.FavNokatModel
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.ui.MainActivity
import com.sarrawi.mynokat.utils.NetworkConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

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

//    val itemss: Flow<PagingData<NokatModel>> = nokatRepo.getAllNokats()
    val itemss: LiveData<PagingData<NokatModel>> = nokatRepo.getAllNokats().cachedIn(viewModelScope)
    val nokatStream: Flow<PagingData<NokatModel>> = nokatRepo.getNokatStream()
        .cachedIn(viewModelScope)

    fun refreshNokats() {
        viewModelScope.launch {
            try {
                nokatRepo.refreshNokats()
            } catch (e: Exception) {
                // التعامل مع الخطأ حسب الحاجة
            }
        }
    }


    fun getAllNokat(): LiveData<PagingData<FavNokatModel>> {
        Log.e("tessst","entred22")
//        viewModelScope.launch {
//          __response.postValue(msgsRepo.getAllFav())
//        }
        return nokatRepo.getAllFav()
    }




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

    val ImageStream: Flow<PagingData<ImgsNokatModel>> = nokatRepo.getAllImgsNokatSerPa()
        .cachedIn(viewModelScope)


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

    val favImg: LiveData<PagingData<FavImgModel>> = nokatRepo.getAllFavImg().cachedIn(viewModelScope)


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
