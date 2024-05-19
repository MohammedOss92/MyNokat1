package com.sarrawi.mynokat.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.sarrawi.mynokat.api.ApiService
import com.sarrawi.mynokat.model.ImgsNokatModel
import com.sarrawi.mynokat.model.NokatModel
import com.sarrawi.mynokat.repository.NokatRepo
import com.sarrawi.mynokat.ui.MainActivity
import kotlinx.coroutines.launch

class NokatViewModel constructor(private val nokatRepo: NokatRepo,val context: Context):ViewModel() {


    private val retrofitService = ApiService.provideRetrofitInstance()

    private val _response = MutableLiveData<List<NokatModel>>()
    val responseNokat: LiveData<List<NokatModel>>
        get() = _response


    fun getAllNokat(): LiveData<PagingData<NokatModel>> {

        var _response = MutableLiveData<PagingData<NokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllNokatSerpa()
                _response = response as MutableLiveData<PagingData<NokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }


    fun getAllImage(): LiveData<PagingData<ImgsNokatModel>> {

        var _response = MutableLiveData<PagingData<ImgsNokatModel>>()
        viewModelScope.launch {
            try {
                val response = nokatRepo.getAllImgsNokatSerPa()
                _response = response as MutableLiveData<PagingData<ImgsNokatModel>>
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }

        return _response
    }

    fun getAllNokat2(): LiveData<PagingData<NokatModel>> {
        val _response = MutableLiveData<PagingData<NokatModel>>()
        viewModelScope.launch {
            try {
                val responseLiveData = nokatRepo.getAllNokatSerpa()
                responseLiveData.observeForever { response ->
                    _response.value = response
                }
            } catch (e: Exception) {
                Log.e("Test", "getAllNokat: Error: ${e.message}")
            }
        }
        return _response
    }

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
