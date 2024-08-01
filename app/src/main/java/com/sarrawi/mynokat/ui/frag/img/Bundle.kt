package com.sarrawi.mynokat.ui.frag.img

//كيف
//تحميل البيانات في كل Fragment بناءً على الحالة
//يمكنك أيضًا تحميل البيانات في كل Fragment بناءً على الحالة وتخزين الصفحة الحالية أو معرف الصورة في Bundle عند الانتقال.
//
//تطبيق التحميل بناءً على الحالة:
//تخزين الصفحة الحالية في Bundle عن

//class ImgFragment : Fragment() {
//
//    private var currentPage: Int = 1
//    private var selectedImageId: String? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        savedInstanceState?.let {
//            currentPage = it.getInt("currentPage", 1)
//            selectedImageId = it.getString("selectedImageId")
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putInt("currentPage", currentPage)
//        outState.putString("selectedImageId", selectedImageId)
//    }
//
//    // استخدم currentPage و selectedImageId في مكان التحميل
//}

//class ImgFragment : Fragment() {
//
//    private lateinit var viewModel: SharedViewModel
//    private lateinit var pagingAdapterImg: PagingDataAdapter<ImgsNokatModel, RecyclerView.ViewHolder>
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // الحصول على SharedViewModel
//        viewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
//
//        // استخدم currentPage لتحميل البيانات من الصفحة الحالية
//        viewModel.loadData(currentPage)
//
//        // قم بإعداد RecyclerView و Adapter
//        setupRecyclerView()
//
//        // الاستماع للتغييرات في البيانات
//        viewModel.imgsNokatData.observe(viewLifecycleOwner) { pagingData ->
//            lifecycleScope.launch {
//                pagingAdapterImg.submitData(pagingData)
//            }
//        }
//    }
//
//    private fun setupRecyclerView() {
//        // تعيين إعدادات RecyclerView
//        binding.rcImgNokat.layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
//        binding.rcImgNokat.adapter = pagingAdapterImg
//    }
//}

//class SharedViewModel(private val repository: NokatRepo) : ViewModel() {
//
//    private val _imgsNokatData = MutableLiveData<PagingData<ImgsNokatModel>>()
//    val imgsNokatData: LiveData<PagingData<ImgsNokatModel>> get() = _imgsNokatData
//
//    fun loadData(page: Int) {
//        viewModelScope.launch {
//            try {
//                repository.getAllImgsNokatSerPag(page).collect { pagingData ->
//                    _imgsNokatData.postValue(pagingData)
//                }
//            } catch (e: Exception) {
//                Log.e("SharedViewModel", "Error loading data: ${e.message}")
//            }
//        }
//    }
//}

//class NokatRepo(private val apiService: ApiService, private val database: PostDatabase) {
//
//    fun getAllImgsNokatSerPag(page: Int): Flow<PagingData<ImgsNokatModel>> {
//        return Pager(
//            config = PagingConfig(pageSize = 12, enablePlaceholders = false),
//            pagingSourceFactory = { ImagePaging(apiService, page) } // Pass page if needed
//        ).flow
//    }
//}